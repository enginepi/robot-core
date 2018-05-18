package com.enginepi.robot;

import com.enginepi.robot.communication.RobotMqttClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangdi
 */
@Slf4j
public abstract class AbstractRobot implements IRobot{
    protected boolean remoteEnable;
    protected RobotMqttClient mqttClient;

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
}
