package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Entidade de domínio: Cotação
 *
 * Representa o preço e prazo de entrega ofertado por um fornecedor
 * para determinado produto (R2, R5, R6, R1H18).
 */
public class Cotacao {

    private final CotacaoId id;
    private final ProdutoId produtoId;
    private double preco;
    private int prazoDias; // prazo em dias úteis
    // Validade simplificada (ativa/inativa)
    private boolean validadeAtiva;

    public Cotacao(CotacaoId id, ProdutoId produtoId, double preco, int prazoDias) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório");
        this.id = id;
        this.produtoId = produtoId;
        this.preco = preco;
        this.prazoDias = prazoDias;
        this.validadeAtiva = true; // por padrão, ativa
    }

    public ProdutoId getProdutoId() { return produtoId; }
    public double getPreco() { return preco; }
    public int getPrazoDias() { return prazoDias; }
    public CotacaoId getId() { return id; }
    public boolean isValidadeAtiva() { return validadeAtiva; }

    public void atualizar(double novoPreco, int novoPrazo) {
        if (novoPreco <= 0) throw new IllegalArgumentException("Preço inválido");
        if (novoPrazo <= 0) throw new IllegalArgumentException("Prazo inválido");
        this.preco = novoPreco;
        this.prazoDias = novoPrazo;
    }

    /** Define a validade como ativa ou inativa. */
    public void definirValidadeAtiva(boolean ativa) {
        this.validadeAtiva = ativa;
    }

    @Override
    public String toString() {
        return String.format("Produto %s → R$ %.2f | %d dias | Validade: %s",
                produtoId, preco, prazoDias,
                validadeAtiva ? "ativa" : "inativa");
    }
}
