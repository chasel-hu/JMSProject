package com.chasel.rest;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
	public RestApplication() {

		// 服务类所在的包路径
		packages("com.chasel.resource");

		// 注册 JSON 转换器
		register(JacksonFeature.class);
		register(MultiPartFeature.class);
	}
}
