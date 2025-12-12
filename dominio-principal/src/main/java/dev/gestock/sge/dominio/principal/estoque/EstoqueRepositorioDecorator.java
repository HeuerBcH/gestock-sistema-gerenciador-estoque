package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

import java.util.List;
import java.util.Optional;

/**
 * Decorator para EstoqueRepositorio que adiciona funcionalidade de auditoria.
 * 
 * Responsabilidade:
 * - Envolve um EstoqueRepositorio real (alvo) e adiciona comportamento de auditoria
 * - Mantém o mesmo contrato da interface EstoqueRepositorio (transparência)
 * - Intercepta operações e registra eventos de auditoria antes de delegar ao repositório alvo
 * 
 * Padrão: Decorator
 * - Adiciona funcionalidade (auditoria) sem modificar a implementação base
 * - Mantém a mesma interface do objeto decorado
 * - Permite empilhar múltiplos decorators (ex.: cache → auditoria → repositório)
 * 
 * Diferença para Proxy:
 * - Decorator: adiciona funcionalidade (comportamento adicional)
 * - Proxy: controla acesso (lazy loading, segurança, cache)
 */
public class EstoqueRepositorioDecorator implements EstoqueRepositorio {
    
    private final EstoqueRepositorio alvo;
    private final EstoqueAuditoria auditoria;
    
    /**
     * Construtor do decorator.
     * 
     * @param alvo O repositório real que será decorado (ex.: EstoqueRepositorioImpl)
     * @param auditoria A implementação de auditoria a ser usada
     */
    public EstoqueRepositorioDecorator(EstoqueRepositorio alvo, EstoqueAuditoria auditoria) {
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
    public void salvar(Estoque estoque) {
        // Registra auditoria antes de delegar
        auditoria.registrarSalvar(estoque);
        // Delega a operação real ao repositório alvo
        alvo.salvar(estoque);
    }
    
    @Override
    public Optional<Estoque> buscarPorId(EstoqueId id) {
        // Registra auditoria antes de delegar
        auditoria.registrarLeitura(id);
        // Delega a operação real ao repositório alvo
        return alvo.buscarPorId(id);
    }
    
    @Override
    public List<Estoque> buscarEstoquesPorClienteId(ClienteId clienteId) {
        // Registra auditoria antes de delegar
        auditoria.registrarBuscaPorCliente(clienteId);
        // Delega a operação real ao repositório alvo
        return alvo.buscarEstoquesPorClienteId(clienteId);
    }
    
    @Override
    public boolean existePorEndereco(String endereco, ClienteId clienteId) {
        // Operações de verificação não precisam de auditoria detalhada
        // Mas poderíamos adicionar se necessário
        return alvo.existePorEndereco(endereco, clienteId);
    }
    
    @Override
    public boolean existePorNome(String nome, ClienteId clienteId) {
        // Operações de verificação não precisam de auditoria detalhada
        // Mas poderíamos adicionar se necessário
        return alvo.existePorNome(nome, clienteId);
    }
}

