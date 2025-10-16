package dev.gestock.sge.aplicacao.acervo.exemplar;

import dev.gestock.sge.aplicacao.acervo.livro.LivroResumo;

public interface ExemplarResumoExpandido extends ExemplarResumo {
	String getId();

	LivroResumo getLivro();

	EmprestimoResumo getEmprestimo();
}