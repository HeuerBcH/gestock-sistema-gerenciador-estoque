package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.cotacao.CotacaoPorProdutoResumo;
import dev.gestock.sge.aplicacao.cotacao.CotacaoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.cotacao.CotacaoResumo;
import java.util.Comparator;
import dev.gestock.sge.dominio.cotacao.Cotacao;
import dev.gestock.sge.dominio.cotacao.CotacaoId;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.Validade;
import dev.gestock.sge.dominio.produto.ProdutoId;

@Repository
class CotacaoRepositorioImpl implements CotacaoRepositorio, CotacaoRepositorioAplicacao {
	@Autowired
	CotacaoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Autowired
	FornecedorJpaRepository fornecedorRepositorio;

	@Override
	public Cotacao salvar(Cotacao cotacao) {
		var cotacaoJpa = new CotacaoJpa();
		cotacaoJpa.id = cotacao.getId().getId();
		cotacaoJpa.preco = cotacao.getPreco().getValor();
		cotacaoJpa.leadTime = cotacao.getLeadTime().getDias();
		cotacaoJpa.validade = cotacao.getValidade().name();
		cotacaoJpa.statusAprovacao = cotacao.getStatusAprovacao().name();
		
		// Carregar produto e fornecedor do banco
		cotacaoJpa.produto = produtoRepositorio.findById(cotacao.getProdutoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		cotacaoJpa.fornecedor = fornecedorRepositorio.findById(cotacao.getFornecedorId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));
		
		// Verificar se já existe cotação para o mesmo produto+fornecedor
		if (cotacao.getId().getId() == 0) {
			// Nova cotação - verificar duplicata
			var existente = repositorio.findByProdutoIdAndFornecedorId(
				cotacao.getProdutoId().getId(),
				cotacao.getFornecedorId().getId()
			);
			if (existente.isPresent()) {
				throw new IllegalArgumentException("Já existe uma cotação para este produto e fornecedor");
			}
		} else {
			// Atualização - verificar se existe outra cotação com mesmo produto+fornecedor
			var existente = repositorio.findByProdutoIdAndFornecedorId(
				cotacao.getProdutoId().getId(),
				cotacao.getFornecedorId().getId()
			);
			if (existente.isPresent() && existente.get().id != cotacao.getId().getId()) {
				throw new IllegalArgumentException("Já existe uma cotação para este produto e fornecedor");
			}
		}
		
		cotacaoJpa = repositorio.save(cotacaoJpa);
		return mapeador.map(cotacaoJpa, Cotacao.class);
	}

	@Transactional
	@Override
	public Cotacao obter(CotacaoId id) {
		var cotacaoJpa = repositorio.findById(id.getId()).orElse(null);
		if (cotacaoJpa == null) {
			return null;
		}
		return mapeador.map(cotacaoJpa, Cotacao.class);
	}

	@Transactional
	@Override
	public List<Cotacao> obterPorProduto(ProdutoId produtoId) {
		var cotacoesJpa = repositorio.findByProdutoId(produtoId.getId());
		if (cotacoesJpa == null || cotacoesJpa.isEmpty()) {
			return List.of();
		}
		var resultado = new ArrayList<Cotacao>();
		for (var cotacaoJpa : cotacoesJpa) {
			resultado.add(mapeador.map(cotacaoJpa, Cotacao.class));
		}
		return resultado;
	}

	@Override
	public void remover(CotacaoId id) {
		repositorio.deleteById(id.getId());
	}

	@Transactional
	@Override
	public List<CotacaoPorProdutoResumo> pesquisarPorProduto() {
		var todasCotacoes = repositorio.findAll();
		
		// Agrupar por NOME do produto (para comparar produtos com mesmo nome de fornecedores diferentes)
		Map<String, List<CotacaoJpa>> porNomeProduto = todasCotacoes.stream()
			.filter(c -> c.produto != null && c.produto.nome != null)
			.collect(Collectors.groupingBy(c -> c.produto.nome.toLowerCase().trim()));
		
		var resultados = new ArrayList<CotacaoPorProdutoResumo>();
		
		for (var entry : porNomeProduto.entrySet()) {
			var nomeProduto = entry.getKey();
			var cotacoesJpa = entry.getValue();
			
			// Obter a cotação mais vantajosa usando a mesma lógica do serviço
			var maisVantajosaJpa = obterMaisVantajosaJpa(cotacoesJpa);
			int idMaisVantajosa = maisVantajosaJpa != null ? maisVantajosaJpa.id : -1;
			
			// Criar resumos das cotações
			var cotacoesResumo = new ArrayList<CotacaoResumo>();
			for (var cotacaoJpa : cotacoesJpa) {
				cotacoesResumo.add(criarResumo(cotacaoJpa, cotacaoJpa.id == idMaisVantajosa));
			}
			
			// Contar aprovadas
			long totalAprovadas = cotacoesJpa.stream()
				.filter(c -> "APROVADA".equals(c.statusAprovacao))
				.count();
			
			var primeiroProdutoJpa = cotacoesJpa.get(0).produto;
			final int primeiroProdutoId = primeiroProdutoJpa != null ? primeiroProdutoJpa.id : 0;
			final String nomeProdutoFinal = primeiroProdutoJpa != null ? primeiroProdutoJpa.nome : nomeProduto;
			
			resultados.add(new CotacaoPorProdutoResumo() {
				@Override
				public int getProdutoId() {
					return primeiroProdutoId;
				}

				@Override
				public String getProdutoNome() {
					return nomeProdutoFinal;
				}

				@Override
				public List<CotacaoResumo> getCotacoes() {
					return cotacoesResumo;
				}

				@Override
				public int getTotalCotacoes() {
					return cotacoesJpa.size();
				}

				@Override
				public int getTotalAprovadas() {
					return (int) totalAprovadas;
				}
			});
		}
		
		return resultados;
	}

