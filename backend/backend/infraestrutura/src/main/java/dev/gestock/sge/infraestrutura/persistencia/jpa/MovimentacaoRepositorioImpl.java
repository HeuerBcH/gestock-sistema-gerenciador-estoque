package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoResumo;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoTotais;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoRepositorio;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;

@Repository
class MovimentacaoRepositorioImpl implements MovimentacaoRepositorio, MovimentacaoRepositorioAplicacao {
	@Autowired
	MovimentacaoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	EstoqueProdutoJpaRepository estoqueProdutoRepositorio;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Autowired
	EstoqueJpaRepository estoqueRepositorio;

	@Transactional
	@Override
	public Movimentacao salvar(Movimentacao movimentacao) {
		var movimentacaoJpa = new MovimentacaoJpa();
		movimentacaoJpa.id = movimentacao.getId().getId();
		movimentacaoJpa.dataHora = movimentacao.getDataHora();
		movimentacaoJpa.quantidade = movimentacao.getQuantidade().getValor();
		movimentacaoJpa.tipo = movimentacao.getTipo().name();
		movimentacaoJpa.motivo = movimentacao.getMotivo().getValor();
		movimentacaoJpa.responsavel = movimentacao.getResponsavel().getValor();

		// Carregar produto e estoque do banco
		movimentacaoJpa.produto = produtoRepositorio.findById(movimentacao.getProdutoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		movimentacaoJpa.estoque = estoqueRepositorio.findById(movimentacao.getEstoqueId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));

		// Atualizar estoque antes de salvar a movimentação
		atualizarEstoqueProduto(movimentacao);

		// Salvar movimentação
		movimentacaoJpa = repositorio.save(movimentacaoJpa);
		return mapeador.map(movimentacaoJpa, Movimentacao.class);
	}

	private void atualizarEstoqueProduto(Movimentacao movimentacao) {
		var estoqueId = movimentacao.getEstoqueId().getId();
		var produtoId = movimentacao.getProdutoId().getId();
		var quantidade = movimentacao.getQuantidade().getValor();

		var estoqueProdutoOpt = estoqueProdutoRepositorio.findByEstoqueIdAndProdutoId(estoqueId, produtoId);
		EstoqueProdutoJpa estoqueProduto;

		if (movimentacao.getTipo() == TipoMovimentacao.ENTRADA) {
			// ENTRADA: incrementar quantidade
			if (estoqueProdutoOpt.isPresent()) {
				estoqueProduto = estoqueProdutoOpt.get();
				estoqueProduto.quantidade += quantidade;
			} else {
				// Criar novo registro
				estoqueProduto = new EstoqueProdutoJpa();
				estoqueProduto.estoque = estoqueRepositorio.findById(estoqueId).orElseThrow();
				estoqueProduto.produto = produtoRepositorio.findById(produtoId).orElseThrow();
				estoqueProduto.quantidade = quantidade;
			}
			estoqueProdutoRepositorio.save(estoqueProduto);
		} else {
			// SAIDA: decrementar quantidade
			if (!estoqueProdutoOpt.isPresent()) {
				throw new IllegalArgumentException("Produto não está disponível no estoque");
			}
			estoqueProduto = estoqueProdutoOpt.get();
			if (estoqueProduto.quantidade < quantidade) {
				throw new IllegalArgumentException("Quantidade insuficiente no estoque. Disponível: " + estoqueProduto.quantidade);
			}
			estoqueProduto.quantidade -= quantidade;
			estoqueProdutoRepositorio.save(estoqueProduto);
		}
	}

	@Transactional
	@Override
	public Movimentacao obter(MovimentacaoId id) {
		var movimentacaoJpa = repositorio.findById(id.getId()).orElse(null);
		if (movimentacaoJpa == null) {
			return null;
		}
		return mapeador.map(movimentacaoJpa, Movimentacao.class);
	}

	@Transactional
	@Override
	public List<Movimentacao> obterPorPeriodo(LocalDate inicio, LocalDate fim) {
		var inicioDateTime = inicio.atStartOfDay();
		var fimDateTime = fim.atTime(23, 59, 59);
		var movimentacoesJpa = repositorio.findByDataHoraBetween(inicioDateTime, fimDateTime);
		if (movimentacoesJpa == null || movimentacoesJpa.isEmpty()) {
			return List.of();
		}
		var resultado = new ArrayList<Movimentacao>();
		for (var movimentacaoJpa : movimentacoesJpa) {
			resultado.add(mapeador.map(movimentacaoJpa, Movimentacao.class));
		}
		return resultado;
	}

	@Transactional
	@Override
	public List<Movimentacao> obterPorTipo(TipoMovimentacao tipo) {
		var movimentacoesJpa = repositorio.findByTipo(tipo.name());
		if (movimentacoesJpa == null || movimentacoesJpa.isEmpty()) {
			return List.of();
		}
		var resultado = new ArrayList<Movimentacao>();
		for (var movimentacaoJpa : movimentacoesJpa) {
			resultado.add(mapeador.map(movimentacaoJpa, Movimentacao.class));
		}
		return resultado;
	}

	@Transactional
	@Override
	public void remover(MovimentacaoId id) {
		// Antes de remover, reverter a alteração no estoque
		var movimentacao = obter(id);
		if (movimentacao != null) {
			reverterEstoqueProduto(movimentacao);
		}
		repositorio.deleteById(id.getId());
	}

