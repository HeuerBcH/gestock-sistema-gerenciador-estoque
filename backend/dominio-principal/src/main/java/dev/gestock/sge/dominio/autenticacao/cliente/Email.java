package dev.gestock.sge.dominio.autenticacao.cliente;

import static org.apache.commons.lang3.Validate.*;
import org.apache.commons.validator.routines.EmailValidator;
import java.util.Objects;

public class Email {
	private final String endereco;

	public Email(String endereco) {
		notNull(endereco, "O endereço de e-mail não pode ser nulo");
		notBlank(endereco, "O endereço de e-mail não pode estar em branco");
		
		boolean passou = EmailValidator.getInstance().isValid(endereco);
		if (!passou) {
			throw new IllegalArgumentException("Endereço de e-mail inválido");
		}

		this.endereco = endereco;
	}

	public String getEndereco() {
		return endereco;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Email) {
			var email = (Email) obj;
			return endereco.equals(email.endereco);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(endereco);
	}

	@Override
	public String toString() {
		return endereco;
	}
}

