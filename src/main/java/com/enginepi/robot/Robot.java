package com.enginepi.robot;

import java.lang.annotation.*;

/**
 * 标注为机器人对象
 */
@Target({ElementType.TYPE})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Robot {
}
