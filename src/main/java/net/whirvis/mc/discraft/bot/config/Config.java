package net.whirvis.mc.discraft.bot.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Config {

	String key();

	String fallback() default "";

}
