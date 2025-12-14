package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.produto.ProdutoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;
import dev.gestock.sge.dominio.produto.Codigo;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;

@Repository
class ProdutoRepositorioImpl implements ProdutoRepositorio, ProdutoRepositorioAplicacao {
	@Autowired
	ProdutoJpaRepository repositorio;

	@Autowired
	FornecedorJpaRepository fornecedorRepositorio;

	@Autowired
	CotacaoJpaRepository cotacaoRepositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public Produto salvar(Produto produto) {
		var produtoJpa = mapeador.map(produto, ProdutoJpa.class);
		
		// Buscar FornecedorJpa do banco para a relação N:M
		if (produto.getFornecedores() != null && !produto.getFornecedores().isEmpty()) {
			var fornecedoresJpa = new java.util.ArrayList<FornecedorJpa>();
			for (var fornecedorId : produto.getFornecedores()) {
				var fornecedorJpa = fornecedorRepositorio.findById(fornecedorId.getId()).orElse(null);
				if (fornecedorJpa != null) {
					fornecedoresJpa.add(fornecedorJpa);
				}
			}
			produtoJpa.fornecedores = fornecedoresJpa;
		}
		
		produtoJpa = repositorio.save(produtoJpa);
		return mapeador.map(produtoJpa, Produto.class);
	}

	@Transactional
	@Override
	public Produto obter(ProdutoId id) {
		var produtoJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(produtoJpa, Produto.class);
	}

	@Transactional
	@Override
	public Produto obterPorCodigo(Codigo codigo) {
		var produtoJpa = repositorio.findByCodigo(codigo.getValor()).orElse(null);
		return mapeador.map(produtoJpa, Produto.class);
	}

	@Transactional
	@Override
	public void remover(ProdutoId id) {
		// Primeiro remove cotações associadas ao produto
		cotacaoRepositorio.deleteByProdutoId(id.getId());
		// Depois remove o produto
		repositorio.deleteById(id.getId());
	}

	@Override
	public boolean existePorCodigo(String codigo, int excludeId) {
		return repositorio.existsByCodigoAndIdNot(codigo, excludeId);
	}

	@Override
	public boolean possuiEstoqueAtivo(int produtoId) {
		return repositorio.existsEstoqueAtivoByProdutoId(produtoId);
	}

	@Override
	public boolean possuiSaldoEmEstoque(int produtoId) {
		return repositorio.existsSaldoEmEstoqueByProdutoId(produtoId);
	}

	@Override
	public boolean possuiPedidosEmAndamento(int produtoId) {
		return repositorio.existsPedidosEmAndamentoByProdutoId(produtoId);
	}

	@Transactional
	@Override
	public List<ProdutoResumo> pesquisarResumos() {
		var produtosJpa = repositorio.findAll();
		return criarResumos(produtosJpa);
	}

	@Override
	public List<ProdutoResumo> pesquisarPorNomeOuCodigo(String termo) {
		var produtosJpa = repositorio.findByNomeContainingIgnoreCaseOrCodigoContaining(termo);
		return criarResumos(produtosJpa);
	}

	@Override
	public List<ProdutoResumo> pesquisarPorStatus(String status) {
		var produtosJpa = repositorio.findByStatus(status);
		return criarResumos(produtosJpa);
	}

	@Override
	public ProdutoResumo obterResumo(int id) {
		var produtoJpa = repositorio.findById(id).orElse(null);
		if (produtoJpa == null) {
			return null;
		}
		return criarResumo(produtoJpa);
	}

	private List<ProdutoResumo> criarResumos(List<ProdutoJpa> produtosJpa) {
		return produtosJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	private ProdutoResumo criarResumo(ProdutoJpa produtoJpa) {
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

