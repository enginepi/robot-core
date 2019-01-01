package robot;


import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.vision.CameraServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liangdi
 */
@Slf4j
@Robot
public class CameraRobot extends AbstractRobot implements IRobot {

    CameraServer cameraServer = new CameraServer();

    @Override
    public void setup() {
        super.setup();
        cameraServer.start();
    }

    @Override
    public void loop() throws InterruptedException {

    }
}
