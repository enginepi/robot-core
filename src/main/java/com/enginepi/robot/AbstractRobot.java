package com.enginepi.robot;

import com.enginepi.robot.communication.RobotMqttClient;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * @author liangdi
 */
@Slf4j
public abstract class AbstractRobot implements IRobot{
    protected boolean remoteEnable;
    protected RobotMqttClient mqttClient;
    protected Properties properties = new Properties();

    @Override
    public void setRemoteEnable(boolean remoteEnable) {
        this.remoteEnable = remoteEnable;
    }

    @Override
    public void setup() {
        if(this.remoteEnable) {
            // 远程配置

        }
    }


    @Override
    public void automatic() {

    }

    @Override
    public boolean supportAutomatic() {
        return false;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getProperty(String key) {
        return  properties.getProperty(key,"");
    }

    @Override
    public Properties getProperties() {
        return properties;
    }
}
