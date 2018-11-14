package com.qianguatech.iot;

import com.qianguatech.iot.core.Device;
import com.qianguatech.iot.core.DeviceAction;
import com.qianguatech.iot.core.DeviceActionListener;
import com.qianguatech.iot.mqtt.DeviceClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

@Slf4j
public class DeviceMqttClientTest {

    @Test
    public void testDeviceClient() throws MqttException, InterruptedException {
        String server = "tcp://123.206.211.14:61613";
        String uuid = "001";
        String user = "admin";
        String password = "password";

        DeviceClient client = new DeviceClient();

        client.init(uuid,server,user,password);



        client.onAction(new DeviceActionListener() {
            @Override
            public void onAction(Device device, DeviceAction action) {
                log.info("device:{}",device.getUuid());
                log.info("action:{}",action.toString());
            }
        });

        while(true) {
            Thread.sleep(1000);
        }
    }
}
