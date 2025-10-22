package dev.gestock.sge.dominio.principal.estoque;

import java.time.LocalDateTime;
import java.util.Map;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
// Entidade de auditoria de movimentos no Estoque.

public class Movimentacao {

    private final Long id;
    private final TipoMovimentacao tipo;
    private final ProdutoId produtoId;
    private final int quantidade;
    private final LocalDateTime dataHora;
    private final String responsavel;
    private final String motivo;
    private final Map<String, String> meta;

    public Movimentacao(Long id,
                        TipoMovimentacao tipo,
                        ProdutoId produtoId,
                        int quantidade,
                        LocalDateTime dataHora,
                        String responsavel,
                        String motivo,
                        Map<String, String> meta) {
        this.id = id;
        this.tipo = tipo;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.responsavel = responsavel;
        this.motivo = motivo;
        this.meta = meta;
    }

    // Getters
    public Long getId() { return id; }
    public TipoMovimentacao getTipo() { return tipo; }
    public ProdutoId getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getResponsavel() { return responsavel; }
    public String getMotivo() { return motivo; }
    public Map<String, String> getMeta() { return meta; }
}

