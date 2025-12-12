package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;

import java.util.Optional;

/**
 * Implementação concreta padrão do Template Method de atualização de estoque.
 * 
 * Responsabilidade:
 * - Implementa os métodos abstratos do template
 * - Usa o repositório para carregar e salvar estoques
 * - Aplica validações padrão de regras de negócio
 * 
 * Regras de negócio aplicadas:
 * - R2H2: Estoque com pedido em andamento não pode ser atualizado (se pedidoRepositorio fornecido)
 */
public class AtualizacaoEstoquePadrao extends AtualizacaoEstoqueTemplate {
    
    private final EstoqueRepositorio repositorio;
    private final PedidoRepositorio pedidoRepositorio;
    
    public AtualizacaoEstoquePadrao(EstoqueRepositorio repositorio) {
        this(repositorio, null);
    }
    
    public AtualizacaoEstoquePadrao(EstoqueRepositorio repositorio, PedidoRepositorio pedidoRepositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("Repositório não pode ser nulo");
        }
        this.repositorio = repositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }
    
    @Override
    protected Optional<Estoque> carregarEstoque(EstoqueId estoqueId) {
        return repositorio.buscarPorId(estoqueId);
    }
    
    @Override
    protected void validarPreCondicoes(EstoqueId estoqueId, Estoque estoque) {
        // R2H2: Validar se há pedidos pendentes (se pedidoRepositorio fornecido)
        if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendentePorEstoqueId(estoqueId)) {
            throw new IllegalStateException("Estoque com pedido em andamento não pode ser atualizado (R2H2)");
        }
    }
    
    @Override
    protected void salvarEstoque(Estoque estoque) {
        repositorio.salvar(estoque);
    }
}

