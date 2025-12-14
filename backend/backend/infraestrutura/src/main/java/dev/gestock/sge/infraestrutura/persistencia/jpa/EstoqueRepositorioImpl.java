package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.estoque.EstoqueResumo;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;
import dev.gestock.sge.dominio.estoque.Estoque;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;

@Repository
class EstoqueRepositorioImpl implements EstoqueRepositorio, EstoqueRepositorioAplicacao {
	@Autowired
	EstoqueJpaRepository repositorio;

	@Autowired
	EstoqueProdutoJpaRepository estoqueProdutoRepositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public Estoque salvar(Estoque estoque) {
		var estoqueJpa = mapeador.map(estoque, EstoqueJpa.class);
		estoqueJpa = repositorio.save(estoqueJpa);
		return mapeador.map(estoqueJpa, Estoque.class);
	}

	@Transactional
	@Override
	public Estoque obter(EstoqueId id) {
		var estoqueJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(estoqueJpa, Estoque.class);
	}

	@Override
	public void remover(EstoqueId id) {
		repositorio.deleteById(id.getId());
	}

	@Override
	public boolean existePorEndereco(String endereco, int excludeId) {
		return repositorio.existsByEnderecoAndIdNot(endereco, excludeId);
	}

	@Override
	public boolean existePorNome(String nome, int excludeId) {
		return repositorio.existsByNomeAndIdNot(nome, excludeId);
	}

	@Override
	public boolean possuiProdutos(int estoqueId) {
		return repositorio.existsProdutosByEstoqueId(estoqueId);
	}

	@Override
	public boolean possuiPedidosEmAndamento(int estoqueId) {
		return repositorio.existsPedidosEmAndamentoByEstoqueId(estoqueId);
	}

	@Override
	public int obterOcupacaoAtual(int estoqueId) {
		return repositorio.calcularOcupacao(estoqueId);
	}

	@Override
	public List<EstoqueResumo> pesquisarResumos() {
		var estoquesJpa = repositorio.findAll();
		return criarResumos(estoquesJpa);
	}

	@Override
	public List<EstoqueResumo> pesquisarPorNomeOuEndereco(String termo) {
		var estoquesJpa = repositorio.findByNomeContainingIgnoreCaseOrEnderecoContaining(termo);
		return criarResumos(estoquesJpa);
	}

	@Override
	public List<EstoqueResumo> pesquisarPorStatus(String status) {
		var estoquesJpa = repositorio.findByStatus(status);
		return criarResumos(estoquesJpa);
	}

	@Override
	public EstoqueResumo obterResumo(int id) {
		var estoqueJpa = repositorio.findById(id).orElse(null);
		if (estoqueJpa == null) {
			return null;
		}
		return criarResumo(estoqueJpa);
	}

	private List<EstoqueResumo> criarResumos(List<EstoqueJpa> estoquesJpa) {
		return estoquesJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	private EstoqueResumo criarResumo(EstoqueJpa estoqueJpa) {
		var quantidadeAtual = repositorio.calcularOcupacao(estoqueJpa.id);
		var capacidadeDisponivel = Math.max(0, estoqueJpa.capacidade - quantidadeAtual);
		var ocupacaoPercentual = estoqueJpa.capacidade > 0 
			? (int) Math.round((quantidadeAtual * 100.0) / estoqueJpa.capacidade)
			: 0;
		
		final int qtdAtual = quantidadeAtual;
		final int capDisp = capacidadeDisponivel;
		
		return new EstoqueResumo() {
			@Override
			public int getId() {
				return estoqueJpa.id;
			}

			@Override
			public String getNome() {
				return estoqueJpa.nome;
			}

			@Override
			public String getEndereco() {
				return estoqueJpa.endereco;
			}

			@Override
			public int getCapacidade() {
				return estoqueJpa.capacidade;
			}

			@Override
			public int getQuantidadeAtual() {
				return qtdAtual;
			}

			@Override
			public int getCapacidadeDisponivel() {
				return capDisp;
			}

			@Override
			public int getOcupacao() {
				return ocupacaoPercentual;
			}

			@Override
			public String getStatus() {
				return estoqueJpa.status;
			}
		};
	}

	@Transactional
	@Override
	public List<ProdutoResumo> pesquisarProdutos(int estoqueId) {
		var estoqueProdutos = estoqueProdutoRepositorio.findByEstoqueId(estoqueId);
		
		return estoqueProdutos.stream()
			.map(ep -> criarProdutoResumo(ep))
			.toList();
	}

	private ProdutoResumo criarProdutoResumo(EstoqueProdutoJpa estoqueProduto) {
		var produtoJpa = estoqueProduto.produto;
		var quantidadeNoEstoque = estoqueProduto.quantidade;
		
		return new ProdutoResumo() {
			@Override
			public int getId() {
				return produtoJpa.id;
			}

			@Override
			public String getCodigo() {
				return produtoJpa.codigo;
			}

			@Override
			public String getNome() {
				return produtoJpa.nome;
			}

			@Override
			public int getPeso() {
				return produtoJpa.peso;
			}

			@Override
			public String getPerecivel() {
				return produtoJpa.perecivel;
			}

			@Override
			public String getStatus() {
				return produtoJpa.status;
			}

			@Override
			public List<String> getFornecedores() {
				if (produtoJpa.fornecedores == null) {
					return List.of();
				}
				return produtoJpa.fornecedores.stream()
					.map(f -> f.nome)
					.toList();
			}
		};
	}
}

