package dev.gestock.sge.dominio.principal.custo;

import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Custo.
 * Garante persistência e recuperação dos custos vinculados a pedidos.
 */
public interface CustoRepositorio {

    void salvar(Custo custo);

    Optional<Custo> buscarPorId(CustoId id);

    Optional<Custo> buscarPorPedido(PedidoId pedidoId);

    List<Custo> listarTodos();

    void remover(Custo custo);
}
