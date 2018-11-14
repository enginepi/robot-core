package robot;

import com.enginepi.robot.AbstractRobot;
import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import lombok.extern.slf4j.Slf4j;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

import java.io.IOException;

@Robot
@Slf4j
public class ArduinoRobot extends AbstractRobot implements IRobot {

    IODevice device;
    Pin pin;

    @Override
    public void setup() {
        super.setup();

        device = new FirmataDevice("/dev/ttyUSB2"); // construct the Firmata device instance using the name of a port
// subscribe to events using device.addEventListener(...);
// and/or device.getPin(n).addEventListener(...);


        try {
            device.start(); // initiate communication to the device
            device.ensureInitializationIsDone(); // wait for initialization is done
            pin = device.getPin(9);
            pin.setMode(Pin.Mode.PULLUP);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loop() throws InterruptedException {
        try {
            if(pin == null) {
                log.info("pin is null");

                Thread.sleep(1000);
                return;
            }
            pin.setValue(1);

            Thread.sleep(3000);

            pin.setValue(0);

            Thread.sleep(3000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
