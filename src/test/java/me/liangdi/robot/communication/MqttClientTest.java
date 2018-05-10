package me.liangdi.robot.communication;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

@Slf4j
public class MqttClientTest {

    @Test
    public void testClient () throws MqttException, InterruptedException {
        RobotMqttClient client = new RobotMqttClient();
        String server = "tcp://0.0.0.0:61613";
        String robot = "test-robot";
        String user = "admin";
        String password = "password";
        client.init(server,robot,user,password);


        client.publish(RobotMqttClient.TOPIC_LOG,"test log");


        Thread.sleep(10000);
    }
}
