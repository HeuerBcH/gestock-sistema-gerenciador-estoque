package dev.gestock.sge.dominio.principal.estoque;

/* Value Object: Saldo de um produto dentro do Estoque.
   fisico: quantidade existente no estoque.
   reservado: quantidade comprometida (pedidos em andamento).
   disponível = fisico - reservado (R15).

   Todas as operações retornam NOVAS instâncias (imutabilidade lógica) */
public record SaldoProduto(int fisico, int reservado) {

    public static SaldoProduto zero() {
        return new SaldoProduto(0, 0);
    }

    public SaldoProduto {
        if (fisico < 0) throw new IllegalArgumentException("Saldo físico não pode ser negativo");
        if (reservado < 0) throw new IllegalArgumentException("Saldo reservado não pode ser negativo");
        if (reservado > fisico) throw new IllegalArgumentException("Reservado não pode exceder o físico");
    }

    public int disponivel() {
        return fisico - reservado;
    }

    public SaldoProduto comEntrada(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        return new SaldoProduto(fisico + qtd, reservado);
    }

    public SaldoProduto comSaida(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (disponivel() < qtd) throw new IllegalStateException("Saldo disponível insuficiente");
        return new SaldoProduto(fisico - qtd, reservado);
    }

    public SaldoProduto comReserva(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (disponivel() < qtd) throw new IllegalStateException("Saldo disponível insuficiente para reserva");
        return new SaldoProduto(fisico, reservado + qtd);
    }

    public SaldoProduto comLiberacao(int qtd) {
        if (qtd <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");
        if (reservado < qtd) throw new IllegalStateException("Quantidade a liberar excede o reservado");
        return new SaldoProduto(fisico, reservado - qtd);
    }
}

