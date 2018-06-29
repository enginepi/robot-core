package com.enginepi.machine.nc;

public class ThreeDimensionalPrinter {
    int usbPort;
    int baudRate = 250000;

    public ThreeDimensionalPrinter(int usbPort) {
        this.usbPort = usbPort;
    }

    public ThreeDimensionalPrinter(int usbPort, int baudRate) {
        this.usbPort = usbPort;
        this.baudRate = baudRate;
    }

    /**
     * 启动
     * @return
     */
    public boolean start() {

        boolean ret = false;

        return ret;
    }

    /**
     * 停止
     */
    public void stop() {

    }

    /**
     * 返回 home
     */
    public void home() {

    }

    /**
     * 执行 gcode
     * @param gcode
     */
    public void runGcode(String gcode) {

    }
}
