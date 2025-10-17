package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Estoque.
 * Implementações (JPA/JDBC/etc.) ficam na camada de infraestrutura.
 */
public interface EstoqueRepositorio {
	void salvar(Estoque estoque);
	Optional<Estoque> buscarPorId(EstoqueId id);
	List<Estoque> buscarEstoquesPorCliente(ClienteId clienteId);
	List<Estoque> buscarEstoquesAtivos();
	List<Estoque> buscarEstoquesInativos();
	List<Estoque> buscarEstoquesPorNome(String nome);
	List<Estoque> buscarEstoquesPorEndereco(String endereco);
	boolean existeEstoqueComNome(String nome);
	boolean existeEstoqueComEndereco(String endereco);
	void remover(Estoque estoque);
}
