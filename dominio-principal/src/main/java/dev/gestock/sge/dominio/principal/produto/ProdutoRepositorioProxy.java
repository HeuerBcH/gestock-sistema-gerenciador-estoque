package dev.gestock.sge.dominio.principal.produto;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy para ProdutoRepositorio que adiciona cache.
 * 
 * Responsabilidade:
 * - Controla acesso ao repositório real através de cache
 * - Melhora performance evitando consultas repetidas ao banco de dados
 * - Mantém o mesmo contrato da interface ProdutoRepositorio (transparência)
 * - Invalida cache automaticamente quando dados são modificados
 * 
 * Padrão: Proxy
 * - Controla acesso (cache) sem modificar a implementação base
 * - Diferente de Decorator: Proxy controla acesso, Decorator adiciona funcionalidade
 * - Permite empilhar com Decorator: Proxy (cache) → Decorator (auditoria) → Repositório real
 * 
 * Cache Strategy:
 * - Cache por ID: Armazena resultados de buscarPorId() em memória
 * - Cache por Código: Armazena resultados de buscarPorCodigo() em memória
 * - Invalidação: Cache é invalidado quando salvar() ou inativar() são chamados
 * - Thread-safe: Usa ConcurrentHashMap para acesso concorrente seguro
 */
public class ProdutoRepositorioProxy implements ProdutoRepositorio {
    
    private final ProdutoRepositorio alvo; // Repositório real
    private final Map<ProdutoId, Produto> cachePorId = new ConcurrentHashMap<>();
    private final Map<CodigoProduto, ProdutoId> cachePorCodigo = new ConcurrentHashMap<>();
    
    /**
     * Construtor do proxy.
     * 
     * @param alvo O repositório real que será envolvido pelo proxy (ex.: ProdutoRepositorioImpl)
     */
    public ProdutoRepositorioProxy(ProdutoRepositorio alvo) {
        if (alvo == null) {
            throw new IllegalArgumentException("Repositório alvo não pode ser nulo");
        }
        this.alvo = alvo;
    }
    
    @Override
    public Optional<Produto> buscarPorId(ProdutoId id) {
        if (id == null) {
            return Optional.empty();
        }
        
        // Cache hit: retorna do cache se existir
        Produto cached = cachePorId.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        
        // Cache miss: busca no repositório real
        Optional<Produto> produtoOpt = alvo.buscarPorId(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            // Armazena no cache por ID
            cachePorId.put(id, produto);
            // Armazena também no cache por código para sincronização
            cachePorCodigo.put(new CodigoProduto(produto.getCodigo()), id);
        }
        return produtoOpt;
    }
    
    @Override
    public Optional<Produto> buscarPorCodigo(CodigoProduto codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        
        // Cache hit: verifica se temos o ID no cache por código
        ProdutoId idCached = cachePorCodigo.get(codigo);
        if (idCached != null) {
            Produto cached = cachePorId.get(idCached);
            if (cached != null) {
                return Optional.of(cached);
            }
        }
        
        // Cache miss: busca no repositório real
        Optional<Produto> produtoOpt = alvo.buscarPorCodigo(codigo);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            // Armazena no cache por ID
            cachePorId.put(produto.getId(), produto);
            // Armazena no cache por código
            cachePorCodigo.put(codigo, produto.getId());
        }
        return produtoOpt;
    }
    
    @Override
    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        
        // Salva no repositório real
        alvo.salvar(produto);
        
        // Atualiza o cache com a versão mais recente
        cachePorId.put(produto.getId(), produto);
        cachePorCodigo.put(new CodigoProduto(produto.getCodigo()), produto.getId());
    }
    
    @Override
    public boolean codigoExiste(CodigoProduto codigo) {
        // Operações de verificação podem usar cache
        return buscarPorCodigo(codigo).isPresent();
    }
    
    @Override
    public void inativar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        
        // Inativa no repositório real
        alvo.inativar(produto);
        
        // Atualiza o cache com a versão inativada
        cachePorId.put(produto.getId(), produto);
        // Mantém o cache por código sincronizado
        cachePorCodigo.put(new CodigoProduto(produto.getCodigo()), produto.getId());
    }
    
    @Override
    public void remover(ProdutoId id) {
        if (id == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo");
        }
        
        // Busca o produto antes de remover para invalidar cache
        Optional<Produto> produtoOpt = buscarPorId(id);
        if (produtoOpt.isPresent()) {
            Produto produto = produtoOpt.get();
            // Remove do repositório real
            alvo.remover(id);
            // Remove do cache
            cachePorId.remove(id);
            cachePorCodigo.remove(new CodigoProduto(produto.getCodigo()));
        } else {
            // Se não está no cache, apenas remove do repositório
            alvo.remover(id);
        }
    }
    
    /**
     * Limpa o cache (útil para testes ou quando necessário invalidar cache manualmente).
     */
    public void limparCache() {
        cachePorId.clear();
        cachePorCodigo.clear();
    }
    
    /**
     * Remove um item específico do cache (útil para invalidação seletiva).
     * 
     * @param id ID do produto a ser removido do cache
     */
    public void invalidarCache(ProdutoId id) {
        if (id != null) {
            Produto produto = cachePorId.remove(id);
            if (produto != null) {
                // Remove também do cache por código
                cachePorCodigo.remove(new CodigoProduto(produto.getCodigo()));
            }
        }
    }
}

