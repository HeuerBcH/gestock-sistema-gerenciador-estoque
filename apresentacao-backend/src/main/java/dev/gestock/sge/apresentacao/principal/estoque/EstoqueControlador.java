package dev.gestock.sge.apresentacao.principal.estoque;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.auth.AutenticacaoServico;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueControlador {

	@Autowired
	private EstoqueServicoAplicacao estoqueServicoAplicacao;

	@Autowired
	private EstoqueServico estoqueServico;

	@Autowired
	private BackendMapeador mapeador;

	@Autowired
	private AutenticacaoServico autenticacaoServico;

	@Autowired
	private dev.gestock.sge.dominio.principal.cliente.ClienteServico clienteServico;

	private Optional<ClienteId> extrairClienteIdDoToken(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return Optional.empty();
		}
		String token = authorizationHeader.substring(7);
		return autenticacaoServico.validarToken(token);
	}

	@GetMapping
	public ResponseEntity<?> listar(
			@RequestHeader(value = "Authorization", required = false) String authorization,
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) String status) {
		
		Optional<ClienteId> clienteIdOpt = extrairClienteIdDoToken(authorization);
		if (clienteIdOpt.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse("Token inválido ou ausente"));
		}
		
		ClienteId clienteId = clienteIdOpt.get();
		var resumos = estoqueServicoAplicacao.pesquisarComFiltros(busca, clienteId, status);
		List<EstoqueResponse> responses = resumos.stream()
			.map(resumo -> new EstoqueResponse(
				mapeador.map(resumo.getId(), Long.class),
				mapeador.map(resumo.getClienteId(), Long.class),
				resumo.getNome(),
				resumo.getEndereco(),
				resumo.getCapacidade(),
				resumo.isAtivo()
			))
			.collect(Collectors.toList());
		
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EstoqueResponse> buscarPorId(@PathVariable Long id) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		var estoqueOpt = estoqueServicoAplicacao.buscarPorId(estoqueId);
		
		if (estoqueOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		var resumo = estoqueOpt.get();
		var response = new EstoqueResponse(
			mapeador.map(resumo.getId(), Long.class),
			mapeador.map(resumo.getClienteId(), Long.class),
			resumo.getNome(),
			resumo.getEndereco(),
			resumo.getCapacidade(),
			resumo.isAtivo()
		);
		
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> criar(@RequestBody EstoqueRequest request) {
		try {
			if (request.clienteId == null) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("ClienteId é obrigatório"));
			}

			var clienteId = mapeador.map(request.clienteId, ClienteId.class);
			try {
				clienteServico.buscarPorId(clienteId);
			} catch (IllegalArgumentException e) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Cliente não encontrado"));
			}

			var estoqueId = EstoqueId.temporario();
			int capacidade = (request.capacidade != null) ? request.capacidade.intValue() : 0;
			var estoque = new dev.gestock.sge.dominio.principal.estoque.Estoque(
				estoqueId,
				clienteId,
				request.nome,
				request.endereco,
				capacidade
			);

			if (request.ativo != null && !request.ativo) {
				estoque.inativar();
			}

			estoqueServico.cadastrar(estoque);

			var estoquesDoCliente = estoqueServico.pesquisarPorCliente(clienteId);
			var estoqueCriado = estoquesDoCliente.stream()
				.filter(e -> e.getNome().equals(request.nome) && e.getEndereco().equals(request.endereco))
				.findFirst()
				.orElse(null);

			if (estoqueCriado == null) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Erro ao criar estoque"));
			}
			var response = new EstoqueResponse(
				mapeador.map(estoqueCriado.getId(), Long.class),
				mapeador.map(estoqueCriado.getClienteId(), Long.class),
				estoqueCriado.getNome(),
				estoqueCriado.getEndereco(),
				estoqueCriado.getCapacidade(),
				estoqueCriado.isAtivo()
			);

			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro ao criar estoque: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody EstoqueRequest request) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		
		try {
			var estoque = estoqueServico.buscarPorId(estoqueId);
			
			Integer capacidade = request.capacidade;
			String nome = (request.nome != null && !request.nome.isBlank()) ? request.nome : null;
			String endereco = (request.endereco != null && !request.endereco.isBlank()) ? request.endereco : null;
			estoqueServico.atualizar(estoqueId, nome, endereco, capacidade);
			
			if (request.ativo != null) {
				if (request.ativo && !estoque.isAtivo()) {
					estoque.ativar();
					estoqueServico.atualizar(estoque);
				} else if (!request.ativo && estoque.isAtivo()) {
					estoqueServico.inativar(estoque);
				}
			}
			
			var estoqueAtualizado = estoqueServicoAplicacao.buscarPorId(estoqueId);
			if (estoqueAtualizado.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Erro ao atualizar estoque"));
			}
			
			var resumo = estoqueAtualizado.get();
			var response = new EstoqueResponse(
				mapeador.map(resumo.getId(), Long.class),
				mapeador.map(resumo.getClienteId(), Long.class),
				resumo.getNome(),
				resumo.getEndereco(),
				resumo.getCapacidade(),
				resumo.isAtivo()
			);
			
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("não encontrado")) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> apagar(@PathVariable Long id) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		
		try {
			estoqueServico.remover(estoqueId);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
		
		return ResponseEntity.noContent().build();
	}

	public static class EstoqueRequest {
		public Long clienteId;
		public String nome;
		public String endereco;
		public Integer capacidade;
		public Boolean ativo;
	}
	
	public static class ErrorResponse {
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}

	public static class EstoqueResponse {
		public Long id;
		public Long clienteId;
		public String nome;
		public String endereco;
		public int capacidade;
		public boolean ativo;

		public EstoqueResponse(Long id, Long clienteId, String nome, String endereco, int capacidade, boolean ativo) {
			this.id = id;
			this.clienteId = clienteId;
			this.nome = nome;
			this.endereco = endereco;
			this.capacidade = capacidade;
			this.ativo = ativo;
		}
	}
}
