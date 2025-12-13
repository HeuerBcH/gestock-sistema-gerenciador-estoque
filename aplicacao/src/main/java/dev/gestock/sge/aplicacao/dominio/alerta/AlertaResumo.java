package dev.gestock.sge.aplicacao.dominio.alerta;

import java.time.LocalDateTime;

public interface AlertaResumo {
	Long getId();

	Long getProdutoId();

	Long getEstoqueId();

	LocalDateTime getDataGeracao();

	Long getFornecedorSugeridoId();

	boolean isAtivo();
}
