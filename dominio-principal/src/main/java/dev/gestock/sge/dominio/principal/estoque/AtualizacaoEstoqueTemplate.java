package dev.gestock.sge.dominio.principal.estoque;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Template Method para atualização de estoque.
 * 
 * Papel duplo: Template Method (define fluxo) + Subject do Observer (notifica observers)
 * 
 * Responsabilidade:
 * - Define o fluxo fixo de atualização de estoque (Template Method)
 * - Notifica observers após a atualização (Observer Subject)
 * 
 * Padrão: Template Method + Observer
 * - Template Method: Define os passos fixos da atualização
 * - Observer: Notifica observers sobre a atualização
 * 
 * Fluxo Fixo (não pode ser alterado):
 * 1. carregarEstoque(id) - busca o estoque (abstrato)
 * 2. validarEstoqueInativo(estoque) - verifica se está inativo (concreto)
 * 3. validarPreCondicoes(estoqueId, estoque) - valida regras de negócio (abstrato)
 * 4. antesDeAtualizar(estoque, estoqueId) - hook opcional (concreto, vazio)
 * 5. aplicarAtualizacao(estoque) - processa mudanças no estoque (concreto)
 * 6. salvarEstoque(estoque) - persiste (abstrato)
 * 7. notificarObservers(estoqueId) - notifica observers registrados (concreto)
 * 8. aposAtualizar(estoque, estoqueId) - hook opcional (concreto, vazio)
 */
public abstract class AtualizacaoEstoqueTemplate {
    
    private final List<EstoqueObserver> observers = new ArrayList<>();
    
    /**
     * Registra um observer para ser notificado sobre atualizações de estoque.
     * 
     * @param observer O observer a ser registrado
     */
    public void registrarObserver(EstoqueObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer não pode ser nulo");
        }
        observers.add(observer);
    }
    
    /**
     * Template Method: Define o fluxo fixo de atualização de estoque.
     * Método final garante que o fluxo não pode ser alterado por subclasses.
     * 
     * @param estoqueId O ID do estoque a ser atualizado
     */
    public final void atualizar(EstoqueId estoqueId) {
        Optional<Estoque> estoqueOpt = carregarEstoque(estoqueId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado: " + estoqueId);
        }
        Estoque estoque = estoqueOpt.get();
        atualizar(estoque);
    }
    
    /**
     * Template Method: Define o fluxo fixo de atualização de estoque.
     * Versão que recebe o estoque já carregado.
     * 
     * @param estoque O estoque a ser atualizado
     */
    public final void atualizar(Estoque estoque) {
        EstoqueId estoqueId = estoque.getId();
        
        validarPreCondicoes(estoqueId, estoque);
        antesDeAtualizar(estoque, estoqueId);
        aplicarAtualizacao(estoque);
        salvarEstoque(estoque);
        notificarObservers(estoqueId);
        aposAtualizar(estoque, estoqueId);
    }
    
    /**
     * Passo 1: Carrega o estoque do repositório.
     * Método abstrato que deve ser implementado pelas subclasses.
     * 
     * @param estoqueId O ID do estoque
     * @return O estoque encontrado
     */
    protected abstract Optional<Estoque> carregarEstoque(EstoqueId estoqueId);
    
    /**
     * Passo 2: Valida se o estoque está inativo.
     * Método concreto com implementação padrão.
     * 
     * @param estoque O estoque a ser validado
     */
    protected void validarEstoqueInativo(Estoque estoque) {
        if (!estoque.isAtivo()) {
            throw new IllegalStateException("Não é possível atualizar um estoque inativo");
        }
    }
    
    /**
     * Passo 3: Valida pré-condições antes da atualização.
     * Método abstrato que deve ser implementado pelas subclasses.
     * Permite validar regras de negócio específicas.
     * 
     * @param estoqueId O ID do estoque
     * @param estoque O estoque a ser validado
     */
    protected abstract void validarPreCondicoes(EstoqueId estoqueId, Estoque estoque);
    
    /**
     * Passo 4: Hook executado antes da atualização.
     * Método concreto com implementação vazia - subclasses podem sobrescrever.
     * 
     * @param estoque O estoque a ser atualizado
     * @param estoqueId O ID do estoque
     */
    protected void antesDeAtualizar(Estoque estoque, EstoqueId estoqueId) {
    }
    
    /**
     * Passo 5: Aplica a atualização no estoque.
     * Método concreto com implementação padrão vazia.
     * Subclasses podem sobrescrever para processar mudanças específicas.
     * 
     * @param estoque O estoque a ser atualizado
     */
    protected void aplicarAtualizacao(Estoque estoque) {
    }
    
    /**
     * Passo 6: Salva o estoque atualizado.
     * Método abstrato que deve ser implementado pelas subclasses.
     * 
     * @param estoque O estoque a ser salvo
     */
    protected abstract void salvarEstoque(Estoque estoque);
    
    /**
     * Passo 7: Notifica todos os observers registrados sobre a atualização.
     * Método concreto com implementação padrão.
     * 
     * @param estoqueId O ID do estoque que foi atualizado
     */
    protected void notificarObservers(EstoqueId estoqueId) {
        for (EstoqueObserver observer : observers) {
            observer.aoAtualizarEstoque(estoqueId);
        }
    }
    
    /**
     * Passo 8: Hook executado após a atualização.
     * Método concreto com implementação vazia - subclasses podem sobrescrever.
     * 
     * @param estoque O estoque que foi atualizado
     * @param estoqueId O ID do estoque
     */
    protected void aposAtualizar(Estoque estoque, EstoqueId estoqueId) {
    }
}

