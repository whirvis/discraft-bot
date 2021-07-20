package net.whirvis.mc.discraft.bot.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Config {

	String key();

	/**
	 * Due to the limitations of annotations in Java, this fallback value is
	 * forced to be a {@code String}. Keep in mind that even though this is a
	 * string, the fallback value for a config may be of a different type!
	 * 
	 * @return the fallback value in string representation.
	 */
	String fallback() default "";

}
