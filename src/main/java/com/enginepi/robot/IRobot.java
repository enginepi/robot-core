package com.enginepi.robot;

/**
 * 机器人类接口
 * @author liangdi
 */
public interface IRobot {
    void setRemoteEnable(boolean remoteEnable);
    void setup();
    void loop() throws InterruptedException;
}
