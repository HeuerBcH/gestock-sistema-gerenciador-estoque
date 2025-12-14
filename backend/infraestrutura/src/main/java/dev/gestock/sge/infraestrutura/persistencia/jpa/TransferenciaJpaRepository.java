package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaResumo;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaTotais;

interface TransferenciaJpaRepository extends JpaRepository<TransferenciaJpa, Integer> {
	@Query("""
			SELECT t FROM TransferenciaJpa t
			 WHERE (:busca IS NULL OR :busca = '' OR 
			        LOWER(t.produto.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR
			        LOWER(t.estoqueOrigem.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR
			        LOWER(t.estoqueDestino.nome) LIKE LOWER(CONCAT('%', :busca, '%')) OR
			        LOWER(t.responsavel) LIKE LOWER(CONCAT('%', :busca, '%')))
			 ORDER BY t.dataHoraTransferencia DESC
			""")
	List<TransferenciaJpa> pesquisarResumosJpa(@Param("busca") String busca);

	@Query("""
			SELECT COUNT(t) FROM TransferenciaJpa t
			""")
	long contarTotal();

	@Query("""
			SELECT COALESCE(SUM(t.quantidade), 0) FROM TransferenciaJpa t
			""")
	long somarUnidadesMovidas();

	@Query("""
			SELECT COUNT(DISTINCT t.produto.id) FROM TransferenciaJpa t
			""")
	long contarProdutosDistintos();
}

