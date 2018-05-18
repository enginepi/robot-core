package me.liangdi.arduino;

import com.pi4j.io.gpio.*;

import lombok.extern.slf4j.Slf4j;
import me.liangdi.robot.input.JoyStick;
import me.liangdi.robot.steppermotor.impl.GenericStepperController;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class FirmataRobot {

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {

       log.info("<--Pi4J--> GPIO Control Example ... started.");

//        L298N l298N = new L298N();
//        l298N.initMotorA(RaspiPin.GPIO_22, GPIO_23,GPIO_24);
//
//        Motor motor = l298N.getMotorA();

        //Servo servo = new Servo(RaspiPin.GPIO_21);

        GenericStepperController stepper = new GenericStepperController(RaspiPin.GPIO_29,RaspiPin.GPIO_28);
        stepper.setResolution(8);
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
