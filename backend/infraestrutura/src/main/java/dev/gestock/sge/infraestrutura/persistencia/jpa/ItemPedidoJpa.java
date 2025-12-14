package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import jakarta.persistence.Embeddable;

@Embeddable
class ItemPedidoJpa {
	int produtoId;
	int quantidade;
	BigDecimal precoUnitario;
}

