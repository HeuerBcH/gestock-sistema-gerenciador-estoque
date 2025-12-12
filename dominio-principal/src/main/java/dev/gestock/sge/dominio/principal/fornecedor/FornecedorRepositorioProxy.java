package dev.gestock.sge.dominio.principal.fornecedor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy para FornecedorRepositorio que adiciona cache.
 * 
 * Responsabilidade:
 * - Controla acesso ao repositório real através de cache
 * - Melhora performance evitando consultas repetidas ao banco de dados
 * - Mantém o mesmo contrato da interface FornecedorRepositorio (transparência)
 * - Invalida cache automaticamente quando dados são modificados
 * 
 * Padrão: Proxy
 * - Controla acesso (cache) sem modificar a implementação base
 * - Diferente de Decorator: Proxy controla acesso, Decorator adiciona funcionalidade
 * - Permite empilhar com Decorator: Proxy (cache) → Decorator (auditoria) → Repositório real
 * 
 * Cache Strategy:
 * - Cache por ID: Armazena resultados de buscarPorId() em memória
 * - Cache por CNPJ: Armazena resultados de buscarPorCnpj() em memória
 * - Invalidação: Cache é invalidado quando salvar() é chamado
 * - Thread-safe: Usa ConcurrentHashMap para acesso concorrente seguro
 */
public class FornecedorRepositorioProxy implements FornecedorRepositorio {
    
    private final FornecedorRepositorio alvo; // Repositório real
    private final Map<FornecedorId, Fornecedor> cachePorId = new ConcurrentHashMap<>();
    private final Map<String, FornecedorId> cachePorCnpj = new ConcurrentHashMap<>();
    
    /**
     * Construtor do proxy.
     * 
     * @param alvo O repositório real que será envolvido pelo proxy (ex.: FornecedorRepositorioImpl)
     */
    public FornecedorRepositorioProxy(FornecedorRepositorio alvo) {
        if (alvo == null) {
            throw new IllegalArgumentException("Repositório alvo não pode ser nulo");
        }
        this.alvo = alvo;
    }
    
    @Override
    public Optional<Fornecedor> buscarPorId(FornecedorId id) {
        if (id == null) {
            return Optional.empty();
        }
        
        // Cache hit: retorna do cache se existir
        Fornecedor cached = cachePorId.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // Cache miss: busca no repositório real
        Optional<Fornecedor> fornecedorOpt = alvo.buscarPorId(id);
        if (fornecedorOpt.isPresent()) {
            Fornecedor fornecedor = fornecedorOpt.get();
            // Armazena no cache por ID
            cachePorId.put(id, fornecedor);
            // Armazena também no cache por CNPJ para sincronização
            cachePorCnpj.put(fornecedor.getCnpj(), id);
        }
        return fornecedorOpt;
    }
    
    @Override
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // Cache hit: verifica se temos o ID no cache por CNPJ
        FornecedorId idCached = cachePorCnpj.get(cnpj);
        if (idCached != null) {
            Fornecedor cached = cachePorId.get(idCached);
            if (cached != null) {
                return Optional.of(cached);
            }
        }
        
        // Cache miss: busca no repositório real
        Optional<Fornecedor> fornecedorOpt = alvo.buscarPorCnpj(cnpj);
        if (fornecedorOpt.isPresent()) {
            Fornecedor fornecedor = fornecedorOpt.get();
            // Armazena no cache por ID
            cachePorId.put(fornecedor.getId(), fornecedor);
            // Armazena no cache por CNPJ
            cachePorCnpj.put(cnpj, fornecedor.getId());
        }
        return fornecedorOpt;
    }
    
    @Override
    public void salvar(Fornecedor fornecedor) {
        if (fornecedor == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        
        // Salva no repositório real
        alvo.salvar(fornecedor);
        
        // Atualiza o cache com a versão mais recente
        cachePorId.put(fornecedor.getId(), fornecedor);
        cachePorCnpj.put(fornecedor.getCnpj(), fornecedor.getId());
    }
    
    /**
     * Limpa o cache (útil para testes ou quando necessário invalidar cache manualmente).
     */
    public void limparCache() {
        cachePorId.clear();
        cachePorCnpj.clear();
    }
    
    /**
     * Remove um item específico do cache (útil para invalidação seletiva).
     * 
     * @param id ID do fornecedor a ser removido do cache
     */
    public void invalidarCache(FornecedorId id) {
        if (id != null) {
            Fornecedor fornecedor = cachePorId.remove(id);
            if (fornecedor != null) {
                // Remove também do cache por CNPJ
                cachePorCnpj.remove(fornecedor.getCnpj());
            }
        }
    }
}

