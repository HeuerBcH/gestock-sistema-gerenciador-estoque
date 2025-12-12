package dev.gestock.sge.apresentacao.principal.pedido;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gestock.sge.aplicacao.dominio.pedido.PedidoResumo;
import dev.gestock.sge.aplicacao.dominio.pedido.PedidoServicoAplicacao;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.pedido.PedidoServico;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;

@RestController
@RequestMapping("backend/pedido")
class PedidoControlador {
	private @Autowired PedidoServico pedidoServico;
	private @Autowired PedidoServicoAplicacao pedidoServicoAplicacao;
	private @Autowired PedidoRepositorio pedidoRepositorio;
	private @Autowired EstoqueRepositorio estoqueRepositorio;
	private @Autowired FornecedorRepositorio fornecedorRepositorio;
	private @Autowired ProdutoRepositorio produtoRepositorio;
	private @Autowired BackendMapeador mapeador;

	@RequestMapping(method = GET, path = "pesquisa")
	List<PedidoResumo> pesquisar() {
		return pedidoServicoAplicacao.pesquisarResumos();
	}

	@RequestMapping(method = GET, path = "{id}")
	Pedido buscarPorId(@PathVariable("id") Long id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		return pedidoRepositorio.buscarPorId(pedidoId)
			.orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
	}

	@RequestMapping(method = POST, path = "gerar")
	Pedido gerarPedido(@RequestBody PedidoForm.GerarPedidoDto dto) {
		var clienteId = mapeador.map(dto.clienteId, ClienteId.class);
		var fornecedorId = mapeador.map(dto.fornecedorId, FornecedorId.class);
		var produtoId = mapeador.map(dto.produtoId, ProdutoId.class);
		
		var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
		var produtoOpt = produtoRepositorio.buscarPorId(produtoId);
		
		if (fornecedorOpt.isEmpty() || produtoOpt.isEmpty()) {
			throw new IllegalArgumentException("Fornecedor ou produto não encontrado");
		}
		
		Fornecedor fornecedor = fornecedorOpt.get();
		Produto produto = produtoOpt.get();
		
		if (dto.estoqueId != null) {
			var estoqueId = mapeador.map(dto.estoqueId, EstoqueId.class);
			var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
			
			if (estoqueOpt.isEmpty()) {
				throw new IllegalArgumentException("Estoque não encontrado");
			}
			
			return pedidoServico.gerarPedidoParaEstoque(clienteId, fornecedor, produto, 
				dto.quantidade, estoqueOpt.get());
		} else {
			return pedidoServico.gerarPedido(clienteId, fornecedor, produto, dto.quantidade);
		}
	}

	@RequestMapping(method = POST, path = "{id}/enviar")
	void enviar(@PathVariable("id") Long id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
		
		if (pedidoOpt.isPresent()) {
			pedidoServico.enviar(pedidoOpt.get());
		} else {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "{id}/cancelar")
	void cancelar(@PathVariable("id") Long id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
		
		if (pedidoOpt.isPresent()) {
			var pedido = pedidoOpt.get();
			// Se o pedido tem estoque associado, precisa liberar a reserva
			if (pedido.getEstoqueId().isPresent()) {
				var estoqueId = pedido.getEstoqueId().get();
				var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
				
				if (estoqueOpt.isPresent() && !pedido.getItens().isEmpty()) {
					var item = pedido.getItens().get(0);
					pedidoServico.cancelarComLiberacao(pedido, estoqueOpt.get(), 
						item.getProdutoId(), item.getQuantidade());
				} else {
					pedidoServico.cancelar(pedido);
				}
			} else {
				pedidoServico.cancelar(pedido);
			}
		} else {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
	}

	@RequestMapping(method = POST, path = "{id}/confirmar-recebimento")
	void confirmarRecebimento(@PathVariable("id") Long id, @RequestBody ConfirmarRecebimentoDto dto) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
		
		if (pedidoOpt.isPresent()) {
			var pedido = pedidoOpt.get();
			var estoqueOpt = pedido.getEstoqueId()
				.flatMap(eid -> estoqueRepositorio.buscarPorId(eid));
			
			if (estoqueOpt.isPresent()) {
				pedidoServico.confirmarRecebimento(pedido, estoqueOpt.get(), dto.responsavel);
			} else {
				throw new IllegalArgumentException("Estoque não encontrado para o pedido");
			}
		} else {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
	}

	public static class ConfirmarRecebimentoDto {
		public String responsavel;
	}

	@RequestMapping(method = POST, path = "{id}/concluir")
	void concluir(@PathVariable("id") Long id) {
		var pedidoId = mapeador.map(id, PedidoId.class);
		var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
		
		if (pedidoOpt.isPresent()) {
			pedidoServico.concluir(pedidoOpt.get());
		} else {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
	}
}
