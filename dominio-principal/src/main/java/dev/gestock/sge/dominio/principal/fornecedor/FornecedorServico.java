package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.*;

// Serviço de domínio para gerenciamento de fornecedores.

public class FornecedorServico {

    private final FornecedorRepositorio fornecedorRepositorio;
    private final PedidoRepositorio pedidoRepositorio;

    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio) {
        this(fornecedorRepositorio, null);
    }

    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio, PedidoRepositorio pedidoRepositorio) {
        this.fornecedorRepositorio = fornecedorRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }

    // Cadastra um novo fornecedor (H5).
    public void cadastrar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        
        // R2H5: Validar lead time positivo (já validado no construtor do LeadTime)
        if (fornecedor.getLeadTimeMedio() != null && fornecedor.getLeadTimeMedio().getDias() < 0) {
            throw new IllegalArgumentException("Lead time deve ser positivo (R2H5)");
        }
        
        fornecedorRepositorio.salvar(fornecedor);
    }

    // Atualiza informações de um fornecedor (H6).
    public void atualizar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        fornecedorRepositorio.salvar(fornecedor);
    }

    // Inativa um fornecedor (H7).
    public void inativar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        
        // R1H7: Validar ausência de pedidos pendentes
        if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendenteParaFornecedor(fornecedor.getId())) {
            throw new IllegalStateException("Fornecedor com pedidos pendentes não pode ser inativado (R1H7)");
        }
        
        fornecedor.inativar();
        fornecedorRepositorio.salvar(fornecedor);
    }

    // Salva um fornecedor no repositório (compatibilidade).
    public void salvar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        fornecedorRepositorio.salvar(fornecedor);
    }

    // Seleciona automaticamente a melhor cotação entre fornecedores (H18-H19).
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        return fornecedores.stream()
                .filter(Fornecedor::isAtivo)
                .map(f -> f.obterCotacaoPorProduto(produtoId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Cotacao::isValidadeAtiva)
                .min((c1, c2) -> {
                    int cmp = Double.compare(c1.getPreco(), c2.getPreco());
                    return (cmp != 0) ? cmp : Integer.compare(c1.getPrazoDias(), c2.getPrazoDias());
                });
    }
}
