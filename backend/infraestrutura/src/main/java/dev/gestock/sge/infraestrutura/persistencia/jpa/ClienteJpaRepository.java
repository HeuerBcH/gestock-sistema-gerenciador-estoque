package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteResumo;

interface ClienteJpaRepository extends JpaRepository<ClienteJpa, Integer> {
	Optional<ClienteJpa> findByEmail(String email);

	Optional<ClienteJpa> findByDocumento(String documento);

	java.util.List<ClienteResumo> findClienteResumoBy();
}

