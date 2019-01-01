package com.enginepi;

import com.enginepi.robot.IRobot;
import com.enginepi.robot.Robot;
import com.google.gson.Gson;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * 全局类
 * @author liangdi
 */
@Slf4j
public class EnginePi {
    public static GpioController gpio = null;
    public static final String PROP_FILE = "robot.properties";
    public static final String KEY_RASPBERRYPI_SUPPORT = "pi.support";
    private static String pack = "robot";
    /**
     * 15 秒自动阶段
     */
    public static final int AUTOMATIC_TIME = 15;

    private static List<IRobot> robots = new ArrayList<>();
    private static  Set<String> properties = new HashSet<>();
    private static Pattern propertiesPattern = Pattern.compile(PROP_FILE);

    private  static Gson gson = new Gson();

    private static boolean raspberryPiSupport =  false;

   static ExecutorService threadPool = Executors.newFixedThreadPool(2);

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

    private static List<String> listRobots() {
        Reflections reflections = new Reflections(pack);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Robot.class);
        List<String> ret = new ArrayList<>();
        for(Class<?> cls : annotated) {

            String name =  cls.getName();
            log.debug("name:{}",name);

            ret.add(name);
        }

        return ret;
    }

    private static IRobot loadRobot(String clsName) {
        Reflections reflections = new Reflections(pack);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Robot.class);

        for(Class<?> cls : annotated) {

            String name =  cls.getName();
            log.debug("name:{}",name);

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
//        properties =
//                reflections.getResources(propertiesPattern);
//        for(String propertie : properties) {
//            log.info("propertie:{}",propertie);
//        }

        String runPath = System.getProperty("user.dir");

        File propFile = new File(runPath + File.separator + PROP_FILE);

        if(propFile.exists()) {
            Properties properties  = new Properties();
            try {
                properties.load(new FileInputStream(propFile));
                robot.setProperties(properties);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        log.info("runPath:{}",propFile.getAbsolutePath());
    }

    public static boolean inRaspberryPi() {
        return raspberryPiSupport;
    }

    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println(gson.toJson(listRobots()));
            return;
        }

        String robotCls = args[0];
        IRobot robot = loadRobot(robotCls);

        if(robot == null) {
            log.info("找不到对因的 IRobot 类");
            return;

        }


        String property = robot.getProperties()
                .getProperty(KEY_RASPBERRYPI_SUPPORT, "");


        if(StringUtils.isBlank(property) || "true".equalsIgnoreCase(property)) {
            try{
                gpio = GpioFactory.getInstance();
                raspberryPiSupport =  true;
            }catch (Exception e) {
                log.warn("robot is not running in raspberry pi");
            }
        }





        try {
            robot.setup();
        } catch (Exception e) {
            log.error("robot setup error:{}",e.getMessage());
            return;
        }



        if(robot.supportAutomatic()){
            // 自动阶段
            AutomaticThread automaticThread = new AutomaticThread(robot);



            automaticThread.setName("automatic thread");
            log.info("automatic function start");

            threadPool.execute(automaticThread);

            long start = System.currentTimeMillis();


            while(true) {
                long now = System.currentTimeMillis();
                int past = (int) ((now - start ) / 1000);

                if(past >= AUTOMATIC_TIME) {
                    (automaticThread).cancel();

                    //threadPool.shutdownNow();
                    log.info("automatic function finished");
                    break;
                }
            };

        }


        Thread loopThead = new Thread(() -> {
            while(true) {
                try {
                    robot.loop();
                } catch (InterruptedException e) {
                    log.error("执行错误:{}",e.getMessage(),e);
                }
            }
        });

        loopThead.setName("loop thread");

        threadPool.execute(loopThead);

        while(true) {
            // wait for command
        }


    }

    public static class AutomaticThread extends Thread{
        IRobot robot;

        public AutomaticThread(IRobot robot) {
            this.robot = robot;
        }

        volatile boolean cancel = false;
        @Override
        public void run() {
            while(!cancel) {
                robot.automatic();
            }


            Thread.currentThread().interrupt();
        }

        public void cancel(){
            log.info("automatic thread cancel");
            this.cancel = true;
        }


    }

}
