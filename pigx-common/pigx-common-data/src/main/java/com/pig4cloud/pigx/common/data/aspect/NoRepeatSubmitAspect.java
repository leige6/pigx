package com.pig4cloud.pigx.common.data.aspect;

import com.pig4cloud.pigx.common.core.constant.CommonConstants;
import com.pig4cloud.pigx.common.core.constant.SecurityConstants;
import com.pig4cloud.pigx.common.core.util.IpUtils;
import com.pig4cloud.pigx.common.core.util.MD5Util;
import com.pig4cloud.pigx.common.core.util.R;
import com.pig4cloud.pigx.common.data.annotation.NoRepeatSubmit;
import com.pig4cloud.pigx.common.data.utils.RedisUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Slf4j
@Aspect
@AllArgsConstructor
public class NoRepeatSubmitAspect {

	private final RedisUtils redisUtils;

	/**
	 * <p> 【环绕通知】 用于拦截指定方法，判断用户表单保存操作是否属于重复提交 <p>
	 *
	 * 定义切入点表达式： execution(public * (…))
	 * 表达式解释： execution：主体 public:可省略 *：标识方法的任意返回值 任意包+类+方法(…) 任意参数
	 *
	 * com.zhengqing.demo.modules.*.api ： 标识AOP所切服务的包名，即需要进行横切的业务类
	 * .*Controller ： 标识类名，*即所有类
	 * .*(..) ： 标识任何方法名，括号表示参数，两个点表示任何参数类型
	 *
	 * @param pjp：切入点对象
	 * @param noRepeatSubmit:自定义的注解对象
	 * @return: java.lang.Object
	 */
	@Around("@annotation(noRepeatSubmit)")
	public Object doAround(ProceedingJoinPoint pjp, NoRepeatSubmit noRepeatSubmit) {
		try {
			HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            StringBuffer str=new StringBuffer();
			// 拿到ip地址、请求路径、token
			String ip = IpUtils.getIpAdrress(request);
			str.append(ip);
			String url=request.getRequestURL().toString();
			str.append(url);

			String authorization = request.getHeader(SecurityConstants.AUTHORIZATION);
			String token="";
			if(authorization!=null&&authorization.length()>7){
				token=authorization.substring(7);
			}
			str.append(token);
			// 现在时间
			long now = System.currentTimeMillis();
			String key = CommonConstants.FRONT_KEY+MD5Util.encrypt(str.toString());
			log.info("表单重复key为{}",key);
			if (redisUtils.hasKey(key)) {
				/*// 上次表单提交时间
				long lastTime = Long.valueOf()redisUtils.get(key);
				// 如果现在距离上次提交时间小于设置的默认时间 则 判断为重复提交 否则 正常提交 -> 进入业务处理
				if ((now - lastTime) > noRepeatSubmit.time()) {
					// 非重复提交操作 - 重新记录操作时间
					redisUtils.delete(key);
					// 进入处理业务
					R result = (R) pjp.proceed();
					return result;
				} else {*/
					return R.builder().code(CommonConstants.FAIL)
							.msg("请勿重复提交!").build();
				/*}*/
			} else {
				// 这里是第一次操作
				redisUtils.set(key, String.valueOf(now),CommonConstants.NO_REPEATSUBMIT_TIME);
				R result = (R) pjp.proceed();
				return result;
			}
		} catch (Throwable e) {
			log.error("校验表单重复提交时异常: {}", e.getMessage());
			return R.builder().code(CommonConstants.FAIL)
					.msg("校验表单重复提交时异常!").build();
		}

	}

}
