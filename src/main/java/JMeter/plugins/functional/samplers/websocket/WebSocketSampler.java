/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JMeter.plugins.functional.samplers.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.util.EncoderCache;
import org.apache.jmeter.protocol.http.util.HTTPArgument;
import org.apache.jmeter.protocol.http.util.HTTPConstants;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.StringProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

/**
 * @author Maciej Zaleski
 */
public class WebSocketSampler extends AbstractSampler implements TestStateListener, ThreadListener {
    public static int DEFAULT_CONNECTION_TIMEOUT = 20000; //20 sec
    public static int DEFAULT_RESPONSE_TIMEOUT = 20000; //20 sec
    public static int MESSAGE_BACKLOG_COUNT = 3;

    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final String ARG_VAL_SEP = "="; // $NON-NLS-1$
    private static final String QRY_SEP = "&"; // $NON-NLS-1$
    private static final String WS_PREFIX = "ws://"; // $NON-NLS-1$
    private static final String WSS_PREFIX = "wss://"; // $NON-NLS-1$
    private static final String DEFAULT_PROTOCOL = "ws";
    private static Pattern REGEX_STOMP = Pattern.compile("(CONNECT|DISCONNECT|SUBSCRIBE|UNSUBSCRIBE|SEND|BEGIN|COMMIT|ACK|ABORT|NACK)(.*)", Pattern.DOTALL | Pattern.MULTILINE);

    private static Map<String, ServiceSocket> connectionList;
    private static ThreadLocal<ServiceSocket> serviceSocketThreadLocal = new ThreadLocal<>();

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public WebSocketSampler() {
        setName("WebSocket Stomp sampler");
    }

    private ServiceSocket getConnectionSocket(URI uri) throws Exception {
        String connectionId = getConnectionId();
        if (StringUtils.isEmpty(connectionId)) {
            connectionId = Thread.currentThread().getName();
        }
        if (isStreamingConnection() && connectionList.containsKey(connectionId)) {
            log.debug("connection " + connectionId + "already in list");
            ServiceSocket socket = connectionList.get(connectionId);
            socket.initialize();
            return socket;
        }
        //Create WebSocket client
        SslContextFactory sslContexFactory = new SslContextFactory();
        sslContexFactory.setTrustAll(isIgnoreSslErrors());
        WebSocketClient webSocketClient = new WebSocketClient(sslContexFactory, executor);

        ServiceSocket socket = new ServiceSocket(this, webSocketClient);
        socket.setSessionId(connectionId);
        if (isStreamingConnection()) {
            connectionList.put(connectionId, socket);
            serviceSocketThreadLocal.set(socket);
        }

        //Start WebSocket client thread and upgrade HTTP connection
        webSocketClient.start();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        webSocketClient.connect(socket, uri, request);

        //Get connection timeout or use the default value
        int connectionTimeout;
        try {
            connectionTimeout = Integer.parseInt(getConnectionTimeout());
        } catch (NumberFormatException ex) {
            log.warn("Connection timeout is not a number; using the default connection timeout of " + DEFAULT_CONNECTION_TIMEOUT + "ms");
            connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        }

        socket.awaitOpen(connectionTimeout, TimeUnit.MILLISECONDS);

        return socket;
    }

