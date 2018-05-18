package me.liangdi.robot.steppermotor.impl;

import lombok.extern.slf4j.Slf4j;

/**
 * 丝杆步进电机
 * @author liangdi
 */
@Slf4j
public class ScrewStepperMotor {
    /**
     * 螺距 单位 mm
     */
    private int pitch;
    private boolean inverted;
    private GenericStepperController stepper;

    public ScrewStepperMotor(int pitch, GenericStepperController stepper) {
        this.pitch = pitch;
        this.stepper = stepper;
    }

    public GenericStepperController getStepper() {
        return stepper;
    }

    /**
     * 执行
     * @param distance
     */
    public void goDistance(int distance) {

    }

    /**
     * 向前执行距离
     * @param distance
     */
    public void goForward(int distance) {

    }

    /**
     * 向后执行距离
     * @param distance
     */
    public void goBackward(int distance) {

    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
        this.stepper.setInverted(inverted);
    }
}
