package me.liangdi.robot;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局类
 * @author liangdi
 */
@Slf4j
public class EnginePi {
    public static final GpioController gpio = GpioFactory.getInstance();


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


    public static void main(String[] args) {

    }

}
