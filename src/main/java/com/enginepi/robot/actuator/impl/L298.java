package com.enginepi.robot.actuator.impl;

import com.enginepi.EnginePi;
import com.enginepi.robot.actuator.Motor;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
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

    public void initMotorB(Pin PIN_PWM_B, Pin PIN_DIR_B) {
        this.motorB = new L298Motor(PIN_PWM_B,PIN_DIR_B);
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
        private Pin PIN_DIR;

        private GpioPinPwmOutput pwm;
        private GpioPinDigitalOutput control1;

        private double speed = 0;
        private boolean inverted = false;

        public L298Motor(Pin PIN_PWM, Pin PIN_DIR) {
            this.PIN_PWM = PIN_PWM;
            this.PIN_DIR = PIN_DIR;
            log.info("PIN_PWM:{}",PIN_PWM);

            this.pwm = EnginePi.gpio.provisionSoftPwmOutputPin(PIN_PWM);
            this.pwm.setShutdownOptions(true,PinState.LOW);
            this.control1 = EnginePi.gpio.provisionDigitalOutputPin(PIN_DIR);
            this.control1.setShutdownOptions(true,PinState.LOW);
        }



        @Override
        public void disable() {
            this.pwm.setPwm(0);
            this.control1.high();
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
                } else {
                    this.control1.low();
                }

                this.pwm.setPwm((int) (speed * 100));

            } else if(this.speed == 0) {
                this.pwm.setPwm(0);
            } else {

                log.info("反转:{},{}",this.speed,this.inverted);

                if(!this.inverted) {
                    this.control1.low();
                } else {
                    this.control1.high();
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
            } else {
                this.control1.low();
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
        }
    }
}
