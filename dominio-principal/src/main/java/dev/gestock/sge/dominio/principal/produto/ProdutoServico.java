package src.main.java.dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio: operações compostas sobre vários produtos.
 * 
 * Responsabilidades:
 * - Classificação ABC de produtos
 * - Validação de regras de negócio complexas
 * - Operações que envolvem múltiplos produtos
 * - Validação de inativação de produtos
 */
public class ProdutoServico {

    private final ProdutoRepositorio repositorio;

    public ProdutoServico(ProdutoRepositorio repositorio) {
        notNull(repositorio, "Repositório é obrigatório");
        this.repositorio = repositorio;
    }

    /**
     * Classifica produtos com base no valor de movimentação total (R24).
     * - Top 20% = A
     * - Próximos 30% = B
     * - Restante = C
     * 
     * @param produtos Lista de produtos
     * @param valoresMovimentacao Lista de valores de movimentação correspondentes
     */
    public void classificarProdutosABC(List<Produto> produtos, List<BigDecimal> valoresMovimentacao) {
        notNull(produtos, "Lista de produtos é obrigatória");
        notNull(valoresMovimentacao, "Lista de valores é obrigatória");
        isTrue(produtos.size() == valoresMovimentacao.size(), "Tamanhos de listas incompatíveis");
        isTrue(!produtos.isEmpty(), "Lista de produtos não pode estar vazia");

        // Ordena produtos por valor de movimentação (decrescente)
        for (int i = 0; i < produtos.size(); i++) {
            BigDecimal valor = valoresMovimentacao.get(i);
            String classificacao;
            
            if (i < produtos.size() * 0.2) {
                classificacao = "A"; // Top 20%
            } else if (i < produtos.size() * 0.5) {
                classificacao = "B"; // Próximos 30%
            } else {
                classificacao = "C"; // Restante
            }
            
            produtos.get(i).classificarABC(classificacao);
            repositorio.salvar(produtos.get(i));
        }
    }

    /**
     * Valida se um produto pode ser inativado (R1H10)
     * Verifica se há saldo positivo ou pedidos em andamento
     * 
     * @param produtoId ID do produto
     * @param saldoTotal Saldo total do produto em todos os estoques
     * @param pedidosEmAndamento Quantidade de pedidos em andamento
     * @return true se pode ser inativado
     */
    public boolean podeInativarProduto(ProdutoId produtoId, int saldoTotal, int pedidosEmAndamento) {
        notNull(produtoId, "ID do produto é obrigatório");
        isTrue(saldoTotal >= 0, "Saldo total não pode ser negativo");
        isTrue(pedidosEmAndamento >= 0, "Quantidade de pedidos não pode ser negativa");
        
        return saldoTotal == 0 && pedidosEmAndamento == 0;
    }

    /**
     * Inativa um produto com validação (R1H10, R2H10)
     * 
     * @param produtoId ID do produto
     * @param saldoTotal Saldo total do produto em todos os estoques
     * @param pedidosEmAndamento Quantidade de pedidos em andamento
     * @param responsavel Responsável pela inativação
     */
    public void inativarProduto(ProdutoId produtoId, int saldoTotal, int pedidosEmAndamento, String responsavel) {
        notNull(produtoId, "ID do produto é obrigatório");
        notBlank(responsavel, "Responsável é obrigatório");
        
        Optional<Produto> produtoOpt = repositorio.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        
        if (!produto.isAtivo()) {
            throw new IllegalStateException("Produto já está inativo");
        }
        
        if (!podeInativarProduto(produtoId, saldoTotal, pedidosEmAndamento)) {
            throw new IllegalStateException("Produto não pode ser inativado: há saldo positivo ou pedidos em andamento");
        }
        
        produto.inativar(responsavel);
        repositorio.salvar(produto);
    }

    /**
     * Reativa um produto
     * 
     * @param produtoId ID do produto
     * @param responsavel Responsável pela reativação
     */
    public void reativarProduto(ProdutoId produtoId, String responsavel) {
        notNull(produtoId, "ID do produto é obrigatório");
        notBlank(responsavel, "Responsável é obrigatório");
        
        Optional<Produto> produtoOpt = repositorio.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        
        if (produto.isAtivo()) {
            throw new IllegalStateException("Produto já está ativo");
        }
        
        produto.reativar(responsavel);
        repositorio.salvar(produto);
    }

    /**
     * Atualiza informações de um produto (R1H9)
     * 
     * @param produtoId ID do produto
     * @param nome Novo nome
     * @param descricao Nova descrição
     * @param unidadeMedida Nova unidade de medida
     * @param embalagem Nova embalagem
     * @param responsavel Responsável pela atualização
     */
    public void atualizarProduto(ProdutoId produtoId, String nome, String descricao, 
                                String unidadeMedida, String embalagem, String responsavel) {
        notNull(produtoId, "ID do produto é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(descricao, "Descrição é obrigatória");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        notBlank(embalagem, "Embalagem é obrigatória");
        notBlank(responsavel, "Responsável é obrigatório");
        
        Optional<Produto> produtoOpt = repositorio.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        produto.atualizarInformacoes(nome, descricao, unidadeMedida, embalagem, responsavel);
        repositorio.salvar(produto);
    }

    /**
     * Define ROP para um produto (R3)
     * 
     * @param produtoId ID do produto
     * @param consumoMedio Consumo médio diário
     * @param leadTimeDias Lead time em dias
     * @param estoqueSeguranca Estoque de segurança
     */
    public void definirROP(ProdutoId produtoId, double consumoMedio, int leadTimeDias, int estoqueSeguranca) {
        notNull(produtoId, "ID do produto é obrigatório");
        isTrue(consumoMedio >= 0, "Consumo médio não pode ser negativo");
        isTrue(leadTimeDias >= 0, "Lead time não pode ser negativo");
        isTrue(estoqueSeguranca >= 0, "Estoque de segurança não pode ser negativo");
        
        Optional<Produto> produtoOpt = repositorio.buscarPorId(produtoId);
        if (produtoOpt.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado");
        }
        
        Produto produto = produtoOpt.get();
        produto.definirROP(consumoMedio, leadTimeDias, estoqueSeguranca);
        repositorio.salvar(produto);
    }

    /**
     * Lista produtos ativos
     * 
     * @return Lista de produtos ativos
     */
    public List<Produto> listarProdutosAtivos() {
        return repositorio.buscarProdutosAtivos();
    }

    /**
     * Lista produtos inativos
     * 
     * @return Lista de produtos inativos
     */
    public List<Produto> listarProdutosInativos() {
        return repositorio.buscarProdutosInativos();
    }

    /**
     * Lista produtos por categoria
     * 
     * @param categoria Categoria dos produtos
     * @return Lista de produtos da categoria
     */
    public List<Produto> listarProdutosPorCategoria(String categoria) {
        notBlank(categoria, "Categoria é obrigatória");
        return repositorio.buscarProdutosPorCategoria(categoria);
    }

    /**
     * Lista produtos perecíveis
     * 
     * @return Lista de produtos perecíveis
     */
    public List<Produto> listarProdutosPereciveis() {
        return repositorio.buscarProdutosPereciveis();
    }
}
