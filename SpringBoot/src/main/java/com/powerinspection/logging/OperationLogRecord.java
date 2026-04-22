package com.powerinspection.logging;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLogRecord {
    String module();
    String action();
}
