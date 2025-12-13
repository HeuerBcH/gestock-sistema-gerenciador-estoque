package dev.gestock.sge.infraestrutura.persistencia.jpa;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorResumo;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "FORNECEDOR")
class FornecedorJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@Column(nullable = false, length = 255)
	String nome;

	@Column(nullable = false, unique = true, length = 18)
	String cnpj;

	@Column(length = 255)
	String contato;

	@Column(name = "LEAD_TIME_MEDIO", nullable = false)
	Integer leadTimeMedio;

	@Column(nullable = false)
	Boolean ativo;


	@Override
	public String toString() {
		return String.format("%s (CNPJ: %s)", nome, cnpj);
	}
}

interface FornecedorJpaRepository extends JpaRepository<FornecedorJpa, Long> {
	Optional<FornecedorJpa> findByCnpj(String cnpj);

	@Query("SELECT f.id as id, f.nome as nome, f.cnpj as cnpj, f.contato as contato, f.leadTimeMedio as leadTimeMedio, f.ativo as ativo FROM FornecedorJpa f ORDER BY f.nome")
	List<FornecedorResumo> findFornecedorResumoByOrderByNome();
}

@Repository
class FornecedorRepositorioImpl implements FornecedorRepositorio, FornecedorRepositorioAplicacao {
	@Autowired
	FornecedorJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Fornecedor fornecedor) {
		var fornecedorJpa = mapeador.map(fornecedor, FornecedorJpa.class);
		repositorio.save(fornecedorJpa);
	}

	@Override
	public Optional<Fornecedor> buscarPorId(FornecedorId id) {
		return repositorio.findById(id.getId())
				.map(f -> mapeador.map(f, Fornecedor.class));
	}

	@Override
	public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
		return repositorio.findByCnpj(cnpj)
				.map(f -> mapeador.map(f, Fornecedor.class));
	}

	@Override
	public List<FornecedorResumo> pesquisarResumos() {
		return repositorio.findFornecedorResumoByOrderByNome();
	}
}
