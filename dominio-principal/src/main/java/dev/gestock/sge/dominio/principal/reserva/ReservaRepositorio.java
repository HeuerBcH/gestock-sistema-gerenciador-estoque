package dev.gestock.sge.dominio.principal.reserva;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Reserva
 */
public interface ReservaRepositorio {
    void salvar(Reserva reserva);
    Optional<Reserva> buscarPorId(ReservaId id);
    List<Reserva> buscarReservasAtivasPorCliente(ClienteId clienteId);
    List<Reserva> buscarReservasAtivasPorEstoque(EstoqueId estoqueId);
    List<Reserva> buscarReservasAtivasPorProduto(ProdutoId produtoId);
    List<Reserva> buscarReservasPorPedido(PedidoId pedidoId);
    void remover(Reserva reserva);
}
