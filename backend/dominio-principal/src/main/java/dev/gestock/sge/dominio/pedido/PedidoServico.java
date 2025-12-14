package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import dev.gestock.sge.dominio.comum.RegraVioladaException;
import dev.gestock.sge.dominio.cotacao.Cotacao;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoServico;
import dev.gestock.sge.dominio.movimentacao.Responsavel;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

/**
 * Serviço de domínio para gerenciar pedidos. Este é o Subject Real do padrão Proxy,
 * implementando a interface IPedidoServico e contendo toda a lógica de negócio
 * para criação, confirmação, cancelamento e alteração de status de pedidos.
 *
 * <p>O padrão Proxy permite que um PedidoServicoProxy controle o acesso a este
 * serviço, adicionando funcionalidades como controle de acesso e auditoria sem
 * modificar o código desta classe.</p>
 *
 * Pattern: Proxy (Subject Real)
 * Funcionalidade: Gerenciar Pedidos (BERNARDO)
 *
 * @see IPedidoServico Interface comum para o serviço de pedidos
 * @see PedidoServicoProxy Proxy que adiciona controle de acesso e auditoria
 */
public class PedidoServico implements IPedidoServico {
	private final PedidoRepositorio repositorio;
	private final FornecedorRepositorio fornecedorRepositorio;
	private final ProdutoRepositorio produtoRepositorio;
	private final EstoqueRepositorio estoqueRepositorio;
	private final CotacaoRepositorio cotacaoRepositorio;
	private final CotacaoServico cotacaoServico;
	private final MovimentacaoServico movimentacaoServico;
	private final EventoBarramento barramento;

	public PedidoServico(PedidoRepositorio repositorio, FornecedorRepositorio fornecedorRepositorio,
			ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio,
			CotacaoRepositorio cotacaoRepositorio, CotacaoServico cotacaoServico,
			MovimentacaoServico movimentacaoServico, EventoBarramento barramento) {
		notNull(repositorio, "O repositório não pode ser nulo");
		notNull(fornecedorRepositorio, "O repositório de fornecedores não pode ser nulo");
		notNull(produtoRepositorio, "O repositório de produtos não pode ser nulo");
		notNull(estoqueRepositorio, "O repositório de estoques não pode ser nulo");
		notNull(cotacaoRepositorio, "O repositório de cotações não pode ser nulo");
		notNull(cotacaoServico, "O serviço de cotação não pode ser nulo");
		notNull(movimentacaoServico, "O serviço de movimentação não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");

		this.repositorio = repositorio;
		this.fornecedorRepositorio = fornecedorRepositorio;
		this.produtoRepositorio = produtoRepositorio;
		this.estoqueRepositorio = estoqueRepositorio;
		this.cotacaoRepositorio = cotacaoRepositorio;
		this.cotacaoServico = cotacaoServico;
		this.movimentacaoServico = movimentacaoServico;
		this.barramento = barramento;
	}

	public Pedido criar(Pedido pedido) {
		notNull(pedido, "O pedido não pode ser nulo");

		// Validar que fornecedor existe e está ativo
		var fornecedor = fornecedorRepositorio.obter(pedido.getFornecedorId());
		if (fornecedor == null) {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}
		if (fornecedor.getStatus() != dev.gestock.sge.dominio.fornecedor.Status.ATIVO) {
			throw new IllegalArgumentException("Fornecedor deve estar ativo");
		}

		// Validar que estoque existe e está ativo
		var estoque = estoqueRepositorio.obter(pedido.getEstoqueId());
		if (estoque == null) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}
		if (estoque.getStatus() != dev.gestock.sge.dominio.fornecedor.Status.ATIVO) {
			throw new IllegalArgumentException("Estoque deve estar ativo");
		}

		// Calcular quantidade total do pedido para verificar capacidade
		int quantidadeTotal = 0;
		for (var item : pedido.getItens()) {
			quantidadeTotal += item.getQuantidade().getValor();
		}

		// Verificar capacidade do estoque
		int capacidadeMaxima = estoque.getCapacidade().getValor();
		if (quantidadeTotal > capacidadeMaxima) {
			throw new IllegalArgumentException(
				String.format("Quantidade do pedido (%d) excede a capacidade do estoque (%d)", 
					quantidadeTotal, capacidadeMaxima));
		}

