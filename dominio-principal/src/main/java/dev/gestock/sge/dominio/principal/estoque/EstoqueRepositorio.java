package dev.gestock.sge.dominio.principal.estoque;


import java.util.Optional;

/**
 * Repositório do agregado Estoque.
 * Implementações (JPA/JDBC/etc.) ficam na camada de infraestrutura.
 */
public interface EstoqueRepositorio {
	void salvar(Estoque estoque);
	Optional<Estoque> buscarPorId(EstoqueId id);
}
