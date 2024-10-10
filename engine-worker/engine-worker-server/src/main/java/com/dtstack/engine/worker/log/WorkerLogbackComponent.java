package com.dtstack.engine.worker.log;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.springframework.beans.factory.InitializingBean;

/**
 *
 *
 * Date: 2017年02月23日 下午1:27:16
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class WorkerLogbackComponent implements InitializingBean {
	private static Logger LOGGER = LoggerFactory.getLogger(WorkerLogbackComponent.class);

	private static String logBackName = StringUtils.isBlank(System.getProperty("logback.file.name")) ? "logback.xml" : System.getProperty("logback.file.name");

	private static String logback = System.getProperty("user.dir") + "/conf/"+logBackName;

	public static void setupLogger() throws IOException, JoranException {
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

		File externalConfigFile = new File(logback);

		if (!externalConfigFile.exists()) {

			throw new IOException("Logback External Config File Parameter does not reference a file that exists");

		} else {

			if (!externalConfigFile.isFile()) {
				throw new IOException("Logback External Config File Parameter exists, but does not reference a file");

			} else {

				if (!externalConfigFile.canRead()) {
					throw new IOException("Logback External Config File exists and is a file, but cannot be read.");

				} else {
					JoranConfigurator configurator = new JoranConfigurator();
					configurator.setContext(lc);
					lc.reset();
					configurator.doConfigure(logback);
					StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
				}

			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			String rpcEnable = System.getProperty("remote.rpcEnable", "true");
			if (Boolean.parseBoolean(rpcEnable)) {
				setupLogger();
			}
		} catch (Throwable e) {
			LOGGER.error("", e);
		}
	}
}
