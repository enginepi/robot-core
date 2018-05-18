package com.enginepi.robot.actuator.impl;

import com.pi4j.io.gpio.*;
import lombok.extern.slf4j.Slf4j;
import com.enginepi.EnginePi;
import com.enginepi.robot.actuator.Motor;

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
public class L298N {



    private L298NMotor motorA;
    private L298NMotor motorB;

    public L298N() {
    }

    public L298N(Pin PIN_ENABLE_A, Pin PIN_IN1, Pin PIN_IN2, Pin PIN_IN3, Pin PIN_IN4, Pin PIN_ENABLE_B) {
      this.initMotorA(PIN_ENABLE_A,PIN_IN1,PIN_IN2);
      this.initMotorB(PIN_ENABLE_B,PIN_IN3,PIN_IN4);
    }

    public void initMotorA(Pin PIN_ENABLE_A, Pin PIN_IN1, Pin PIN_IN2) {
        this.motorA = new L298NMotor(PIN_ENABLE_A,PIN_IN1,PIN_IN2);
    }

    public void initMotorB(Pin PIN_ENABLE_B, Pin PIN_IN3,Pin PIN_IN4) {
        this.motorB = new L298NMotor(PIN_ENABLE_B,PIN_IN3,PIN_IN4);
    }


    public L298NMotor getMotorA() {
        return motorA;
    }

    public L298NMotor getMotorB() {
        return motorB;
    }

    /**
     * 马达子类
     */
    public static class L298NMotor implements Motor{
        private Pin PIN_ENABLE;
        private Pin PIN_IN1;
        private Pin PIN_IN2;

        private GpioPinPwmOutput pwm;
        private GpioPinDigitalOutput control1;
        private GpioPinDigitalOutput control2;

        private double speed = 0;
        private boolean inverted = false;

        public L298NMotor(Pin PIN_ENABLE, Pin PIN_IN1, Pin PIN_IN2) {
            this.PIN_ENABLE = PIN_ENABLE;
            this.PIN_IN1 = PIN_IN1;
            this.PIN_IN2 = PIN_IN2;

            this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(PIN_ENABLE);
            this.pwm.setShutdownOptions(true,PinState.LOW);
            this.control1 = EnginePi.gpio.provisionDigitalOutputPin(PIN_IN1);
            this.control1.setShutdownOptions(true,PinState.LOW);
            this.control2 = EnginePi.gpio.provisionDigitalOutputPin(PIN_IN2);
            this.control2.setShutdownOptions(true,PinState.LOW);
        }



        @Override
        public void disable() {
            this.pwm.setPwm(0);
            this.control1.high();
            this.control2.high();
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
                log.info("正转:{},{}",this.speed,this.inverted);
                if(!this.inverted) {
                    this.control1.high();
                    this.control2.low();
                } else {
                    this.control1.low();
                    this.control2.high();
                }

                this.pwm.setPwm((int) (speed * 100));

            } else if(this.speed == 0) {
                this.pwm.setPwm(0);
            } else {

                log.info("反转:{},{}",this.speed,this.inverted);

                if(!this.inverted) {
                    this.control1.low();
                    this.control2.high();
                } else {
                    this.control1.high();
                    this.control2.low();
                }

                this.pwm.setPwm((int) (Math.abs(speed) * 100));
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

            if(!this.inverted) {
                this.control1.high();
                this.control2.low();
            } else {
                this.control1.low();
                this.control2.high();
            }


            this.set(speed);
        }

        @Override
        public boolean getInverted() {
            return this.inverted;
        }

        @Override
        public void stop() {
            this.pwm.setPwm(0);
            this.control1.low();
            this.control2.low();
        }
    }
}
