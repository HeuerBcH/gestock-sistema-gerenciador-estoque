package dev.gestock.sge.dominio.pedido;

/**
 * Interface comum para o serviço de pedidos, permitindo que o componente real
 * (PedidoServico) e o proxy (PedidoServicoProxy) implementem o mesmo contrato.
 * Essencial para o padrão Proxy, garantindo substituição transparente.
 *
 * <p>O padrão Proxy permite controlar o acesso ao objeto real, adicionando
 * funcionalidades como controle de acesso, logging, cache, lazy loading, etc.,
 * sem modificar o código do objeto real.</p>
 *
 * <p>Esta interface define o contrato comum que tanto o Subject Real quanto
 * o Proxy devem implementar, permitindo que o cliente use qualquer um deles
 * de forma transparente.</p>
 *
 * Pattern: Proxy (Subject Interface)
 * Funcionalidade: Gerenciar Pedidos (BERNARDO)
 *
 * @see PedidoServico Implementação real do serviço
 * @see PedidoServicoProxy Proxy que adiciona controle de acesso e auditoria
 */
public interface IPedidoServico {
	/**
	 * Cria um novo pedido com validações e cálculos necessários.
	 *
	 * @param pedido O pedido a ser criado
	 * @return O pedido criado com dados completos (preços, datas, etc.)
	 * @throws IllegalArgumentException Se os dados do pedido forem inválidos
	 */
	Pedido criar(Pedido pedido);

	/**
	 * Confirma o recebimento de um pedido, criando movimentações de entrada
	 * no estoque e publicando eventos correspondentes.
	 *
	 * @param id O identificador do pedido
	 * @throws IllegalArgumentException Se o pedido não for encontrado
	 */
	void confirmarRecebimento(PedidoId id);

	/**
	 * Cancela um pedido, respeitando as regras de negócio (ex: não pode
	 * cancelar pedidos em transporte).
	 *
	 * @param id O identificador do pedido
	 * @throws IllegalArgumentException Se o pedido não for encontrado
	 * @throws RegraVioladaException Se a regra de negócio não permitir o cancelamento
	 */
	void cancelar(PedidoId id);

	/**
	 * Altera o status de um pedido.
	 *
	 * @param id O identificador do pedido
	 * @param novoStatus O novo status a ser atribuído
	 * @throws IllegalArgumentException Se o pedido não for encontrado ou se o status for nulo
	 */
	void alterarStatus(PedidoId id, StatusPedido novoStatus);
}

