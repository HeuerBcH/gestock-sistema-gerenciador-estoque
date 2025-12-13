package dev.gestock.sge.apresentacao.principal.produto;

import java.util.List;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoServico;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoControlador {

	@Autowired
	private ProdutoServicoAplicacao produtoServicoAplicacao;

	@Autowired
	private ProdutoServico produtoServico;

	@Autowired
	private BackendMapeador mapeador;

	@GetMapping
	public ResponseEntity<?> listar(
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) String status) {
		
		var resumos = produtoServicoAplicacao.pesquisarResumos();
		
		List<ProdutoResponse> responses = resumos.stream()
			.filter(resumo -> {
				if (busca != null && !busca.isEmpty()) {
					String buscaLower = busca.toLowerCase();
					boolean matchNome = resumo.getNome() != null && resumo.getNome().toLowerCase().contains(buscaLower);
					boolean matchCodigo = resumo.getCodigo() != null && resumo.getCodigo().toLowerCase().contains(buscaLower);
					if (!matchNome && !matchCodigo) {
						return false;
					}
				}
				if (status != null && !status.isEmpty() && !status.equalsIgnoreCase("todos")) {
					boolean ativo = status.equalsIgnoreCase("ativo");
					if (ativo != resumo.isAtivo()) {
						return false;
					}
				}
				return true;
			})
			.map(resumo -> new ProdutoResponse(
				resumo.getId(),
				resumo.getCodigo(),
				resumo.getNome(),
				resumo.getUnidadePeso(),
				resumo.getPeso(),
				resumo.isPerecivel(),
				resumo.isAtivo()
			))
			.collect(Collectors.toList());
		
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
		var produtoId = mapeador.map(id, ProdutoId.class);
		var produtoOpt = produtoServicoAplicacao.buscarPorId(produtoId);
		
		if (produtoOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		var resumo = produtoOpt.get();
		var response = new ProdutoResponse(
			resumo.getId(),
			resumo.getCodigo(),
			resumo.getNome(),
			resumo.getUnidadePeso(),
			resumo.getPeso(),
			resumo.isPerecivel(),
			resumo.isAtivo()
		);
		
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> criar(@RequestBody ProdutoRequest request) {
		try {
			var produtoId = ProdutoId.temporario();
			double peso = (request.peso != null) ? request.peso.doubleValue() : 0.0;
			var produto = new Produto(
				produtoId,
				request.codigo,
				request.nome,
				request.unidadePeso,
				request.perecivel != null ? request.perecivel : false,
				peso
			);

			if (request.ativo != null && !request.ativo) {
				produto.inativar();
			}

			produtoServico.cadastrar(produto);
			
			var response = new ProdutoResponse();
			response.id = null;
			response.codigo = request.codigo;
			response.nome = request.nome;
			response.unidadePeso = request.unidadePeso;
			response.peso = peso;
			response.perecivel = request.perecivel != null ? request.perecivel : false;
			response.ativo = request.ativo != null ? request.ativo : true;
			
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro ao criar produto: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody ProdutoRequest request) {
		var produtoId = mapeador.map(id, ProdutoId.class);
		
		try {
			var produto = produtoServico.buscarPorId(produtoId);
			
			String nome = (request.nome != null && !request.nome.isBlank()) ? request.nome : produto.getNome();
			String unidadePeso = (request.unidadePeso != null && !request.unidadePeso.isBlank()) ? request.unidadePeso : produto.getUnidadePeso();
			
			if (request.peso != null && request.peso > 0) {
				produto.atualizar(nome, unidadePeso, request.peso.doubleValue());
			} else {
				produto.atualizar(nome, unidadePeso);
			}
			
			if (request.ativo != null) {
				if (request.ativo && !produto.isAtivo()) {
					produto.ativar();
				} else if (!request.ativo && produto.isAtivo()) {
					produtoServico.inativar(produto);
				}
			}
			
			produtoServico.atualizar(produto);
			
			var produtoAtualizado = produtoServicoAplicacao.buscarPorId(produtoId);
			if (produtoAtualizado.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Erro ao atualizar produto"));
			}
			
			var resumo = produtoAtualizado.get();
			var response = new ProdutoResponse(
				resumo.getId(),
				resumo.getCodigo(),
				resumo.getNome(),
				resumo.getUnidadePeso(),
				resumo.getPeso(),
				resumo.isPerecivel(),
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
		try {
			var produtoId = mapeador.map(id, ProdutoId.class);
			produtoServico.remover(produtoId);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.notFound().build();
		} catch (IllegalStateException e) {
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			String mensagem = e.getMessage();
			if (mensagem != null && mensagem.contains("cotacao")) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Não é possível deletar o produto. Existem cotações associadas a este produto. Remova as cotações primeiro ou inative o produto."));
			}
			return ResponseEntity.badRequest()
				.body(new ErrorResponse("Não é possível deletar o produto. Existem registros associados a este produto."));
		} catch (Exception e) {
			return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro ao deletar produto: " + e.getMessage()));
		}
	}

	public static class ProdutoRequest {
		public String codigo;
		public String nome;
		public String unidadePeso;
		public Double peso;
		public Boolean perecivel;
		public Boolean ativo;
	}
	
	public static class ErrorResponse {
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}

	public static class ProdutoResponse {
		public Long id;
		public String codigo;
		public String nome;
		public String unidadePeso;
		public double peso;
		public boolean perecivel;
		public boolean ativo;

		public ProdutoResponse() {
		}

		public ProdutoResponse(Long id, String codigo, String nome, String unidadePeso, double peso, boolean perecivel, boolean ativo) {
			this.id = id;
			this.codigo = codigo;
			this.nome = nome;
			this.unidadePeso = unidadePeso;
			this.peso = peso;
			this.perecivel = perecivel;
			this.ativo = ativo;
		}
	}
}

