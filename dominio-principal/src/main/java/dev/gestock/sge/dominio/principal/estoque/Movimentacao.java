package dev.gestock.sge.dominio.principal.estoque;

import java.time.LocalDateTime;
import java.util.Map;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
/**
 * Entidade de auditoria de movimentos no Estoque.
 * Mantém trilha completa: tipo, produto, quantidade, data/hora, responsável, motivo, metadados.
 */
public class Movimentacao {

    private final Long id;                   // identidade do registro de movimento
    private final TipoMovimentacao tipo;     // ENTRADA ou SAIDA
    private final ProdutoId produtoId;       // referência ao agregado Produto
    private final int quantidade;            // quantidade movimentada
    private final LocalDateTime dataHora;    // timestamp do evento
    private final String responsavel;        // quem realizou
    private final String motivo;             // por quê (opcional)
    private final Map<String, String> meta;  // metadados (ex.: lote, validade, flags)

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

