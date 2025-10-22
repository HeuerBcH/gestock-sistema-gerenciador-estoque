package dev.gestock.sge.infraestrutura.persistencia.memoria;

import dev.gestock.sge.dominio.principal.alerta.*;
import dev.gestock.sge.dominio.principal.cliente.*;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.pedido.*;
import dev.gestock.sge.dominio.principal.produto.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementação em memória de todos os repositórios de domínio.
 * Utilizada principalmente para testes.
 * * Características:
 * - Armazenamento volátil (dados perdidos ao reiniciar)
 * - Thread-safe usando ConcurrentHashMap
 * - Geração automática de IDs
 * - Implementa todos os repositórios: Estoque, Produto, Fornecedor, Pedido, Alerta, Cliente
 */
public class Repositorio implements
        EstoqueRepositorio,
        ProdutoRepositorio,
        FornecedorRepositorio,
        PedidoRepositorio,
        AlertaRepositorio,
        ClienteRepositorio {

    // Armazenamento em memória
    private final Map<EstoqueId, Estoque> estoques = new ConcurrentHashMap<>();
    private final Map<ProdutoId, Produto> produtos = new ConcurrentHashMap<>();
    private final Map<FornecedorId, Fornecedor> fornecedores = new ConcurrentHashMap<>();
    private final Map<PedidoId, Pedido> pedidos = new ConcurrentHashMap<>();
    private final Map<AlertaId, Alerta> alertas = new ConcurrentHashMap<>();
    private final Map<ClienteId, Cliente> clientes = new ConcurrentHashMap<>();

    // Geradores de ID
    private final AtomicLong estoqueIdSeq = new AtomicLong(1);
    private final AtomicLong produtoIdSeq = new AtomicLong(1);
    private final AtomicLong fornecedorIdSeq = new AtomicLong(1);
    private final AtomicLong pedidoIdSeq = new AtomicLong(1);
    private final AtomicLong alertaIdSeq = new AtomicLong(1);
    private final AtomicLong clienteIdSeq = new AtomicLong(1);

    // ==================== EstoqueRepositorio ====================

    @Override
    public void salvar(Estoque estoque) {
        if (estoque == null) {
            throw new IllegalArgumentException("Estoque não pode ser nulo");
        }
        estoques.put(estoque.getId(), estoque);
    }

    @Override
    public Optional<Estoque> buscarPorId(EstoqueId id) {
        return Optional.ofNullable(estoques.get(id));
    }

    @Override
    public List<Estoque> buscarEstoquesPorClienteId(ClienteId clienteId) {
        if (clienteId == null) {
            return Collections.emptyList();
        }
        return estoques.values().stream()
                .filter(e -> e.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorEndereco(String endereco, ClienteId clienteId) {
        if (endereco == null || clienteId == null) {
            return false;
        }
        return estoques.values().stream()
                .anyMatch(e -> e.getClienteId().equals(clienteId) &&
                        e.getEndereco().equalsIgnoreCase(endereco));
    }

    @Override
    public boolean existePorNome(String nome, ClienteId clienteId) {
        if (nome == null || clienteId == null) {
            return false;
        }
        return estoques.values().stream()
                .anyMatch(e -> e.getClienteId().equals(clienteId) &&
                        e.getNome().equalsIgnoreCase(nome));
    }

    // ==================== ProdutoRepositorio ====================

    @Override
    public void salvar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        produtos.put(produto.getId(), produto);
    }

    @Override
    public Optional<Produto> buscarPorId(ProdutoId id) {
        return Optional.ofNullable(produtos.get(id));
    }

    @Override
    public Optional<Produto> buscarPorCodigo(CodigoProduto codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        return produtos.values().stream()
                .filter(p -> new CodigoProduto(p.getCodigo()).equals(codigo))
                .findFirst();
    }

    @Override
    public boolean codigoExiste(CodigoProduto codigo) {
        return buscarPorCodigo(codigo).isPresent();
    }

    @Override
    public void inativar(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }
        produto.inativar();
        produtos.put(produto.getId(), produto);
    }

    // ==================== FornecedorRepositorio ====================

    @Override
    public void salvar(Fornecedor fornecedor) {
        if (fornecedor == null) {
            throw new IllegalArgumentException("Fornecedor não pode ser nulo");
        }
        fornecedores.put(fornecedor.getId(), fornecedor);
    }

    @Override
    public Optional<Fornecedor> buscarPorId(FornecedorId id) {
        return Optional.ofNullable(fornecedores.get(id));
    }

    @Override
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        if (cnpj == null) {
            return Optional.empty();
        }
        return fornecedores.values().stream()
                .filter(f -> f.getCnpj().equals(cnpj))
                .findFirst();
    }

    // ==================== PedidoRepositorio ====================

    @Override
    public void salvar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public Optional<Pedido> buscarPorId(PedidoId id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos.values());
    }

    @Override
    public void cancelar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não pode ser nulo");
        }
        pedido.cancelar();
        pedidos.put(pedido.getId(), pedido);
    }

    @Override
    public List<Pedido> buscarPorStatus(StatusPedido status) {
        if (status == null) {
            return Collections.emptyList();
        }
        return pedidos.values().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> buscarPorFornecedorId(FornecedorId fornecedorId) {
        if (fornecedorId == null) {
            return Collections.emptyList();
        }
        return pedidos.values().stream()
                .filter(p -> p.getFornecedorId().equals(fornecedorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pedido> buscarPedidosPorClienteId(ClienteId clienteId) {
        if (clienteId == null) {
            return Collections.emptyList();
        }
        return pedidos.values().stream()
                .filter(p -> p.getClienteId().equals(clienteId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePedidoPendenteParaFornecedor(FornecedorId fornecedorId) {
        if (fornecedorId == null) {
            return false;
        }
        return pedidos.values().stream()
                .anyMatch(p -> p.getFornecedorId().equals(fornecedorId) &&
                        (p.getStatus() == StatusPedido.CRIADO ||
                                p.getStatus() == StatusPedido.ENVIADO ||
                                p.getStatus() == StatusPedido.EM_TRANSPORTE));
    }

    // ==================== AlertaRepositorio ====================

    @Override
    public void salvar(Alerta alerta) {
        if (alerta == null) {
            throw new IllegalArgumentException("Alerta não pode ser nulo");
        }
        alertas.put(alerta.getId(), alerta);
    }

    @Override
    public Optional<Alerta> obter(AlertaId id) {
        return Optional.ofNullable(alertas.get(id));
    }

    @Override
    public List<Alerta> listarAtivos() {
        return alertas.values().stream()
                .filter(Alerta::isAtivo)
                .collect(Collectors.toList());
    }

    @Override
    public List<Alerta> listarPorProduto(ProdutoId produtoId) {
        if (produtoId == null) {
            return Collections.emptyList();
        }
        return alertas.values().stream()
                .filter(a -> a.getProdutoId().equals(produtoId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Alerta> listarPorEstoque(EstoqueId estoqueId) {
        if (estoqueId == null) {
            return Collections.emptyList();
        }
        return alertas.values().stream()
                .filter(a -> a.getEstoqueId().equals(estoqueId))
                .collect(Collectors.toList());
    }

    // ==================== ClienteRepositorio ====================

    @Override
    public void salvar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        clientes.put(cliente.getId(), cliente);
    }

    @Override
    public Optional<Cliente> buscarPorId(ClienteId id) {
        return Optional.ofNullable(clientes.get(id));
    }

    // ==================== Métodos Utilitários ====================

    /**
     * Retorna todos os objetos de um tipo específico (útil para testes).
     * @param tipo A classe dos objetos a serem retornados.
     * @param <T> O tipo de objeto (ex: Produto, Estoque).
     * @return Uma coleção de todos os objetos do tipo especificado.
     */
    @SuppressWarnings("unchecked")
    public <T> Collection<T> buscarTodos(Class<T> tipo) {
        if (tipo.equals(Estoque.class)) {
            return (Collection<T>) estoques.values();
        } else if (tipo.equals(Produto.class)) {
            return (Collection<T>) produtos.values();
        } else if (tipo.equals(Fornecedor.class)) {
            return (Collection<T>) fornecedores.values();
        } else if (tipo.equals(Pedido.class)) {
            return (Collection<T>) pedidos.values();
        } else if (tipo.equals(Alerta.class)) {
            return (Collection<T>) alertas.values();
        } else if (tipo.equals(Cliente.class)) {
            return (Collection<T>) clientes.values();
        }
        return Collections.emptyList();
    }

    /**
     * Gera um novo ID de estoque.
     */
    public EstoqueId novoEstoqueId() {
        return new EstoqueId(estoqueIdSeq.getAndIncrement());
    }

    /**
     * Gera um novo ID de produto.
     */
    public ProdutoId novoProdutoId() {
        return new ProdutoId(produtoIdSeq.getAndIncrement());
    }

    /**
     * Gera um novo ID de fornecedor.
     */
    public FornecedorId novoFornecedorId() {
        return new FornecedorId(fornecedorIdSeq.getAndIncrement());
    }

    /**
     * Gera um novo ID de pedido.
     */
    public PedidoId novoPedidoId() {
        return new PedidoId(pedidoIdSeq.getAndIncrement());
    }

    /**
     * Gera um novo ID de alerta.
     */
    public AlertaId novoAlertaId() {
        return new AlertaId(alertaIdSeq.getAndIncrement());
    }

    /**
     * Gera um novo ID de cliente.
     */
    public ClienteId novoClienteId() {
        return new ClienteId(clienteIdSeq.getAndIncrement());
    }

    /**
     * Limpa todos os dados armazenados (útil para testes).
     */
    public void limparTodos() {
        estoques.clear();
        produtos.clear();
        fornecedores.clear();
        pedidos.clear();
        alertas.clear();
        clientes.clear();
    }

    /**
     * Retorna estatísticas do repositório.
     */
    public String estatisticas() {
        return String.format(
                "Repositório em Memória:\n" +
                        "  - Estoques: %d\n" +
                        "  - Produtos: %d\n" +
                        "  - Fornecedores: %d\n" +
                        "  - Pedidos: %d\n" +
                        "  - Alertas: %d\n" +
                        "  - Clientes: %d",
                estoques.size(),
                produtos.size(),
                fornecedores.size(),
                pedidos.size(),
                alertas.size(),
                clientes.size()
        );
    }
}