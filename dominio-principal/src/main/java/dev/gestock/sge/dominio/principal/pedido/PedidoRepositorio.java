package dev.gestock.sge.dominio.principal.pedido;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Pedido.
 * Suporta as funcionalidades H11-H13 (Gerenciar Pedidos) e suas regras de negócio.
 * Define as operações que a camada de aplicação pode executar.
 * Implementação concreta fica na infraestrutura.
 */
public interface PedidoRepositorio {

    /** Persiste ou atualiza um pedido (H11, H13) */
    void salvar(Pedido pedido);

    /** Busca um pedido por seu ID */
    Optional<Pedido> buscarPorId(PedidoId id);

    /** Lista todos os pedidos do sistema */
    List<Pedido> listarTodos();

    /** Remove um pedido do sistema (H12) */
    void cancelar(Pedido pedido);

    /** Busca pedidos por status (H12 - para validar cancelamento) */
    List<Pedido> buscarPorStatus(StatusPedido status);

    /** Busca pedidos de um fornecedor específico (R1H7 - validar inativação) */
    List<Pedido> buscarPorFornecedorId(FornecedorId fornecedorId);

    /** Busca pedidos de um cliente específico */
    List<Pedido> buscarPedidosPorClienteId(ClienteId clienteId);
    
    /** Verifica se existem pedidos pendentes para um fornecedor (R1H7) */
    boolean existePedidoPendenteParaFornecedor(FornecedorId fornecedorId);
}
