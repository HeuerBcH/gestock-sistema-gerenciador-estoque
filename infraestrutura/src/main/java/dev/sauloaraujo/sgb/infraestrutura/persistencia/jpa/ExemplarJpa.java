package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarRepositorioAplicacao;
import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarResumo;
import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarResumoExpandido;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.ExemplarId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.livro.Isbn;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "EXEMPLAR")
@AttributeOverrides({
		@AttributeOverride(name = "emprestimo.periodo.inicio", column = @Column(name = "EMPRESTIMO_PERIODO_INICIO")),
		@AttributeOverride(name = "emprestimo.periodo.fim", column = @Column(name = "EMPRESTIMO_PERIODO_FIM")) })
class ExemplarJpa {
	@Id
	int id;

	@ManyToOne()
	LivroJpa livro;

	@Embedded
	EmprestimoJpa emprestimo;
}

@Embeddable
class EmprestimoJpa {
	@Embedded
	PeriodoJpa periodo;

	@ManyToOne
	@JoinColumn(name = "EMPRESTIMO_TOMADOR_ID")
	SocioJpa tomador;
}

@Embeddable
class PeriodoJpa {
	LocalDate inicio;
	LocalDate fim;
}

interface ExemplarJpaRepository extends JpaRepository<ExemplarJpa, Integer> {
	List<ExemplarJpa> findByLivroIdAndEmprestimoIsNull(String isbn);

	List<ExemplarResumo> findExemplarResumoByOrderByLivroTitulo();

	// @formatter:off
	@Query("""
			SELECT e
			  FROM ExemplarJpa e
		JOIN FETCH e.livro
	    JOIN FETCH e.emprestimo.tomador
          ORDER BY e.livro.titulo,
                   e.id
			""")
	// @formatter:on
	List<ExemplarResumoExpandido> findExemplarResumoExpandidoByEmprestimoIsNotNull();

}

@Repository
class ExemplarRepositorioImpl implements EstoqueRepositorio, ExemplarRepositorioAplicacao {
	@Autowired
	ExemplarJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Estoque exemplar) {
		var exemplarJpa = mapeador.map(exemplar, ExemplarJpa.class);
		repositorio.save(exemplarJpa);
	}

	@Override
	public Estoque obter(ExemplarId id) {
		var exemplarJpa = repositorio.findById(id.getId()).get();
		return mapeador.map(exemplarJpa, Estoque.class);
	}

	@Override
	public List<Estoque> pesquisarDisponiveis(Isbn livro) {
		var exemplares = repositorio.findByLivroIdAndEmprestimoIsNull(livro.toString());
		return mapeador.map(exemplares, new TypeToken<List<Estoque>>() {
		}.getType());
	}

	@Override
	public List<ExemplarResumo> pesquisarResumos() {
		return repositorio.findExemplarResumoByOrderByLivroTitulo();
	}

	@Override
	public List<ExemplarResumoExpandido> pesquisarEmprestados() {
		return repositorio.findExemplarResumoExpandidoByEmprestimoIsNotNull();
	}
}