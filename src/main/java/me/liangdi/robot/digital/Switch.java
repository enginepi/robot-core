package me.liangdi.robot.digital;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import me.liangdi.robot.EnginePi;

/**
 * @author liangdi
 */
@Slf4j
public class Switch {
    private GpioPinDigitalInput input;

    public Switch(Pin pin) {
        this.input = EnginePi.gpio.provisionDigitalInputPin(pin, PinPullResistance.PULL_UP);

        this.input.setShutdownOptions(true, PinState.LOW);

    }

    /**
     * 判断是否开启
     * @return
     */
    public  boolean on() {
        return input.getState().isLow();
    }

    public boolean off() {
        return input.getState().isHigh();
    }
}
