package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDate;
import java.util.Objects;

public class DataPrevista {
	private final LocalDate valor;

	public DataPrevista(LocalDate valor) {
		notNull(valor, "A data prevista não pode ser nula");
		this.valor = valor;
	}

	public LocalDate getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataPrevista) {
			var dataPrevista = (DataPrevista) obj;
			return valor.equals(dataPrevista.valor);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(valor);
	}

	@Override
	public String toString() {
		return valor.toString();
	}
}

