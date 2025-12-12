package dev.gestock.sge.apresentacao.principal.estoque;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping
	public ResponseEntity<List<EstoqueResponse>> listar(
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) Long clienteId,
			@RequestParam(required = false) String status) {
		
		if (busca == null && clienteId == null && status == null) {
			var resumos = estoqueServicoAplicacao.pesquisarResumos();
			var responses = resumos.stream()
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
		
		var clienteIdVO = clienteId != null ? mapeador.map(clienteId, ClienteId.class) : null;
		var resumos = estoqueServicoAplicacao.pesquisarComFiltros(busca, clienteIdVO, status);
		var responses = resumos.stream()
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
