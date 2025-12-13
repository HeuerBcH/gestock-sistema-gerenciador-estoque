package dev.gestock.sge.apresentacao.principal.fornecedor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;

@RestController
@RequestMapping("/api/fornecedores")
public class FornecedorControlador {

	@Autowired
	private FornecedorServicoAplicacao fornecedorServicoAplicacao;

	@Autowired
	private FornecedorServico fornecedorServico;

	@Autowired
	private BackendMapeador mapeador;

	@GetMapping
	public ResponseEntity<?> listar(
			@RequestParam(required = false) String busca,
			@RequestParam(required = false) Boolean ativo) {
		
		System.out.println("📋 [FornecedorControlador] Listando fornecedores - busca: " + busca + ", ativo: " + ativo);
		
		var resumos = fornecedorServicoAplicacao.pesquisarComFiltros(busca, ativo);
		System.out.println("✅ [FornecedorControlador] Encontrados " + resumos.size() + " fornecedores");
		
		List<FornecedorResponse> responses = resumos.stream()
			.map(resumo -> {
				var response = new FornecedorResponse();
				response.id = mapeador.map(resumo.getId(), Long.class);
				response.nome = resumo.getNome();
				response.cnpj = resumo.getCnpj();
				response.contato = resumo.getContato();
				response.leadTimeMedio = new LeadTimeResponse(resumo.getLeadTimeMedio().getDias());
				response.ativo = resumo.isAtivo();
				return response;
			})
			.collect(Collectors.toList());
		
		return ResponseEntity.ok(responses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<FornecedorResponse> buscarPorId(@PathVariable Long id) {
		System.out.println("🔍 [FornecedorControlador] Buscando fornecedor por ID: " + id);
		
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		var fornecedorOpt = fornecedorServicoAplicacao.buscarPorId(fornecedorId);
		
		if (fornecedorOpt.isEmpty()) {
			System.out.println("❌ [FornecedorControlador] Fornecedor não encontrado: " + id);
			return ResponseEntity.notFound().build();
		}
		
		var resumo = fornecedorOpt.get();
		var response = new FornecedorResponse();
		response.id = mapeador.map(resumo.getId(), Long.class);
		response.nome = resumo.getNome();
		response.cnpj = resumo.getCnpj();
		response.contato = resumo.getContato();
		response.leadTimeMedio = new LeadTimeResponse(resumo.getLeadTimeMedio().getDias());
		response.ativo = resumo.isAtivo();
		
		System.out.println("✅ [FornecedorControlador] Fornecedor encontrado: " + resumo.getNome());
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> criar(@RequestBody FornecedorRequest request) {
		System.out.println("➕ [FornecedorControlador] Criando novo fornecedor");
		System.out.println("📝 [FornecedorControlador] Dados recebidos: nome=" + request.nome + ", cnpj=" + request.cnpj);
		
		try {
			if (request.nome == null || request.nome.trim().isEmpty()) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("Nome é obrigatório"));
			}
			
			if (request.cnpj == null || request.cnpj.trim().isEmpty()) {
				return ResponseEntity.badRequest()
					.body(new ErrorResponse("CNPJ é obrigatório"));
			}

			var fornecedorId = FornecedorId.temporario();
			var leadTime = request.leadTimeMedio != null && request.leadTimeMedio.dias != null
				? new LeadTime(request.leadTimeMedio.dias)
				: new LeadTime(0);
			
			var fornecedor = new Fornecedor(
				fornecedorId,
				request.nome,
				request.cnpj,
				request.contato != null ? request.contato : "",
				leadTime
			);

			if (request.ativo != null && !request.ativo) {
				fornecedor.inativar();
			}

			fornecedorServico.cadastrar(fornecedor);
			System.out.println("✅ [FornecedorControlador] Fornecedor criado com sucesso");

			return ResponseEntity.status(HttpStatus.CREATED).build();
		} catch (IllegalArgumentException e) {
			System.out.println("❌ [FornecedorControlador] Erro ao criar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (Exception e) {
			System.out.println("❌ [FornecedorControlador] Erro inesperado ao criar fornecedor: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(new ErrorResponse("Erro ao criar fornecedor: " + e.getMessage()));
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@PathVariable Long id, @RequestBody FornecedorRequest request) {
		System.out.println("✏️ [FornecedorControlador] Editando fornecedor ID: " + id);
		System.out.println("📝 [FornecedorControlador] Dados recebidos: nome=" + request.nome + ", contato=" + request.contato);
		
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		
		try {
			var fornecedor = fornecedorServico.buscarPorId(fornecedorId);
			
			if (request.nome != null && !request.nome.trim().isEmpty()) {
				fornecedor.atualizarDados(request.nome, request.contato != null ? request.contato : fornecedor.getContato());
			} else if (request.contato != null) {
				fornecedor.atualizarDados(fornecedor.getNome(), request.contato);
			}
			
			if (request.leadTimeMedio != null && request.leadTimeMedio.dias != null) {
				var novoLeadTime = new LeadTime(request.leadTimeMedio.dias);
				fornecedor.atualizarLeadTime(novoLeadTime);
			}
			
			if (request.ativo != null) {
				if (request.ativo && !fornecedor.isAtivo()) {
					fornecedor.ativar();
				} else if (!request.ativo && fornecedor.isAtivo()) {
					fornecedorServico.inativar(fornecedor);
					return ResponseEntity.ok().build();
				}
			}
			
			fornecedorServico.atualizar(fornecedor);
			System.out.println("✅ [FornecedorControlador] Fornecedor atualizado com sucesso");
			
			var fornecedorAtualizado = fornecedorServicoAplicacao.buscarPorId(fornecedorId);
			if (fornecedorAtualizado.isEmpty()) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse("Erro ao atualizar fornecedor"));
			}
			
			var resumo = fornecedorAtualizado.get();
			var response = new FornecedorResponse();
			response.id = mapeador.map(resumo.getId(), Long.class);
			response.nome = resumo.getNome();
			response.cnpj = resumo.getCnpj();
			response.contato = resumo.getContato();
			response.leadTimeMedio = new LeadTimeResponse(resumo.getLeadTimeMedio().getDias());
			response.ativo = resumo.isAtivo();
			
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("não encontrado")) {
				System.out.println("❌ [FornecedorControlador] Fornecedor não encontrado: " + id);
				return ResponseEntity.notFound().build();
			}
			System.out.println("❌ [FornecedorControlador] Erro ao editar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			System.out.println("❌ [FornecedorControlador] Erro de estado ao editar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
	}

	@PatchMapping("/{id}/inativar")
	public ResponseEntity<?> inativar(@PathVariable Long id) {
		System.out.println("🚫 [FornecedorControlador] Inativando fornecedor ID: " + id);
		
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		
		try {
			var fornecedor = fornecedorServico.buscarPorId(fornecedorId);
			fornecedorServico.inativar(fornecedor);
			System.out.println("✅ [FornecedorControlador] Fornecedor inativado com sucesso");
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("não encontrado")) {
				System.out.println("❌ [FornecedorControlador] Fornecedor não encontrado: " + id);
				return ResponseEntity.notFound().build();
			}
			System.out.println("❌ [FornecedorControlador] Erro ao inativar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		} catch (IllegalStateException e) {
			System.out.println("❌ [FornecedorControlador] Erro de estado ao inativar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
	}

	@PatchMapping("/{id}/ativar")
	public ResponseEntity<?> ativar(@PathVariable Long id) {
		System.out.println("✅ [FornecedorControlador] Ativando fornecedor ID: " + id);
		
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		
		try {
			var fornecedor = fornecedorServico.buscarPorId(fornecedorId);
			fornecedor.ativar();
			fornecedorServico.atualizar(fornecedor);
			System.out.println("✅ [FornecedorControlador] Fornecedor ativado com sucesso");
			return ResponseEntity.ok().build();
		} catch (IllegalArgumentException e) {
			if (e.getMessage().contains("não encontrado")) {
				System.out.println("❌ [FornecedorControlador] Fornecedor não encontrado: " + id);
				return ResponseEntity.notFound().build();
			}
			System.out.println("❌ [FornecedorControlador] Erro ao ativar fornecedor: " + e.getMessage());
			return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
		}
	}

	public static class FornecedorRequest {
		public String nome;
		public String cnpj;
		public String contato;
		public LeadTimeRequest leadTimeMedio;
		public Boolean ativo;
	}
	
	public static class LeadTimeRequest {
		public Integer dias;
	}
	
	public static class ErrorResponse {
		public String message;
		
		public ErrorResponse(String message) {
			this.message = message;
		}
	}

	public static class LeadTimeResponse {
		public Integer dias;
		
		public LeadTimeResponse(Integer dias) {
			this.dias = dias;
		}
	}
	
	public static class FornecedorResponse {
		public Long id;
		public String nome;
		public String cnpj;
		public String contato;
		public LeadTimeResponse leadTimeMedio;
		public boolean ativo;
	}
}

