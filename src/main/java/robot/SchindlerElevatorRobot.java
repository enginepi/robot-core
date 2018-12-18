package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.actuator.impl.L298;
import com.enginepi.robot.digital.Relay;
import com.enginepi.robot.digital.Switch;
import com.pi4j.io.gpio.RaspiPin;
import com.qianguatech.iot.core.DeviceEvent;
import com.qianguatech.iot.mqtt.DeviceClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import robot.schinder.Actions;

import java.util.List;

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
     * 上行灯带
     * GPIO 7 ， 0
     * 下行灯带 2,3
     */

    Relay upLightRelay = null;

    Relay downLightRelay = null;


    /**
     * 太极门 / 氛围灯
     * 22  ， 23   24  25
     */
    Relay doorLightRelay = null;
    Relay ambientLightRelay = null;




    /**
     * iot MQTT 客户端
     */
    DeviceClient client = null;

    /**
     * 门的限位开关
     * GPIO_26
     */

    Switch doorLimitSwitch  = null;

    /**
     * 开门的 l298
     *
     * 4/5   27/28
     */
    L298 doorMotor = new L298();


    int floor = 1;
    /**
     *  0 ，初始化, 1， 开门  , 2 关门
     */
    volatile int status = 0;


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

        doorMotor.initMotorAWithDir(RaspiPin.GPIO_04,RaspiPin.GPIO_05);
        doorMotor.initMotorBWithDir(RaspiPin.GPIO_27,RaspiPin.GPIO_28);



        upLightRelay = new Relay(RaspiPin.GPIO_07);
        downLightRelay = new Relay(RaspiPin.GPIO_02);

        doorLightRelay = new Relay(RaspiPin.GPIO_22);
        ambientLightRelay = new Relay(RaspiPin.GPIO_24);



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
                    stop();
                }  else if(actionName.equals(Actions.ACTION_GOTO_FLOOR)) {

                    log.info("电梯运行到指定楼层");

                    List<Object> args = action.getArgs();

                    if(!args.isEmpty()) {
                        int floor = (int) args.get(0);
                        gotoFloor(floor);
                    } else {
                        log.warn("楼层参数错误");
                    }

                } else if(actionName.equals(Actions.ACTION_CLOSE_DOOR)) {
                    log.info("关闭电梯门");
                    closeDoor();
                } else if(actionName.equals(Actions.ID_ENTITY)) {
                    log.info("身份认证");

                    List<Object> args = action.getArgs();
                    if(!args.isEmpty()) {

                    }
                }


            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    void gotoFloor(int floor) {
        if(this.floor > floor) {
            // 下行
            log.info("下行");
            turnDownLight(true);
        } else {
            // 上行
            log.info("上行");
            turnUpLight(true);
        }



        try {
            Thread.sleep(3000);
            DeviceEvent event = new DeviceEvent();

            event.setName("floor");

            event.setContent(floor);
            client.emit(event);
            turnUpLight(false);
            turnDownLight(false);
            this.floor = floor;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    /**
     * 停止开门电机
     */
    void stopDoor() {
        log.info("stop door");
        doorMotor.getMotorA().disable();
        doorMotor.getMotorB().disable();
        turnDoorLight(false);
        if(this.status ==1) {
            this.status = 0;
            DeviceEvent event = new DeviceEvent();

            event.setName("stopDoor");

            client.emit(event);
        }

    }

    /**
     * 开门
     */
    void openDoor() {

        this.status = 1;

        doorMotor.getMotorA().set(1.0);
        doorMotor.getMotorB().set(-1.0);
        turnAmbientLight(true);
        turnDoorLight(true);
    }


    void closeDoor() {
        doorMotor.getMotorA().set(-1.0);
        doorMotor.getMotorB().set(1.0);

        try {
            Thread.sleep(5000);

            this.status = 1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void turnUpLight(boolean on) {
        if(on) {
            upLightRelay.set(Relay.Value.On);
        } else {
            upLightRelay.set(Relay.Value.Off);
        }
    }

    void turnDownLight(boolean on) {
        if(on) {
            downLightRelay.set(Relay.Value.On);
        } else {
            downLightRelay.set(Relay.Value.Off);
        }
    }

    /**
     * 门上的灯控制
     * @param on
     */
    void turnDoorLight(boolean on) {
        if(on) {
            doorLightRelay.set(Relay.Value.On);
        } else {
            doorLightRelay.set(Relay.Value.Off);
        }
    }

    void turnAmbientLight(boolean on) {
        if(on) {
            ambientLightRelay.set(Relay.Value.On);
        } else {
            ambientLightRelay.set(Relay.Value.Off);
        }
    }

    /**
     * 电梯停止运行
     */
    void stop() {
        turnDownLight(false);
        turnUpLight(false);

        turnDoorLight(false);
    }

    @Override
    public void loop() throws InterruptedException {

       if (doorLimitSwitch.on()){
          //log.info("switch on");
           if(status == 1) {
               stopDoor();
           }
            //log.info("switch on");
            //stopDoor();
           // closeDoor();
            // upLightRelay.set(Relay.Value.On);
//
//            doorLightRelay.set(Relay.Value.On);
//            ambientLightRelay.set(Relay.Value.On);

        } else {
          // openDoor();
           // upLightRelay.set(Relay.Value.Off);
//           upLightRelay.set(Relay.Value.Off);
//           downLightRelay.set(Relay.Value.Off);
//
//           doorLightRelay.set(Relay.Value.Off);
//           ambientLightRelay.set(Relay.Value.Off);
       }


    }
}
