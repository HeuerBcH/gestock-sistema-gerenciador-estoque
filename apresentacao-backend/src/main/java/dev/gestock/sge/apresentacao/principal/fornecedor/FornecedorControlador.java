package dev.gestock.sge.apresentacao.principal.fornecedor;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorResumo;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.principal.fornecedor.FornecedorForm.FornecedorDto;
import dev.gestock.sge.dominio.principal.fornecedor.Cotacao;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

@RestController
@RequestMapping("backend/fornecedor")
class FornecedorControlador {
	private @Autowired FornecedorServico fornecedorServico;
	private @Autowired FornecedorServicoAplicacao fornecedorServicoAplicacao;
	private @Autowired FornecedorRepositorio fornecedorRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<FornecedorResumo> pesquisar() {
		return fornecedorServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	Fornecedor buscarPorId(@PathVariable("id") Long id) {
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		return fornecedorRepositorio.buscarPorId(fornecedorId)
			.orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));
	}

	@RequestMapping(method = POST, path = "salvar")
	void salvar(@RequestBody FornecedorDto dto) {
		dto.id = null; // ID será gerado pela persistência
		var fornecedor = mapeador.map(dto, Fornecedor.class);
		fornecedorServico.cadastrar(fornecedor);
	}

	@RequestMapping(method = POST, path = "{id}/atualizar")
	void atualizar(@PathVariable("id") Long id, @RequestBody FornecedorDto dto) {
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
		
		if (fornecedorOpt.isPresent()) {
			var fornecedor = fornecedorOpt.get();
			fornecedor.atualizarDados(dto.nome, dto.contato);
			if (dto.leadTimeMedio != null) {
				fornecedor.recalibrarLeadTime(java.util.List.of(dto.leadTimeMedio));
			}
			fornecedorServico.atualizar(fornecedor);
		} else {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") Long id) {
		var fornecedorId = mapeador.map(id, FornecedorId.class);
		var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
		
		if (fornecedorOpt.isPresent()) {
			fornecedorServico.inativar(fornecedorOpt.get());
		} else {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "cotacao/selecionar-melhor")
	java.util.Optional<Cotacao> selecionarMelhorCotacao(@RequestBody SelecionarCotacaoDto dto) {
		var fornecedores = dto.fornecedorIds.stream()
			.map(fid -> fornecedorRepositorio.buscarPorId(mapeador.map(fid, FornecedorId.class)))
			.filter(java.util.Optional::isPresent)
			.map(java.util.Optional::get)
			.toList();
		
		var produtoIdVO = mapeador.map(dto.produtoId, ProdutoId.class);
		return fornecedorServico.selecionarMelhorCotacao(fornecedores, produtoIdVO);
	}

	public static class SelecionarCotacaoDto {
		public java.util.List<Long> fornecedorIds;
		public Long produtoId;
	}
}
