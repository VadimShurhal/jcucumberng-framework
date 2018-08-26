package jcucumberng.api.logger;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import jcucumberng.api.utils.PropsUtil;

/**
 * {@code LoggerHelper} handles the logging mechanism of the framework.
 * 
 * @author Kat Rollo &lt;rollo.katherine@gmail.com&gt;
 */
public final class LoggerHelper {

	/**
	 * Loads {@code log4j2.conf.file} from {@code framework.properties}.
	 */
	public static void initLogger() {
		String cfgFile = null;
		try {
			cfgFile = PropsUtil.frameworkConf("log4j2.conf.file");
		} catch (IOException ioe) {
			throw new MissingLoggerException("Cannot find logger config file: " + cfgFile);
		}

		StringBuilder builder = new StringBuilder();
		builder.append(StringUtils.replace(System.getProperty("user.dir"), "\\", "/"));
		builder.append("/src/test/resources/jcucumberng/framework/");
		builder.append(cfgFile);

		File log4j2File = new File(builder.toString());
		System.setProperty("log4j2.configurationFile", log4j2File.toURI().toString());
	}

}