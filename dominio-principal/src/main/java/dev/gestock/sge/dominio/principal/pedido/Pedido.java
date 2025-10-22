package dev.gestock.sge.dominio.principal.pedido;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

/* Aggregate Root: Pedido (ordem de compra Cliente → Fornecedor).
    Regras:
    - R5/R6: seleção de cotação (aplicada via serviço e/ou fornecedor)
    - R7: validação (ex.: quantidade mínima)
    - R8: recebimento dispara entrada no estoque (feito pela aplicação/orquestração)
    - R26: custo total (itens + frete + logística) */
public class Pedido {

    private final PedidoId id;
    private final ClienteId clienteId;
    private final FornecedorId fornecedorId;
    private final LocalDate dataCriacao;
    private LocalDate dataPrevistaEntrega; // R2H11
    private EstoqueId estoqueId; // Estoque onde será recebido/reservado (H24)

    private final List<ItemPedido> itens = new ArrayList<>();
    private CustoPedido custo; // Total do pedido (R26)
    private StatusPedido status = StatusPedido.CRIADO;

    public Pedido(PedidoId id, ClienteId clienteId, FornecedorId fornecedorId) {
        notNull(id, "ID é obrigatório");
        notNull(clienteId, "Cliente é obrigatório");
        notNull(fornecedorId, "Fornecedor é obrigatório");
        this.id = id;
        this.clienteId = clienteId;
        this.fornecedorId = fornecedorId;
        this.dataCriacao = LocalDate.now();
        this.dataPrevistaEntrega = null;
        this.estoqueId = null;
    }

    /* (R7) Adiciona um item validando quantidade e preço. */
    public void adicionarItem(ItemPedido item) {
        notNull(item, "Item é obrigatório");
        itens.add(item);
    }

    /* (R26) Registra o custo total do pedido. */
    public void registrarCusto(CustoPedido custo) {
        notNull(custo, "Custo é obrigatório");
        this.custo = custo;
    }

    /* Envia o pedido ao fornecedor (transição CRIADO → ENVIADO). */
    public void enviar() {
        isTrue(status == StatusPedido.CRIADO, "Só é possível enviar pedidos em estado CRIADO");
        isTrue(!itens.isEmpty(), "Pedido sem itens não pode ser enviado");
        this.status = StatusPedido.ENVIADO;
    }

    /* (R8) Registra o recebimento do pedido (ENVIADO → RECEBIDO). */
    public void registrarRecebimento() {
        isTrue(status == StatusPedido.ENVIADO, "Somente pedidos ENVIADO podem ser recebidos");
        this.status = StatusPedido.RECEBIDO;
    }

    /* Inicia o transporte do pedido (ENVIADO → EM_TRANSPORTE). */
    public void iniciarTransporte() {
        isTrue(status == StatusPedido.ENVIADO, "Somente pedidos ENVIADO podem iniciar transporte");
        this.status = StatusPedido.EM_TRANSPORTE;
    }

    /* Cancela o pedido (exceto quando EM_TRANSPORTE e CONCLUIDO) - R1H12. */
    public void cancelar() {
        isTrue(status != StatusPedido.CONCLUIDO, "Pedido CONCLUIDO não pode ser cancelado");
        isTrue(status != StatusPedido.EM_TRANSPORTE, "Pedido EM TRANSPORTE não pode ser cancelado");
        this.status = StatusPedido.CANCELADO;
    }

    /* Conclui o pedido (apenas após RECEBIDO). */
    public void concluir() {
        isTrue(status == StatusPedido.RECEBIDO, "Concluir só é permitido após o recebimento");
        this.status = StatusPedido.CONCLUIDO;
    }

    public BigDecimal calcularTotalItens() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters

    public PedidoId getId(){ return id; }
    public ClienteId getClienteId(){ return clienteId; }
    public FornecedorId getFornecedorId(){ return fornecedorId; }
    public LocalDate getDataCriacao(){ return dataCriacao; }
    public List<ItemPedido> getItens(){ return Collections.unmodifiableList(itens); }
    public CustoPedido getCusto(){ return custo; }
    public StatusPedido getStatus(){ return status; }
    public LocalDate getDataPrevistaEntrega() { return dataPrevistaEntrega; }
    public Optional<EstoqueId> getEstoqueId(){ return Optional.ofNullable(estoqueId); }

    // Setters usados por PedidoServico

    public void setDataPrevistaEntrega(LocalDate data) { this.dataPrevistaEntrega = data; }
    public void setEstoqueId(EstoqueId estoqueId) { this.estoqueId = estoqueId; }

    /* Calcula o peso total do pedido somando o peso de cada item.
       Requer um provider que informe o peso por unidade de cada ProdutoId. */
    public double calcularPesoTotal(Function<ProdutoId, Double> pesoPorUnidadeProvider) {
        notNull(pesoPorUnidadeProvider, "Provider de peso é obrigatório");
        double total = 0.0d;
        for (ItemPedido item : itens) {
            Double pesoUnit = pesoPorUnidadeProvider.apply(item.getProdutoId());
            isTrue(pesoUnit != null && pesoUnit > 0, "Peso por unidade inválido");
            total += item.calcularPesoTotal(pesoUnit);
        }
        return total;
    }

    @Override public String toString() {
        return "Pedido " + id + " | " + status + " | Itens: " + itens.size() +
                " | TotalItens: " + calcularTotalItens() +
                " | Custo: " + (custo != null ? custo.getValorTotal() : "-");
    }
}
