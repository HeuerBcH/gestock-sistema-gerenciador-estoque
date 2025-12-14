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
 * Proxy para IPedidoServico que adiciona controle de acesso e auditoria/logging
 * sem modificar o código do objeto real (PedidoServico).
 *
 * <p>O padrão Proxy permite controlar o acesso ao objeto real, adicionando
 * funcionalidades extras como controle de acesso, logging, cache, lazy loading,
 * etc., mantendo a mesma interface do objeto real.</p>
 *
 * <p>Este proxy implementa IPedidoServico, permitindo substituição transparente
 * com PedidoServico. O cliente não precisa saber se está usando o objeto real
 * ou o proxy.</p>
 *
 * <p><strong>Funcionalidades adicionadas pelo Proxy:</strong></p>
 * <ul>
 *   <li><strong>Controle de Acesso:</strong> Permite habilitar/desabilitar o acesso
 *       ao serviço através do método {@link #permitirAcesso(boolean)}</li>
 *   <li><strong>Auditoria/Logging:</strong> Registra todas as operações realizadas
 *       para fins de auditoria e rastreabilidade</li>
 * </ul>
 *
 * <p><strong>Exemplo de uso:</strong></p>
 * <pre>{@code
 * // Criar o proxy
 * IPedidoServico proxy = new PedidoServicoProxy(repositorio, ...);
 *
 * // Controlar acesso
 * proxy.permitirAcesso(true);
 * Pedido pedido = proxy.criar(novoPedido); // Operação com controle de acesso e auditoria
 *
 * // Bloquear acesso temporariamente
 * proxy.permitirAcesso(false);
 * proxy.criar(pedido); // Lança IllegalStateException
 * }</pre>
 *
 * Pattern: Proxy (Proxy)
 * Funcionalidade: Gerenciar Pedidos (BERNARDO)
 *
 * @see IPedidoServico Interface comum para o serviço de pedidos
 * @see PedidoServico Implementação real do serviço (Subject Real)
 */
public class PedidoServicoProxy implements IPedidoServico {
	private final IPedidoServico servicoReal;
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

		// Cria o serviço real e armazena como IPedidoServico para permitir substituição transparente
		// Em uma implementação mais avançada, poderia usar lazy initialization aqui
		this.servicoReal = new PedidoServico(repositorio, fornecedorRepositorio, produtoRepositorio,
				estoqueRepositorio, cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
	}

	/**
	 * Controla o acesso ao serviço. Quando o acesso está desabilitado,
	 * todas as operações lançam IllegalStateException.
	 *
	 * @param permitido true para permitir acesso, false para bloquear
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

