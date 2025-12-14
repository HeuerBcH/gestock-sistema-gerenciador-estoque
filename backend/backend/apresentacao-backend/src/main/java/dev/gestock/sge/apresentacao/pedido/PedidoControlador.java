package dev.gestock.sge.apresentacao.pedido;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.cotacao.CotacaoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.cotacao.CotacaoResumo;
import dev.gestock.sge.aplicacao.pedido.PedidoResumo;
import dev.gestock.sge.aplicacao.pedido.PedidoServicoAplicacao;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.PedidoServico;
import dev.gestock.sge.dominio.pedido.StatusPedido;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.pedido.PedidoFormulario.PedidoDto;
import dev.gestock.sge.apresentacao.pedido.PedidoFormulario.PedidoAutomaticoDto;
import dev.gestock.sge.apresentacao.pedido.PedidoFormulario.ItemPedidoDto;

@RestController
@RequestMapping("backend/pedido")
class PedidoControlador {

	private @Autowired PedidoServico pedidoServico;
	private @Autowired PedidoServicoAplicacao pedidoServicoAplicacao;
	private @Autowired CotacaoRepositorioAplicacao cotacaoRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<PedidoResumo> pesquisar() {
		return pedidoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	PedidoResumo obter(@PathVariable("id") int id) {
		return pedidoServicoAplicacao.obterResumo(id);
	}

	@RequestMapping(method = POST, path = "criar")
	PedidoResumo criar(@RequestBody PedidoDto dto) {
		dto.id = null; // Garante que é novo
		if (dto.dataPedido == null) {
			dto.dataPedido = java.time.LocalDate.now();
		}
		if (dto.status == null) {
			dto.status = "CRIADO";
		}
		var pedido = mapeador.map(dto, Pedido.class);
		var pedidoCriado = pedidoServico.criar(pedido);
		return pedidoServicoAplicacao.obterResumo(pedidoCriado.getId().getId());
	}

	@RequestMapping(method = PUT, path = "{id}/status")
	void alterarStatus(@PathVariable("id") int id, @RequestBody String status) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		var statusPedido = mapeador.map(status, StatusPedido.class);
		pedidoServico.alterarStatus(pedidoId, statusPedido);
	}

	@RequestMapping(method = POST, path = "{id}/confirmar-recebimento")
	void confirmarRecebimento(@PathVariable("id") int id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		pedidoServico.confirmarRecebimento(pedidoId);
	}

	@RequestMapping(method = POST, path = "{id}/cancelar")
	void cancelar(@PathVariable("id") int id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		pedidoServico.cancelar(pedidoId);
	}

	@RequestMapping(method = POST, path = "criar-automatico")
	List<PedidoResumo> criarAutomatico(@RequestBody PedidoAutomaticoDto dto) {
		if (dto.itens == null || dto.itens.isEmpty()) {
			throw new IllegalArgumentException("É necessário adicionar pelo menos um item ao pedido");
		}
		if (dto.estoqueId == null || dto.estoqueId <= 0) {
			throw new IllegalArgumentException("É necessário selecionar um estoque para o pedido");
		}

		// Para cada produto, buscar a melhor cotação APROVADA
		Map<Integer, List<ItemPedidoDto>> itensPorFornecedor = new HashMap<>();

		for (var item : dto.itens) {
			var cotacoes = cotacaoRepositorio.pesquisarPorProduto(item.produtoId);
			
			// Filtrar apenas cotações aprovadas e encontrar a melhor
			CotacaoResumo melhorCotacao = cotacoes.stream()
				.filter(c -> "APROVADA".equals(c.getStatusAprovacao()))
				.filter(c -> "ATIVA".equals(c.getValidade()))
				.min((c1, c2) -> {
					// Ordenar por: preço (menor), lead time (menor), id (menor)
					int comparePreco = c1.getPreco().compareTo(c2.getPreco());
					if (comparePreco != 0) return comparePreco;
					int compareLeadTime = Integer.compare(c1.getLeadTime(), c2.getLeadTime());
					if (compareLeadTime != 0) return compareLeadTime;
					return Integer.compare(c1.getId(), c2.getId());
				})
				.orElse(null);

			if (melhorCotacao == null) {
				throw new IllegalArgumentException(
					"Produto ID " + item.produtoId + " não possui cotação aprovada disponível. " +
					"Aprove uma cotação para este produto antes de criar o pedido."
				);
			}

			// Agrupar por fornecedor
			int fornecedorId = melhorCotacao.getFornecedorId();
			itensPorFornecedor
				.computeIfAbsent(fornecedorId, k -> new ArrayList<>())
				.add(item);
		}

		// Criar um pedido para cada fornecedor
		var pedidosCriados = new ArrayList<PedidoResumo>();

		for (var entry : itensPorFornecedor.entrySet()) {
			var fornecedorId = entry.getKey();
			var itensFornecedor = entry.getValue();

			var pedidoDto = new PedidoDto();
			pedidoDto.fornecedorId = fornecedorId;
			pedidoDto.estoqueId = dto.estoqueId;
			pedidoDto.itens = itensFornecedor;
			pedidoDto.dataPedido = java.time.LocalDate.now();
			pedidoDto.status = "CRIADO";

			var pedido = mapeador.map(pedidoDto, Pedido.class);
			var pedidoCriado = pedidoServico.criar(pedido);
			var resumo = pedidoServicoAplicacao.obterResumo(pedidoCriado.getId().getId());
			pedidosCriados.add(resumo);
		}

		return pedidosCriados;
	}
}

