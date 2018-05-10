package me.liangdi.robot.actuator.impl;

import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import me.liangdi.robot.EnginePi;

/**
 * @author liangdi
 */
@Slf4j
public class Servo {
    private GpioPinPwmOutput pwm;

    public Servo(Pin pin) {
        this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(pin);
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
