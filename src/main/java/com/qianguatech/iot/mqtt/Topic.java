package com.qianguatech.iot.mqtt;

/**
 * IOT  MQTT topic
 * 格式为  /iot/{type}/{device_uuid}
 * @author liangdi
 */
public class Topic {
    /**
     * 状态 topic
     */
    public static final String TOPIC_STATUS = "iot/status/";
    /**
     * 执行操作 topic
     */
    public static final String TOPIC_ACTION = "iot/action/";
    /**
     * 事件 topic
     */
    public static final String TOPIC_EVENT = "iot/event/";
    /**
     * 属性对象 topic
     */
    public static final String TOPIC_PROP = "iot/prop/";
    /**
     * 时间序列 topic
     */
    public static final String TOPIC_DATA = "iot/data/";

    /**
     * 返回 action topic
     * @param uuid
     * @return
     */
    public static String action(String uuid) {
        return TOPIC_ACTION + uuid;
    }

    /**
     * 判断是否为 action topic
     * @param topic
     * @return
     */
    public static boolean isAction(String topic) {
        return topic.startsWith(TOPIC_ACTION);
    }

    /**
     * 创建 event topic
     * @param uuid
     * @return
     */
    public static  String event(String uuid) {
        return TOPIC_EVENT + uuid;
    }
}
