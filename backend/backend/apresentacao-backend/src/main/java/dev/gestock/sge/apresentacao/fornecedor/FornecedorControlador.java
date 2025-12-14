package dev.gestock.sge.apresentacao.fornecedor;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorResumo;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.dominio.fornecedor.*;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.fornecedor.FornecedorFormulario.FornecedorDto;
import dev.gestock.sge.dominio.autenticacao.cliente.Email;

@RestController
@RequestMapping("backend/fornecedor")
class FornecedorControlador {

	private @Autowired dev.gestock.sge.dominio.fornecedor.FornecedorServico fornecedorServico;
	private @Autowired FornecedorServicoAplicacao fornecedorServicoAplicacao;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<FornecedorResumo> pesquisar(
			@RequestParam(required = false) String termo,
			@RequestParam(required = false) String status) {
		
		if (termo != null && !termo.isBlank()) {
			return fornecedorServicoAplicacao.pesquisarPorNomeOuCnpj(termo);
		}
		
		if (status != null && !status.isBlank()) {
			return fornecedorServicoAplicacao.pesquisarPorStatus(status);
		}
		
		return fornecedorServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	FornecedorResumo obter(@PathVariable("id") int id) {
		return fornecedorServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "salvar")
	FornecedorResumo salvar(@RequestBody FornecedorDto dto) {
		var fornecedorId = dto.id != null ? new FornecedorId(dto.id) : new FornecedorId(0);
		var cnpj = mapeador.map(dto.cnpj, Cnpj.class);
		var contato = mapeador.map(dto.contato, Email.class);
		var leadTime = mapeador.map(dto.leadTime, LeadTime.class);
		var custo = mapeador.map(dto.custo, Custo.class);
		var status = dto.status != null ? Status.valueOf(dto.status.toUpperCase()) : Status.ATIVO;
		
		var fornecedor = new Fornecedor(fornecedorId, dto.nome, cnpj, contato, leadTime, custo, status);
		var fornecedorSalvo = fornecedorServico.salvar(fornecedor);
		
		return fornecedorServicoAplicacao.obterResumo(fornecedorSalvo.getId().getId());
	}

	@RequestMapping(method = DELETE, path = "{id}")
	void remover(@PathVariable("id") int id) {
		var fornecedorId = new FornecedorId(id);
		fornecedorServico.remover(fornecedorId);
	}

	@RequestMapping(method = POST, path = "{id}/ativar")
	void ativar(@PathVariable("id") int id) {
		var fornecedorId = new FornecedorId(id);
		fornecedorServico.ativar(fornecedorId);
	}

	@RequestMapping(method = POST, path = "{id}/inativar")
	void inativar(@PathVariable("id") int id) {
		var fornecedorId = new FornecedorId(id);
		fornecedorServico.inativar(fornecedorId);
	}
}

