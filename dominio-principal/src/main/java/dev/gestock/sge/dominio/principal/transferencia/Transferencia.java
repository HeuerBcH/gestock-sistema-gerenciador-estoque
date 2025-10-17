package dev.gestock.sge.dominio.principal.transferencia;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;

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
    private ClienteId cliente;

    public Transferencia(ProdutoId produto, EstoqueId origem, EstoqueId destino, 
                        double quantidade, ClienteId cliente) {
        notNull(produto, "Produto é obrigatório");
        notNull(origem, "Estoque de origem é obrigatório");
        notNull(destino, "Estoque de destino é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notNull(cliente, "Cliente é obrigatório");

        this.id = new TransferenciaId();
        this.produto = produto;
        this.origem = origem;
        this.destino = destino;
        this.quantidade = quantidade;
        this.dataHora = LocalDateTime.now();
        this.status = StatusTransferencia.PENDENTE;
        this.cliente = cliente;
    }

    public Transferencia(TransferenciaId id, ProdutoId produto, EstoqueId origem, 
                        EstoqueId destino, double quantidade, LocalDateTime dataHora, 
                        StatusTransferencia status, ClienteId cliente) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        notNull(origem, "Estoque de origem é obrigatório");
        notNull(destino, "Estoque de destino é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notNull(dataHora, "Data e hora são obrigatórias");
        notNull(status, "Status é obrigatório");
        notNull(cliente, "Cliente é obrigatório");

        this.id = id;
        this.produto = produto;
        this.origem = origem;
        this.destino = destino;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.status = status;
        this.cliente = cliente;
    }

    /**
     * Inicia uma transferência
     * R1H22: A transferência só pode ocorrer entre estoques pertencentes ao mesmo cliente
     * R2H22: O estoque de origem deve possuir quantidade suficiente do produto para efetuar a transferência
     */
    public static Transferencia iniciarTransferencia(EstoqueId origem, EstoqueId destino, 
                                                    ProdutoId produto, double quantidade, 
                                                    ClienteId cliente) {
        // R1H22: Validação seria feita pelo serviço de domínio
        // R2H22: Validação seria feita pelo serviço de domínio
        return new Transferencia(produto, origem, destino, quantidade, cliente);
    }

    /**
     * Conclui a transferência
     * R3H22: Ao realizar a transferência, o sistema deve registrar automaticamente uma movimentação de saída no estoque de origem e uma movimentação de entrada no estoque de destino
     */
    public void concluirTransferencia() {
        if (status != StatusTransferencia.PENDENTE) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser concluídas");
        }
        this.status = StatusTransferencia.CONCLUIDA;
        // R3H22: A geração das movimentações seria feita pelo serviço de domínio
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
    public ClienteId getCliente() { return cliente; }

    @Override
    public String toString() {
        return String.format("Transferencia[%s] - Produto: %s, Origem: %s, Destino: %s, Quantidade: %.2f, Status: %s", 
                           id, produto, origem, destino, quantidade, status);
    }
}