package com.enginepi.robot.input;


import com.enginepi.EnginePi;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import lombok.extern.slf4j.Slf4j;

/**
 * 编码器
 * @author liangdi
 */
@Slf4j
public class Encoder {

    GpioPinDigitalInput pinA = null;
    GpioPinDigitalInput pinB = null;
    GpioPinDigitalInput pinZ = null;

    long countA = 0;
    long countB = 0;
    long countZ = 0;

    long count = 0;

    boolean autoReset = false;


    public Encoder(Pin aChannel) {
        this.pinA = EnginePi.gpio.provisionDigitalInputPin(aChannel,PinPullResistance.PULL_UP);
    }

    public Encoder(Pin aChannel, Pin bChannel) {
        this.pinA =  EnginePi.gpio.provisionDigitalInputPin(aChannel, PinPullResistance.PULL_UP);
        this.pinB =  EnginePi.gpio.provisionDigitalInputPin(bChannel,PinPullResistance.PULL_UP);
    }

    public Encoder(Pin aChannel, Pin bChannel, Pin zChannel) {
        this.pinA = EnginePi.gpio.provisionDigitalInputPin(aChannel,PinPullResistance.PULL_UP);
        this.pinB = EnginePi.gpio.provisionDigitalInputPin(bChannel,PinPullResistance.PULL_UP);
        this.pinZ = EnginePi.gpio.provisionDigitalInputPin(zChannel,PinPullResistance.PULL_UP);
    }

    public void init() {
        if(pinA != null) {
            log.info("init pin a");

            pinA.addListener((GpioPinListenerDigital) event -> {
                log.info("A event state:{}",event.getState());
                //log.info("A event edge:{}",event.getEdge());


            });
        }

        if(pinB != null) {
            log.info("init pin b");
            pinB.addListener((GpioPinListenerDigital) event -> {
                //log.info("B event state:{}",event.getState());
                //log.info("B event edge:{}",event.getEdge());

                if(event.getState()== PinState.HIGH) {
                    if(pinA.getState() == PinState.HIGH) {
                        count--;
                    } else {
                        count++;
                    }
                };
            });
        }

        if(pinZ != null) {
            log.info("init pin z");
            pinZ.addListener((GpioPinListenerDigital) event -> {
                log.info("Z event state:{}",event.getState());
                //log.info("Z event edge:{}",event.getEdge());

                if(autoReset) {
                    this.count = 0;
                }
            });
        }
    }

    public long read() {
        return count;
    }
}
