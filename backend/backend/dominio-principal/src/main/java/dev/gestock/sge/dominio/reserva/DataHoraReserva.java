package dev.gestock.sge.dominio.reserva;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class DataHoraReserva {
	private final LocalDateTime valor;

	public DataHoraReserva(LocalDateTime valor) {
		notNull(valor, "A data/hora da reserva não pode ser nula");
		this.valor = valor;
	}

	public LocalDateTime getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataHoraReserva) {
			var dataHoraReserva = (DataHoraReserva) obj;
			return valor.equals(dataHoraReserva.valor);
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

