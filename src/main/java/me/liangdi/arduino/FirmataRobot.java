package me.liangdi.arduino;

import com.pi4j.io.gpio.*;

import lombok.extern.slf4j.Slf4j;
import me.liangdi.robot.digital.Switch;
import me.liangdi.robot.input.JoyStick;

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

        Switch sw = new Switch(RaspiPin.GPIO_21);
        //Relay relay2 = new Relay(RaspiPin.GPIO_25);


        JoyStick joyStick = new JoyStick(0);

        while (true) {

            if(sw.on()) {
                log.info("switch is on");
            } else{
                log.info("switch is off");
            }

            if(joyStick.isEnable()) {
                log.info("手柄:{}",joyStick.getName());
                log.info("轴数量:{}",joyStick.getAxisCount());
                log.info("按钮量:{}",joyStick.getButtonCount());

                while (true) {



                    log.info("POV:{}",joyStick.POV());

                    if(joyStick.isButtonPressed(0)) {
                        log.info("button pressed");
                    }

                    Thread.sleep(500);
                }
            } else {
                log.info("手柄无效");
            }


            Thread.sleep(1000);





//            motor.set(0.5);
//            Thread.sleep(3000);
//
//
//            motor.stop();
//            Thread.sleep(2000);
//
//
//            motor.set(1);
//            Thread.sleep(3000);
//
//            motor.stop();
//            Thread.sleep(2000);
//
//
//            motor.set(-0.5);
//            Thread.sleep(3000);
//
//            motor.disable();
//            Thread.sleep(2000);


        }

    }
}
