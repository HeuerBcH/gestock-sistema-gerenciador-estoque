package dev.gestock.sge.dominio.principal.fornecedor;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDate;

/**
 * Value Object: Cotacao
 * 
 * Representa uma cotação de preço para um produto
 */
public class Cotacao {

    private final CotacaoId id;
    private final ProdutoId produto;
    private final double preco;
    private final LocalDate validade;

    public Cotacao(ProdutoId produto, double preco, LocalDate validade) {
        notNull(produto, "Produto é obrigatório");
        isTrue(preco > 0, "Preço deve ser positivo");
        notNull(validade, "Validade é obrigatória");

        this.id = new CotacaoId();
        this.produto = produto;
        this.preco = preco;
        this.validade = validade;
    }

    public Cotacao(CotacaoId id, ProdutoId produto, double preco, LocalDate validade) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        isTrue(preco > 0, "Preço deve ser positivo");
        notNull(validade, "Validade é obrigatória");

        this.id = id;
        this.produto = produto;
        this.preco = preco;
        this.validade = validade;
    }

    public CotacaoId getId() {
        return id;
    }

    public ProdutoId getProduto() {
        return produto;
    }

    public double getPreco() {
        return preco;
    }

    public LocalDate getValidade() {
        return validade;
    }

    /**
     * Verifica se a cotação está válida (não expirada)
     */
    public boolean isValida() {
        return validade.isAfter(LocalDate.now()) || validade.isEqual(LocalDate.now());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cotacao other = (Cotacao) obj;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Cotacao[%s] - Produto: %s, R$ %.2f, válida até %s", id, produto, preco, validade);
    }
}