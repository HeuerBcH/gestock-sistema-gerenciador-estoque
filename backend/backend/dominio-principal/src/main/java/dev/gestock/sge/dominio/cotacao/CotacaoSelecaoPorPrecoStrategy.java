package dev.gestock.sge.dominio.cotacao;

import java.util.Comparator;
import java.util.List;

/**
 * Estratégia que seleciona a cotação com menor preço.
 * 
 * Pattern: Strategy
 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
 */
public class CotacaoSelecaoPorPrecoStrategy implements CotacaoSelecaoStrategy {

	@Override
	public Cotacao selecionar(List<Cotacao> cotacoes) {
		if (cotacoes == null || cotacoes.isEmpty()) {
			return null;
		}

		return cotacoes.stream()
			.min(Comparator.comparing(c -> c.getPreco().getValor()))
			.orElse(null);
	}
}

