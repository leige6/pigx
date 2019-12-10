/*
 * *************************************************************************
 *   Copyright (c) 2018-2025, dreamlu.net All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the dreamlu.net developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: chunmeng.lu (qq596392912@gmail.com)
 * *************************************************************************
 */

package com.pig4cloud.pigx.common.security.feign;

import cn.hutool.core.io.IoUtil;
import com.pig4cloud.pigx.common.core.constant.CommonConstants;
import com.pig4cloud.pigx.common.core.util.R;
import com.pig4cloud.pigx.common.security.util.ConcurrentDateFormat;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import static feign.Util.checkNotNull;
import static java.lang.String.format;
import static java.util.Locale.US;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 异常处理，将返回的数据反序列化成 R
 *
 * @author L.cm
 */
public class PigxFeignErrorDecoder extends ErrorDecoder.Default {
	private final RetryAfterDecoder retryAfterDecoder = new RetryAfterDecoder();
	private static final String REGEX = "^[0-9]+$";


	@Override
	public Exception decode(String methodKey, Response response) {
		PigxFeignException exception = errorStatus(methodKey, response);
		Date retryAfter = retryAfterDecoder.apply(firstOrNull(response.headers()));
		return new RetryableException(exception.getMessage(), response.request().httpMethod(), exception, retryAfter);
	}

	private static PigxFeignException errorStatus(String methodKey, Response response) {
		try {
			if (response.body() != null) {
				Reader reader = response.body().asReader();
				return new PigxFeignException(R.builder()
						.msg(IoUtil.read(reader))
						.code(CommonConstants.FAIL).build());
			}
		} catch (IOException ignored) { // NOPMD
		}
		String message = format("status %s reading %s", response.status(), methodKey);
		return new PigxFeignException(message);
	}

	@Nullable
	private <T> T firstOrNull(Map<String, Collection<T>> map) {
		String key = feign.Util.RETRY_AFTER;
		if (map.containsKey(key) && !map.get(key).isEmpty()) {
			return map.get(key).iterator().next();
		}
		return null;
	}

	/**
	 * Decodes a {@link feign.Util#RETRY_AFTER} header into an absolute date, if possible. <br> See <a
	 * href="https://tools.ietf.org/html/rfc2616#section-14.37">Retry-After format</a>
	 */
	static class RetryAfterDecoder {

		static final ConcurrentDateFormat RFC822_FORMAT = ConcurrentDateFormat.of
				("EEE, dd MMM yyyy HH:mm:ss 'GMT'", US, TimeZone.getTimeZone(ZoneId.of("GMT")));
		private final ConcurrentDateFormat rfc822Format;

		RetryAfterDecoder() {
			this(RFC822_FORMAT);
		}

		RetryAfterDecoder(ConcurrentDateFormat rfc822Format) {
			this.rfc822Format = checkNotNull(rfc822Format, "rfc822Format");
		}

		private long currentTimeMillis() {
			return System.currentTimeMillis();
		}

		/**
		 * returns a date that corresponds to the first time a request can be retried.
		 *
		 * @param retryAfter String in <a href="https://tools.ietf.org/html/rfc2616#section-14.37">Retry-After format</a>
		 */
		@Nullable
		Date apply(@Nullable String retryAfter) {
			if (retryAfter == null) {
				return null;
			}
			if (retryAfter.matches(REGEX)) {
				long deltaMillis = SECONDS.toMillis(Long.parseLong(retryAfter));
				return new Date(currentTimeMillis() + deltaMillis);
			}
			try {
				return rfc822Format.parse(retryAfter);
			} catch (ParseException ignored) {
				return null;
			}
		}
	}
}
