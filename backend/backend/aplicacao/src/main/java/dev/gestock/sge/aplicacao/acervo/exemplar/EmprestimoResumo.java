package dev.gestock.sge.aplicacao.acervo.exemplar;

import dev.gestock.sge.aplicacao.administracao.socio.SocioResumo;

public interface EmprestimoResumo {
	PeriodoResumo getPeriodo();

	SocioResumo getTomador();
}