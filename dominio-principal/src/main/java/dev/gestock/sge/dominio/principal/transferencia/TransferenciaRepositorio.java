package dev.gestock.sge.dominio.principal.transferencia;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Transferencia
 */
public interface TransferenciaRepositorio {
    void salvar(Transferencia transferencia);
    Optional<Transferencia> buscarPorId(TransferenciaId id);
    List<Transferencia> buscarTransferenciasPorCliente(ClienteId clienteId);
    List<Transferencia> buscarTransferenciasPorEstoqueOrigem(EstoqueId estoqueId);
    List<Transferencia> buscarTransferenciasPorEstoqueDestino(EstoqueId estoqueId);
    List<Transferencia> buscarTransferenciasPorProduto(ProdutoId produtoId);
    void remover(Transferencia transferencia);
}
