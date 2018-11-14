package com.qianguatech.iot.core;

/**
 * 设备 action 监听对象
 * @author liangdi
 */
public interface DeviceActionListener {
     void onAction(Device device, DeviceAction action);
}