    @Override
    public SampleResult sample(Entry entry) {
        ServiceSocket socket = null;
        SampleResult sampleResult = new SampleResult();
        sampleResult.setSampleLabel(getName());
        sampleResult.setDataEncoding(getContentEncoding());

        //This StringBuilder will track all exceptions related to the protocol processing
        StringBuilder errorList = new StringBuilder();
        errorList.append("\n\n[Problems]\n");

        boolean isOK = false;

        //Set the message payload in the Sampler
        String connectPayloadMessage = getConnectPayload();
        int responseTimeout;
        try {
            responseTimeout = Integer.parseInt(getResponseTimeout());
        } catch (NumberFormatException ex) {
            log.warn("Request timeout is not a number; using the default request timeout of " + DEFAULT_RESPONSE_TIMEOUT + "ms");
            responseTimeout = DEFAULT_RESPONSE_TIMEOUT;
        }

        StringBuilder sampleDataBuilder = new StringBuilder();

        //Could improve precision by moving this closer to the action
        sampleResult.sampleStart();

        try {
            URI uri = getUri();
            sampleDataBuilder.append(uri).append('\n');
            socket = getConnectionSocket(uri);
            if (socket == null) {
                //Couldn't open a connection, set the status and exit
                sampleResult.setResponseCode("500");
                sampleResult.setSuccessful(false);
                sampleResult.sampleEnd();
                sampleResult.setResponseMessage(errorList.toString());
                errorList.append(" - Connection couldn't be opened").append("\n");
                return sampleResult;
            }
            sampleDataBuilder.append("Socket: ").append(socket.hashCode()).append('\n');

            sendMessage(socket, connectPayloadMessage);

            //Wait for any of the following:
            // - Response matching response pattern is received
            // - Response matching connection closing pattern is received
            // - Timeout is reached

            boolean result = socket.awaitResponseMessage(responseTimeout, TimeUnit.MILLISECONDS);
            sampleResult.setResponseCode(getCodeRetour(socket));
            sampleDataBuilder.append(connectPayloadMessage);

            sampleResult.setSamplerData(sampleDataBuilder.toString());

            //Set sampler response code
            if (!result || socket.getError() != 0) {
                isOK = false;
                sampleResult.setResponseCode(socket.getError().toString());
            } else {
                sampleResult.setResponseCodeOK();
                isOK = true;
            }

            //set sampler response
            sampleResult.setResponseData(socket.getResponseMessage(), getContentEncoding());
            if (!isStreamingConnection()) {
                socket.close();
            }
        } catch (URISyntaxException e) {
            errorList.append(" - Invalid URI syntax: ").append(e.getMessage()).append("\n").append(StringUtils.join(e.getStackTrace(), "\n")).append("\n");
        } catch (IOException e) {
            errorList.append(" - IO Exception: ").append(e.getMessage()).append("\n").append(StringUtils.join(e.getStackTrace(), "\n")).append("\n");
        } catch (NumberFormatException e) {
            errorList.append(" - Cannot parse number: ").append(e.getMessage()).append("\n").append(StringUtils.join(e.getStackTrace(), "\n")).append("\n");
        } catch (InterruptedException e) {
            errorList.append(" - Execution interrupted: ").append(e.getMessage()).append("\n").append(StringUtils.join(e.getStackTrace(), "\n")).append("\n");
        } catch (Exception e) {
            errorList.append(" - Unexpected error: ").append(e.getMessage()).append("\n").append(StringUtils.join(e.getStackTrace(), "\n")).append("\n");
        }

        sampleResult.sampleEnd();
        sampleResult.setSuccessful(isOK);

        String logMessage = (socket != null) ? socket.getLogMessage() : "";
        sampleResult.setResponseMessage(logMessage + errorList);
        return sampleResult;
    }

    public void removeConnection(String sessionId) {
        connectionList.remove(sessionId);
    }

    private void sendMessage(ServiceSocket socket, String payloadMessage) throws IOException, InterruptedException {
        //Send message only if it is not empty
        if (!StringUtils.isEmpty(payloadMessage)) {
            String termMessage = payloadMessage + "\u0000";
            socket.sendMessage(termMessage, getResponseExpression());
        }else{
            log.warn("empty message. send skipped");
        }
    }

    private String getCodeRetour(ServiceSocket socket) {
        String codeRetour = null;

        //If no response is received set code 204; actually not used...needs to do something else
        if (socket.getResponseMessage() == null || socket.getResponseMessage().isEmpty()) {
            codeRetour = "204";
        }
        return codeRetour;
    }


    @Override
    public void setName(String name) {
        if (name != null) {
            setProperty(TestElement.NAME, name);
        }
    }

    @Override
    public String getName() {
        return getPropertyAsString(TestElement.NAME);
    }

    @Override
    public void setComment(String comment) {
        setProperty(new StringProperty(TestElement.COMMENTS, comment));
    }

    @Override
    public String getComment() {
        return getProperty(TestElement.COMMENTS).getStringValue();
    }

    public URI getUri() throws URISyntaxException {
        String path = this.getContextPath();
        // Hack to allow entire URL to be provided in host field
        if (path.startsWith(WS_PREFIX)
                || path.startsWith(WSS_PREFIX)) {
            return new URI(path);
        }
        String domain = getServerAddress();
        String protocol = getProtocol();
        // HTTP URLs must be absolute, allow file to be relative
        if (!path.startsWith("/")) { // $NON-NLS-1$
            path = "/" + path; // $NON-NLS-1$
        }

        String queryString = getQueryString(getContentEncoding());
        if (isProtocolDefaultPort()) {
            return new URI(protocol, null, domain, -1, path, queryString, null);
        }
        return new URI(protocol, null, domain, Integer.parseInt(getServerPort()), path, queryString, null);
    }

    /**
     * Tell whether the default port for the specified protocol is used
     *
     * @return true if the default port number for the protocol is used, false
     * otherwise
     */
    public boolean isProtocolDefaultPort() {
        final int port = Integer.parseInt(getServerPort());
        final String protocol = getProtocol();
        return ("ws".equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTP_PORT)
                || ("wss".equalsIgnoreCase(protocol) && port == HTTPConstants.DEFAULT_HTTPS_PORT);
    }

    public String getServerPort() {
        final String port_s = getPropertyAsString("serverPort", "0");
        Integer port;
        String protocol = getProtocol();

        try {
            port = Integer.parseInt(port_s);
        } catch (Exception ex) {
            port = 0;
        }

        if (port == 0) {
            if ("wss".equalsIgnoreCase(protocol)) {
                return String.valueOf(HTTPConstants.DEFAULT_HTTPS_PORT);
            } else if ("ws".equalsIgnoreCase(protocol)) {
                return String.valueOf(HTTPConstants.DEFAULT_HTTP_PORT);
            }
        }
        return port.toString();
    }

