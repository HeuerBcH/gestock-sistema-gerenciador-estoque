package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Entidade de domínio: Cotação
 *
 * Representa o preço e prazo de entrega ofertado por um fornecedor
 * para determinado produto (R2, R5, R6).
 */
public class Cotacao {

    private final CotacaoId id;
    private final ProdutoId produtoId;
    private double preco;
    private int prazoDias; // prazo em dias úteis

    public Cotacao(ProdutoId produtoId, double preco, int prazoDias) {
        this.id = new CotacaoId();
        this.produtoId = produtoId;
        this.preco = preco;
        this.prazoDias = prazoDias;
    }

    public ProdutoId getProdutoId() { return produtoId; }
    public double getPreco() { return preco; }
    public int getPrazoDias() { return prazoDias; }

    public void atualizar(double novoPreco, int novoPrazo) {
        if (novoPreco <= 0) throw new IllegalArgumentException("Preço inválido");
        if (novoPrazo <= 0) throw new IllegalArgumentException("Prazo inválido");
        this.preco = novoPreco;
        this.prazoDias = novoPrazo;
    }

    @Override
    public String toString() {
        return String.format("Produto %s → R$ %.2f | %d dias", produtoId, preco, prazoDias);
    }
}

