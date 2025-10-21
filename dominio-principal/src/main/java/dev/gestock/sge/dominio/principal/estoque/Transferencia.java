package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import java.time.LocalDateTime;

/* Entidade de auditoria para transferências entre estoques.
   Mantém registro completo de origem, destino e produto transferido. */
public class Transferencia {

    private final Long id;
    private final ProdutoId produtoId;
    private final EstoqueId estoqueOrigemId;
    private final EstoqueId estoqueDestinoId;
    private final int quantidade;
    private final LocalDateTime dataHora;
    private final String responsavel;
    private final String motivo;

    public Transferencia(Long id, ProdutoId produtoId, EstoqueId estoqueOrigemId,
                         EstoqueId estoqueDestinoId, int quantidade, LocalDateTime dataHora,
                         String responsavel, String motivo) {
        this.id = id;
        this.produtoId = produtoId;
        this.estoqueOrigemId = estoqueOrigemId;
        this.estoqueDestinoId = estoqueDestinoId;
        this.quantidade = quantidade;
        this.dataHora = dataHora != null ? dataHora : LocalDateTime.now();
        this.responsavel = responsavel;
        this.motivo = motivo;
    }

    // Construtor simplificado para testes
    public Transferencia(ProdutoId produtoId, EstoqueId estoqueOrigemId,
                         EstoqueId estoqueDestinoId, int quantidade) {
        this(1L, produtoId, estoqueOrigemId, estoqueDestinoId, quantidade,
                LocalDateTime.now(), "Sistema", "Transferência");
    }

    // Getters
    public Long getId() { return id; }
    public ProdutoId getProdutoId() { return produtoId; }
    public EstoqueId getEstoqueOrigemId() { return estoqueOrigemId; }
    public EstoqueId getEstoqueDestinoId() { return estoqueDestinoId; }
    public int getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public String getResponsavel() { return responsavel; }
    public String getMotivo() { return motivo; }
}