    public void setServerPort(String port) {
        setProperty("serverPort", port);
    }

    public String getResponseTimeout() {
        return getPropertyAsString("responseTimeout", "20000");
    }

    public void setResponseTimeout(String responseTimeout) {
        setProperty("responseTimeout", responseTimeout);
    }


    public String getConnectionTimeout() {
        return getPropertyAsString("connectionTimeout", "5000");
    }

    public void setConnectionTimeout(String connectionTimeout) {
        setProperty("connectionTimeout", connectionTimeout);
    }

    public void setProtocol(String protocol) {
        setProperty("protocol", protocol);
    }

    public String getProtocol() {
        String protocol = getPropertyAsString("protocol");
        if (protocol == null || protocol.isEmpty()) {
            return DEFAULT_PROTOCOL;
        }
        return protocol;
    }

    public void setServerAddress(String serverAddress) {
        setProperty("serverAddress", serverAddress);
    }

    public String getServerAddress() {
        return getPropertyAsString("serverAddress");
    }

    public void setContextPath(String contextPath) {
        setProperty("contextPath", contextPath);
    }

    public String getContextPath() {
        return getPropertyAsString("contextPath");
    }

    public void setContentEncoding(String contentEncoding) {
        setProperty("contentEncoding", contentEncoding);
    }

    public String getContentEncoding() {
        return getPropertyAsString("contentEncoding", "UTF-8");
    }

    public void setConnectPayload(String connectPayload) {
        setProperty("connectPayload", connectPayload);
    }

    public String getConnectPayload() {
        return getPropertyAsString("connectPayload");
    }

    public void setResponseExpression(String expression) {
        setProperty("responseExpression", expression);
    }

    public String getResponseExpression() {
        return getPropertyAsString("responseExpression");
    }

    public void setIgnoreSslErrors(Boolean ignoreSslErrors) {
        setProperty("ignoreSslErrors", ignoreSslErrors);
    }

    public Boolean isIgnoreSslErrors() {
        return getPropertyAsBoolean("ignoreSslErrors");
    }

    public void setStreamingConnection(Boolean streamingConnection) {
        setProperty("streamingConnection", streamingConnection);
    }

    public Boolean isStreamingConnection() {
        return getPropertyAsBoolean("streamingConnection");
    }

    public void setConnectionId(String connectionId) {
        setProperty("connectionId", connectionId);
    }

    public String getConnectionId() {
        return getPropertyAsString("connectionId");
    }

    public String getQueryString(String contentEncoding) {
        // Check if the sampler has a specified content encoding
        if (JOrphanUtils.isBlank(contentEncoding)) {
            // We use the encoding which should be used according to the HTTP spec, which is UTF-8
            contentEncoding = EncoderCache.URL_ARGUMENT_ENCODING;
        }
        StringBuilder buf = new StringBuilder();
        PropertyIterator iter = getQueryStringParameters().iterator();
        boolean first = true;
        while (iter.hasNext()) {
            HTTPArgument item;
            Object objectValue = iter.next().getObjectValue();
            try {
                item = (HTTPArgument) objectValue;
            } catch (ClassCastException e) {
                item = new HTTPArgument((Argument) objectValue);
            }
            final String encodedName = item.getEncodedName();
            if (encodedName.length() == 0) {
                continue; // Skip parameters with a blank name (allows use of optional variables in parameter lists)
            }
            if (!first) {
                buf.append(QRY_SEP);
            } else {
                first = false;
            }
            buf.append(encodedName);
            if (item.getMetaData() == null) {
                buf.append(ARG_VAL_SEP);
            } else {
                buf.append(item.getMetaData());
            }

            // Encode the parameter value in the specified content encoding
            try {
                buf.append(item.getEncodedValue(contentEncoding));
            } catch (UnsupportedEncodingException e) {
                log.warn("Unable to encode parameter in encoding " + contentEncoding + ", parameter value not included in query string");
            }
        }
        return buf.toString();
    }

    public void setQueryStringParameters(Arguments queryStringParameters) {
        setProperty(new TestElementProperty("queryStringParameters", queryStringParameters));
    }

    public Arguments getQueryStringParameters() {
        return (Arguments) getProperty("queryStringParameters").getObjectValue();
    }


    @Override
    public void testStarted() {
        testStarted("unknown");
    }

    @Override
    public void testStarted(String host) {
        connectionList = new ConcurrentHashMap<>();
    }

    @Override
    public void testEnded() {
        testEnded("unknown");
    }

    @Override
    public void testEnded(String host) {
        for (ServiceSocket socket : connectionList.values()) {
            socket.close();
        }
    }

    @Override
    public void threadStarted() {
    }

    @Override
    public void threadFinished() {
        ServiceSocket serviceSocket = serviceSocketThreadLocal.get();
        if (serviceSocket != null) {
            serviceSocket.close();
        }
    }
}
