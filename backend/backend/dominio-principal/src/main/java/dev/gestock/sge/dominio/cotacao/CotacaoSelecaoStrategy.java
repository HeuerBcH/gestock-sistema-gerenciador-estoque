package dev.gestock.sge.dominio.cotacao;

import java.util.List;

/**
 * Interface Strategy para diferentes algoritmos de seleção de cotação.
 * 
 * Pattern: Strategy
 * Funcionalidade: Selecionar Cotação Mais Vantajosa (DUDU)
 */
public interface CotacaoSelecaoStrategy {
	/**
	 * Seleciona a melhor cotação de acordo com a estratégia implementada.
	 * 
	 * @param cotacoes Lista de cotações disponíveis
	 * @return A cotação selecionada ou null se não houver cotações
	 */
	Cotacao selecionar(List<Cotacao> cotacoes);
}

