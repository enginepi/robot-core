package robot;

import ch.qos.logback.core.util.TimeUtil;
import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.actuator.impl.L298;
import com.enginepi.robot.input.JoyStick;
import com.pi4j.io.gpio.RaspiPin;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author liangdi
 */
@Slf4j
@Robot
public class AutomaticTestRobot extends AbstractRobot implements IRobot {

    public static final int Turn=0;
    public static final int Speed=1;
    JoyStick joyStick;
    L298 left=new L298();
    L298 right=new L298();


    @Override
    public void setup() {
        left.initMotorA(RaspiPin.GPIO_08,RaspiPin.GPIO_09, RaspiPin.GPIO_07);
        joyStick=new JoyStick(0);
        left.initMotorB(RaspiPin.GPIO_15,RaspiPin.GPIO_16, RaspiPin.GPIO_01);
        right.initMotorA(RaspiPin.GPIO_12,RaspiPin.GPIO_13,RaspiPin.GPIO_14);
        right.initMotorB(RaspiPin.GPIO_06,RaspiPin.GPIO_10,RaspiPin.GPIO_11);
    }

    @Override
    public boolean supportAutomatic() {
        // 返回 true 则会执行自动阶段的代码
        return true;
    }

    boolean stopped = false;
    /**
     * 自动阶段 循环被调用
     */
    @Override
    public void automatic() throws InterruptedException {


        /**
         * 如果已经执行了一遍 就不再执行 避免错误
         */
        if(stopped) {
            return;
        }


        // 前进 5秒  速递 0.3

        go(-0.2);

        TimeUnit.SECONDS.sleep(2);


        turn(0.4);

        TimeUnit.SECONDS.sleep(1);

        turn(-0.4);

        TimeUnit.SECONDS.sleep(1);

        // 后退
        go(0.2);

        TimeUnit.SECONDS.sleep(2);

        // 停止
        stop();

        // 执行一遍后 设置一个标志  stopped = true  ，避免重复执行
        stopped  = true;
    }

    public void go(double speed) {
        left.getMotorA().set(speed);
        left.getMotorB().set(speed);
        right.getMotorA().set(speed);
        right.getMotorB().set(speed);
    }

    public void stop() {

        left.getMotorA().set(0);
        left.getMotorB().set(0);
        right.getMotorA().set(0);
        right.getMotorB().set(0);
    }

    public void turn(double turn) {
        left.getMotorA().set(-turn);
        left.getMotorB().set(-turn);
        right.getMotorA().set(turn);
        right.getMotorB().set(turn);
    }

    @Override
    public void loop() throws InterruptedException {
        if(joyStick.isEnable()){
            double speed=joyStick.getAxisValue(Speed);
            double turn=joyStick.getAxisValue(Turn);
            if(Math.abs(speed)>0.3){
                log.info("前进或后退:{}",speed);
                go(speed);
            }else{
                if(Math.abs(turn)>0.3){
                    log.info("转弯:{}",turn);
                    turn(turn);


                }else{
                    stop();

                }

            }


        }
    }
}