	private void reverterEstoqueProduto(Movimentacao movimentacao) {
		var estoqueId = movimentacao.getEstoqueId().getId();
		var produtoId = movimentacao.getProdutoId().getId();
		var quantidade = movimentacao.getQuantidade().getValor();

		var estoqueProdutoOpt = estoqueProdutoRepositorio.findByEstoqueIdAndProdutoId(estoqueId, produtoId);
		if (!estoqueProdutoOpt.isPresent()) {
			return; // Não há estoque para reverter
		}

		var estoqueProduto = estoqueProdutoOpt.get();
		if (movimentacao.getTipo() == TipoMovimentacao.ENTRADA) {
			// Era ENTRADA: decrementar (reverter)
			estoqueProduto.quantidade -= quantidade;
		} else {
			// Era SAIDA: incrementar (reverter)
			estoqueProduto.quantidade += quantidade;
		}
		estoqueProdutoRepositorio.save(estoqueProduto);
	}

	@Transactional
	@Override
	public List<MovimentacaoResumo> pesquisarResumos() {
		var movimentacoesJpa = repositorio.findAll();
		return criarResumos(movimentacoesJpa);
	}

	@Transactional
	@Override
	public List<MovimentacaoResumo> pesquisarPorPeriodo(LocalDate inicio, LocalDate fim) {
		var inicioDateTime = inicio.atStartOfDay();
		var fimDateTime = fim.atTime(23, 59, 59);
		var movimentacoesJpa = repositorio.findByDataHoraBetween(inicioDateTime, fimDateTime);
		return criarResumos(movimentacoesJpa);
	}

	@Transactional
	@Override
	public List<MovimentacaoResumo> pesquisarPorTipo(String tipo) {
		var movimentacoesJpa = repositorio.findByTipo(tipo);
		return criarResumos(movimentacoesJpa);
	}

	@Override
	public MovimentacaoResumo obterResumo(int id) {
		var movimentacaoJpa = repositorio.findById(id).orElse(null);
		if (movimentacaoJpa == null) {
			return null;
		}
		return criarResumo(movimentacaoJpa);
	}

	@Override
	public MovimentacaoTotais obterTotais() {
		var total = repositorio.count();
		var totalEntradas = repositorio.countByTipo("ENTRADA");
		var totalSaidas = repositorio.countByTipo("SAIDA");

		return new MovimentacaoTotais() {
			@Override
			public int getTotalMovimentacoes() {
				return (int) total;
			}

			@Override
			public int getTotalEntradas() {
				return (int) totalEntradas;
			}

			@Override
			public int getTotalSaidas() {
				return (int) totalSaidas;
			}
		};
	}

	@Override
	public MovimentacaoTotais obterTotaisPorPeriodo(LocalDate inicio, LocalDate fim) {
		var inicioDateTime = inicio.atStartOfDay();
		var fimDateTime = fim.atTime(23, 59, 59);
		var movimentacoesJpa = repositorio.findByDataHoraBetween(inicioDateTime, fimDateTime);

		long totalEntradas = movimentacoesJpa.stream()
			.filter(m -> "ENTRADA".equals(m.tipo))
			.count();
		long totalSaidas = movimentacoesJpa.stream()
			.filter(m -> "SAIDA".equals(m.tipo))
			.count();

		final long total = movimentacoesJpa.size();
		final long entradas = totalEntradas;
		final long saidas = totalSaidas;

		return new MovimentacaoTotais() {
			@Override
			public int getTotalMovimentacoes() {
				return (int) total;
			}

			@Override
			public int getTotalEntradas() {
				return (int) entradas;
			}

			@Override
			public int getTotalSaidas() {
				return (int) saidas;
			}
		};
	}

	private List<MovimentacaoResumo> criarResumos(List<MovimentacaoJpa> movimentacoesJpa) {
		return movimentacoesJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	private MovimentacaoResumo criarResumo(MovimentacaoJpa movimentacaoJpa) {
		var quantidadeComSinal = "ENTRADA".equals(movimentacaoJpa.tipo)
			? movimentacaoJpa.quantidade
			: -movimentacaoJpa.quantidade;

		return new MovimentacaoResumo() {
			@Override
			public int getId() {
				return movimentacaoJpa.id;
			}

			@Override
			public java.time.LocalDateTime getDataHora() {
				return movimentacaoJpa.dataHora;
			}

			@Override
			public String getTipo() {
				return movimentacaoJpa.tipo;
			}

			@Override
			public int getProdutoId() {
				return movimentacaoJpa.produto != null ? movimentacaoJpa.produto.id : 0;
			}

			@Override
			public String getProdutoNome() {
				return movimentacaoJpa.produto != null ? movimentacaoJpa.produto.nome : "";
			}

			@Override
			public int getQuantidade() {
				return quantidadeComSinal;
			}

			@Override
			public String getMotivo() {
				return movimentacaoJpa.motivo;
			}

			@Override
			public int getEstoqueId() {
				return movimentacaoJpa.estoque != null ? movimentacaoJpa.estoque.id : 0;
			}

			@Override
			public String getEstoqueNome() {
				return movimentacaoJpa.estoque != null ? movimentacaoJpa.estoque.nome : "";
			}

			@Override
			public String getResponsavel() {
				return movimentacaoJpa.responsavel;
			}
		};
	}
}

