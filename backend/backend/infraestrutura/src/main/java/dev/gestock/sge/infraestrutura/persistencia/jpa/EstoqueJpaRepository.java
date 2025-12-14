package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.gestock.sge.aplicacao.estoque.EstoqueResumo;

interface EstoqueJpaRepository extends JpaRepository<EstoqueJpa, Integer> {
	@Query("SELECT e FROM EstoqueJpa e WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(e.endereco) LIKE LOWER(CONCAT('%', :termo, '%'))")
	List<EstoqueJpa> findByNomeContainingIgnoreCaseOrEnderecoContaining(@Param("termo") String termo);

	List<EstoqueJpa> findByStatus(String status);

	@Query("SELECT COALESCE(SUM(ep.quantidade), 0) FROM EstoqueProdutoJpa ep WHERE ep.estoque.id = :estoqueId")
	Integer calcularOcupacao(@Param("estoqueId") int estoqueId);

	// R2H1: Verificar se existe estoque com mesmo endereço
	@Query("SELECT COUNT(e) > 0 FROM EstoqueJpa e WHERE LOWER(e.endereco) = LOWER(:endereco) AND e.id != :excludeId")
	boolean existsByEnderecoAndIdNot(@Param("endereco") String endereco, @Param("excludeId") int excludeId);

	// R3H1: Verificar se existe estoque com mesmo nome
	@Query("SELECT COUNT(e) > 0 FROM EstoqueJpa e WHERE LOWER(e.nome) = LOWER(:nome) AND e.id != :excludeId")
	boolean existsByNomeAndIdNot(@Param("nome") String nome, @Param("excludeId") int excludeId);

	// R1H2: Verificar se estoque possui produtos
	@Query("SELECT COUNT(ep) > 0 FROM EstoqueProdutoJpa ep WHERE ep.estoque.id = :estoqueId AND ep.quantidade > 0")
	boolean existsProdutosByEstoqueId(@Param("estoqueId") int estoqueId);

	// R2H2: Verificar se estoque possui pedidos em andamento
	@Query("SELECT COUNT(p) > 0 FROM PedidoJpa p WHERE p.estoque.id = :estoqueId AND p.status IN ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE')")
	boolean existsPedidosEmAndamentoByEstoqueId(@Param("estoqueId") int estoqueId);
}

