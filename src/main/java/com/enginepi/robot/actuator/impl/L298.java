package com.enginepi.robot.actuator.impl;

import com.enginepi.EnginePi;
import com.enginepi.robot.actuator.Motor;
import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;

/**
 * L298N 驱动
 *
 * ENABLE   LOW         HIGH/PWM    HIGH/PWM        HIGH/PWM
 * IN1      *           HIGH        LOW             HIGH
 * IN2      *           LOW         HIGH            HIGH
 * 状态      停止        正传         反转              停止/刹车
 *
 * @author liangdi
 */
@Slf4j
public class L298 {



    private L298Motor motorA;
    private L298Motor motorB;

    public L298() {
    }

    public L298(Pin PIN_PWM_A, Pin PIN_DIR_A, Pin PIN_PWM_B,Pin PIN_DIR_B) {
      this.initMotorA(PIN_PWM_A,PIN_DIR_A);
      this.initMotorB(PIN_PWM_B,PIN_DIR_B);
    }

    public void initMotorA(Pin PIN_PWM_A, Pin PIN_DIR_A) {
        this.motorA = new L298Motor(PIN_PWM_A,PIN_DIR_A);
    }

    public void initMotorA(Pin PIN_PWM_A,Pin PIN_DIR_A ,Pin PIN_DIR2_A) {
        this.motorA = new L298Motor(PIN_PWM_A,PIN_DIR_A,PIN_DIR2_A);
    }

    public void initMotorAWithDir(Pin PIN_DIR_A ,Pin PIN_DIR2_A) {
        this.motorA = new L298Motor();
        this.motorA.initWithDir(PIN_DIR_A,PIN_DIR2_A);
    }

    public void initMotorB(Pin PIN_PWM_B, Pin PIN_DIR_B) {
        this.motorB = new L298Motor(PIN_PWM_B,PIN_DIR_B);
    }
    public void initMotorB(Pin PIN_PWM_B, Pin PIN_DIR_B,Pin PIN_DIR2_B) {
        this.motorB = new L298Motor(PIN_PWM_B,PIN_DIR_B,PIN_DIR2_B);
    }

    public void initMotorBWithDir(Pin PIN_DIR_B, Pin PIN_DIR2_B) {
        this.motorB = new L298Motor();
        this.motorB.initWithDir(PIN_DIR_B,PIN_DIR2_B);
    }

    public L298Motor getMotorA() {
        return motorA;
    }

    public L298Motor getMotorB() {
        return motorB;
    }

    /**
     * 马达子类
     */
    public static class L298Motor implements Motor{
        private Pin PIN_PWM;

        private GpioPinPwmOutput pwm = null;
        private GpioPinDigitalOutput control1;
        private GpioPinDigitalOutput control2;

        private double speed = 0;
        private boolean inverted = false;
        public L298Motor(){

        }

        public L298Motor(Pin PIN_PWM, Pin PIN_DIR) {
            log.info("PIN_PWM:{}",PIN_PWM);

            this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(PIN_PWM);
            this.pwm.setShutdownOptions(true,PinState.LOW);
            this.control1 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR);
            this.control1.setShutdownOptions(true,PinState.LOW);
        }


        public L298Motor(Pin PIN_PWM, Pin PIN_DIR,Pin PIN_DIR2) {
            log.info("PIN_PWM:{}",PIN_PWM);
            log.info("PIN_DIR:{}",PIN_DIR);
            log.info("PIN_DIR2:{}",PIN_DIR2);

            this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(PIN_PWM);
            this.pwm.setShutdownOptions(true,PinState.LOW);
            this.control1 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR);
            this.control1.setShutdownOptions(true,PinState.LOW);
            this.control2 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR2);
            this.control2.setShutdownOptions(true,PinState.HIGH);
        }


        public void initWithDir(Pin PIN_DIR, Pin PIN_DIR2) {

            this.control1 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR);
            this.control1.setShutdownOptions(true,PinState.LOW);
            this.control2 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR2);
            this.control2.setShutdownOptions(true,PinState.LOW);
        }





        @Override
        public void disable() {
            if(this.pwm != null) {
                this.pwm.setPwm(0);
            }
            this.control1.high();
            if(this.control2 != null) {
                this.control2.high();
            }
        }

        @Override
        public void set(double speed) {
            this.speed = speed;
            if(this.speed > 1) {
                this.speed = 1;
            }
            if(this.speed < -1) {
                this.speed = -1;
            }

            if(this.speed > 0) {
                // 正转
                //log.info("正转:{},{}",this.speed,this.inverted);
                if(!this.inverted) {
                    this.control1.high();
                    if(this.control2!=null) {
                        this.control2.low();
                    }
                } else {
                    this.control1.low();
                    if(this.control2!=null) {
                        this.control2.high();
                    }
                }
                if(this.pwm != null) {
                    this.pwm.setPwm(Math.abs((int) (speed * 100)));

                }


            } else if(this.speed == 0) {
                this.disable();
            } else {

                //log.info("反转:{},{}",this.speed,this.inverted);

                if(!this.inverted) {
                    this.control1.low();
                    if(this.control2!=null) {
                        this.control2.high();
                    }
                } else {
                    this.control1.high();
                    if(this.control2!=null) {
                        this.control2.low();
                    }
                }

                if(this.pwm != null) {
                    this.pwm.setPwm((int) (Math.abs(speed) * 100));

                }
            }
        }

        @Override
        public double get() {
            return speed;
        }

        @Override
        public void setInverted(boolean inverted) {
            this.inverted  = inverted;


            double speed = this.speed;

            this.stop();


            this.set(speed);
        }

        @Override
        public boolean getInverted() {
            return this.inverted;
        }

        @Override
        public void stop() {
            if(this.pwm != null) {
                this.pwm.setPwm(0);

            }
            this.control1.low();

            if(this.control2!= null) {
                this.control2.low();
            }
        }
    }
}
