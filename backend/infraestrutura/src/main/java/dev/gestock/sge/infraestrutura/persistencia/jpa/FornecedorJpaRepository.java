package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorResumo;

interface FornecedorJpaRepository extends JpaRepository<FornecedorJpa, Integer> {
	Optional<FornecedorJpa> findByCnpj(String cnpj);

	@Query("SELECT f FROM FornecedorJpa f WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR f.cnpj LIKE CONCAT('%', :termo, '%')")
	List<FornecedorJpa> findByNomeContainingIgnoreCaseOrCnpjContaining(@Param("termo") String termo);

	List<FornecedorJpa> findByStatus(String status);

	List<FornecedorResumo> findFornecedorResumoBy();

	@Query(value = """
		SELECT COALESCE(AVG(f.LEAD_TIME), 0)
		FROM FORNECEDOR f
		INNER JOIN PRODUTO_FORNECEDOR pf ON f.ID = pf.FORNECEDOR_ID
		WHERE pf.PRODUTO_ID = :produtoId
		AND f.STATUS = 'ATIVO'
	""", nativeQuery = true)
	int calcularLeadTimeMedio(@Param("produtoId") int produtoId);

	@Query(value = """
		SELECT COALESCE(MAX(f.LEAD_TIME), 0)
		FROM FORNECEDOR f
		INNER JOIN PRODUTO_FORNECEDOR pf ON f.ID = pf.FORNECEDOR_ID
		WHERE pf.PRODUTO_ID = :produtoId
		AND f.STATUS = 'ATIVO'
	""", nativeQuery = true)
	int calcularLeadTimeMaximo(@Param("produtoId") int produtoId);

	// R1H7: Verificar se fornecedor possui pedidos pendentes
	@Query("SELECT COUNT(p) > 0 FROM PedidoJpa p WHERE p.fornecedor.id = :fornecedorId AND p.status IN ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE')")
	boolean existsPedidosPendentesByFornecedorId(@Param("fornecedorId") int fornecedorId);
}

