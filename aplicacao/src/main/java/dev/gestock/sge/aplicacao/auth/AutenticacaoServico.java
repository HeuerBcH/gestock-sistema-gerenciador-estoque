package dev.gestock.sge.aplicacao.auth;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import javax.crypto.SecretKey;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class AutenticacaoServico {

	private final ClienteRepositorio clienteRepositorio;
	private final PasswordEncoder passwordEncoder;
	private final SecretKey secretKey;

	public AutenticacaoServico(ClienteRepositorio clienteRepositorio) {
		notNull(clienteRepositorio, "O repositório não pode ser nulo");
		this.clienteRepositorio = clienteRepositorio;
		this.passwordEncoder = new BCryptPasswordEncoder();
		// Em produção, isso deve vir de uma variável de ambiente ou configuração
		this.secretKey = Keys.hmacShaKeyFor(
			"minha-chave-secreta-muito-longa-e-segura-para-jwt-token-geracao".getBytes()
		);
	}

	/**
	 * Resultado da autenticação contendo token e dados do usuário.
	 */
	public static class ResultadoAutenticacao {
		public final String token;
		public final String nome;
		public final String email;

		public ResultadoAutenticacao(String token, String nome, String email) {
			this.token = token;
			this.nome = nome;
			this.email = email;
		}
	}

	/**
	 * Autentica um cliente usando email e senha.
	 * 
	 * @param email Email do cliente
	 * @param senha Senha em texto plano
	 * @return Resultado da autenticação com token e dados do usuário
	 * @throws IllegalArgumentException se credenciais forem inválidas
	 */
	public ResultadoAutenticacao autenticar(String email, String senha) {
		notNull(email, "Email é obrigatório");
		notNull(senha, "Senha é obrigatória");

		Optional<Cliente> clienteOpt = clienteRepositorio.buscarPorEmail(email);
		
		if (clienteOpt.isEmpty()) {
			throw new IllegalArgumentException("Email ou senha inválidos");
		}

		Cliente cliente = clienteOpt.get();
		
		if (cliente.getSenhaHash() == null) {
			throw new IllegalArgumentException("Email ou senha inválidos");
		}

		if (!passwordEncoder.matches(senha, cliente.getSenhaHash())) {
			throw new IllegalArgumentException("Email ou senha inválidos");
		}

		String token = gerarToken(cliente.getId());
		return new ResultadoAutenticacao(token, cliente.getNome(), cliente.getEmail());
	}

	/**
	 * Gera um token JWT para o cliente.
	 */
	private String gerarToken(ClienteId clienteId) {
		Instant agora = Instant.now();
		Instant expiracao = agora.plus(24, ChronoUnit.HOURS); // Token válido por 24 horas

		return Jwts.builder()
			.subject(clienteId.getId().toString())
			.issuedAt(Date.from(agora))
			.expiration(Date.from(expiracao))
			.signWith(secretKey)
			.compact();
	}

	/**
	 * Valida um token JWT e retorna o ID do cliente.
	 */
	public Optional<ClienteId> validarToken(String token) {
		try {
			String subject = Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();

			Long clienteIdLong = Long.parseLong(subject);
			return Optional.of(new ClienteId(clienteIdLong));
		} catch (Exception e) {
			return Optional.empty();
		}
	}
}

