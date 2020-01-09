package com.pig4cloud.pigx.common.data.annotation;

import com.pig4cloud.pigx.common.core.constant.CommonConstants;

import java.lang.annotation.*;

// 作用到方法上
@Target(ElementType.METHOD)
// 运行时有效
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoRepeatSubmit {
	/**
	 * 默认时间3秒
	 */
	long time() default CommonConstants.NO_REPEATSUBMIT_TIME;
}
