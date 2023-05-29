package com.doudou.log.config;

import com.doudou.log.utils.WebUtil;
import lombok.Getter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 服务器信息
 *
 * @author Chill
 */
@Getter
@Configuration
@MapperScan({"com.doudou.log.**.mapper"})
public class ServerInfo implements SmartInitializingSingleton {
	private final ServerProperties serverProperties;
	private String hostName;
	private String ip;
	private Integer port;
	private String ipWithPort;

	@Autowired(required = false)
	public ServerInfo(ServerProperties serverProperties) {
		this.serverProperties = serverProperties;
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.hostName = WebUtil.getHostName();
		this.ip = WebUtil.getHostIp();
		this.port = serverProperties.getPort();
		this.ipWithPort = String.format("%s:%d", ip, port);
	}
}
