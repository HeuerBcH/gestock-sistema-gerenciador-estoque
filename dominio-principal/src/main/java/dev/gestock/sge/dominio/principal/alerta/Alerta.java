package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

/* Aggregate Root: Alerta

  Responsabilidades:
  - Notificar quando um produto atinge o ponto de ressuprimento (H16, R1H16, R2H16)
  - Manter histórico de alertas ativos (H17, R1H17)
  - Sugerir fornecedor com melhor cotação */
public class Alerta {

    private final AlertaId id;
    private final ProdutoId produtoId;
    private final EstoqueId estoqueId;
    private final LocalDateTime dataGeracao;
    private FornecedorId fornecedorSugerido;
    private boolean ativo;

    public Alerta(AlertaId id, ProdutoId produtoId, EstoqueId estoqueId, FornecedorId fornecedorSugerido) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório");
        }
        if (produtoId == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (estoqueId == null) {
            throw new IllegalArgumentException("Estoque é obrigatório");
        }

        this.id = id;
        this.produtoId = produtoId;
        this.estoqueId = estoqueId;
        this.fornecedorSugerido = fornecedorSugerido;
        this.dataGeracao = LocalDateTime.now();
        this.ativo = true;
    }

    /* Desativa o alerta após recebimento do pedido (R1H17) */
    public void desativar() {
        this.ativo = false;
    }

    /* Atualiza o fornecedor sugerido */
    public void atualizarFornecedorSugerido(FornecedorId fornecedorId) {
        this.fornecedorSugerido = fornecedorId;
    }

    // Getters
    public AlertaId getId() { return id; }
    public ProdutoId getProdutoId() { return produtoId; }
    public EstoqueId getEstoqueId() { return estoqueId; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public FornecedorId getFornecedorSugerido() { return fornecedorSugerido; }
    public boolean isAtivo() { return ativo; }

    @Override
    public String toString() {
        return "Alerta{produto=" + produtoId +
                ", estoque=" + estoqueId +
                ", ativo=" + ativo +
                ", data=" + dataGeracao + "}";
    }
}
