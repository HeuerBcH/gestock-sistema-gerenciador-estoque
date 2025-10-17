package dev.gestock.sge.dominio.principal.pedido;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

import java.time.LocalDate;
import java.util.List;

/**
 * Aggregate Root: Pedido
 *
 * Responsabilidades:
 * - Gerenciar pedidos de compra
 * - Controlar status do pedido
 * - Gerenciar itens do pedido
 * - Controlar datas de entrega
 */
public class Pedido {

    private final PedidoId id;
    private ClienteId cliente;
    private FornecedorId fornecedor;
    private LocalDate dataCriacao;
    private LocalDate dataPrevistaEntrega;
    private PedidoStatus status;
    private List<ItemPedido> itens;

    public Pedido(ClienteId cliente, FornecedorId fornecedor, List<ItemPedido> itens) {
        notNull(cliente, "Cliente é obrigatório");
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(itens, "Lista de itens é obrigatória");
        isTrue(!itens.isEmpty(), "Pedido deve ter pelo menos um item");

        this.id = new PedidoId();
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.dataCriacao = LocalDate.now();
        this.dataPrevistaEntrega = null; // Será definida pelo serviço
        this.status = PedidoStatus.PENDENTE;
        this.itens = List.copyOf(itens);
    }

    public Pedido(PedidoId id, ClienteId cliente, FornecedorId fornecedor, 
                  LocalDate dataCriacao, LocalDate dataPrevistaEntrega, 
                  PedidoStatus status, List<ItemPedido> itens) {
        notNull(id, "ID é obrigatório");
        notNull(cliente, "Cliente é obrigatório");
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(dataCriacao, "Data de criação é obrigatória");
        notNull(status, "Status é obrigatório");
        notNull(itens, "Lista de itens é obrigatória");

        this.id = id;
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.dataCriacao = dataCriacao;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.status = status;
        this.itens = List.copyOf(itens);
    }

    /**
     * Cria um pedido
     * R1H11: O pedido só pode ser criado se existir uma cotação válida para o produto
     */
    public void criarPedido(ClienteId cliente, FornecedorId fornecedor, List<ItemPedido> itens) {
        notNull(cliente, "Cliente é obrigatório");
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(itens, "Lista de itens é obrigatória");
        isTrue(!itens.isEmpty(), "Pedido deve ter pelo menos um item");

        // R1H11: Validação de cotação válida seria feita pelo serviço de domínio
        // Aqui apenas registramos que o pedido foi criado
        this.cliente = cliente;
        this.fornecedor = fornecedor;
        this.dataCriacao = LocalDate.now();
        this.status = PedidoStatus.PENDENTE;
        this.itens = List.copyOf(itens);
    }

    /**
     * Atualiza o status do pedido
     */
    public void atualizarStatus(PedidoStatus novoStatus) {
        notNull(novoStatus, "Status é obrigatório");
        this.status = novoStatus;
    }

    /**
     * Cancela o pedido
     * R1H12: Pedidos com status "Em transporte" não podem ser cancelados
     */
    public void cancelar() {
        if (status == PedidoStatus.EM_TRANSPORTE) {
            throw new IllegalStateException("Pedidos em transporte não podem ser cancelados");
        }
        this.status = PedidoStatus.CANCELADO;
    }

    /**
     * Define a data prevista de entrega
     */
    public void definirDataPrevistaEntrega(LocalDate dataPrevistaEntrega) {
        notNull(dataPrevistaEntrega, "Data prevista de entrega é obrigatória");
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    /**
     * Confirma o recebimento do pedido
     * R1H13: Ao confirmar o recebimento, o sistema gera automaticamente uma movimentação de entrada
     */
    public void confirmarRecebimento() {
        if (status != PedidoStatus.EM_TRANSPORTE) {
            throw new IllegalStateException("Apenas pedidos em transporte podem ser confirmados como recebidos");
        }
        this.status = PedidoStatus.RECEBIDO;
        // R1H13: A geração da movimentação seria feita pelo serviço de domínio
    }

    /**
     * Verifica se o pedido está pendente
     */
    public boolean isPendente() {
        return status == PedidoStatus.PENDENTE;
    }

    /**
     * Verifica se o pedido está em transporte
     */
    public boolean isEmTransporte() {
        return status == PedidoStatus.EM_TRANSPORTE;
    }

    /**
     * Verifica se o pedido foi recebido
     */
    public boolean isRecebido() {
        return status == PedidoStatus.RECEBIDO;
    }

    /**
     * Verifica se o pedido foi cancelado
     */
    public boolean isCancelado() {
        return status == PedidoStatus.CANCELADO;
    }

    // Getters
    public PedidoId getId() { return id; }
    public ClienteId getCliente() { return cliente; }
    public FornecedorId getFornecedor() { return fornecedor; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public PedidoStatus getStatus() { return status; }
    public List<ItemPedido> getItens() { return List.copyOf(itens); }

    @Override
    public String toString() {
        return String.format("Pedido[%s] - Cliente: %s, Fornecedor: %s, Status: %s", 
                           id, cliente, fornecedor, status);
    }
}