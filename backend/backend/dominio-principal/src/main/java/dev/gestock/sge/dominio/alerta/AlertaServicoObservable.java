package dev.gestock.sge.dominio.alerta;

import static org.apache.commons.lang3.Validate.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Versão observável do AlertaServico que notifica observadores quando alertas são gerados.
 * 
 * Pattern: Observer
 * Funcionalidade: Emitir Alertas de Estoque Baixo (RODRIGO)
 */
public class AlertaServicoObservable {
	private final AlertaServico servico;
	private final List<AlertaObserver> observadores;

	public AlertaServicoObservable(AlertaServico servico) {
		notNull(servico, "O serviço não pode ser nulo");
		this.servico = servico;
		this.observadores = new ArrayList<>();
	}

	/**
	 * Adiciona um observador à lista.
	 */
	public void adicionarObservador(AlertaObserver observador) {
		notNull(observador, "O observador não pode ser nulo");
		observadores.add(observador);
	}

	/**
	 * Remove um observador da lista.
	 */
	public void removerObservador(AlertaObserver observador) {
		observadores.remove(observador);
	}

	/**
	 * Determina o nível do alerta e notifica os observadores se um alerta for gerado.
	 */
	public NivelAlerta determinarNivelENotificar(PercentualAbaixoRop percentual, int produtoId,
			String produtoNome, int estoqueId, String estoqueNome, int quantidadeAtual, int rop) {
		
		NivelAlerta nivel = servico.determinarNivel(percentual);
		
		// Se um alerta foi gerado, notificar os observadores
		if (nivel != null) {
			AlertaInfo alertaInfo = new AlertaInfo(produtoId, produtoNome, estoqueId, estoqueNome,
					quantidadeAtual, rop, percentual.getValor(), nivel);
			notificarObservadores(alertaInfo);
		}
		
		return nivel;
	}

	/**
	 * Notifica todos os observadores sobre um alerta gerado.
	 */
	private void notificarObservadores(AlertaInfo alertaInfo) {
		for (AlertaObserver observador : observadores) {
			try {
				observador.notificarAlertaGerado(alertaInfo);
			} catch (Exception e) {
				// Log do erro sem interromper a notificação dos outros observadores
				System.err.println("Erro ao notificar observador: " + e.getMessage());
			}
		}
	}
}

