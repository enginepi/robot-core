package com.enginepi.robot.arduino;

import lombok.extern.slf4j.Slf4j;
import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.junit.Test;

import java.io.IOException;

@Slf4j
public class FirmataTest {

    @Test
    public void testFirmata( ) throws InterruptedException, IOException {

        IODevice device;
        Pin pin = null;

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

        if(pin == null) {
            log.warn(" device is null");

            return;
        }

        pin.setValue(1);




        Thread.sleep(100000);
    }
}
