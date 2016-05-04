/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JMeter.plugins.functional.samplers.websocket;

import org.apache.commons.lang3.StringUtils;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Maciej Zaleski
 */
@WebSocket(maxTextMessageSize = 256 * 1024 * 1024)
public class ServiceSocket {

    protected final WebSocketSampler parent;
    protected WebSocketClient client;
    private static final Logger log = LoggingManager.getLoggerForClass();
    protected Deque<String> responseBacklog = new LinkedList<>();
    protected Integer error = 0;
    protected StringBuffer logMessage = new StringBuffer();
    protected CountDownLatch openLatch = new CountDownLatch(1);
    protected CountDownLatch closeLatch = new CountDownLatch(1);
    protected CountDownLatch sendLatch;
    protected Session session = null;
    protected String disconnectPattern = "^RECEIPT.*";
    protected int messageCounter = 1;
    protected Pattern sendExpression;
    protected Pattern disconnectExpression;
    protected boolean connected = false;
    private String sessionId;

    public ServiceSocket(WebSocketSampler parent, WebSocketClient client) {
        this.parent = parent;
        this.client = client;

        logMessage.append("\n\n[Execution Flow]\n");
        logMessage.append(" - Opening new connection\n");
        initializePatterns();
    }

    @OnWebSocketMessage
    public void onMessage(String msg) {
        synchronized (parent) {
            log.debug("Received message {" + sessionId + "}: " + msg);
            String length = " (" + msg.length() + " bytes)";
            logMessage.append(" - Received message #").append(messageCounter).append(length);
            logMessage.append(msg);
            addResponseMessage("[Message " + (messageCounter++) + "]\n" + msg + "\n\n");

            if (!disconnectPattern.isEmpty() && disconnectExpression.matcher(msg).find()) {
                logMessage.append("; matched connection close pattern").append("\n");
                closeLatch.countDown();
                close(StatusCode.NORMAL, "JMeter closed session.");
            } else if (sendExpression == null || sendExpression.matcher(msg).find()) {
                logMessage.append("; matched send pattern").append("\n");
                sendLatch.countDown();
            } else {
                logMessage.append("; didn't match any pattern").append("\n");
            }
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        logMessage.append(" - WebSocket conection has been opened").append("\n");
        log.debug("Connect " + session.isOpen());
        this.session = session;
        connected = true;
        openLatch.countDown();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        if (statusCode != 1000) {
            log.error("Disconnect " + statusCode + ": " + reason);
            logMessage.append(" - WebSocket conection closed unexpectedly by the server: [").append(statusCode).append("] ").append(reason).append("\n");
            error = statusCode;
        } else {
            logMessage.append(" - WebSocket conection has been successfully closed by the server").append("\n");
            log.debug("Disconnect " + statusCode + ": " + reason);
        }

        //Notify connection opening and closing latches of the closed connection
        openLatch.countDown();
        closeLatch.countDown();
        connected = false;
    }

    /**
     * @return response message made of messages saved in the responseBacklog cache
     */
    public String getResponseMessage() {
        synchronized (parent) {
            String responseMessage = "";
            //Iterate through response messages saved in the responseBacklog cache
            for (String response : responseBacklog) {
                responseMessage += response;
            }
            return responseMessage;
        }
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        logMessage.append(" - Waiting for messages for ").append(duration).append(" ").append(unit.toString()).append("\n");
        boolean res = this.closeLatch.await(duration, unit);

        if (!parent.isStreamingConnection()) {
            close(StatusCode.NORMAL, "JMeter closed session.");
        } else {
            logMessage.append(" - Leaving streaming connection open").append("\n");
        }

        return res;
    }

    public boolean awaitResponseMessage(int duration, TimeUnit unit) throws InterruptedException {
        logMessage.append(" - Waiting for messages for ").append(duration).append(" ").append(unit.toString()).append("\n");
        boolean res = true;
        if (sendExpression != null) {
            res = this.sendLatch.await(duration, unit);
        }

        if (!parent.isStreamingConnection()) {
            close(StatusCode.NORMAL, "JMeter closed session.");
        } else {
            logMessage.append(" - Leaving streaming connection open").append("\n");
        }

        return res;
    }

    public boolean awaitOpen(int duration, TimeUnit unit) throws InterruptedException {
        logMessage.append(" - Waiting for the server connection for ").append(duration).append(" ").append(unit.toString()).append("\n");
        boolean res = this.openLatch.await(duration, unit);

        if (connected) {
            logMessage.append(" - Connection established").append("\n");
        } else {
            logMessage.append(" - Cannot connect to the remote server").append("\n");
        }

        return res;
    }

    /**
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    public void sendMessage(String message, String expression) throws IOException {
        log.debug("** send message ** session id {" + sessionId + "} : " + message + ", bytes: " + Arrays.toString(message.getBytes()));
        sendExpression = initPattern(expression, "response");
        responseBacklog.clear();
        sendLatch = new CountDownLatch(1);
        session.getRemote().sendString(message);
    }

    public void close() {
        close(StatusCode.NORMAL, "JMeter closed session.");
    }

    public void close(int statusCode, String statusText) {
        //Closing WebSocket session
        if (session != null) {
            session.close(statusCode, statusText);
            logMessage.append(" - WebSocket session closed by the client").append("\n");
        } else {
            logMessage.append(" - WebSocket session wasn't started (...that's odd)").append("\n");
        }


        //Stoping WebSocket client; thanks m0ro
        try {
            client.stop();
            logMessage.append(" - WebSocket client closed by the client").append("\n");
        } catch (Exception e) {
            logMessage.append(" - WebSocket client wasn't started (...that's odd)").append("\n");
        }
        parent.removeConnection(sessionId);
    }

    /**
     * @return the error
     */
    public Integer getError() {
        return error;
    }

    /**
     * @return the logMessage
     */
    public String getLogMessage() {
        logMessage.append("\n\n[Variables]\n");
        logMessage.append(" - Message count: ").append(messageCounter - 1).append("\n");

        return logMessage.toString();
    }

    public void log(String message) {
        logMessage.append(message);
    }

    protected void initializePatterns() {
        disconnectExpression = initPattern(disconnectPattern, "disconnect");
    }

    private Pattern initPattern(String pattern, String type) {
        try {
            logMessage.append(" - Using ").append(type).append(" message pattern \"").append(pattern).append("\"\n");
            return StringUtils.isNotEmpty(pattern) ? Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE) : null;
        } catch (Exception ex) {
            logMessage.append(" - Invalid ").append(type).append(" message regular expression pattern: ").append(ex.getLocalizedMessage()).append("\n");
            log.error("Invalid " + type + " message regular expression pattern: " + ex.getLocalizedMessage());
        }
        return null;
    }

    /**
     * @return the connected
     */
    public boolean isConnected() {
        return connected;
    }

    public void initialize() {
        logMessage = new StringBuffer();
        logMessage.append("\n\n[Execution Flow]\n");
        logMessage.append(" - Reusing exising connection\n");
        error = 0;
    }

    private void addResponseMessage(String message) {
        while (responseBacklog.size() >= WebSocketSampler.MESSAGE_BACKLOG_COUNT) {
            responseBacklog.poll();
        }
        responseBacklog.add(message);
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
        log.debug("Session id {" + sessionId + "} was set.");
        logMessage.append(" Session id : ").append(sessionId).append("\n");
    }
}
