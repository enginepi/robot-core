package com.enginepi.robot;

/**
 * 机器人类接口
 * @author liangdi
 */
public interface IRobot {
    boolean supportAutomatic();
    void setRemoteEnable(boolean remoteEnable);

    /**
     * 自动阶段调用
     */
    void automatic();

    /**
     * 程序启动调用
     */
    void setup();

    /**
     * 系统循环调用
     * @throws InterruptedException
     */
    void loop() throws InterruptedException;

}
