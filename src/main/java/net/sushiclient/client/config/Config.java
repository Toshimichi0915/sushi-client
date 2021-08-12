package net.sushiclient.client.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {
    String id();

    String name();

    String desc() default "";

    String when() default "";

    boolean temp() default false;

    int prio() default 0;
}
