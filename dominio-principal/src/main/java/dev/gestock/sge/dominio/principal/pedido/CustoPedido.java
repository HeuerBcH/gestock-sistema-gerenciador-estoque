package src.main.java.dev.gestock.sge.dominio.principal.pedido;

import java.math.BigDecimal;
import static org.apache.commons.lang3.Validate.*;

/**
 * VO: Custo do pedido.
 * R26: custo final = valor dos itens + frete + custos logísticos.
 */
public class CustoPedido {
    private final BigDecimal valorItens;
    private final BigDecimal frete;
    private final BigDecimal custosLogisticos;

    public CustoPedido(BigDecimal valorItens, BigDecimal frete, BigDecimal custosLogisticos) {
        notNull(valorItens, "Valor dos itens é obrigatório");
        notNull(frete, "Frete é obrigatório");
        notNull(custosLogisticos, "Custos logísticos são obrigatórios");
        isTrue(valorItens.signum() >= 0, "Valor dos itens inválido");
        isTrue(frete.signum() >= 0, "Frete inválido");
        isTrue(custosLogisticos.signum() >= 0, "Custo logístico inválido");

        this.valorItens = valorItens;
        this.frete = frete;
        this.custosLogisticos = custosLogisticos;
    }

    public BigDecimal getValorItens(){ return valorItens; }
    public BigDecimal getFrete(){ return frete; }
    public BigDecimal getCustosLogisticos(){ return custosLogisticos; }
    public BigDecimal getValorTotal(){ return valorItens.add(frete).add(custosLogisticos); }

    @Override public String toString(){ return "Total: R$ " + getValorTotal(); }
}
