package com.enginepi.robot.actuator.impl;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.wiringpi.Gpio;
import lombok.extern.slf4j.Slf4j;
import com.enginepi.EnginePi;

/**
 * @author liangdi
 */
@Slf4j
public class Servo {
    private GpioPinPwmOutput pwm;
    public static final int PWM_RANGE = 200;
    public static final int PWM_CLOCK = 1920;
    public Servo(Pin pin) {
        Gpio.pwmSetClock(PWM_CLOCK);
        Gpio.pwmSetRange(PWM_RANGE);
        this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(pin);
        this.pwm.setPwmRange(PWM_RANGE);
        this.pwm.setShutdownOptions(true, PinState.LOW);

    }

    /**
     * 设置角度
     * @param angle
     */
    public void set(int angle) {
        long v = EnginePi.map(angle, 0, 180, 0, 100);
        log.info("set angle:{}",(int)v);
        this.pwm.setPwm((int) v);

    }
}
