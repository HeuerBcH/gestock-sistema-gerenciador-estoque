package dev.gestock.sge.dominio.pedido;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDate;
import java.util.Objects;

public class DataPedido {
	private final LocalDate valor;

	public DataPedido(LocalDate valor) {
		notNull(valor, "A data do pedido não pode ser nula");
		this.valor = valor;
	}

	public LocalDate getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataPedido) {
			var dataPedido = (DataPedido) obj;
			return valor.equals(dataPedido.valor);
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

