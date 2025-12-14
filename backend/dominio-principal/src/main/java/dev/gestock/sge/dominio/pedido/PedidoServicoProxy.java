package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoServico;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

/**
 * Proxy para PedidoServico que adiciona controle de acesso e validações adicionais.
 * 
 * Pattern: Proxy
 * Funcionalidade: Gerenciar Pedidos (BERNARDO)
 */
public class PedidoServicoProxy {
	private final PedidoServico servicoReal;
	private boolean acessoPermitido = true;

	public PedidoServicoProxy(PedidoRepositorio repositorio, FornecedorRepositorio fornecedorRepositorio,
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

		// Cria o serviço real (lazy initialization poderia ser usado aqui)
		this.servicoReal = new PedidoServico(repositorio, fornecedorRepositorio, produtoRepositorio,
				estoqueRepositorio, cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
	}

	/**
	 * Controla o acesso ao serviço.
	 */
	public void permitirAcesso(boolean permitido) {
		this.acessoPermitido = permitido;
	}

	/**
	 * Verifica se o acesso está permitido.
	 */
	private void verificarAcesso() {
		if (!acessoPermitido) {
			throw new IllegalStateException("Acesso ao serviço de pedidos não está permitido no momento");
		}
	}

	public Pedido criar(Pedido pedido) {
		verificarAcesso();
		logAcesso("criar", pedido != null ? pedido.getId() : null);
		return servicoReal.criar(pedido);
	}

	public void confirmarRecebimento(PedidoId id) {
		verificarAcesso();
		logAcesso("confirmarRecebimento", id);
		servicoReal.confirmarRecebimento(id);
	}

	public void cancelar(PedidoId id) {
		verificarAcesso();
		logAcesso("cancelar", id);
		servicoReal.cancelar(id);
	}

	public void alterarStatus(PedidoId id, StatusPedido novoStatus) {
		verificarAcesso();
		logAcesso("alterarStatus", id);
		servicoReal.alterarStatus(id, novoStatus);
	}

	/**
	 * Registra o acesso ao serviço para auditoria.
	 */
	private void logAcesso(String operacao, Object parametro) {
		// Em produção, usar um framework de logging adequado
		System.out.println("[PEDIDO-PROXY] " + java.time.LocalDateTime.now() + 
			" - Operação: " + operacao + 
			" - Parâmetro: " + (parametro != null ? parametro.toString() : "null"));
	}
}

