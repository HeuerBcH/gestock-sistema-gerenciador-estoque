package dev.gestock.sge.dominio.movimentacao;

import java.time.LocalDate;
import java.util.List;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

public interface MovimentacaoRepositorio {
	Movimentacao salvar(Movimentacao movimentacao);

	Movimentacao obter(MovimentacaoId id);

	List<Movimentacao> obterPorPeriodo(LocalDate inicio, LocalDate fim);

	List<Movimentacao> obterPorTipo(TipoMovimentacao tipo);

	void remover(MovimentacaoId id);
}

