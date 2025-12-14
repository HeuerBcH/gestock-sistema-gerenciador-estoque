package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface CotacaoJpaRepository extends JpaRepository<CotacaoJpa, Integer> {
	List<CotacaoJpa> findByProdutoId(int produtoId);

	java.util.Optional<CotacaoJpa> findByProdutoIdAndFornecedorId(int produtoId, int fornecedorId);

	void deleteByProdutoId(int produtoId);

	void deleteByFornecedorId(int fornecedorId);
}

