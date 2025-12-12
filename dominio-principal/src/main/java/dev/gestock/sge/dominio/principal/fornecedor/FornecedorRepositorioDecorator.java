package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Optional;

/**
 * Decorator para FornecedorRepositorio que adiciona funcionalidade de auditoria.
 * 
 * Responsabilidade:
 * - Envolve um FornecedorRepositorio real (alvo) e adiciona comportamento de auditoria
 * - Mantém o mesmo contrato da interface FornecedorRepositorio (transparência)
 * - Intercepta operações e registra eventos de auditoria antes de delegar ao repositório alvo
 * 
 * Padrão: Decorator
 * - Adiciona funcionalidade (auditoria) sem modificar a implementação base
 * - Mantém a mesma interface do objeto decorado
 * - Permite empilhar múltiplos decorators (ex.: cache → auditoria → repositório)
 */
public class FornecedorRepositorioDecorator implements FornecedorRepositorio {
    
    private final FornecedorRepositorio alvo;
    private final FornecedorAuditoria auditoria;
    
    /**
     * Construtor do decorator.
     * 
     * @param alvo O repositório real que será decorado (ex.: FornecedorRepositorioImpl)
     * @param auditoria A implementação de auditoria a ser usada
     */
    public FornecedorRepositorioDecorator(FornecedorRepositorio alvo, FornecedorAuditoria auditoria) {
        if (alvo == null) {
            throw new IllegalArgumentException("Repositório alvo não pode ser nulo");
        }
        if (auditoria == null) {
            throw new IllegalArgumentException("Auditoria não pode ser nula");
        }
        this.alvo = alvo;
        this.auditoria = auditoria;
    }
    
    @Override
    public void salvar(Fornecedor fornecedor) {
        auditoria.registrarSalvar(fornecedor);
        alvo.salvar(fornecedor);
    }
    
    @Override
    public Optional<Fornecedor> buscarPorId(FornecedorId id) {
        auditoria.registrarLeitura(id);
        return alvo.buscarPorId(id);
    }
    
    @Override
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        auditoria.registrarBuscaPorCnpj(cnpj);
        return alvo.buscarPorCnpj(cnpj);
    }
}

