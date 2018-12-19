package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.actuator.impl.L298;
import com.enginepi.robot.input.JoyStick;
import com.pi4j.io.gpio.RaspiPin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Robot
public class L298Robot  extends AbstractRobot implements IRobot {
    public static final int TURN = 2;
    public static final int SPEED  = 1;

    JoyStick joyStick;
    L298 left = new L298();

    @Override
    public void setup() {
        super.setup();

        left.initMotorA(RaspiPin.GPIO_08,RaspiPin.GPIO_09,RaspiPin.GPIO_07);
        joyStick = new JoyStick(0);
        left.initMotorB(RaspiPin.GPIO_15,RaspiPin.GPIO_16,RaspiPin.GPIO_01);
    }

    @Override
    public void loop() throws InterruptedException {

        if(joyStick.isEnable()) {
            // 手柄控制

            double speed = joyStick.getAxisValue(SPEED);

            double turn = joyStick.getAxisValue(TURN);
            if(Math.abs(speed) > 0.3) {
                // log.info("speed:{}",speed);

                if(speed > 0) {
                    // 后退
                    left.getMotorA().set(speed);
                    left.getMotorB().set(speed);
                    log.info("后退:{}",speed);
                } else {
                    // 前进
                    log.info("前进:{}",speed);
                    left.getMotorA().set(speed);
                    left.getMotorB().set(speed);

                }
            }else {
                left.getMotorA().set(0);
                left.getMotorB().set(0);
            }

            if(Math.abs(turn) > 0.3) {
                if(turn > 0) {
                    left.getMotorA().set(turn);
                    left.getMotorB().set(-turn);
                    log.info("后退:{}",turn);
                } else {
                    left.getMotorA().set(turn);
                    left.getMotorB().set(-turn);
                    log.info("后退:{}",turn);
                }

            }
        }

    }
}
