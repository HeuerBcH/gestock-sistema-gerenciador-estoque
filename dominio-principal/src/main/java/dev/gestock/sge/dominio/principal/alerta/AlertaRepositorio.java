package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Alerta
 */
public interface AlertaRepositorio {
    void salvar(Alerta alerta);
    Optional<Alerta> buscarPorId(AlertaId id);
    List<Alerta> buscarAlertasAtivosPorCliente(ClienteId clienteId);
    List<Alerta> buscarAlertasAtivosPorEstoque(EstoqueId estoqueId);
    List<Alerta> buscarAlertasAtivosPorProduto(ProdutoId produtoId);
    void remover(Alerta alerta);
}
