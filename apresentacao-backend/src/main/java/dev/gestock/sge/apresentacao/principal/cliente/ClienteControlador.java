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
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

	@Autowired
	private ClienteServico clienteServico;

	@Autowired
	private ClienteRepositorio clienteRepositorio;

	@Autowired
	private BackendMapeador mapeador;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping
	public ResponseEntity<List<ClienteResponse>> listar() {
		var clientes = clienteRepositorio.listarTodos();
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
			// Verifica se o email já existe
			if (clienteRepositorio.buscarPorEmail(request.email).isPresent()) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Email já cadastrado"));
			}

			// Verifica se o documento já existe
			if (clienteRepositorio.buscarPorDocumento(request.documento).isPresent()) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("CPF/CNPJ já cadastrado"));
			}

			// Faz hash da senha
			String senhaHash = passwordEncoder.encode(request.senha);

			// Cria um Cliente temporário (ID será gerado pelo JPA ao salvar)
			ClienteId clienteIdTemp = ClienteId.temporario();
			Cliente cliente = new Cliente(clienteIdTemp, request.nome, request.documento, request.email, senhaHash);

			// Salva o cliente (o JPA vai gerar um novo ID)
			clienteServico.registrarCliente(cliente);

			// Busca o cliente salvo para retornar com o ID correto
			var clienteSalvo = clienteRepositorio.buscarPorEmail(request.email);
			
			if (clienteSalvo.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Erro ao criar cliente"));
			}

			Cliente clienteCriado = clienteSalvo.get();
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

