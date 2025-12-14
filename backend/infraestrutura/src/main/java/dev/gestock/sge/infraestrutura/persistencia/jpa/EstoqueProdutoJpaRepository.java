package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface EstoqueProdutoJpaRepository extends JpaRepository<EstoqueProdutoJpa, EstoqueProdutoId> {
	@Query("SELECT ep FROM EstoqueProdutoJpa ep WHERE ep.estoque.id = :estoqueId AND ep.produto.id = :produtoId")
	Optional<EstoqueProdutoJpa> findByEstoqueIdAndProdutoId(@Param("estoqueId") int estoqueId, @Param("produtoId") int produtoId);

	@Query("SELECT ep FROM EstoqueProdutoJpa ep WHERE ep.estoque.id = :estoqueId AND ep.quantidade > 0")
	List<EstoqueProdutoJpa> findByEstoqueId(@Param("estoqueId") int estoqueId);
}

