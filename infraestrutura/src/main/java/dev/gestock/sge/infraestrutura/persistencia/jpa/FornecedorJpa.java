package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorResumo;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
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
		return repositorio.findAll().stream()
				.sorted((a, b) -> {
					if (a.nome == null && b.nome == null) return 0;
					if (a.nome == null) return 1;
					if (b.nome == null) return -1;
					return a.nome.compareToIgnoreCase(b.nome);
				})
				.map(this::criarFornecedorResumo)
				.toList();
	}
	
	@Override
	public Optional<FornecedorResumo> buscarResumoPorId(FornecedorId id) {
		if (id == null || id.getId() == null) {
			return Optional.empty();
		}
		return repositorio.findById(id.getId())
				.map(this::criarFornecedorResumo);
	}
	
	@Override
	public List<FornecedorResumo> pesquisarComFiltros(String busca, Boolean ativo) {
		var fornecedores = repositorio.findAll().stream()
				.sorted((a, b) -> {
					if (a.nome == null && b.nome == null) return 0;
					if (a.nome == null) return 1;
					if (b.nome == null) return -1;
					return a.nome.compareToIgnoreCase(b.nome);
				})
				.toList();
		
		return fornecedores.stream()
				.filter(f -> {
					if (busca != null && !busca.trim().isEmpty()) {
						String buscaLower = busca.trim().toLowerCase();
						boolean matchNome = f.nome != null && f.nome.toLowerCase().contains(buscaLower);
						boolean matchCnpj = f.cnpj != null && f.cnpj.toLowerCase().contains(buscaLower);
						if (!matchNome && !matchCnpj) {
							return false;
						}
					}
					if (ativo != null && ativo != f.ativo) {
						return false;
					}
					return true;
				})
				.map(this::criarFornecedorResumo)
				.toList();
	}
	
	private FornecedorResumo criarFornecedorResumo(FornecedorJpa f) {
		return new FornecedorResumo() {
			@Override
			public FornecedorId getId() {
				if (f.id == null) {
					return FornecedorId.temporario();
				}
				return new FornecedorId(f.id);
			}

			@Override
			public String getNome() {
				return f.nome;
			}

			@Override
			public String getCnpj() {
				return f.cnpj;
			}

			@Override
			public String getContato() {
				return f.contato != null ? f.contato : "";
			}

			@Override
			public LeadTime getLeadTimeMedio() {
				return new LeadTime(f.leadTimeMedio != null ? f.leadTimeMedio : 0);
			}

			@Override
			public boolean isAtivo() {
				return f.ativo != null ? f.ativo : true;
			}
		};
	}
}
