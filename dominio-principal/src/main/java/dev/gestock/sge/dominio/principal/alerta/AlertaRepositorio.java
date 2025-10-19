package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

public interface AlertaRepositorio {
    void salvar(Alerta alerta);
    Optional<Alerta> obter(AlertaId id);
    List<Alerta> listarAtivos();
    List<Alerta> listarPorProduto(ProdutoId produtoId);
    List<Alerta> listarPorEstoque(EstoqueId estoqueId);
}
