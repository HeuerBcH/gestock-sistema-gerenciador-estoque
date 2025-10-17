package dev.gestock.sge.dominio.principal.reserva;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;

/**
 * Serviço de domínio para operações que envolvem reservas
 * 
 * Responsabilidades:
 * - Criar reservas automaticamente ao gerar pedidos
 * - Liberar reservas quando pedidos são cancelados
 * - Consumir reservas quando pedidos são processados
 * - Gerenciar o ciclo de vida das reservas
 */
public class ReservaServico {
    
    private final ReservaRepositorio reservaRepositorio;
    
    public ReservaServico(ReservaRepositorio reservaRepositorio) {
        notNull(reservaRepositorio, "Repositório de reserva é obrigatório");
        this.reservaRepositorio = reservaRepositorio;
    }
    
    /**
     * Cria reserva para um pedido (R1H24)
     * 
     * @param clienteId ID do cliente
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param pedidoId ID do pedido
     * @param quantidade Quantidade a ser reservada
     * @return Reserva criada
     */
    public Reserva criarReserva(ClienteId clienteId, EstoqueId estoqueId, 
                               ProdutoId produtoId, PedidoId pedidoId, int quantidade) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        notNull(pedidoId, "Pedido é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        
        Reserva reserva = new Reserva(clienteId, estoqueId, produtoId, pedidoId, quantidade);
        reservaRepositorio.salvar(reserva);
        
        return reserva;
    }
    
    /**
     * Libera todas as reservas de um pedido (R1H25)
     * Usado quando o pedido é cancelado
     * 
     * @param pedidoId ID do pedido
     * @param motivo Motivo da liberação
     */
    public void liberarReservasPorPedido(PedidoId pedidoId, String motivo) {
        notNull(pedidoId, "Pedido é obrigatório");
        notBlank(motivo, "Motivo é obrigatório");
        
        List<Reserva> reservas = reservaRepositorio.buscarReservasPorPedido(pedidoId);
        for (Reserva reserva : reservas) {
            if (reserva.isAtiva()) {
                reserva.liberar(motivo);
                reservaRepositorio.salvar(reserva);
            }
        }
    }
    
    /**
     * Consome todas as reservas de um pedido
     * Usado quando o pedido é processado
     * 
     * @param pedidoId ID do pedido
     */
    public void consumirReservasPorPedido(PedidoId pedidoId) {
        notNull(pedidoId, "Pedido é obrigatório");
        
        List<Reserva> reservas = reservaRepositorio.buscarReservasPorPedido(pedidoId);
        for (Reserva reserva : reservas) {
            if (reserva.isAtiva()) {
                reserva.consumir();
                reservaRepositorio.salvar(reserva);
            }
        }
    }
    
    /**
     * Lista todas as reservas ativas de um cliente
     * 
     * @param clienteId ID do cliente
     * @return Lista de reservas ativas
     */
    public List<Reserva> listarReservasAtivas(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        return reservaRepositorio.buscarReservasAtivasPorCliente(clienteId);
    }
    
    /**
     * Lista todas as reservas ativas de um estoque
     * 
     * @param estoqueId ID do estoque
     * @return Lista de reservas ativas
     */
    public List<Reserva> listarReservasAtivasPorEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        return reservaRepositorio.buscarReservasAtivasPorEstoque(estoqueId);
    }
    
    /**
     * Lista todas as reservas ativas de um produto
     * 
     * @param produtoId ID do produto
     * @return Lista de reservas ativas
     */
    public List<Reserva> listarReservasAtivasPorProduto(ProdutoId produtoId) {
        notNull(produtoId, "Produto é obrigatório");
        return reservaRepositorio.buscarReservasAtivasPorProduto(produtoId);
    }
}
