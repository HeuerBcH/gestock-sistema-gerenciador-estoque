package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoResumo;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoTotais;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.pontoresuprimento.ConsumoMedioDiario;
import dev.gestock.sge.dominio.pontoresuprimento.EstoqueSeguranca;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimento;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoId;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoRepositorio;
import dev.gestock.sge.dominio.pontoresuprimento.RopCalculado;
import dev.gestock.sge.dominio.pontoresuprimento.SaldoAtual;
import dev.gestock.sge.dominio.pontoresuprimento.StatusRop;
import dev.gestock.sge.dominio.produto.ProdutoId;

@Repository
class PontoRessuprimentoRepositorioImpl implements PontoRessuprimentoRepositorio, PontoRessuprimentoRepositorioAplicacao {
	private static final int DIAS_CONSUMO = 90;

	@Autowired
	PontoRessuprimentoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	MovimentacaoJpaRepository movimentacaoRepositorio;

	@Autowired
	FornecedorJpaRepository fornecedorRepositorio;

	@Autowired
	EstoqueProdutoJpaRepository estoqueProdutoRepositorio;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Autowired
	EstoqueJpaRepository estoqueRepositorio;

	@Transactional
	@Override
	public PontoRessuprimento salvar(PontoRessuprimento ponto) {
		var pontoJpa = new PontoRessuprimentoJpa();
		pontoJpa.id = ponto.getId().getId();
		pontoJpa.estoqueSeguranca = ponto.getEstoqueSeguranca().getValor();

		// Carregar estoque e produto do banco
		pontoJpa.estoque = estoqueRepositorio.findById(ponto.getEstoqueId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));
		pontoJpa.produto = produtoRepositorio.findById(ponto.getProdutoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));

		pontoJpa = repositorio.save(pontoJpa);
		return mapeador.map(pontoJpa, PontoRessuprimento.class);
	}

	@Transactional
	@Override
	public PontoRessuprimento obter(PontoRessuprimentoId id) {
		var pontoJpa = repositorio.findById(id.getId()).orElse(null);
		if (pontoJpa == null) {
			return null;
		}
		return mapeador.map(pontoJpa, PontoRessuprimento.class);
	}

	@Transactional
	@Override
	public PontoRessuprimento obterPorEstoqueEProduto(EstoqueId estoqueId, ProdutoId produtoId) {
		var pontoJpa = repositorio.findByEstoqueIdAndProdutoId(estoqueId.getId(), produtoId.getId()).orElse(null);
		if (pontoJpa == null) {
			return null;
		}
		return mapeador.map(pontoJpa, PontoRessuprimento.class);
	}

	@Transactional
	@Override
	public List<PontoRessuprimento> obterTodos() {
		var pontosJpa = repositorio.findAll();
		if (pontosJpa == null || pontosJpa.isEmpty()) {
			return List.of();
		}
		var resultado = new ArrayList<PontoRessuprimento>();
		for (var pontoJpa : pontosJpa) {
			resultado.add(mapeador.map(pontoJpa, PontoRessuprimento.class));
		}
		return resultado;
	}

	@Override
	public void remover(PontoRessuprimentoId id) {
		repositorio.deleteById(id.getId());
	}

	@Transactional
	@Override
	public List<PontoRessuprimentoResumo> pesquisarResumos() {
		var pontosJpa = repositorio.findAll();
		return criarResumos(pontosJpa);
	}

	@Transactional
	@Override
	public List<PontoRessuprimentoResumo> pesquisarPorProdutoOuEstoque(String termo) {
		var pontosJpa = repositorio.findAll().stream()
			.filter(p -> (p.produto != null && (p.produto.nome.toLowerCase().contains(termo.toLowerCase()) || 
				p.produto.codigo.toLowerCase().contains(termo.toLowerCase()))) ||
				(p.estoque != null && (p.estoque.nome.toLowerCase().contains(termo.toLowerCase()) ||
				p.estoque.endereco.toLowerCase().contains(termo.toLowerCase()))))
			.toList();
		return criarResumos(pontosJpa);
	}

	@Transactional
	@Override
	public List<PontoRessuprimentoResumo> pesquisarPorStatus(String status) {
		var pontosJpa = repositorio.findAll();
		return criarResumos(pontosJpa).stream()
			.filter(r -> status.equalsIgnoreCase(r.getStatus()))
			.toList();
	}

	@Override
	public PontoRessuprimentoResumo obterResumo(int id) {
		var pontoJpa = repositorio.findById(id).orElse(null);
		if (pontoJpa == null) {
			return null;
		}
		return criarResumo(pontoJpa);
	}

	@Override
	public PontoRessuprimentoTotais obterTotais() {
		var pontosJpa = repositorio.findAll();
		var resumos = criarResumos(pontosJpa);
		
		long adequado = resumos.stream()
			.filter(r -> "ADEQUADO".equals(r.getStatus()))
			.count();
		long inadequado = resumos.stream()
			.filter(r -> "INADEQUADO".equals(r.getStatus()))
			.count();

		final long total = resumos.size();
		final long estoqueAdequado = adequado;
		final long abaixoDoRop = inadequado;

		return new PontoRessuprimentoTotais() {
			@Override
			public int getTotalMonitorado() {
				return (int) total;
			}

			@Override
			public int getEstoqueAdequado() {
				return (int) estoqueAdequado;
			}

			@Override
			public int getAbaixoDoRop() {
				return (int) abaixoDoRop;
			}
		};
	}

	@Override
	public double calcularConsumoMedioDiario(int estoqueId, int produtoId, int dias) {
		var dataInicio = LocalDateTime.now().minusDays(dias);
		return movimentacaoRepositorio.calcularConsumoMedioDiario(estoqueId, produtoId, dataInicio, dias);
	}

	@Override
	public double calcularConsumoMaximoDiario(int estoqueId, int produtoId, int dias) {
		var dataInicio = LocalDateTime.now().minusDays(dias);
		Double resultado = movimentacaoRepositorio.calcularConsumoMaximoDiario(estoqueId, produtoId, dataInicio);
		return resultado != null ? resultado : 0.0;
	}

	@Override
	public int obterSaldoAtual(int estoqueId, int produtoId) {
		var estoqueProdutoOpt = estoqueProdutoRepositorio.findByEstoqueIdAndProdutoId(estoqueId, produtoId);
		return estoqueProdutoOpt.map(ep -> ep.quantidade).orElse(0);
	}

	@Override
	public int obterLeadTimeMedio(int produtoId) {
		return fornecedorRepositorio.calcularLeadTimeMedio(produtoId);
	}

	@Override
	public int obterLeadTimeMaximo(int produtoId) {
		return fornecedorRepositorio.calcularLeadTimeMaximo(produtoId);
	}

	private List<PontoRessuprimentoResumo> criarResumos(List<PontoRessuprimentoJpa> pontosJpa) {
		return pontosJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	@Transactional
	@Override
	public int sincronizarPontosRessuprimento() {
		int registrosCriados = 0;
		
		// Buscar todas as combinações de estoque-produto que possuem quantidade > 0
		var estoqueProdutos = estoqueProdutoRepositorio.findAll().stream()
			.filter(ep -> ep.quantidade > 0)
			.toList();
		
		for (var ep : estoqueProdutos) {
			// Verificar se já existe ponto de ressuprimento para esta combinação
			var existente = repositorio.findByEstoqueIdAndProdutoId(ep.estoque.id, ep.produto.id);
			
			if (existente.isEmpty()) {
				// Calcular Estoque de Segurança usando a fórmula:
				// ES = (Consumo Máximo Diário × Lead Time Máximo) - (Consumo Médio Diário × Lead Time Médio)
				var consumoMedio = calcularConsumoMedioDiario(ep.estoque.id, ep.produto.id, DIAS_CONSUMO);
				var consumoMaximo = calcularConsumoMaximoDiario(ep.estoque.id, ep.produto.id, DIAS_CONSUMO);
				var leadTimeMedio = obterLeadTimeMedio(ep.produto.id);
				var leadTimeMaximo = obterLeadTimeMaximo(ep.produto.id);
				
				int estoqueSegurancaCalculado = (int) Math.round(
					(consumoMaximo * leadTimeMaximo) - (consumoMedio * leadTimeMedio)
				);
				
				// Garantir valor mínimo de 5
				int estoqueSegurancaFinal = Math.max(5, estoqueSegurancaCalculado);
				
				// Criar novo ponto de ressuprimento
				var novoPonto = new PontoRessuprimentoJpa();
				novoPonto.estoque = ep.estoque;
				novoPonto.produto = ep.produto;
				novoPonto.estoqueSeguranca = estoqueSegurancaFinal;
				
				repositorio.save(novoPonto);
				registrosCriados++;
			}
		}
		
		return registrosCriados;
	}

	private PontoRessuprimentoResumo criarResumo(PontoRessuprimentoJpa pontoJpa) {
		var estoqueId = pontoJpa.estoque != null ? pontoJpa.estoque.id : 0;
		var produtoId = pontoJpa.produto != null ? pontoJpa.produto.id : 0;

		// Calcular valores dinâmicos
		var consumoMedioDiario = calcularConsumoMedioDiario(estoqueId, produtoId, DIAS_CONSUMO);
		var consumoMaximoDiario = calcularConsumoMaximoDiario(estoqueId, produtoId, DIAS_CONSUMO);
		var leadTimeMedio = obterLeadTimeMedio(produtoId);
		var leadTimeMaximo = obterLeadTimeMaximo(produtoId);
		var saldoAtual = obterSaldoAtual(estoqueId, produtoId);
		var estoqueSeguranca = pontoJpa.estoqueSeguranca;

		// Calcular ROP = (Consumo Médio × Lead Time Médio) + Estoque de Segurança
		var ropCalculado = (int) Math.round((consumoMedioDiario * leadTimeMedio) + estoqueSeguranca);
		ropCalculado = Math.max(0, ropCalculado);

		// Determinar status
		var status = saldoAtual >= ropCalculado ? "ADEQUADO" : "INADEQUADO";

		final double consumoMedio = consumoMedioDiario;
		final double consumoMax = consumoMaximoDiario;
		final int ltMedio = leadTimeMedio;
		final int ltMaximo = leadTimeMaximo;
		final int estoqueSeg = estoqueSeguranca;
		final int rop = ropCalculado;
		final int saldo = saldoAtual;
		final String statusFinal = status;

		return new PontoRessuprimentoResumo() {
			@Override
			public int getId() {
				return pontoJpa.id;
			}

			@Override
			public int getEstoqueId() {
				return estoqueId;
			}

			@Override
			public String getEstoqueNome() {
				return pontoJpa.estoque != null ? pontoJpa.estoque.nome : "";
			}

			@Override
			public int getProdutoId() {
				return produtoId;
			}

			@Override
			public String getProdutoNome() {
				return pontoJpa.produto != null ? pontoJpa.produto.nome : "";
			}

			@Override
			public double getConsumoMedioDiario() {
				return consumoMedio;
			}

			@Override
			public double getConsumoMaximoDiario() {
				return consumoMax;
			}

			@Override
			public int getLeadTimeMedio() {
				return ltMedio;
			}

			@Override
			public int getLeadTimeMaximo() {
				return ltMaximo;
			}

			@Override
			public int getEstoqueSeguranca() {
				return estoqueSeg;
			}

			@Override
			public int getRopCalculado() {
				return rop;
			}

			@Override
			public int getSaldoAtual() {
				return saldo;
			}

			@Override
			public String getStatus() {
				return statusFinal;
			}
		};
	}
}

