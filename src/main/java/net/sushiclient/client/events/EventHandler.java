package net.sushiclient.client.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventHandler {

    int priority() default 0;

    boolean ignoreCancelled() default true;

    EventTiming[] timing();
}
