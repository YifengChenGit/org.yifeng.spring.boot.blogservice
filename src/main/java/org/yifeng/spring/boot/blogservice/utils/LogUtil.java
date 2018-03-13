package org.yifeng.spring.boot.blogservice.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtil {

	private static final Logger logger = LogManager.getLogger();

	public static Logger getLogger() {
		return logger;
	}
}
