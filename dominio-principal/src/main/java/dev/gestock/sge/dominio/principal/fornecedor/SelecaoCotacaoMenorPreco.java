package dev.gestock.sge.dominio.principal.fornecedor;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Estratégia concreta que seleciona a cotação com menor preço.
 * 
 * Responsabilidade:
 * - Implementa o algoritmo de seleção por menor preço
 * - Desempata por menor prazo (R2H18)
 * - Aplica validações R1H18 (fornecedor ativo, validade ativa)
 * 
 * Algoritmo:
 * 1. Filtra fornecedores ativos (R1H18)
 * 2. Filtra cotações com validade ativa (R1H18)
 * 3. Seleciona a cotação com menor preço
 * 4. Em caso de empate, seleciona a com menor prazo (R2H18)
 */
public class SelecaoCotacaoMenorPreco implements SelecaoCotacaoStrategy {
    
    @Override
    public Optional<Cotacao> selecionar(List<Fornecedor> fornecedores, ProdutoId produtoId) {
        return fornecedores.stream()
                .filter(Fornecedor::isAtivo) // R1H18: fornecedor ativo
                .map(f -> f.obterCotacaoPorProduto(produtoId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Cotacao::isValidadeAtiva) // R1H18: validade ativa
                .min((c1, c2) -> {
                    // Compara por menor preço
                    int cmp = Double.compare(c1.getPreco(), c2.getPreco());
                    // R2H18: Desempate por menor lead time (prazo)
                    return (cmp != 0) ? cmp : Integer.compare(c1.getPrazoDias(), c2.getPrazoDias());
                });
    }
}

