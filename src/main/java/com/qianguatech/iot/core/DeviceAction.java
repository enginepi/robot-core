package com.qianguatech.iot.core;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liangdi
 */
@Data
public class DeviceAction {
    public String name;
    public List<Object> args = new ArrayList<>();
    public long time;
}
