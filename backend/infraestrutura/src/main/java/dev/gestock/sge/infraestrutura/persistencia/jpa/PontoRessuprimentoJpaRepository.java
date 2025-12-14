package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface PontoRessuprimentoJpaRepository extends JpaRepository<PontoRessuprimentoJpa, Integer> {
	Optional<PontoRessuprimentoJpa> findByEstoqueIdAndProdutoId(int estoqueId, int produtoId);
}

