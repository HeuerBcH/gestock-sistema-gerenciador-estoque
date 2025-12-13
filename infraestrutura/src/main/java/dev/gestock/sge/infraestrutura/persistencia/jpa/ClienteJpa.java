package dev.gestock.sge.infraestrutura.persistencia.jpa;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.cliente.ClienteRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.cliente.ClienteResumo;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "CLIENTE")
class ClienteJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@Column(nullable = false, length = 255)
	String nome;

	@Column(nullable = false, unique = true, length = 20)
	String documento;

	@Column(nullable = false, unique = true, length = 255)
	String email;

	@OneToMany(mappedBy = "cliente")
	List<EstoqueJpa> estoques;

	@Override
	public String toString() {
		return nome + " (" + documento + ")";
	}
}

interface ClienteJpaRepository extends JpaRepository<ClienteJpa, Long> {
	Optional<ClienteJpa> findByDocumento(String documento);

	Optional<ClienteJpa> findByEmail(String email);

	@Query("SELECT c.id as id, c.nome as nome, c.documento as documento, c.email as email FROM ClienteJpa c ORDER BY c.nome")
	List<ClienteResumo> findClienteResumoByOrderByNome();
}

@Repository
class ClienteRepositorioImpl implements ClienteRepositorio, ClienteRepositorioAplicacao {
	@Autowired
	ClienteJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Cliente cliente) {
		var clienteJpa = mapeador.map(cliente, ClienteJpa.class);
		repositorio.save(clienteJpa);
	}

	@Override
	public Optional<Cliente> buscarPorId(ClienteId id) {
		return repositorio.findById(id.getId())
				.map(c -> mapeador.map(c, Cliente.class));
	}

	@Override
	public List<ClienteResumo> pesquisarResumos() {
		return repositorio.findClienteResumoByOrderByNome();
	}
}
