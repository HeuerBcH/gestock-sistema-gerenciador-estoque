package dev.sauloaraujo.sgb.aplicacao.analise;

import dev.sauloaraujo.dominio.analise.emprestimo.EmprestimoRegistro;
import dev.gestock.sge.dominio.principal.estoque.Emprestimo;
import dev.gestock.sge.dominio.principal.estoque.ExemplarId;

public interface EmprestimoRegistroRepositorioAplicacao {
	EmprestimoRegistro buscar(ExemplarId exemplar, Emprestimo emprestimo);
}