package com.enginepi.robot.digital;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import com.enginepi.EnginePi;

/**
 * 继电器
 * @author liangdi
 */
@Slf4j
public class Relay {

    private GpioPinDigitalOutput output;

    private Value status = Value.Off;

    public Relay(Pin pin) {
        this.output = EnginePi.gpio.provisionDigitalOutputPin(pin,PinState.LOW);
        this.output.setShutdownOptions(true, PinState.LOW);
    }

    public void set(Value value) {
        //log.info("设置:{}",value);
        if(value == Value.On) {
            output.high();
        } else {
            output.low();
        }

        status = value;
    }

    public Value get() {
        return status;
    }

    public enum Value {
        On,
        Off
    }
}
