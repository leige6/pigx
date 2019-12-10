package com.pig4cloud.pigx.common.core.logs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.PatternLayoutEncoderBase;
import org.apache.commons.lang.StringUtils;

public class ExtendPatternLayoutEncoder extends PatternLayoutEncoderBase<ILoggingEvent> {
	private static String DEFAULT_PATTERN = "|%ip|%X{traceId}|%X{parentSpanIdx}|%X{spanIdx}|%d{yyyy-MM-dd HH:mm:ss.SSS}|[%thread]|%level|%logger{36}|%msg%n";
	private String appName;

	public ExtendPatternLayoutEncoder() {
	}

	public void start() {
		ExtendPatternLayout patternLayout = new ExtendPatternLayout();
		patternLayout.setContext(this.context);
		if (StringUtils.isEmpty(this.getPattern())) {
			patternLayout.setPattern(this.getAppName() + DEFAULT_PATTERN);
		} else {
			patternLayout.setPattern(this.getPattern());
		}

		patternLayout.setOutputPatternAsHeader(this.outputPatternAsHeader);
		patternLayout.start();
		this.layout = patternLayout;
		super.start();
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
}

