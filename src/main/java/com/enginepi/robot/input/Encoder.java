package com.enginepi.robot.input;


import com.enginepi.EnginePi;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioInterrupt;
import com.pi4j.wiringpi.GpioInterruptEvent;
import com.pi4j.wiringpi.GpioInterruptListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 编码器
 * @author liangdi
 */
@Slf4j
public class Encoder {

    GpioPinDigitalInput pinA = null;
    int addressA  = 0;
    int addressB = 0;
    GpioPinDigitalInput pinB = null;
    GpioPinDigitalInput pinZ = null;

    long countA = 0;
    long countB = 0;
    long countZ = 0;

    volatile long count = 0;

    boolean autoReset = false;

    // based on [lastEncoded][encoded] lookup
    private static final int stateTable[][]= {
            {0, 1, 1, -1},
            {-1, 0, 1, -1},
            {-1, 1, 0, -1},
            {-1, 1, 1, 0}
    };


    private int lastEncoded = 0;
    private boolean firstPass = true;



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

            GpioInterrupt.addListener(new GpioInterruptListener() {
                @Override
                public  void pinStateChange(GpioInterruptEvent event) {

                    log.info("GpioInterrupt:{}",event.getPin());
                    //log.info("GpioInterrupt value:{}",event.getStateValue());
                    boolean state = event.getState();
                    int read = Gpio.digitalRead(addressB);

                    if(state) {
                        if(read  == 1) {
                            count--;
                        } else {
                            count++;
                        }
                    }
                }
            });

            addressA = pinA.getPin().getAddress();
            log.info("address:{}",addressA);

            GpioInterrupt.enablePinStateChangeCallback(addressA);
//
//            pinA.addListener( (GpioPinListenerDigital) new GpioPinListenerDigital() {
//                @Override
//                public synchronized void  handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
//                    //log.info("A event state:{}", event.getState());
//                    //log.info("A event edge:{}", event.getEdge());
//
//                    PinEdge edge = event.getEdge();
//                    PinState state = event.getState();
//                    PinState bChannelState = pinB.getState();
//                    if (edge == PinEdge.RISING) {
//
//                        if (state == PinState.HIGH) {
//                            if (bChannelState == PinState.LOW) {
//                                count++;
//                            } else {
//                                count--;
//                            }
//                        }
//                    }
//
//                    // int stateA = pinA.getState().getValue();
//                    //int stateB = pinB.getState().getValue();
//                    // calcEncoderValue(stateA, stateB);
//                }
//            });
        }

        if(pinB != null) {
            addressB = pinB.getPin().getAddress();
            GpioInterrupt.enablePinStateChangeCallback(addressB);

//            log.info("init pin b");
//            pinB.addListener((GpioPinListenerDigital) event -> {
//                //log.info("B event state:{}",event.getState());
//                //log.info("B event edge:{}",event.getEdge());
//
//
//            });
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

    private void calcEncoderValue(int stateA, int stateB) {

        // converting the 2 pin value to single number to end up with 00, 01, 10 or 11
        int encoded = (stateA << 1) | stateB;

        if (firstPass) {
            firstPass = false;
        } else {
            // going up states, 01, 11
            // going down states 00, 10
            int state = stateTable[lastEncoded][encoded];
            count += state;

        }

        lastEncoded = encoded;
    }

    public long read() {
        return count;
    }
}
