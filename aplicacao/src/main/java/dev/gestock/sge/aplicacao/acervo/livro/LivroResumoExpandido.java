package dev.gestock.sge.aplicacao.acervo.livro;

import dev.gestock.sge.aplicacao.acervo.autor.AutorResumo;

public interface LivroResumoExpandido {
	LivroResumo getLivro();

	AutorResumo getAutor();

	int getExemplaresDisponiveis();

	int getTotalExemplares();
}