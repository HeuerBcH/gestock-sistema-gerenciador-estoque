package dev.gestock.sge.apresentacao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
	
	@Value("${jwt.secret:gestock-secret-key-very-long-and-secure-key-for-jwt-token-generation}")
	private String secret;
	
	@Value("${jwt.expiration:86400000}")
	private long expiration; // 24 horas em milissegundos

	public String getSecret() {
		return secret;
	}

	public long getExpiration() {
		return expiration;
	}
}

