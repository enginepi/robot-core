package com.enginepi.robot;

import lombok.extern.slf4j.Slf4j;
import net.java.games.input.*;
import org.junit.Test;

@Slf4j
public class JInputTest {


    @Test
    public void testController (){
        log.info("test");
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Controller joystick = null;
        for(int i =0;i<ca.length;i++) {

            Controller ctrl = ca[i];
            /* Get the name of the controller */
            System.out.println(ca[i].getName());

            log.info("name:{}",ca[i].getName());
            log.info("port:{}",ca[i].getPortNumber());
            log.info("type:{}",ca[i].getType().toString());

            if(ctrl.getType().toString().equals("Stick")) {
                joystick = ctrl;
            }
            Component[] components = ctrl.getComponents();

            for (Component comp :
                    components) {
                log.info("comp name:{}",comp.getName());
                log.info("comp getIdentifier:{}",comp.getIdentifier().getName());
                log.info("comp isAnalog:{}",comp.isAnalog());
                //log.info("comp type:{}",comp.ge);
            }
        }

        if(joystick != null) {
            while (true) {
                joystick.poll();
                EventQueue queue =joystick.getEventQueue();
                Event event = new Event();
                while(queue.getNextEvent(event)) {
                    log.info("event:{}",event.toString());
                    StringBuffer buffer = new StringBuffer(joystick.getName());
                    buffer.append(" at ");
                    buffer.append(event.getNanos()).append(", ");
                    Component comp = event.getComponent();
                    buffer.append(comp.getName()).append(" changed to ");
                    float value = event.getValue();
                    if(comp.isAnalog()) {
                        buffer.append(value);
                    } else {
                        if(value==1.0f) {
                            buffer.append("On");
                        } else {


                            buffer.append("Off");
                        }
                    }
                   log.info("{}",buffer.toString());
                }


            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            }
        }
    }
}
