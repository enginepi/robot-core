package me.liangdi.robot.input;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class JoyStickTest {

    @Test
    public void testJoyStick () throws InterruptedException {
        int port = 0;

        JoyStick joyStick = new JoyStick(port);

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


    }
}
