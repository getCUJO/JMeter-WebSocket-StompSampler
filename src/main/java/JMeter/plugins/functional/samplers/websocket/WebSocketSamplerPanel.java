/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package JMeter.plugins.functional.samplers.websocket;

import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.protocol.http.gui.HTTPArgumentsPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Maciej Zaleski
 */
public class WebSocketSamplerPanel extends javax.swing.JPanel {
    private HTTPArgumentsPanel attributePanel;

    /**
     * Creates new form WebSocketSamplerPanel
     */
    public WebSocketSamplerPanel() {
        initComponents();

        attributePanel = new HTTPArgumentsPanel();
        querystringAttributesPanel.add(attributePanel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        serverAddressTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        serverPortTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        connectionTimeoutTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        responseTimeoutTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        contextPathTextField = new javax.swing.JTextField();
        protocolTextField = new javax.swing.JTextField();
        contentEncodingTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        connectionIdTextField = new javax.swing.JTextField();
        querystringAttributesPanel = new javax.swing.JPanel();
        ignoreSslErrorsCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        connectPayloadEditorPane = new javax.swing.JEditorPane();
        jLabelConnect = new javax.swing.JLabel();
        streamingConnectionCheckBox = new javax.swing.JCheckBox();
        responseExpressionTextField = new JTextField();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Web Server"));

        jLabel1.setText("Server Name or IP:");

        jLabel2.setText("Port Number:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(serverAddressTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(serverPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(serverAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2)
                                .addComponent(serverPortTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Timeout (milliseconds)"));

        jLabel3.setText("Connection:");

        jLabel17.setText("Response:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connectionTimeoutTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(responseTimeoutTextField)
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(connectionTimeoutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel17)
                                        .addComponent(responseTimeoutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("WebSocket Request"));

        jLabel4.setText("Protocol [ws/wss]:");

        jLabel5.setText("Path:");

        jLabel6.setText("Content encoding:");

        protocolTextField.setToolTipText("");

        jLabel8.setText("Connection Id:");

        querystringAttributesPanel.setLayout(new javax.swing.BoxLayout(querystringAttributesPanel, javax.swing.BoxLayout.LINE_AXIS));

        ignoreSslErrorsCheckBox.setText("Ignore SSL certificate errors");

        jScrollPane1.setViewportView(connectPayloadEditorPane);
        jLabelConnect.setText("Payload");

        streamingConnectionCheckBox.setText("Streaming connection");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(querystringAttributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                        .addComponent(jLabel4)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(protocolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jLabel6)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(contentEncodingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(jLabel8)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(connectionIdTextField)
                                        )
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabelConnect)
                                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                                        .addComponent(ignoreSslErrorsCheckBox)
                                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                        .addComponent(streamingConnectionCheckBox)
                                                        ))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(contextPathTextField)))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(jLabel4)
                                                .addComponent(protocolTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel6)
                                                .addComponent(contentEncodingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jLabel8)
                                                .addComponent(connectionIdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(contextPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(ignoreSslErrorsCheckBox)
                                                .addComponent(streamingConnectionCheckBox)
                                )
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(querystringAttributesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabelConnect)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                                .addContainerGap()
        ));

        JPanel responsePanel = new JPanel(new BorderLayout(10, 10));
        responsePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Response"));
        responsePanel.add(new JLabel("Expression:"), BorderLayout.WEST);
        responsePanel.add(responseExpressionTextField);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addComponent(responsePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                )
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(responsePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField connectionIdTextField;
    private javax.swing.JTextField connectionTimeoutTextField;
    private javax.swing.JTextField contentEncodingTextField;
    private javax.swing.JTextField contextPathTextField;
    private javax.swing.JCheckBox ignoreSslErrorsCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelConnect;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField protocolTextField;
    private javax.swing.JPanel querystringAttributesPanel;
    private javax.swing.JEditorPane connectPayloadEditorPane;
    private javax.swing.JTextField responseTimeoutTextField;
    private javax.swing.JTextField serverAddressTextField;
    private javax.swing.JTextField serverPortTextField;
    private javax.swing.JCheckBox streamingConnectionCheckBox;
    private javax.swing.JTextField responseExpressionTextField;
    // End of variables declaration//GEN-END:variables

    public void initFields() {
    }


    public void setConnectionId(String connectionId) {
        connectionIdTextField.setText(connectionId);
    }

    public String getConnectionId() {
        return connectionIdTextField.getText();
    }

    public void setContentEncoding(String contentEncoding) {
        contentEncodingTextField.setText(contentEncoding);
    }

    public String getContentEncoding() {
        return contentEncodingTextField.getText();
    }

    public void setContextPath(String contextPath) {
        contextPathTextField.setText(contextPath);
    }

    public String getContextPath() {
        return contextPathTextField.getText();
    }

    public void setProtocol(String protocol) {
        protocolTextField.setText(protocol);
    }

    public String getProtocol() {
        return protocolTextField.getText();
    }

    public void setResponseTimeout(String responseTimeout) {
        responseTimeoutTextField.setText(responseTimeout);
    }

    public String getResponseTimeout() {
        return responseTimeoutTextField.getText();
    }

    public void setConnectionTimeout(String connectionTimeout) {
        connectionTimeoutTextField.setText(connectionTimeout);
    }

    public String getConnectionTimeout() {
        return connectionTimeoutTextField.getText();
    }

    public void setServerAddress(String serverAddress) {
        serverAddressTextField.setText(serverAddress);
    }

    public String getServerAddress() {
        return serverAddressTextField.getText();
    }

    public void setServerPort(String serverPort) {
        serverPortTextField.setText(serverPort);
    }

    public String getServerPort() {
        return serverPortTextField.getText();
    }

    public void setConnectPayload(String connectPayload) {
        connectPayloadEditorPane.setText(connectPayload);
    }

    public String getConnectPayload() {
        return connectPayloadEditorPane.getText();
    }

    public void setStreamingConnection(Boolean streamingConnection) {
        streamingConnectionCheckBox.setSelected(streamingConnection);
    }

    public Boolean isStreamingConnection() {
        return streamingConnectionCheckBox.isSelected();
    }

    public String getResponseExpression() {
        return responseExpressionTextField.getText();
    }

    public void setResponseExpression(String expression) {
        responseExpressionTextField.setText(expression);
    }

    public void setIgnoreSslErrors(Boolean ignoreSslErrors) {
        ignoreSslErrorsCheckBox.setSelected(ignoreSslErrors);
    }

    public Boolean isIgnoreSslErrors() {
        return ignoreSslErrorsCheckBox.isSelected();
    }

    /**
     * @return the attributePanel
     */
    public ArgumentsPanel getAttributePanel() {
        return attributePanel;
    }
}
