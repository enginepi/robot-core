package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.input.JoyStick;
import com.enginepi.robot.steppermotor.impl.GenericStepperController;
import com.pi4j.io.gpio.*;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author liangdi
 */
@Slf4j
@Robot
public class TestRobot extends AbstractRobot implements IRobot{

    GenericStepperController stepper ;


    @Override
    public void setup() {
        super.setup();
        log.info("robot setup");
        stepper = new GenericStepperController(RaspiPin.GPIO_29,RaspiPin.GPIO_28);

        stepper.setResolution(8);
    }

    @Override
    public void loop() throws InterruptedException {

        while (true) {
            JoyStick joyStick = new JoyStick(0);
            if(joyStick.isEnable()) {
                log.info("手柄:{}", joyStick.getName());
                log.info("轴数量:{}", joyStick.getAxisCount());
                log.info("按钮量:{}", joyStick.getButtonCount());
                while (true) {



                    // log.info("POV:{}",joyStick.POV());

                    if(joyStick.isButtonPressed(0)) {
                        log.info("button pressed");

                        // stepper.runStepper(10);
                        stepper.runAngle(180);
                    }

                    if(joyStick.isButtonPressed(1)) {
                        stepper.runCircle(2);
                    }

                    Thread.sleep(10);
                }

            } else {
                log.info("手柄无效");
            }

            Thread.sleep(1000);
        }
    }
}
