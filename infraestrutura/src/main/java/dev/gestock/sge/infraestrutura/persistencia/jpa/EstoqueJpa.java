package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;

import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueResumo;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ESTOQUE")
class EstoqueJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "CLIENTE_ID", nullable = false)
	ClienteJpa cliente;

	@Column(nullable = false, length = 255)
	String nome;

	@Column(nullable = false, length = 500)
	String endereco;

	@Column(nullable = false)
	Integer capacidade;

	@Column(nullable = false)
	Boolean ativo;

	@Override
	public String toString() {
		return nome + " (" + endereco + ")";
	}
}

interface EstoqueJpaRepository extends JpaRepository<EstoqueJpa, Long> {
	List<EstoqueJpa> findByClienteId(Long clienteId);

	boolean existsByEndereco(String endereco);

	boolean existsByClienteIdAndNome(Long clienteId, String nome);

	@Query("SELECT e FROM EstoqueJpa e WHERE e.cliente.id = :clienteId ORDER BY e.nome")
	List<EstoqueJpa> findEstoqueJpaByClienteIdOrderByNome(Long clienteId);
}

@Repository
class EstoqueRepositorioImpl implements EstoqueRepositorio, EstoqueRepositorioAplicacao {
	@Autowired
	EstoqueJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Estoque estoque) {
		var estoqueJpa = mapeador.map(estoque, EstoqueJpa.class);
		repositorio.save(estoqueJpa);
	}

	@Override
	public Optional<Estoque> buscarPorId(EstoqueId id) {
		return repositorio.findById(id.getId())
				.map(e -> mapeador.map(e, Estoque.class));
	}

	@Override
	public List<Estoque> buscarEstoquesPorClienteId(ClienteId clienteId) {
		var estoques = repositorio.findByClienteId(clienteId.getId());
		return mapeador.map(estoques, new TypeToken<List<Estoque>>() {
		}.getType());
	}

	@Override
	public boolean existePorEndereco(String endereco, ClienteId clienteId) {
		return repositorio.existsByEndereco(endereco);
	}

	@Override
	public boolean existePorNome(String nome, ClienteId clienteId) {
		return repositorio.existsByClienteIdAndNome(clienteId.getId(), nome);
	}

	@Override
	public Optional<EstoqueResumo> buscarResumoPorId(EstoqueId id) {
		return repositorio.findById(id.getId())
				.map(e -> criarEstoqueResumo(e));
	}

	@Override
	public List<EstoqueResumo> pesquisarResumos() {
		var estoques = repositorio.findAll();
		return estoques.stream()
				.map(this::criarEstoqueResumo)
				.sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
				.toList();
	}
	
	private EstoqueResumo criarEstoqueResumo(EstoqueJpa e) {
		return new EstoqueResumo() {
			@Override
			public EstoqueId getId() {
				return mapeador.map(e.id, EstoqueId.class);
			}

			@Override
			public ClienteId getClienteId() {
				return mapeador.map(e.cliente.id, ClienteId.class);
			}

			@Override
			public String getNome() {
				return e.nome;
			}

			@Override
			public String getEndereco() {
				return e.endereco;
			}

			@Override
			public int getCapacidade() {
				return e.capacidade;
			}

			@Override
			public boolean isAtivo() {
				return e.ativo;
			}
		};
	}
}
