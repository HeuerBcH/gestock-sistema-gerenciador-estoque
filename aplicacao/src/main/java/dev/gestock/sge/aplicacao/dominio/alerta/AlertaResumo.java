package dev.gestock.sge.aplicacao.dominio.alerta;

import java.time.LocalDateTime;

import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public interface AlertaResumo {
	AlertaId getId();

	ProdutoId getProdutoId();

	EstoqueId getEstoqueId();

	LocalDateTime getDataGeracao();

	FornecedorId getFornecedorSugerido();

	boolean isAtivo();
}
