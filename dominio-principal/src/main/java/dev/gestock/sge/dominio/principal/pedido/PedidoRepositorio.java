package src.main.java.dev.gestock.sge.dominio.principal.pedido;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Pedido.
 * - Define as operações que a camada de aplicação pode executar.
 * - Implementação concreta fica na infraestrutura (ex.: JPA).
 */
public interface PedidoRepositorio {

    void salvar(Pedido pedido);

    Optional<Pedido> buscarPorId(PedidoId id);

    List<Pedido> listarTodos();

    void remover(Pedido pedido);

    List<Pedido> buscarPorStatus(StatusPedido status);

    List<Pedido> buscarPorFornecedorId(String fornecedorId);

    List<Pedido> buscarPorClienteId(String clienteId);
}
