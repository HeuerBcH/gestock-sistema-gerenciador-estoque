package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

// Registro de auditoria de reservas/liberações (R2H25)
public class ReservaRegistro {
    public enum Tipo { RESERVA, LIBERACAO }

    private final ProdutoId produtoId;
    private final int quantidade;
    private final LocalDateTime dataHora;
    private final Tipo tipo;

    private ReservaRegistro(ProdutoId produtoId, int quantidade, LocalDateTime dataHora, Tipo tipo) {
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.tipo = tipo;
    }

    public static ReservaRegistro reserva(ProdutoId produtoId, int quantidade) {
        return new ReservaRegistro(produtoId, quantidade, LocalDateTime.now(), Tipo.RESERVA);
    }

    public static ReservaRegistro liberacao(ProdutoId produtoId, int quantidade) {
        return new ReservaRegistro(produtoId, quantidade, LocalDateTime.now(), Tipo.LIBERACAO);
    }

    public ProdutoId getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Tipo getTipo() { return tipo; }
}
