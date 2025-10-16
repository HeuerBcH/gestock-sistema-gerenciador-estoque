package dev.gestock.sge.dominio.principal.pedido;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Aggregate Root: Pedido (ordem de compra Cliente → Fornecedor).
 * Regras:
 *  - R5/R6: seleção de cotação (aplicada via serviço e/ou fornecedor)
 *  - R7: validação (ex.: quantidade mínima)
 *  - R8: recebimento dispara entrada no estoque (feito pela aplicação/orquestração)
 *  - R26: custo total (itens + frete + logística)
 */
public class Pedido {

    private final PedidoId id;
    private final ClienteId clienteId;
    private final FornecedorId fornecedorId;
    private final LocalDate dataCriacao;

    private final List<ItemPedido> itens = new ArrayList<>();
    private CustoPedido custo;               // VO com total do pedido (R26)
    private StatusPedido status = StatusPedido.CRIADO;

    public Pedido(ClienteId clienteId, FornecedorId fornecedorId) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(fornecedorId, "Fornecedor é obrigatório");
        this.id = new PedidoId();
        this.clienteId = clienteId;
        this.fornecedorId = fornecedorId;
        this.dataCriacao = LocalDate.now();
    }

    // --------- Comportamentos de domínio ---------

    /** (R7) Adiciona um item validando quantidade e preço. */
    public void adicionarItem(ItemPedido item) {
        notNull(item, "Item é obrigatório");
        itens.add(item);
    }

    /** (R26) Registra o custo total do pedido (itens + frete + logística). */
    public void registrarCusto(CustoPedido custo) {
        notNull(custo, "Custo é obrigatório");
        this.custo = custo;
    }

    /** Envia o pedido ao fornecedor (transição CRIADO → ENVIADO). */
    public void enviar() {
        isTrue(status == StatusPedido.CRIADO, "Só é possível enviar pedidos em estado CRIADO");
        isTrue(!itens.isEmpty(), "Pedido sem itens não pode ser enviado");
        this.status = StatusPedido.ENVIADO;
    }

    /** (R8) Registra o recebimento do pedido (ENVIADO → RECEBIDO). */
    public void registrarRecebimento() {
        isTrue(status == StatusPedido.ENVIADO, "Somente pedidos ENVIADO podem ser recebidos");
        this.status = StatusPedido.RECEBIDO;
    }

    /** Cancela o pedido (qualquer estado exceto CONCLUIDO). */
    public void cancelar() {
        isTrue(status != StatusPedido.CONCLUIDO, "Pedido CONCLUIDO não pode ser cancelado");
        this.status = StatusPedido.CANCELADO;
    }

    /** Conclui o pedido (apenas após RECEBIDO). */
    public void concluir() {
        isTrue(status == StatusPedido.RECEBIDO, "Concluir só é permitido após o recebimento");
        this.status = StatusPedido.CONCLUIDO;
    }

    // --------- Consultas auxiliares ---------

    public BigDecimal calcularTotalItens() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --------- Getters imutáveis ---------

    public PedidoId getId(){ return id; }
    public ClienteId getClienteId(){ return clienteId; }
    public FornecedorId getFornecedorId(){ return fornecedorId; }
    public LocalDate getDataCriacao(){ return dataCriacao; }
    public List<ItemPedido> getItens(){ return Collections.unmodifiableList(itens); }
    public CustoPedido getCusto(){ return custo; }
    public StatusPedido getStatus(){ return status; }

    @Override public String toString() {
        return "Pedido " + id + " | " + status + " | Itens: " + itens.size() +
                " | TotalItens: " + calcularTotalItens() +
                " | Custo: " + (custo != null ? custo.getValorTotal() : "-");
    }
}
