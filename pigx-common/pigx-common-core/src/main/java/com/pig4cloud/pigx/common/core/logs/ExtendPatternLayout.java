package com.pig4cloud.pigx.common.core.logs;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ExtendPatternLayout extends PatternLayout {
	public ExtendPatternLayout() {
	}

	public String doLayout(ILoggingEvent event) {
		super.start();
		return super.doLayout(event);
	}

	static {
		defaultConverterMap.put("ip", IPAddressConverter.class.getName());
		defaultConverterMap.put("thread", ThreadConverter.class.getName());
		defaultConverterMap.put("t", ThreadConverter.class.getName());
	}
}
