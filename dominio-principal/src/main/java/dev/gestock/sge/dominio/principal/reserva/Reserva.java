package dev.gestock.sge.dominio.principal.reserva;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

/**
 * Aggregate Root: Reserva
 *
 * Responsabilidades:
 * - Reservar produtos para pedidos pendentes
 * - Controlar status da reserva
 * - Registrar data e hora da reserva
 */
public class Reserva {

    private final ReservaId id;
    private ProdutoId produto;
    private EstoqueId estoque;
    private PedidoId pedido;
    private double quantidadeReservada;
    private StatusReserva status;
    private LocalDateTime dataHora;

    public Reserva(ProdutoId produto, EstoqueId estoque, PedidoId pedido, 
                   double quantidadeReservada) {
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(pedido, "Pedido é obrigatório");
        isTrue(quantidadeReservada > 0, "Quantidade reservada deve ser positiva");

        this.id = new ReservaId();
        this.produto = produto;
        this.estoque = estoque;
        this.pedido = pedido;
        this.quantidadeReservada = quantidadeReservada;
        this.status = StatusReserva.ATIVA;
        this.dataHora = LocalDateTime.now();
    }

    public Reserva(ReservaId id, ProdutoId produto, EstoqueId estoque, 
                   PedidoId pedido, double quantidadeReservada, 
                   StatusReserva status, LocalDateTime dataHora) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(pedido, "Pedido é obrigatório");
        isTrue(quantidadeReservada > 0, "Quantidade reservada deve ser positiva");
        notNull(status, "Status é obrigatório");
        notNull(dataHora, "Data e hora são obrigatórias");

        this.id = id;
        this.produto = produto;
        this.estoque = estoque;
        this.pedido = pedido;
        this.quantidadeReservada = quantidadeReservada;
        this.status = status;
        this.dataHora = dataHora;
    }

    /**
     * Reserva um produto
     */
    public static Reserva reservarProduto(ProdutoId produto, EstoqueId estoque, 
                                         PedidoId pedido, double quantidade) {
        return new Reserva(produto, estoque, pedido, quantidade);
    }

    /**
     * Libera a reserva
     */
    public void liberarReserva() {
        if (status != StatusReserva.ATIVA) {
            throw new IllegalStateException("Apenas reservas ativas podem ser liberadas");
        }
        this.status = StatusReserva.LIBERADA;
    }

    /**
     * Verifica se a reserva está ativa
     */
    public boolean isAtiva() {
        return status == StatusReserva.ATIVA;
    }

    /**
     * Verifica se a reserva foi liberada
     */
    public boolean isLiberada() {
        return status == StatusReserva.LIBERADA;
    }

    // Getters
    public ReservaId getId() { return id; }
    public ProdutoId getProduto() { return produto; }
    public EstoqueId getEstoque() { return estoque; }
    public PedidoId getPedido() { return pedido; }
    public double getQuantidadeReservada() { return quantidadeReservada; }
    public StatusReserva getStatus() { return status; }
    public LocalDateTime getDataHora() { return dataHora; }

    @Override
    public String toString() {
        return String.format("Reserva[%s] - Produto: %s, Estoque: %s, Pedido: %s, Quantidade: %.2f, Status: %s", 
                           id, produto, estoque, pedido, quantidadeReservada, status);
    }
}