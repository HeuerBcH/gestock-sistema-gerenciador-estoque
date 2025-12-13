package dev.gestock.sge.apresentacao.principal.cliente;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

	@Autowired
	private ClienteServico clienteServico;

	@Autowired
	private BackendMapeador mapeador;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping
	public ResponseEntity<List<ClienteResponse>> listar() {
		var clientes = clienteServico.listarTodos();
		List<ClienteResponse> responses = clientes.stream()
			.map(cliente -> {
				ClienteResponse response = new ClienteResponse();
				response.id = mapeador.map(cliente.getId(), Long.class);
				response.nome = cliente.getNome();
				response.documento = cliente.getDocumento();
				response.email = cliente.getEmail();
				return response;
			})
			.collect(Collectors.toList());
		return ResponseEntity.ok(responses);
	}

	@PostMapping
	public ResponseEntity<?> criar(@RequestBody ClienteRequest request) {
		try {
			if (clienteServico.emailJaExiste(request.email)) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Email já cadastrado"));
			}

			if (clienteServico.documentoJaExiste(request.documento)) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("CPF/CNPJ já cadastrado"));
			}

			String senhaHash = passwordEncoder.encode(request.senha);
			ClienteId clienteIdTemp = ClienteId.temporario();
			Cliente cliente = new Cliente(clienteIdTemp, request.nome, request.documento, request.email, senhaHash);

			clienteServico.registrarCliente(cliente);

			Cliente clienteCriado = clienteServico.buscarPorEmail(request.email);
			ClienteResponse response = new ClienteResponse();
			response.id = mapeador.map(clienteCriado.getId(), Long.class);
			response.nome = clienteCriado.getNome();
			response.documento = clienteCriado.getDocumento();
			response.email = clienteCriado.getEmail();

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			String mensagem = e.getMessage();
			if (mensagem != null && mensagem.contains("uk_cliente_documento")) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("CPF/CNPJ já cadastrado"));
			}
			if (mensagem != null && mensagem.contains("uk_cliente_email")) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Email já cadastrado"));
			}
			return ResponseEntity.badRequest()
				.body(new ErrorResponse("Dados inválidos. Verifique se o CPF/CNPJ ou email já estão cadastrados."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro ao criar cliente. Tente novamente."));
		}
	}

	public static class ClienteRequest {
		public String nome;
		public String documento;
		public String email;
		public String senha;
	}

	public static class ClienteResponse {
		public Long id;
		public String nome;
		public String documento;
		public String email;
	}

	public static class ErrorResponse {
		public String message;

		public ErrorResponse(String message) {
			this.message = message;
		}
	}
}

