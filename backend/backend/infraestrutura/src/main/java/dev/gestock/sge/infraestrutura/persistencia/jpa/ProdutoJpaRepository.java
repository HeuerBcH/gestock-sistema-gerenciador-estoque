package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;

interface ProdutoJpaRepository extends JpaRepository<ProdutoJpa, Integer> {
	Optional<ProdutoJpa> findByCodigo(String codigo);

	@Query("SELECT p FROM ProdutoJpa p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR p.codigo LIKE CONCAT('%', :termo, '%')")
	List<ProdutoJpa> findByNomeContainingIgnoreCaseOrCodigoContaining(@Param("termo") String termo);

	List<ProdutoJpa> findByStatus(String status);

	List<ProdutoResumo> findProdutoResumoBy();

	// R1H8: Verificar se existe produto com mesmo código
	@Query("SELECT COUNT(p) > 0 FROM ProdutoJpa p WHERE LOWER(p.codigo) = LOWER(:codigo) AND p.id != :excludeId")
	boolean existsByCodigoAndIdNot(@Param("codigo") String codigo, @Param("excludeId") int excludeId);

	// R3H8: Verificar se produto está vinculado a estoque ativo
	@Query("SELECT COUNT(ep) > 0 FROM EstoqueProdutoJpa ep WHERE ep.produto.id = :produtoId AND ep.estoque.status = 'ATIVO' AND ep.quantidade > 0")
	boolean existsEstoqueAtivoByProdutoId(@Param("produtoId") int produtoId);

	// R1H10: Verificar se produto possui saldo em estoque
	@Query("SELECT COUNT(ep) > 0 FROM EstoqueProdutoJpa ep WHERE ep.produto.id = :produtoId AND ep.quantidade > 0")
	boolean existsSaldoEmEstoqueByProdutoId(@Param("produtoId") int produtoId);

	// R1H10: Verificar se produto possui pedidos em andamento
	@Query(value = """
		SELECT COUNT(*) > 0 FROM PEDIDO_ITEM pi 
		JOIN PEDIDO p ON pi.PEDIDO_ID = p.ID 
		WHERE pi.PRODUTO_ID = :produtoId 
		AND p.STATUS IN ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE')
		""", nativeQuery = true)
	boolean existsPedidosEmAndamentoByProdutoId(@Param("produtoId") int produtoId);
}

