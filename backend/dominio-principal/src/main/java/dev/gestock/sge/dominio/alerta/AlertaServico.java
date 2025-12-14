package dev.gestock.sge.dominio.alerta;

import static org.apache.commons.lang3.Validate.*;

public class AlertaServico {
	
	/**
	 * Determina o nível do alerta baseado na porcentagem abaixo do ROP.
	 * 
	 * @param percentual Percentual abaixo do ROP (deve ser negativo, ex: -60.0 para 60% abaixo)
	 * @return Nível do alerta ou null se não deve gerar alerta
	 */
	public NivelAlerta determinarNivel(PercentualAbaixoRop percentual) {
		notNull(percentual, "O percentual não pode ser nulo");
		
		var valor = percentual.getValor();
		
		// Se percentual > -20%, não gera alerta (produto ainda está próximo do ROP)
		if (valor > -20.0) {
			return null;
		}
		
		// CRITICO: >= 60% abaixo do ROP (percentual <= -60%)
		if (valor <= -60.0) {
			return NivelAlerta.CRITICO;
		}
		
		// ALTO: 40-59% abaixo do ROP (percentual entre -59% e -40%)
		if (valor <= -40.0) {
			return NivelAlerta.ALTO;
		}
		
		// MEDIO: 20-39% abaixo do ROP (percentual entre -39% e -20%)
		return NivelAlerta.MEDIO;
	}
}

