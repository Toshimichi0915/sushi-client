package net.sushiclient.client.command.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAlias {
    String value();

    String[] aliases() default {};

    String description() default "";

    String syntax() default "";
}
