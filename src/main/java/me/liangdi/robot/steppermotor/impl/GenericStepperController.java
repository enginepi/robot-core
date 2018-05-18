package me.liangdi.robot.steppermotor.impl;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import lombok.extern.slf4j.Slf4j;
import me.liangdi.robot.EnginePi;

import java.util.concurrent.TimeUnit;

/**
 * 通用步进电机控制器
 *
 * 相关计算
 *      分辨率 = 最小度数 / 360 * 每圈步数
 *      每圈步数 = 360 / 最小度数 * 分辨率
 *
 * 减速比
 *
 *
 * @author liangdi
 */
@Slf4j
public class GenericStepperController {

    /**
     * 是否反转
     */
    private boolean inverted = false;
    /**
     * 分辨率
     */
    private int resolution = 50;
    /**
     * 每圈步数
     *
     */
    private int stepPerCircle = 10000;
    /**
     * 最小度数
     */
    private double stepAngle = 1.8;

    /**
     * 脉冲上升间歇时间 （两步数之间间隔）
     */
    private int risingMicrosecond = 120;
    /**
     * 脉冲下降间歇时间
     */
    private int fallingMicrosecond = 50;

    GpioPinDigitalOutput pul = null;
    GpioPinDigitalOutput dir = null;
    GpioPinDigitalOutput enable = null;

    /**
     *
     * @param pulPin 脉冲引脚
     * @param dirPin 方向引脚
     */
    public GenericStepperController(Pin pulPin, Pin dirPin) {
        this.pul = EnginePi.gpio.provisionDigitalOutputPin(pulPin);
        this.dir = EnginePi.gpio.provisionDigitalOutputPin(dirPin);

        this.dir.high();
        this.pul.high();

        this.pul.setShutdownOptions(true, PinState.LOW);
        this.dir.setShutdownOptions(true, PinState.LOW);



        log.info("GenericController init.");
    }

    public GenericStepperController(Pin pul, Pin dir, Pin enable) {
        this.pul = EnginePi.gpio.provisionDigitalOutputPin(pul);
        this.dir = EnginePi.gpio.provisionDigitalOutputPin(dir);
        this.enable = EnginePi.gpio.provisionDigitalOutputPin(enable);

        this.pul.setShutdownOptions(true, PinState.LOW);
        this.dir.setShutdownOptions(true, PinState.LOW);
        this.enable.setShutdownOptions(true, PinState.LOW);
        this.dir.high();

    }

    public boolean isInverted() {
        return inverted;
    }

    public void setInverted(boolean inverted) {
        this.inverted = inverted;
        if(inverted) {
            this.dir.low();
        } else {
            this.dir.high();
        }
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getStepPerCircle() {
        return stepPerCircle;
    }

    public void setStepPerCircle(int stepPerCircle) {
        this.stepPerCircle = stepPerCircle;
        this.setResolution((int) (this.getStepAngle() / 360 / stepPerCircle));
    }

    public double getStepAngle() {
        return stepAngle;
    }

    public void setStepAngle(double stepAngle) {
        this.stepAngle = stepAngle;
    }

    /**
     * 旋转角度
     * @param angle
     */
    public void runAngle(double angle) throws InterruptedException {
        int step = (int) (angle / stepAngle * resolution);

        runStepper(step);
    }

    /**
     * 旋转步数
     * @param stepper
     */
    public void runStepper(int stepper) throws InterruptedException {
        log.info("run step:{}",stepper);
        while(stepper >0) {
            this.pul.high();
            long delay = fallingMicrosecond * 1000;
            // while(start + fallingMicrosecond * 1000 >= System.nanoTime()){};

            // log.info("delay:{}",delay);

            EnginePi.delayNanos(delay);

            // log.info("delay end");

            // TimeUnit.MICROSECONDS.sleep(fallingMicrosecond);
            this.pul.low();


            delay = risingMicrosecond * 1000;
            //log.info("delay:{}",delay);

            EnginePi.delayNanos(delay);

            // log.info("delay end");
            // TimeUnit.MICROSECONDS.sleep(risingMicrosecond);
            stepper--;
        }

    }

    /**
     *
     * @param circleCount
     */
    public void runCircle(int circleCount) throws InterruptedException {
        this.runAngle(circleCount * 360);
    }
}
