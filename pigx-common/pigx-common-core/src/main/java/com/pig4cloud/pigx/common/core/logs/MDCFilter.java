package com.pig4cloud.pigx.common.core.logs;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

public class MDCFilter implements Filter {
	public MDCFilter() {
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		String traceId = httpRequest.getHeader("x-header-log-trace-id");
		String parentSpanId = httpRequest.getHeader("x-header-log-parent-span-id");
		if (StringUtils.isEmpty(traceId)) {
			traceId = this.nextTraceId();
		}

		MDC.put("traceId", traceId);
		MDC.put("spanId", this.nextTraceId());
		if (StringUtils.isEmpty(parentSpanId)) {
			parentSpanId = "-1";
		}

		try {
			filterChain.doFilter(request, response);
		} finally {
			MDC.clear();
		}

	}

	public void destroy() {
	}

	public String nextTraceId(){
		return  UUID.randomUUID().toString().replace("-", "");
	}
}
