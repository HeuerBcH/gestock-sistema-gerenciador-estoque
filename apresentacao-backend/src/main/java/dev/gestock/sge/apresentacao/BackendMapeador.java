package dev.gestock.sge.apresentacao;

import org.springframework.stereotype.Component;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;

@Component
public class BackendMapeador {

	@SuppressWarnings("unchecked")
	public <D> D map(Object source, Class<D> destinationType) {
		if (source == null) {
			return null;
		}

		// Long -> Value Object
		if (source instanceof Long longValue) {
			if (destinationType == EstoqueId.class) {
				return (D) new EstoqueId(longValue);
			}
			if (destinationType == ClienteId.class) {
				return (D) new ClienteId(longValue);
			}
		}

		// Value Object -> Long
		if (destinationType == Long.class) {
			if (source instanceof EstoqueId estoqueId) {
				return (D) Long.valueOf(estoqueId.getId());
			}
			if (source instanceof ClienteId clienteId) {
				return (D) Long.valueOf(clienteId.getId());
			}
		}

		throw new IllegalArgumentException("Tipo não suportado: " + source.getClass() + " -> " + destinationType);
	}
}
