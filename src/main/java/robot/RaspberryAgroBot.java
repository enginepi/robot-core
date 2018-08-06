package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.input.JoyStick;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangdi
 */
@Slf4j
@Robot
public class RaspberryAgroBot extends AbstractRobot implements IRobot {
    public static final int TURN = 0;
    public static final int SPEED  = 1;
    JoyStick joyStick;


    @Override
    public void setup() {
        super.setup();
        joyStick = new JoyStick(0);
    }

    @Override
    public void loop() throws InterruptedException {
            if(joyStick.isEnable()) {
                // 手柄控制

                double speed = joyStick.getAxisValue(SPEED);

                double turn = joyStick.getAxisValue(TURN);
                if(Math.abs(speed) > 0.2) {
                    // log.info("speed:{}",speed);

                    if(speed > 0) {
                        // 后退
                        log.info("后退:{}",speed);
                    } else {
                        // 前进
                        log.info("前进:{}",speed);
                    }
                }

                if(Math.abs(turn) > 0.2) {
                    if(turn > 0) {
                        log.info("右转:{}",turn);
                    } else {
                        log.info("左传:{}",turn);
                    }
                }
            }
    }
}
