package me.liangdi.robot.actuator;

/**
 * @author liangdi
 */
public interface Motor {
    void disable();

    /**
     * 设置速度
     * @param speed
     */
    void set(double speed);

    /**
     * 获得速度
     * @return
     */
    double get();

    /**
     * 设置正反转
     * @param inverted
     */
    void setInverted(boolean inverted);

    /**
     * 获得正反转状态
     * @return
     */
    boolean getInverted();

    /**
     * 停止
     */
    void stop();
}
