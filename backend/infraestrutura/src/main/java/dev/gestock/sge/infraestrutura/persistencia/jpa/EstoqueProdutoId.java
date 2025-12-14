package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.io.Serializable;
import java.util.Objects;

class EstoqueProdutoId implements Serializable {
	int estoque;
	int produto;

	EstoqueProdutoId() {
	}

	EstoqueProdutoId(int estoque, int produto) {
		this.estoque = estoque;
		this.produto = produto;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof EstoqueProdutoId) {
			var other = (EstoqueProdutoId) obj;
			return estoque == other.estoque && produto == other.produto;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(estoque, produto);
	}
}

