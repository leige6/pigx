package com.pig4cloud.pigx.act.config;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * @author new
 * <p>
 * WebSocket配置类
 */
@Slf4j
@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	private RemoteTokenServices tokenService;

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		//这个方法的作用是添加一个服务端点，来接收客户端的连接。
		registry.addEndpoint("/ws")
				.setAllowedOrigins("*")
				.withSockJS();
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		//点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认是/user/
		registry.setUserDestinationPrefix("/task/");
		//指服务端接收地址的前缀，意思就是说客户端给服务端发消息的地址的前缀
		registry.setApplicationDestinationPrefixes("/app");
		//表示客户端订阅地址的前缀信息，也就是客户端接收服务端消息的地址的前缀信息
		registry.enableSimpleBroker("/topic");
	}


	/**
	 * 配置客户端入站通道拦截器,此拦截器取出header中的token,用于鉴权
	 */
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					String tokens = accessor.getFirstNativeHeader("Authorization");
					log.info("webSocket token is {}", tokens);
					if (StrUtil.isBlank(tokens)) {
						return null;
					}
					OAuth2Authentication auth2Authentication = tokenService.loadAuthentication(tokens.split(" ")[1]);
					SecurityContextHolder.getContext().setAuthentication(auth2Authentication);
					accessor.setUser(() -> (String) auth2Authentication.getPrincipal());
				}
				return message;
			}
		});
	}
}
