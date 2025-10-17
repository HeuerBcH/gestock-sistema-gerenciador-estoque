package dev.gestock.sge.dominio.principal.movimentacao;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório do agregado Movimentacao
 */
public interface MovimentacaoRepositorio {
    void salvar(Movimentacao movimentacao);
    Optional<Movimentacao> buscarPorId(MovimentacaoId id);
    List<Movimentacao> buscarMovimentacoesPorCliente(ClienteId clienteId);
    List<Movimentacao> buscarMovimentacoesPorEstoque(EstoqueId estoqueId);
    List<Movimentacao> buscarMovimentacoesPorProduto(ProdutoId produtoId);
    List<Movimentacao> buscarMovimentacoesPorTipo(TipoMovimentacao tipo);
    List<Movimentacao> buscarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<Movimentacao> buscarMovimentacoesPorProdutoEPeriodo(ProdutoId produtoId, LocalDateTime dataInicio, LocalDateTime dataFim);
    void remover(Movimentacao movimentacao);
}
