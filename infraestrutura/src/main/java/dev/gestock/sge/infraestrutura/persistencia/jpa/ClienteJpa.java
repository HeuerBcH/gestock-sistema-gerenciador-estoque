package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import static jakarta.persistence.GenerationType.IDENTITY;
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

	@Column(name = "senha_hash", nullable = false, length = 255)
	String senhaHash;

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
}

@Repository
class ClienteRepositorioImpl implements ClienteRepositorio {
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
	public Optional<Cliente> buscarPorEmail(String email) {
		return repositorio.findByEmail(email)
				.map(c -> mapeador.map(c, Cliente.class));
	}

	@Override
	public Optional<Cliente> buscarPorDocumento(String documento) {
		return repositorio.findByDocumento(documento)
				.map(c -> mapeador.map(c, Cliente.class));
	}

	@Override
	public List<Cliente> listarTodos() {
		var clientesJpa = repositorio.findAll();
		return mapeador.map(clientesJpa, new org.modelmapper.TypeToken<List<Cliente>>() {}.getType());
	}
}
