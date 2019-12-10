package com.pig4cloud.pigx.common.core.logs;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ThreadConverter extends ClassicConverter {
	public ThreadConverter() {
	}

	public String convert(ILoggingEvent event) {
		return event.getThreadName().replace("|", "/");
	}
}

