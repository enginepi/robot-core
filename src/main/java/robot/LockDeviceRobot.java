package robot;

import com.enginepi.EnginePi;
import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.enginepi.robot.digital.Relay;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.qianguatech.iot.core.Device;
import com.qianguatech.iot.core.DeviceAction;
import com.qianguatech.iot.core.DeviceActionListener;
import com.qianguatech.iot.mqtt.DeviceClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;

@Robot
@Slf4j
public class LockDeviceRobot extends AbstractRobot implements IRobot {

    Relay lock = null;
    Relay unlock = null;

    @Override
    public void setup() {
        super.setup();

        Pin gpio04 = RaspiPin.GPIO_04;


        lock = new Relay(RaspiPin.GPIO_04);
        lock.set(Relay.Value.Off);

        unlock = new Relay(RaspiPin.GPIO_05);

        lock.set(Relay.Value.Off);

        String server = "tcp://123.206.211.14:61613";
        String uuid = "001";
        String user = "admin";
        String password = "password";

        DeviceClient client = new DeviceClient();

        try {
            client.init(uuid,server,user,password);
            client.onAction(new DeviceActionListener() {
                @Override
                public void onAction(Device device, DeviceAction action) {
                    log.info("device:{}",device.getUuid());
                    log.info("action:{}",action.toString());

                    if(action.getName().equals("lock")) {
                        lock.set(Relay.Value.Off);

                        try {
                            Thread.sleep(300);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            lock.set(Relay.Value.On);
                        }
                    } else if(action.getName().equals("unlock")) {
                        unlock.set(Relay.Value.Off);

                        try {
                            Thread.sleep(300);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            unlock.set(Relay.Value.On);
                        }
                    }



                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void loop() throws InterruptedException {

    }
}
