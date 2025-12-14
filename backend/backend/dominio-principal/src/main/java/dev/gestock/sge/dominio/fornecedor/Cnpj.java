package dev.gestock.sge.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class Cnpj {
	private final String numero;

	private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{14}$");

	public Cnpj(String numero) {
		notNull(numero, "O CNPJ não pode ser nulo");
		notBlank(numero, "O CNPJ não pode estar em branco");
		
		// Remove formatação (pontos, traços, barras)
		String numeroLimpo = numero.replaceAll("[^0-9]", "");
		
		if (!CNPJ_PATTERN.matcher(numeroLimpo).matches()) {
			throw new IllegalArgumentException("CNPJ inválido. Deve ter 14 dígitos");
		}

		this.numero = numeroLimpo;
	}

	public String getNumero() {
		return numero;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Cnpj) {
			var cnpj = (Cnpj) obj;
			return numero.equals(cnpj.numero);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(numero);
	}

	@Override
	public String toString() {
		return numero;
	}
}

