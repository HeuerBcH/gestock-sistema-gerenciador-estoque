package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface MovimentacaoJpaRepository extends JpaRepository<MovimentacaoJpa, Integer> {
	List<MovimentacaoJpa> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

	List<MovimentacaoJpa> findByTipo(String tipo);

	long countByTipo(String tipo);

	@Query("""
		SELECT COALESCE(SUM(m.quantidade) / CAST(:dias AS double), 0.0)
		FROM MovimentacaoJpa m
		WHERE m.estoque.id = :estoqueId
		AND m.produto.id = :produtoId
		AND m.tipo = 'SAIDA'
		AND m.dataHora >= :dataInicio
	""")
	double calcularConsumoMedioDiario(@Param("estoqueId") int estoqueId, @Param("produtoId") int produtoId, 
		@Param("dataInicio") LocalDateTime dataInicio, @Param("dias") int dias);

	@Query("""
		SELECT COALESCE(MAX(consumoDia), 0.0) FROM (
			SELECT SUM(m.quantidade) as consumoDia
			FROM MovimentacaoJpa m
			WHERE m.estoque.id = :estoqueId
			AND m.produto.id = :produtoId
			AND m.tipo = 'SAIDA'
			AND m.dataHora >= :dataInicio
			GROUP BY CAST(m.dataHora AS date)
		)
	""")
	Double calcularConsumoMaximoDiario(@Param("estoqueId") int estoqueId, @Param("produtoId") int produtoId, 
		@Param("dataInicio") LocalDateTime dataInicio);
}

