package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;
import java.util.Objects;
import java.util.regex.Pattern;

public class CpfCnpj {
	private final String documento;

	private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$");
	private static final Pattern CNPJ_PATTERN = Pattern.compile("^\\d{14}$");

	public CpfCnpj(String documento) {
		notNull(documento, "O documento não pode ser nulo");
		notBlank(documento, "O documento não pode estar em branco");
		
		// Remove formatação (pontos, traços, barras)
		String documentoLimpo = documento.replaceAll("[^0-9]", "");
		
		boolean isCpf = CPF_PATTERN.matcher(documentoLimpo).matches();
		boolean isCnpj = CNPJ_PATTERN.matcher(documentoLimpo).matches();
		
		if (!isCpf && !isCnpj) {
			throw new IllegalArgumentException("Documento inválido. Deve ser CPF (11 dígitos) ou CNPJ (14 dígitos)");
		}

		this.documento = documentoLimpo;
	}

	public String getDocumento() {
		return documento;
	}

	public boolean isCpf() {
		return documento.length() == 11;
	}

	public boolean isCnpj() {
		return documento.length() == 14;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CpfCnpj) {
			var cpfCnpj = (CpfCnpj) obj;
			return documento.equals(cpfCnpj.documento);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(documento);
	}

	@Override
	public String toString() {
		return documento;
	}
}

