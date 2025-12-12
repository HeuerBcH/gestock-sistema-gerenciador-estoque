package dev.gestock.sge.dominio.principal.produto;

import java.util.Optional;

/**
 * Decorator para ProdutoRepositorio que adiciona funcionalidade de auditoria.
 * 
 * Responsabilidade:
 * - Envolve um ProdutoRepositorio real (alvo) e adiciona comportamento de auditoria
 * - Mantém o mesmo contrato da interface ProdutoRepositorio (transparência)
 * - Intercepta operações e registra eventos de auditoria antes de delegar ao repositório alvo
 * 
 * Padrão: Decorator
 * - Adiciona funcionalidade (auditoria) sem modificar a implementação base
 * - Mantém a mesma interface do objeto decorado
 * - Permite empilhar múltiplos decorators (ex.: cache → auditoria → repositório)
 */
public class ProdutoRepositorioDecorator implements ProdutoRepositorio {
    
    private final ProdutoRepositorio alvo;
    private final ProdutoAuditoria auditoria;
    
    /**
     * Construtor do decorator.
     * 
     * @param alvo O repositório real que será decorado (ex.: ProdutoRepositorioImpl)
     * @param auditoria A implementação de auditoria a ser usada
     */
    public ProdutoRepositorioDecorator(ProdutoRepositorio alvo, ProdutoAuditoria auditoria) {
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
    public void salvar(Produto produto) {
        auditoria.registrarSalvar(produto);
        alvo.salvar(produto);
    }
    
    @Override
    public Optional<Produto> buscarPorId(ProdutoId id) {
        auditoria.registrarLeitura(id);
        return alvo.buscarPorId(id);
    }
    
    @Override
    public Optional<Produto> buscarPorCodigo(CodigoProduto codigo) {
        auditoria.registrarBuscaPorCodigo(codigo);
        return alvo.buscarPorCodigo(codigo);
    }
    
    @Override
    public boolean codigoExiste(CodigoProduto codigo) {
        // Operações de verificação não precisam de auditoria detalhada
        return alvo.codigoExiste(codigo);
    }
    
    @Override
    public void inativar(Produto produto) {
        auditoria.registrarInativacao(produto);
        alvo.inativar(produto);
    }
}

