package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import dev.gestock.sge.aplicacao.dominio.produto.ProdutoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoResumo;
import dev.gestock.sge.dominio.principal.produto.CodigoProduto;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRODUTO")
class ProdutoJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@Column(nullable = false, unique = true, length = 100)
	String codigo;

	@Column(nullable = false, length = 255)
	String nome;

	@Column(name = "UNIDADE_PESO", nullable = false, length = 50)
	String unidadePeso;

	@Column(nullable = false, precision = 10, scale = 3)
	BigDecimal peso;

	@Column(nullable = false)
	Boolean perecivel;

	@Column(nullable = false)
	Boolean ativo;

	@Override
	public String toString() {
		return nome + " (" + codigo + ")";
	}
}

interface ProdutoJpaRepository extends JpaRepository<ProdutoJpa, Long> {
	Optional<ProdutoJpa> findByCodigo(String codigo);

	boolean existsByCodigo(String codigo);

	@Query("SELECT p FROM ProdutoJpa p ORDER BY p.nome")
	List<ProdutoResumo> findProdutoResumoByOrderByNome();

	@Query("SELECT p FROM ProdutoJpa p WHERE p.id = :id")
	Optional<ProdutoResumo> findProdutoResumoById(Long id);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM gestock.COTACAO WHERE PRODUTO_ID = :produtoId", nativeQuery = true)
	void deletarCotacoesPorProdutoId(Long produtoId);
}

@Repository
class ProdutoRepositorioImpl implements ProdutoRepositorio, ProdutoRepositorioAplicacao {
	@Autowired
	ProdutoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Produto produto) {
		var produtoJpa = mapeador.map(produto, ProdutoJpa.class);
		repositorio.save(produtoJpa);
	}

	@Override
	public Optional<Produto> buscarPorId(ProdutoId id) {
		return repositorio.findById(id.getId())
				.map(p -> mapeador.map(p, Produto.class));
	}

	@Override
	public Optional<Produto> buscarPorCodigo(CodigoProduto codigo) {
		return repositorio.findByCodigo(codigo.getValor())
				.map(p -> mapeador.map(p, Produto.class));
	}

	@Override
	public boolean codigoExiste(CodigoProduto codigo) {
		return repositorio.existsByCodigo(codigo.getValor());
	}

	@Override
	public void inativar(Produto produto) {
		var produtoJpa = mapeador.map(produto, ProdutoJpa.class);
		produtoJpa.ativo = false;
		repositorio.save(produtoJpa);
	}

	@Override
	public void remover(ProdutoId id) {
		repositorio.deletarCotacoesPorProdutoId(id.getId());
		repositorio.deleteById(id.getId());
	}

	@Override
	public List<ProdutoResumo> pesquisarResumos() {
		return repositorio.findProdutoResumoByOrderByNome();
	}
	
	@Override
	public Optional<ProdutoResumo> buscarResumoPorId(ProdutoId id) {
		return repositorio.findById(id.getId())
				.map(this::criarProdutoResumo);
	}
	
	private ProdutoResumo criarProdutoResumo(ProdutoJpa p) {
		return new ProdutoResumo() {
			@Override
			public Long getId() {
				return p.id;
			}

			@Override
			public String getCodigo() {
				return p.codigo;
			}

			@Override
			public String getNome() {
				return p.nome;
			}

			@Override
			public String getUnidadePeso() {
				return p.unidadePeso;
			}

			@Override
			public double getPeso() {
				return p.peso != null ? p.peso.doubleValue() : 0.0;
			}

			@Override
			public boolean isPerecivel() {
				return p.perecivel;
			}

			@Override
			public boolean isAtivo() {
				return p.ativo;
			}
		};
	}
}
