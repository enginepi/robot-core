package com.enginepi.robot.input;

import lombok.extern.slf4j.Slf4j;
import net.java.games.input.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangdi
 */
@Slf4j
public class JoyStick {

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int AXIS_Z = 2;

    public static final String TYPE_JOYSTICK = "Stick";
    public static final String POV_NAME = "pov";

    private String name;
    private int port;
    private int axisCount;
    private int buttonCount;
    private boolean enable = false;
    private boolean hasPOV = false;

    private List<Component> axises = new ArrayList<>();
    private List<Component> buttons = new ArrayList<>();

    private Component pov = null;

    Controller ctrl = null;

    public JoyStick(int port) {

        this.port = port;

        this.init();
    }


    private synchronized void init() {
        log.info("joystick init");

        ControllerEnvironment env = new LinuxEnvironmentPlugin();
        if(!env.isSupported()) {
            env = ControllerEnvironment.getDefaultEnvironment();
        }

        Controller[] ca = env.getControllers();
        // ctrl = null;
        enable = false;
        for (Controller c :
                ca) {

            log.info("find controller:{},{}",c.getName(),c.getType().toString());

            if(c.getType().toString().equals(TYPE_JOYSTICK) && c.getPortNumber() == port) {
                log.info("find joystick:{}",c.getName());
                ctrl = c;
                enable = true;
                name = ctrl.getName();

                Component[] components = ctrl.getComponents();
                axises.clear();
                buttons.clear();
                for (Component comp :
                        components) {
                    if (comp.isAnalog()) {
                        // axis
                        axises.add(comp);
                    } else {
                        // button
                        buttons.add(comp);
                    }

                    if(comp.getIdentifier().getName().equals(POV_NAME)) {
                        hasPOV = true;
                        pov = comp;
                    }
                }
                axisCount = axises.size();
                buttonCount = buttons.size();

            }
        }
    }

    /**
     * 获得指定轴的值
     * @param index
     * @return
     */
    public double getAxisValue(int index) {
        if(!ctrl.poll()){
            this.init();
        }
        return axises.get(index).getPollData();
    }

    /**
     * 按钮是否按下
     * @param index
     * @return
     */
    public boolean isButtonPressed(int index) {
        if(!ctrl.poll()){
            this.init();
        }
        float pollData = buttons.get(index).getPollData();
        // log.info("poolData:{}",pollData);
        return pollData == 1.0F;
    }

    public float POV() {
        if(hasPOV) {
            if(!ctrl.poll()){
                this.init();
            }
           return pov.getPollData();
        } else {
            return 0.0F;
        }
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public int getAxisCount() {
        return axisCount;
    }

    public int getButtonCount() {
        return buttonCount;
    }

    public boolean isEnable() {
        return enable;
    }
}