	@Transactional
	@Override
	public List<CotacaoResumo> pesquisarPorProduto(int produtoId) {
		var cotacoesJpa = repositorio.findByProdutoId(produtoId);
		
		// Obter a cotação mais vantajosa usando a mesma lógica do serviço
		var maisVantajosaJpa = obterMaisVantajosaJpa(cotacoesJpa);
		int idMaisVantajosa = maisVantajosaJpa != null ? maisVantajosaJpa.id : -1;
		
		return cotacoesJpa.stream()
			.map(c -> criarResumo(c, c.id == idMaisVantajosa))
			.toList();
	}

	@Override
	public CotacaoResumo obterResumo(int id) {
		var cotacaoJpa = repositorio.findById(id).orElse(null);
		if (cotacaoJpa == null) {
			return null;
		}
		
		// Verificar se é a mais vantajosa - buscar todas as cotações do produto
		var cotacoesJpa = repositorio.findByProdutoId(cotacaoJpa.produto.id);
		var maisVantajosaJpa = obterMaisVantajosaJpa(cotacoesJpa);
		boolean isMaisVantajosa = maisVantajosaJpa != null && maisVantajosaJpa.id == id;
		
		return criarResumo(cotacaoJpa, isMaisVantajosa);
	}

	/**
	 * Implementa a mesma lógica do CotacaoServico.obterMaisVantajosa() mas trabalhando diretamente com JPA.
	 * Ordena por: preço (menor), lead time (menor), validade (ATIVA primeiro), ordem original (ID).
	 */
	private CotacaoJpa obterMaisVantajosaJpa(List<CotacaoJpa> cotacoesJpa) {
		if (cotacoesJpa == null || cotacoesJpa.isEmpty()) {
			return null;
		}

		return cotacoesJpa.stream()
			.sorted(Comparator
				.comparing((CotacaoJpa c) -> c.preco)
				.thenComparing(c -> c.leadTime)
				.thenComparing(c -> c.validade.equals("EXPIRADA") ? 1 : 0)
				.thenComparing(c -> c.id))
			.findFirst()
			.orElse(null);
	}

	@Transactional
	@Override
	public int sincronizarCotacoes() {
		int cotacoesCriadas = 0;
		
		// Buscar todos os produtos com seus fornecedores
		var todosProdutos = produtoRepositorio.findAll();
		
		for (var produto : todosProdutos) {
			if (produto.fornecedores == null || produto.fornecedores.isEmpty()) {
				continue;
			}
			
			for (var fornecedor : produto.fornecedores) {
				// Verificar se já existe cotação para este par produto-fornecedor
				var cotacaoExistente = repositorio.findByProdutoIdAndFornecedorId(produto.id, fornecedor.id);
				
				if (cotacaoExistente.isEmpty()) {
					// Criar nova cotação usando o custo e leadTime do fornecedor
					var novaCotacao = new CotacaoJpa();
					novaCotacao.produto = produto;
					novaCotacao.fornecedor = fornecedor;
					novaCotacao.preco = fornecedor.custo;
					novaCotacao.leadTime = fornecedor.leadTime;
					novaCotacao.validade = "ATIVA";
					novaCotacao.statusAprovacao = "PENDENTE";
					
					repositorio.save(novaCotacao);
					cotacoesCriadas++;
				} else {
					// Atualizar cotação existente com valores atuais do fornecedor
					var cotacao = cotacaoExistente.get();
					cotacao.preco = fornecedor.custo;
					cotacao.leadTime = fornecedor.leadTime;
					repositorio.save(cotacao);
				}
			}
		}
		
		return cotacoesCriadas;
	}

	private CotacaoResumo criarResumo(CotacaoJpa cotacaoJpa, boolean isMaisVantajosa) {
		return new CotacaoResumo() {
			@Override
			public int getId() {
				return cotacaoJpa.id;
			}

			@Override
			public int getProdutoId() {
				return cotacaoJpa.produto != null ? cotacaoJpa.produto.id : 0;
			}

			@Override
			public String getProdutoNome() {
				return cotacaoJpa.produto != null ? cotacaoJpa.produto.nome : "";
			}

			@Override
			public int getFornecedorId() {
				return cotacaoJpa.fornecedor != null ? cotacaoJpa.fornecedor.id : 0;
			}

			@Override
			public String getFornecedorNome() {
				return cotacaoJpa.fornecedor != null ? cotacaoJpa.fornecedor.nome : "";
			}

			@Override
			public java.math.BigDecimal getPreco() {
				return cotacaoJpa.preco;
			}

			@Override
			public int getLeadTime() {
				return cotacaoJpa.leadTime;
			}

			@Override
			public String getValidade() {
				return cotacaoJpa.validade;
			}

			@Override
			public String getStatusAprovacao() {
				return cotacaoJpa.statusAprovacao;
			}

			@Override
			public boolean isMaisVantajosa() {
				return isMaisVantajosa;
			}
		};
	}
}

