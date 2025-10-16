package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.notBlank;

/**
 * Value Object: Código do produto.
 * Garante unicidade e validade sintática (R29).
 */
public class CodigoProduto {
    private final String valor;

    public CodigoProduto(String valor) {
        notBlank(valor, "Código do produto é obrigatório");
        this.valor = valor.trim().toUpperCase();
    }

    public String getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        return o instanceof CodigoProduto other && valor.equals(other.valor);
    }

    @Override
    public int hashCode() { return valor.hashCode(); }

    @Override
    public String toString() { return valor; }
}
