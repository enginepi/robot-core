package com.enginepi;

import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 全局类
 * @author liangdi
 */
@Slf4j
public class EnginePi {
    public static final GpioController gpio = null;// = GpioFactory.getInstance();

    private static String pack = "robot";

    private static List<IRobot> robots = new ArrayList<>();
    private static  Set<String> properties = new HashSet<>();
    private static Pattern propertiesPattern = Pattern.compile("robot.properties");

    /**
     * 帮助函数 map
     * @param x
     * @param in_min
     * @param in_max
     * @param out_min
     * @param out_max
     * @return
     */
    public static long map(long x, long in_min, long in_max, long out_min, long out_max) {
        return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
    }

    public static void delayNanos(long nanos)
    {
        long elapsed;
        final long startTime = System.nanoTime();
        do {
            elapsed = System.nanoTime() - startTime;
        } while (elapsed < nanos);
    }

    private static IRobot loadRobot(String clsName) {
        Reflections reflections = new Reflections(pack);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Robot.class);
        log.info("pack size:{}",annotated.size());

        for(Class<?> cls : annotated) {

            String name =  cls.getName();
            log.info("name:{}",name);

            if(name.equals(clsName) ) {
                try {
                    IRobot iRobot = (IRobot) cls.newInstance();

                    initProperties(iRobot,reflections);

                    return iRobot;

                } catch (InstantiationException | IllegalAccessException e) {
                    log.error("加载 IRobot 对象错误:{}",e.getMessage());
                }
            }
        }

        return null;
    }

    private static void initProperties(IRobot robot,Reflections reflections) {
        properties =
                reflections.getResources(propertiesPattern);
        for(String propertie : properties) {
            log.info("propertie:{}",propertie);
        }

        String runPath = System.getProperty("user.dir");

        log.info("runPath:{}",runPath);
    }

    public static void main(String[] args) {

        if(args.length == 0) {
            log.info("请输入 IRobot 类名");
            return;
        }

        String robotCls = args[0];
        IRobot robot = loadRobot(robotCls);

        if(robot != null) {
            robot.setup();
        } else {
            log.info("找不到对因的 IRobot 类");
            return;
        }

        while(true) {
            try {
                robot.loop();
            } catch (InterruptedException e) {
                log.error("执行错误:{}",e.getMessage(),e);
            }
        }

    }

}
