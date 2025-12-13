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

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody EstoqueRequest request) {
		var estoqueId = mapeador.map(id, EstoqueId.class);
		
		try {
			estoqueServico.atualizar(estoqueId, request.nome, request.endereco, request.capacidade);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("não encontrado")) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
		
		return ResponseEntity.noContent().build();
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
