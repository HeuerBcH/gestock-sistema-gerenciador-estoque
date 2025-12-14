package dev.gestock.sge.dominio.cotacao;

import java.util.Comparator;
import java.util.List;

/**
 * Estratégia completa que seleciona a cotação considerando:
 * 1. Preço (menor)
 * 2. Lead time (menor)
 * 3. Validade (ATIVA primeiro)
 * 4. ID (ordem original)
 * 
 * Esta é a estratégia padrão usada no sistema.
 * 
 * Pattern: Strategy
 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
 */
public class CotacaoSelecaoCompletaStrategy implements CotacaoSelecaoStrategy {

	@Override
	public Cotacao selecionar(List<Cotacao> cotacoes) {
		if (cotacoes == null || cotacoes.isEmpty()) {
			return null;
		}

		// Ordenar por: preço (menor), lead time (menor), validade (ATIVA primeiro), ordem original
		return cotacoes.stream()
			.sorted(Comparator
				.comparing((Cotacao c) -> c.getPreco().getValor())
				.thenComparing(c -> c.getLeadTime().getDias())
				.thenComparing(c -> c.getValidade() == Validade.EXPIRADA ? 1 : 0)
				.thenComparing(c -> c.getId().getId()))
			.findFirst()
			.orElse(null);
	}
}

