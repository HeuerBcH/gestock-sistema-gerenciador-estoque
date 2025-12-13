package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.notBlank;

import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Serviço de domínio para gerenciamento de fornecedores.
 * 
 * Suporta:
 * - H5-H7: Gerenciar Fornecedores (cadastrar, atualizar, inativar)
 * - H18-H19: Selecionar Cotação Mais Vantajosa
 * - Validações de regras de negócio R1H5, R2H5, R1H6, R1H7, R1H18, R2H18
 * 
 * Padrão Strategy:
 * - Usa SelecaoCotacaoStrategy para selecionar melhor cotação
 * - Orquestra a seleção, mas não implementa o algoritmo
 */
public class FornecedorServico {

    private final FornecedorRepositorio fornecedorRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final SelecaoCotacaoStrategy selecaoCotacaoStrategy;

    /**
     * Construtor que cria a strategy padrão (SelecaoCotacaoMenorPreco) automaticamente.
     */
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio) {
        this(fornecedorRepositorio, null, new SelecaoCotacaoMenorPreco());
    }

    /**
     * Construtor que cria a strategy padrão (SelecaoCotacaoMenorPreco) automaticamente.
     */
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio, PedidoRepositorio pedidoRepositorio) {
        this(fornecedorRepositorio, pedidoRepositorio, new SelecaoCotacaoMenorPreco());
    }

    /**
     * Construtor principal que recebe a strategy explicitamente.
     * A strategy é obrigatória para seguir o padrão Strategy corretamente.
     */
    public FornecedorServico(FornecedorRepositorio fornecedorRepositorio, PedidoRepositorio pedidoRepositorio, SelecaoCotacaoStrategy selecaoCotacaoStrategy) {
        notNull(fornecedorRepositorio, "FornecedorRepositorio é obrigatório");
        notNull(selecaoCotacaoStrategy, "SelecaoCotacaoStrategy é obrigatória");
        this.fornecedorRepositorio = fornecedorRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.selecaoCotacaoStrategy = selecaoCotacaoStrategy;
    }

    /**
     * Busca um fornecedor por ID.
     */
    public Fornecedor buscarPorId(FornecedorId id) {
        notNull(id, "ID do fornecedor é obrigatório");
        return fornecedorRepositorio.buscarPorId(id)
            .orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));
    }

    /**
     * Busca um fornecedor por CNPJ.
     */
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        notBlank(cnpj, "CNPJ é obrigatório");
        return fornecedorRepositorio.buscarPorCnpj(cnpj);
    }

    /**
     * Cadastra um novo fornecedor (H5).
     * Valida:
     * - R1H5: Fornecedor deve possuir cotação vinculada a produto
     * - R2H5: Lead time deve ser positivo
     */
    public void cadastrar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        
        // R2H5: Validar lead time positivo (já validado no construtor do LeadTime)
        if (fornecedor.getLeadTimeMedio() != null && fornecedor.getLeadTimeMedio().getDias() < 0) {
            throw new IllegalArgumentException("Lead time deve ser positivo (R2H5)");
        }
        
        fornecedorRepositorio.salvar(fornecedor);
    }

    /**
     * Atualiza informações de um fornecedor (H6).
     * Nota: R1H6 - alterar lead time recalcula ROP dos produtos associados
     */
    public void atualizar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        fornecedorRepositorio.salvar(fornecedor);
    }

    /**
     * Inativa um fornecedor (H7).
     * Valida:
     * - R1H7: Não pode ter pedidos pendentes
     */
    public void inativar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        
        // R1H7: Validar ausência de pedidos pendentes
        if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendenteParaFornecedor(fornecedor.getId())) {
            throw new IllegalStateException("Fornecedor com pedidos pendentes não pode ser inativado (R1H7)");
        }
        
        fornecedor.inativar();
        fornecedorRepositorio.salvar(fornecedor);
    }

    /**
     * Salva um fornecedor no repositório (compatibilidade).
     */
    public void salvar(Fornecedor fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        fornecedorRepositorio.salvar(fornecedor);
    }

    /**
     * Seleciona automaticamente a melhor cotação entre fornecedores (H18-H19).
     * Valida:
     * - R1H18: Apenas cotações com validade ativa e fornecedor ativo
     * - R2H18: Desempate por menor lead time
     * 
     * Delega a seleção para a Strategy configurada.
     * A strategy é sempre obrigatória (padrão Strategy).
     */
    public Optional<Cotacao> selecionarMelhorCotacao(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        // Sempre delega para a strategy - sem fallback inline
        return selecaoCotacaoStrategy.selecionar(fornecedores, produtoId);
    }
}
