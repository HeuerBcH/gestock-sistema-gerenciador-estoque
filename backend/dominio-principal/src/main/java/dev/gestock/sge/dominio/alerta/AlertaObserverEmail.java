package dev.gestock.sge.dominio.alerta;

/**
 * Observador concreto que envia notificações por email quando alertas críticos são gerados.
 * 
 * Pattern: Observer
 * Funcionalidade: Emitir Alertas de Estoque Baixo (RODRIGO)
 */
public class AlertaObserverEmail implements AlertaObserver {

	@Override
	public void notificarAlertaGerado(AlertaInfo alertaInfo) {
		// Enviar email apenas para alertas críticos
		if (alertaInfo.getNivel() == NivelAlerta.CRITICO) {
			String assunto = "ALERTA CRÍTICO: Estoque Baixo - " + alertaInfo.getProdutoNome();
			String mensagem = String.format(
				"Produto: %s (ID: %d)\n" +
				"Estoque: %s (ID: %d)\n" +
				"Quantidade Atual: %d\n" +
				"ROP: %d\n" +
				"Percentual Abaixo do ROP: %.2f%%\n" +
				"Nível: %s",
				alertaInfo.getProdutoNome(), alertaInfo.getProdutoId(),
				alertaInfo.getEstoqueNome(), alertaInfo.getEstoqueId(),
				alertaInfo.getQuantidadeAtual(),
				alertaInfo.getRop(),
				alertaInfo.getPercentualAbaixoRop(),
				alertaInfo.getNivel()
			);
			
			// Em produção, usar um serviço de email real
			System.out.println("[EMAIL-ALERTA] Enviando email:");
			System.out.println("Assunto: " + assunto);
			System.out.println("Mensagem:\n" + mensagem);
		}
	}
}

