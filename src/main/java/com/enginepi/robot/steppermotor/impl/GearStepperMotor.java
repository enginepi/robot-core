package com.enginepi.robot.steppermotor.impl;

import com.enginepi.EnginePi;
import com.pi4j.io.gpio.GpioPin;
import lombok.extern.slf4j.Slf4j;

/**
 * 减速步进电机
 * @author liangdi
 */
@Slf4j
public class GearStepperMotor {
    GpioPin pinA = null;
    GpioPin pinB = null;
    GpioPin pinZ = null;

    int type = 0;


    public GearStepperMotor(GpioPin pinA) {
        this.pinA = pinA;
    }

    public GearStepperMotor(GpioPin pinA, GpioPin pinB) {
        this.pinA = pinA;
        this.pinB = pinB;
    }

    public GearStepperMotor(GpioPin pinA, GpioPin pinB, GpioPin pinZ) {
        this.pinA = pinA;
        this.pinB = pinB;
        this.pinZ = pinZ;
    }

    public void init() {}

}
