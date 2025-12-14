package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.gestock.sge.aplicacao.reserva.ReservaResumo;
import dev.gestock.sge.aplicacao.reserva.ReservaTotais;

interface ReservaJpaRepository extends JpaRepository<ReservaJpa, Integer> {
	List<ReservaJpa> findByPedidoId(int pedidoId);

	@Query("""
			SELECT r FROM ReservaJpa r
			 WHERE (:busca IS NULL OR :busca = '' OR 
			        LOWER(r.produto.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR
			        CAST(r.pedido.id AS string) LIKE CONCAT('%', :busca, '%'))
			 ORDER BY r.dataHoraReserva DESC
			""")
	List<ReservaJpa> pesquisarResumosJpa(@Param("busca") String busca);

	@Query("""
			SELECT COUNT(r) FROM ReservaJpa r
			""")
	long contarTotal();

	@Query("""
			SELECT COUNT(r) FROM ReservaJpa r WHERE r.status = 'ATIVA'
			""")
	long contarAtivas();

	@Query("""
			SELECT COUNT(r) FROM ReservaJpa r WHERE r.status = 'LIBERADA'
			""")
	long contarLiberadas();

	@Query("""
			SELECT COALESCE(SUM(r.quantidade), 0) FROM ReservaJpa r WHERE r.status = 'ATIVA'
			""")
	long somarQuantidadeAtiva();
}

