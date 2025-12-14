package dev.gestock.sge.dominio.transferencia;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class DataHoraTransferencia {
	private final LocalDateTime valor;

	public DataHoraTransferencia(LocalDateTime valor) {
		notNull(valor, "A data/hora da transferência não pode ser nula");
		this.valor = valor;
	}

	public LocalDateTime getValor() {
		return valor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof DataHoraTransferencia) {
			var dataHoraTransferencia = (DataHoraTransferencia) obj;
			return valor.equals(dataHoraTransferencia.valor);
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

