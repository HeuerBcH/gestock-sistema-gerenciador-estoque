package dev.gestock.sge.dominio.cotacao;

import java.util.Comparator;
import java.util.List;

/**
 * Estratégia que seleciona a cotação com menor lead time.
 * 
 * Pattern: Strategy
 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
 */
public class CotacaoSelecaoPorLeadTimeStrategy implements CotacaoSelecaoStrategy {

	@Override
	public Cotacao selecionar(List<Cotacao> cotacoes) {
		if (cotacoes == null || cotacoes.isEmpty()) {
			return null;
		}

		return cotacoes.stream()
			.min(Comparator.comparing(c -> c.getLeadTime().getDias()))
			.orElse(null);
	}
}

