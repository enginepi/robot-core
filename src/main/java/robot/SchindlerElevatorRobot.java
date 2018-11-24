package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.actuator.impl.L298;
import com.enginepi.robot.digital.Relay;
import com.enginepi.robot.digital.Switch;
import com.pi4j.io.gpio.RaspiPin;
import com.qianguatech.iot.mqtt.DeviceClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import robot.schinder.Actions;

/**
 * @author liangdi
 */
@Robot
@Slf4j
public class SchindlerElevatorRobot extends AbstractRobot implements IRobot {

    /**
     * 开关门电机继电器
     */
    Relay doorRelay = null;

    /**
     * 太极门 灯带
     */
    Relay circleLightRelay = null;

    /**
     * 氛围灯带
     */
    Relay AmbientLightRelay = null;


    /**
     * iot MQTT 客户端
     */
    DeviceClient client = null;

    /**
     * 门的限位开关
     * GPIO_26
     */

    Switch doorLimitSwitch  = null;


    L298 doorMotor = new L298();


    // MQTT 信息

    String server = "tcp://123.206.211.14:61613";
    String uuid = "001";
    String user = "admin";
    String password = "password";

    @Override
    public void setup() {
        super.setup();

        client = new DeviceClient();

        doorLimitSwitch = new Switch(RaspiPin.GPIO_26);

        doorMotor.initMotorA(RaspiPin.GPIO_04,RaspiPin.GPIO_05);

        try {
            client.init(uuid,server,user,password);
            client.onAction((device, action) -> {
                log.info("device:{}",device.getUuid());
                log.info("action:{}",action.toString());

                String actionName = action.getName();

                if(actionName.equals(Actions.ACTION_OPEN_DOOR)) {
                    log.info("打开电梯门");
                    openDoor();
                } else if(actionName.equals(Actions.ACTION_STOP)) {
                    log.info("电梯停止运行");
                }  else if(actionName.equals(Actions.ACTION_GOTO_FLOOR)) {
                    log.info("电梯运行到指定楼层");
                }


            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    /**
     * 停止开门电机
     */
    void stopDoor() {
        doorMotor.getMotorA().set(1.0);
    }

    /**
     * 开门
     */
    void openDoor() {
        doorMotor.getMotorA().disable();
    }

    /**
     * 电梯停止运行
     */
    void stop() {

    }

    @Override
    public void loop() throws InterruptedException {

       if (doorLimitSwitch.on()){
            log.info("switch on");
            stopDoor();

        }
    }
}
