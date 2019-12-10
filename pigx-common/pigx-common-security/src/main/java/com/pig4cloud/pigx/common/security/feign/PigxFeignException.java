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

import com.pig4cloud.pigx.common.core.constant.CommonConstants;
import com.pig4cloud.pigx.common.core.util.R;
import lombok.Getter;

/**
 * Fegin 异常
 *
 * @author L.cm
 */
public class PigxFeignException extends RuntimeException {
	@Getter
	private final R result;

	public PigxFeignException(R result) {
		super(result.getMsg());
		this.result = result;
	}

	public PigxFeignException(String message) {
		super(message);
		this.result = R.builder()
				.code(CommonConstants.FAIL)
				.msg(message).build();
	}

	/**
	 * 提高性能
	 *
	 * @return {Throwable}
	 */
	@Override
	public Throwable fillInStackTrace() {
		return this;
	}
}
