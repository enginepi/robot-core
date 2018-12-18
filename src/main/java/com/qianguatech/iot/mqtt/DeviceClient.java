package com.qianguatech.iot.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.qianguatech.iot.core.Device;
import com.qianguatech.iot.core.DeviceAction;
import com.qianguatech.iot.core.DeviceActionListener;
import com.qianguatech.iot.core.DeviceEvent;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备客户端
 * @author liangdi
 */
@Slf4j
public class DeviceClient implements MqttCallback {
    private String server;
    private String username;
    private String password;
    /**
     * 设备 UUID
     */
    private String deviceUUID;

    private  int qos = 2;
    MqttClient client = null;

    ObjectMapper mapper = new ObjectMapper();

    Gson gson = new Gson();


    List<DeviceActionListener> actionListeners = new ArrayList<>();

    public void init(String deviceUUID,String server,String username,String password) throws MqttException {

        this.deviceUUID = deviceUUID;
        this.server = server;
        this.username = username;
        this.password = password;

        client = new MqttClient(server,deviceUUID);
        client.setCallback(this);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        IMqttToken token = client.connectWithResult(options);

        log.info("token:{}",token.getSessionPresent());
        log.info("token:{}",token.isComplete());
        log.info("token:{}",token.getTopics());

        client.subscribe(Topic.action(deviceUUID));
    }

    /**
     * 添加
     * @param listener
     */
    public void onAction(DeviceActionListener listener) {
        actionListeners.add(listener);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("connection lost:",cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String body = new String(message.getPayload());
        int id = message.getId();

        log.info("mqtt id:{}",id);
        if(Topic.isAction(topic)) {

            Device device = new Device();
            device.setUuid(deviceUUID);

            try {
                DeviceAction action = mapper.readValue(body,DeviceAction.class);

                this.actionListeners.forEach(listener -> {
                    listener.onAction(device,action);
                });
            } catch (Exception ex) {
                log.warn("解析 deviceAction 出错:{}",body);
            }

        } else {
            log.warn("未处理 topic:{},{}",topic,body);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    /**
     * 发送事件
     * @param event
     * @return
     */
    public boolean emit(DeviceEvent event) {

        String content = gson.toJson(event);

        MqttMessage message=new MqttMessage(content.getBytes());
        message.setQos(qos);
        //设置是否在服务器中保存消息体
        message.setRetained(false);

        String topic = Topic.event(this.deviceUUID);

        MqttTopic mqttTopic=client.getTopic(topic);
        MqttDeliveryToken token= null;
        try {
            token = mqttTopic.publish(message);
        } catch (MqttException e) {

            e.printStackTrace();

            return false;
        }
        log.info("mqtt publish:{},\ntopic{}",content,topic);
        return token.isComplete();
    }
}
