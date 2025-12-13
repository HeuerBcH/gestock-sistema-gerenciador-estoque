package dev.gestock.sge.apresentacao.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.auth.AutenticacaoServico;

@RestController
@RequestMapping("/api/auth")
public class AutenticacaoControlador {

	@Autowired
	private AutenticacaoServico autenticacaoServico;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest request) {
		try {
			var resultado = autenticacaoServico.autenticar(request.email, request.senha);
			
			LoginResponse response = new LoginResponse();
			response.token = resultado.token;
			response.nome = resultado.nome;
			response.email = resultado.email;
			response.message = "Login realizado com sucesso";
			
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.message = e.getMessage();
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	public static class LoginRequest {
		public String email;
		public String senha;
	}

	public static class LoginResponse {
		public String token;
		public String nome;
		public String email;
		public String message;
	}

	public static class ErrorResponse {
		public String message;
	}
}

