package dev.gestock.sge.dominio.principal.transferencia;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

/**
 * Aggregate Root: Transferencia
 *
 * Responsabilidades:
 * - Transferir produtos entre estoques
 * - Controlar status da transferência
 * - Registrar data e hora da transferência
 */
public class Transferencia {

    private final TransferenciaId id;
    private ProdutoId produto;
    private EstoqueId origem;
    private EstoqueId destino;
    private double quantidade;
    private LocalDateTime dataHora;
    private StatusTransferencia status;

    public Transferencia(ProdutoId produto, EstoqueId origem, EstoqueId destino, 
                        double quantidade) {
        notNull(produto, "Produto é obrigatório");
        notNull(origem, "Estoque de origem é obrigatório");
        notNull(destino, "Estoque de destino é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");

        this.id = new TransferenciaId();
        this.produto = produto;
        this.origem = origem;
        this.destino = destino;
        this.quantidade = quantidade;
        this.dataHora = LocalDateTime.now();
        this.status = StatusTransferencia.PENDENTE;
    }

    public Transferencia(TransferenciaId id, ProdutoId produto, EstoqueId origem, 
                        EstoqueId destino, double quantidade, LocalDateTime dataHora, 
                        StatusTransferencia status) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        notNull(origem, "Estoque de origem é obrigatório");
        notNull(destino, "Estoque de destino é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notNull(dataHora, "Data e hora são obrigatórias");
        notNull(status, "Status é obrigatório");

        this.id = id;
        this.produto = produto;
        this.origem = origem;
        this.destino = destino;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.status = status;
    }

    /**
     * Inicia uma transferência
     */
    public static Transferencia iniciarTransferencia(EstoqueId origem, EstoqueId destino, 
                                                    ProdutoId produto, double quantidade) {
        return new Transferencia(produto, origem, destino, quantidade);
    }

    /**
     * Conclui a transferência
     */
    public void concluirTransferencia() {
        if (status != StatusTransferencia.PENDENTE) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser concluídas");
        }
        this.status = StatusTransferencia.CONCLUIDA;
    }

    /**
     * Verifica se a transferência está pendente
     */
    public boolean isPendente() {
        return status == StatusTransferencia.PENDENTE;
    }

    /**
     * Verifica se a transferência foi concluída
     */
    public boolean isConcluida() {
        return status == StatusTransferencia.CONCLUIDA;
    }

    // Getters
    public TransferenciaId getId() { return id; }
    public ProdutoId getProduto() { return produto; }
    public EstoqueId getOrigem() { return origem; }
    public EstoqueId getDestino() { return destino; }
    public double getQuantidade() { return quantidade; }
    public LocalDateTime getDataHora() { return dataHora; }
    public StatusTransferencia getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Transferencia[%s] - Produto: %s, Origem: %s, Destino: %s, Quantidade: %.2f, Status: %s", 
                           id, produto, origem, destino, quantidade, status);
    }
}