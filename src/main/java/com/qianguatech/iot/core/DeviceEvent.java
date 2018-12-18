package com.qianguatech.iot.core;

import lombok.Data;

/**
 * 设备事件
 * @author liangdi
 */
@Data
public class DeviceEvent {
    /**
     * 事件名称
     */
    public String name;
    /**
     * 事件内容
     */
    public Object content;
    /**
     * 发生事件时间
     */
    public long time;
}
