package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangdi
 */
@Slf4j
@Robot
public class AutomaticTestRobot extends AbstractRobot implements IRobot {

    @Override
    public boolean supportAutomatic() {
        return true;
    }

    /**
     * 自动阶段 循环被调用
     */
    @Override
    public void automatic() {

            try {
                log.info("automatic running");

                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }



    }

    @Override
    public void loop() throws InterruptedException {
        log.info("loop............");
        Thread.sleep(1000);
    }
}
