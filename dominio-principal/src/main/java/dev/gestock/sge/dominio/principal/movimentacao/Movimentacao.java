package dev.gestock.sge.dominio.principal.movimentacao;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

/**
 * Aggregate Root: Movimentacao
 *
 * Responsabilidades:
 * - Registrar movimentações de entrada e saída
 * - Manter histórico de movimentações
 * - Controlar tipos de movimentação
 */
public class Movimentacao {

    private final MovimentacaoId id;
    private ProdutoId produto;
    private EstoqueId estoque;
    private LocalDateTime dataHora;
    private TipoMovimentacao tipo;
    private double quantidade;
    private String motivo;

    public Movimentacao(ProdutoId produto, EstoqueId estoque, TipoMovimentacao tipo, 
                       double quantidade, String motivo) {
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(tipo, "Tipo de movimentação é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(motivo, "Motivo é obrigatório");

        this.id = new MovimentacaoId();
        this.produto = produto;
        this.estoque = estoque;
        this.dataHora = LocalDateTime.now();
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.motivo = motivo;
    }

    public Movimentacao(MovimentacaoId id, ProdutoId produto, EstoqueId estoque, 
                       LocalDateTime dataHora, TipoMovimentacao tipo, 
                       double quantidade, String motivo) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(dataHora, "Data e hora são obrigatórias");
        notNull(tipo, "Tipo de movimentação é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(motivo, "Motivo é obrigatório");

        this.id = id;
        this.produto = produto;
        this.estoque = estoque;
        this.dataHora = dataHora;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.motivo = motivo;
    }

    /**
     * Registra uma movimentação de entrada
     */
    public static Movimentacao registrarEntrada(ProdutoId produto, EstoqueId estoque, 
                                               double quantidade, String motivo) {
        return new Movimentacao(produto, estoque, TipoMovimentacao.ENTRADA, quantidade, motivo);
    }

    /**
     * Registra uma movimentação de saída
     */
    public static Movimentacao registrarSaida(ProdutoId produto, EstoqueId estoque, 
                                             double quantidade, String motivo) {
        return new Movimentacao(produto, estoque, TipoMovimentacao.SAIDA, quantidade, motivo);
    }

    /**
     * Verifica se a movimentação é de entrada
     */
    public boolean isEntrada() {
        return tipo == TipoMovimentacao.ENTRADA;
    }

    /**
     * Verifica se a movimentação é de saída
     */
    public boolean isSaida() {
        return tipo == TipoMovimentacao.SAIDA;
    }

    // Getters
    public MovimentacaoId getId() { return id; }
    public ProdutoId getProduto() { return produto; }
    public EstoqueId getEstoque() { return estoque; }
    public LocalDateTime getDataHora() { return dataHora; }
    public TipoMovimentacao getTipo() { return tipo; }
    public double getQuantidade() { return quantidade; }
    public String getMotivo() { return motivo; }

    @Override
    public String toString() {
        return String.format("Movimentacao[%s] - %s %s %.2f em %s - %s", 
                           id, tipo, produto, quantidade, estoque, motivo);
    }
}