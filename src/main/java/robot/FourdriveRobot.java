package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.actuator.impl.L298;
import com.enginepi.robot.input.JoyStick;
import com.pi4j.io.gpio.RaspiPin;
import lombok.extern.slf4j.Slf4j;

@Robot
@Slf4j
public class FourdriveRobot extends AbstractRobot implements IRobot {
    public static final int Turn=0;
    public static final int Speed=1;
    JoyStick joyStick;
    L298 left=new L298();
    L298 right=new L298();

    @Override
    public void setup() {
        super.setup();
        left.initMotorA(RaspiPin.GPIO_08,RaspiPin.GPIO_09, RaspiPin.GPIO_07);
        joyStick=new JoyStick(0);
        left.initMotorB(RaspiPin.GPIO_15,RaspiPin.GPIO_16, RaspiPin.GPIO_01);
        right.initMotorA(RaspiPin.GPIO_12,RaspiPin.GPIO_13,RaspiPin.GPIO_14);
        right.initMotorB(RaspiPin.GPIO_06,RaspiPin.GPIO_10,RaspiPin.GPIO_11);
    }

    @Override
    public void loop() throws InterruptedException {
        if(joyStick.isEnable()){
            double speed=joyStick.getAxisValue(Speed);
            double turn=joyStick.getAxisValue(Turn);
            if(Math.abs(speed)>0.3){
                log.info("前进:{}",speed);
                if(speed>0) {
                    left.getMotorA().set(speed);
                    left.getMotorB().set(speed);
                    right.getMotorA().set(speed);
                    right.getMotorB().set(speed);

                }else {
                    left.getMotorA().set(speed);
                    left.getMotorB().set(speed);
                    right.getMotorA().set(speed);
                    right.getMotorB().set(speed);
                }
            }else{
                if(Math.abs(turn)>0.3){
                    log.info("转弯:{}",turn);

                        left.getMotorA().set(-turn);
                        left.getMotorB().set(-turn);
                        right.getMotorA().set(turn);
                        right.getMotorB().set(turn);


                }else{

                    left.getMotorA().set(0);
                    left.getMotorB().set(0);
                    right.getMotorA().set(0);
                    right.getMotorB().set(0);

                }

            }


        }



    }
}
