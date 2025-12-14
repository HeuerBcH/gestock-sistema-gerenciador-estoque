package dev.gestock.sge.dominio.alerta;

/**
 * Interface para observadores de alertas.
 * 
 * Pattern: Observer
 * Funcionalidade: Emitir Alertas de Estoque Baixo (RODRIGO)
 */
public interface AlertaObserver {
	/**
	 * Notifica o observador quando um alerta é gerado.
	 * 
	 * @param alertaInfo Informações do alerta gerado
	 */
	void notificarAlertaGerado(AlertaInfo alertaInfo);
}

