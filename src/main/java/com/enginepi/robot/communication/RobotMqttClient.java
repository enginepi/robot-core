package com.enginepi.robot.communication;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

/**
 * MQTT 客户端程序
 * @author liangdi
 */
@Slf4j
public class RobotMqttClient implements MqttCallback{
    public static final String TOPIC_STICK = "topic_stick";
    public static final String TOPIC_COMMAND = "topic_command";
    public static final String TOPIC_VAR = "topic_var";
    public static final String TOPIC_LOG = "topic_log";
    public static final String TOPIC_COMPONENT = "topic_component";
    private String server;
    private String username;
    private String password;
    private String robot;
    private  int qos = 2;
    MqttClient client = null;

    public void init(String server, String robot,String username,String password) throws MqttException {
        this.server = server;
        this.username = username;
        this.password = password;
        this.robot = robot;

        client = new MqttClient(server,robot);
        client.setCallback(this);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        IMqttToken token = client.connectWithResult(options);

        log.info("token:{}",token.getSessionPresent());
        log.info("token:{}",token.isComplete());
        log.info("token:{}",token.getTopics());

        //if(token.)

        client.subscribe(TOPIC_STICK);
        client.subscribe(TOPIC_COMMAND);
        client.subscribe(TOPIC_VAR);

    }

    /**
     * 发布数据
     * @param topic
     * @param payload
     * @throws MqttException
     */
    public void publish(String topic,String payload) throws MqttException {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload.getBytes());
        message.setQos(qos);

        client.publish(topic,message);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.info("connectionLost");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        log.info("message:{},{}",topic,new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.info("deliveryComplete");
    }
}
