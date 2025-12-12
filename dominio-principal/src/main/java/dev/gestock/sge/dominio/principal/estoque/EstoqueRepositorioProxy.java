package dev.gestock.sge.dominio.principal.estoque;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy para EstoqueRepositorio que adiciona cache.
 * 
 * Responsabilidade:
 * - Controla acesso ao repositório real através de cache
 * - Melhora performance evitando consultas repetidas ao banco de dados
 * - Mantém o mesmo contrato da interface EstoqueRepositorio (transparência)
 * - Invalida cache automaticamente quando dados são modificados
 * 
 * Padrão: Proxy
 * - Controla acesso (cache) sem modificar a implementação base
 * - Diferente de Decorator: Proxy controla acesso, Decorator adiciona funcionalidade
 * - Permite empilhar com Decorator: Proxy (cache) → Decorator (auditoria) → Repositório real
 * 
 * Cache Strategy:
 * - Cache por ID: Armazena resultados de buscarPorId() em memória
 * - Invalidação: Cache é invalidado quando salvar() é chamado
 * - Thread-safe: Usa ConcurrentHashMap para acesso concorrente seguro
 */
public class EstoqueRepositorioProxy implements EstoqueRepositorio {
    
    private final EstoqueRepositorio alvo; // Repositório real
    private final Map<EstoqueId, Estoque> cachePorId = new ConcurrentHashMap<>();
    
    /**
     * Construtor do proxy.
     * 
     * @param alvo O repositório real que será envolvido pelo proxy (ex.: EstoqueRepositorioImpl)
     */
    public EstoqueRepositorioProxy(EstoqueRepositorio alvo) {
        if (alvo == null) {
            throw new IllegalArgumentException("Repositório alvo não pode ser nulo");
        }
        this.alvo = alvo;
    }
    
    @Override
    public Optional<Estoque> buscarPorId(EstoqueId id) {
        if (id == null) {
            return Optional.empty();
        }
        
        // Cache hit: retorna do cache se existir
        Estoque cached = cachePorId.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // Cache miss: busca no repositório real
        Optional<Estoque> estoqueOpt = alvo.buscarPorId(id);
        if (estoqueOpt.isPresent()) {
            // Armazena no cache para próximas consultas
            cachePorId.put(id, estoqueOpt.get());
        }
        return estoqueOpt;
    }
    
    @Override
    public void salvar(Estoque estoque) {
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque não pode ser nulo");
        }
        
        // Salva no repositório real
        alvo.salvar(estoque);
        
        // Atualiza o cache com a versão mais recente
        cachePorId.put(estoque.getId(), estoque);
    }
    
    @Override
    public List<Estoque> buscarEstoquesPorClienteId(ClienteId clienteId) {
        // Para listas, não fazemos cache (muito complexo e pode consumir muita memória)
        // Delega diretamente ao repositório real
        return alvo.buscarEstoquesPorClienteId(clienteId);
    }
    
    @Override
    public boolean existePorEndereco(String endereco, ClienteId clienteId) {
        // Operações de verificação não precisam de cache
        // São rápidas e podem ter resultados dinâmicos
        return alvo.existePorEndereco(endereco, clienteId);
    }
    
    @Override
    public boolean existePorNome(String nome, ClienteId clienteId) {
        // Operações de verificação não precisam de cache
        // São rápidas e podem ter resultados dinâmicos
        return alvo.existePorNome(nome, clienteId);
    }
    
    /**
     * Limpa o cache (útil para testes ou quando necessário invalidar cache manualmente).
     */
    public void limparCache() {
        cachePorId.clear();
    }
    
    /**
     * Remove um item específico do cache (útil para invalidação seletiva).
     * 
     * @param id ID do estoque a ser removido do cache
     */
    public void invalidarCache(EstoqueId id) {
        if (id != null) {
            cachePorId.remove(id);
        }
    }
}

