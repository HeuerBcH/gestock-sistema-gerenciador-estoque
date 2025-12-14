package dev.gestock.sge.apresentacao.config;

import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {
	
	@Autowired
	private JwtConfig jwtConfig;

	public String gerarToken(int clienteId, String email) {
		Date agora = new Date();
		Date expiracao = new Date(agora.getTime() + jwtConfig.getExpiration());
		
		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
		
		return Jwts.builder()
				.subject(String.valueOf(clienteId))
				.claim("email", email)
				.issuedAt(agora)
				.expiration(expiracao)
				.signWith(key)
				.compact();
	}

	public boolean validarToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
			Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public int obterClienteIdDoToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
		Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return Integer.parseInt(claims.getSubject());
	}

	public String obterEmailDoToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
		Claims claims = Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		
		return claims.get("email", String.class);
	}
}