		// Validar produtos e buscar cotações
		var itensComPreco = new ArrayList<ItemPedido>();
		for (var item : pedido.getItens()) {
			// Validar que produto existe e está ativo
			var produto = produtoRepositorio.obter(item.getProdutoId());
			if (produto == null) {
				throw new IllegalArgumentException("Produto não encontrado: " + item.getProdutoId().getId());
			}
			if (!produto.getStatus().equals(dev.gestock.sge.dominio.fornecedor.Status.ATIVO)) {
				throw new IllegalArgumentException("Produto deve estar ativo: " + item.getProdutoId().getId());
			}

			// Buscar cotação do produto com o fornecedor especificado
			var cotacoes = cotacaoRepositorio.obterPorProduto(item.getProdutoId());
			Cotacao cotacao = null;
			for (var c : cotacoes) {
				if (c.getFornecedorId().equals(pedido.getFornecedorId())) {
					cotacao = c;
					break;
				}
			}

			// Se não houver cotação com o fornecedor, usar a mais vantajosa
			if (cotacao == null) {
				cotacao = cotacaoServico.obterMaisVantajosa(item.getProdutoId());
				if (cotacao == null) {
					throw new IllegalArgumentException(
							"Produto não possui cotação disponível: " + item.getProdutoId().getId());
				}
			}

			// Criar item com preço unitário da cotação
			var itemComPreco = new ItemPedido(item.getProdutoId(), item.getQuantidade(), cotacao.getPreco());
			itensComPreco.add(itemComPreco);
		}

		// Criar novo pedido com itens e preços
		var pedidoCompleto = new Pedido(pedido.getId(), pedido.getFornecedorId(), pedido.getEstoqueId(),
				itensComPreco, pedido.getDataPedido(), pedido.getStatus());
		
		// Calcular valor total
		pedidoCompleto.calcularValorTotal();
		
		// Calcular data prevista baseada no lead time do fornecedor
		pedidoCompleto.calcularDataPrevista(fornecedor.getLeadTime());

		var pedidoSalvo = repositorio.salvar(pedidoCompleto);
		
		// Publicar evento de pedido criado
		var evento = pedidoSalvo.criarEvento();
		barramento.postar(evento);
		
		return pedidoSalvo;
	}

	public void confirmarRecebimento(PedidoId id) {
		notNull(id, "O id não pode ser nulo");
		var pedido = repositorio.obter(id);
		if (pedido == null) {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
		
		var evento = pedido.confirmarRecebimento();
		repositorio.salvar(pedido);
		
		// Criar movimentações de ENTRADA para cada item do pedido
		for (var item : pedido.getItens()) {
			var movimentacao = new Movimentacao(
				new MovimentacaoId(0), // ID será gerado pelo banco
				LocalDateTime.now(),
				item.getProdutoId(),
				pedido.getEstoqueId(),
				item.getQuantidade(),
				TipoMovimentacao.ENTRADA,
				new Motivo("Recebimento de pedido #" + id.getId()),
				new Responsavel("Sistema - Pedido Recebido")
			);
			movimentacaoServico.registrar(movimentacao);
		}
		
		barramento.postar(evento);
	}

	public void cancelar(PedidoId id) {
		notNull(id, "O id não pode ser nulo");
		var pedido = repositorio.obter(id);
		if (pedido == null) {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
		
		// R1H12: Pedidos com status "Em transporte" não podem ser cancelados
		if (pedido.getStatus() == StatusPedido.EM_TRANSPORTE) {
			throw new RegraVioladaException("R1H12", 
				"Não é possível cancelar um pedido em transporte");
		}
		
		var evento = pedido.cancelar();
		repositorio.salvar(pedido);
		barramento.postar(evento);
	}

	public void alterarStatus(PedidoId id, StatusPedido novoStatus) {
		notNull(id, "O id não pode ser nulo");
		notNull(novoStatus, "O novo status não pode ser nulo");
		var pedido = repositorio.obter(id);
		if (pedido == null) {
			throw new IllegalArgumentException("Pedido não encontrado");
		}
		pedido.alterarStatus(novoStatus);
		repositorio.salvar(pedido);
	}
}
