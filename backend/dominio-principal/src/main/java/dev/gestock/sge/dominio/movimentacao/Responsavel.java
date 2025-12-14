package dev.gestock.sge.dominio.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Responsavel {
	private final String valor;

	public Responsavel(String valor) {
		notNull(valor, "O responsável não pode ser nulo");
		notBlank(valor, "O responsável não pode estar em branco");
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Responsavel) {
			var responsavel = (Responsavel) obj;
			return valor.equals(responsavel.valor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor;
	}
}

