package dev.gestock.sge.dominio.principal.custo;

import dev.gestock.sge.dominio.principal.pedido.PedidoId;

import java.math.BigDecimal;
import java.util.*;
import static org.apache.commons.lang3.Validate.*;

/**
 * Aggregate Root: Custo
 *
 * Responsável por consolidar os custos associados a um Pedido.
 *
 * Regras cobertas:
 * - R26: custo total = valor produtos + frete + custos logísticos.
 * - R19: custo médio do estoque é atualizado conforme entradas.
 * - R22: rastreabilidade financeira de ajustes e perdas.
 */
public class Custo {

    private final CustoId id;
    private final PedidoId pedidoId;              // vinculado a um pedido
    private final List<CustoItem> itens;          // custos por produto/tipo
    private BigDecimal total;                     // total consolidado

    public Custo(PedidoId pedidoId) {
        notNull(pedidoId, "Pedido é obrigatório");
        this.id = new CustoId();
        this.pedidoId = pedidoId;
        this.itens = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // ---------------- Métodos de domínio ----------------

    /** Adiciona um custo de item ao registro. */
    public void adicionarItem(CustoItem item) {
        notNull(item, "Item de custo é obrigatório");
        itens.add(item);
        recalcularTotal();
    }

    /** Remove um custo específico. */
    public void removerItem(CustoItem item) {
        itens.remove(item);
        recalcularTotal();
    }

    /** Recalcula o total geral. */
    private void recalcularTotal() {
        this.total = itens.stream()
                .map(CustoItem::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Retorna todos os custos de um tipo específico. */
    public List<CustoItem> filtrarPorTipo(TipoCusto tipo) {
        return itens.stream()
                .filter(i -> i.getTipo() == tipo)
                .toList();
    }

    /** (R26) Obtém o custo total (produtos + frete + logística). */
    public BigDecimal getTotal() {
        return total;
    }

    /** (R19) Calcula o custo médio unitário por produto. */
    public BigDecimal calcularCustoMedioPorProduto() {
        if (itens.isEmpty()) return BigDecimal.ZERO;
        BigDecimal soma = itens.stream()
                .filter(i -> i.getTipo() == TipoCusto.PRODUTO)
                .map(CustoItem::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(itens.size()), 2, BigDecimal.ROUND_HALF_UP);
    }

    // ---------------- Getters ----------------
    public CustoId getId() { return id; }
    public PedidoId getPedidoId() { return pedidoId; }
    public List<CustoItem> getItens() { return Collections.unmodifiableList(itens); }

    @Override
    public String toString() {
        return String.format("Custo %s | Pedido: %s | Total: R$%.2f", id, pedidoId, total);
    }
}
