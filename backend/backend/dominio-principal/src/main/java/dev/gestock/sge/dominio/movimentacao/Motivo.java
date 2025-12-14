package dev.gestock.sge.dominio.movimentacao;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;

public class Motivo {
	private final String valor;

	public Motivo(String valor) {
		notNull(valor, "O motivo não pode ser nulo");
		notBlank(valor, "O motivo não pode estar em branco");
		this.valor = valor;
	}

	public String getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Motivo) {
			var motivo = (Motivo) obj;
			return valor.equals(motivo.valor);
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

