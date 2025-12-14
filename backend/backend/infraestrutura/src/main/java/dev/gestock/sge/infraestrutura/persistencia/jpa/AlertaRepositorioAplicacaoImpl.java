package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.alerta.AlertaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.alerta.AlertaResumo;
import dev.gestock.sge.aplicacao.alerta.AlertaTotais;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoResumo;
import dev.gestock.sge.dominio.alerta.AlertaServico;
import dev.gestock.sge.dominio.alerta.NivelAlerta;
import dev.gestock.sge.dominio.alerta.PercentualAbaixoRop;

@Repository
class AlertaRepositorioAplicacaoImpl implements AlertaRepositorioAplicacao {
	@Autowired
	PontoRessuprimentoRepositorioAplicacao pontoRessuprimentoRepositorio;

	@Autowired
	AlertaServico alertaServico;

	@Transactional
	@Override
	public List<AlertaResumo> pesquisarResumos() {
		// Buscar todos os pontos de ressuprimento
		var pontosResumo = pontoRessuprimentoRepositorio.pesquisarResumos();
		
		// Filtrar apenas os com status INADEQUADO e gerar alertas
		var alertas = new ArrayList<AlertaResumo>();
		for (var ponto : pontosResumo) {
			if ("INADEQUADO".equals(ponto.getStatus())) {
				var alerta = criarAlerta(ponto);
				if (alerta != null) {
					alertas.add(alerta);
				}
			}
		}
		
		return alertas;
	}

	@Transactional
	@Override
	public List<AlertaResumo> pesquisarPorNivel(String nivel) {
		var todosAlertas = pesquisarResumos();
		return todosAlertas.stream()
			.filter(a -> nivel.equalsIgnoreCase(a.getNivel()))
			.toList();
	}

	@Override
	public AlertaTotais obterTotais() {
		var alertas = pesquisarResumos();
		
		long criticos = alertas.stream()
			.filter(a -> "CRITICO".equals(a.getNivel()))
			.count();
		long altos = alertas.stream()
			.filter(a -> "ALTO".equals(a.getNivel()))
			.count();
		long medios = alertas.stream()
			.filter(a -> "MEDIO".equals(a.getNivel()))
			.count();

		final long totalCriticos = criticos;
		final long totalAltos = altos;
		final long totalMedios = medios;

		return new AlertaTotais() {
			@Override
			public int getTotalCriticos() {
				return (int) totalCriticos;
			}

			@Override
			public int getTotalAltos() {
				return (int) totalAltos;
			}

			@Override
			public int getTotalMedios() {
				return (int) totalMedios;
			}
		};
	}

	@Override
	public double calcularPercentualAbaixoRop(int saldoAtual, int rop) {
		if (rop <= 0) {
			return 0.0;
		}
		if (saldoAtual >= rop) {
			return 0.0; // Não está abaixo do ROP
		}
		return ((saldoAtual - rop) / (double) rop) * 100.0;
	}

	private AlertaResumo criarAlerta(PontoRessuprimentoResumo ponto) {
		var saldoAtual = ponto.getSaldoAtual();
		var rop = ponto.getRopCalculado();
		
		// Validar que está abaixo do ROP
		if (saldoAtual >= rop || rop <= 0) {
			return null;
		}
		
		// Calcular percentual abaixo do ROP
		var percentual = calcularPercentualAbaixoRop(saldoAtual, rop);
		var percentualAbaixoRop = new PercentualAbaixoRop(percentual);
		
		// Determinar nível do alerta
		var nivel = alertaServico.determinarNivel(percentualAbaixoRop);
		if (nivel == null) {
			return null; // Não gera alerta se percentual > -20%
		}
		
		// Criar resumo do alerta
		final String nivelStr = nivel.name();
		final double percentualFinal = percentual;
		final LocalDateTime dataAtual = LocalDateTime.now();
		
		return new AlertaResumo() {
			@Override
			public String getNivel() {
				return nivelStr;
			}

			@Override
			public int getProdutoId() {
				return ponto.getProdutoId();
			}

			@Override
			public String getProdutoNome() {
				return ponto.getProdutoNome();
			}

			@Override
			public int getEstoqueId() {
				return ponto.getEstoqueId();
			}

			@Override
			public String getEstoqueNome() {
				return ponto.getEstoqueNome();
			}

			@Override
			public int getQuantidadeAtual() {
				return saldoAtual;
			}

			@Override
			public int getRop() {
				return rop;
			}

			@Override
			public double getPercentualAbaixoRop() {
				return percentualFinal;
			}

			@Override
			public LocalDateTime getData() {
				return dataAtual;
			}
		};
	}
}

