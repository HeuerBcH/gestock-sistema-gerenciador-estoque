package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: Estoque
 *
 * Responsabilidades:
 * - Gerenciar estoques do cliente
 * - Controlar capacidade máxima
 * - Manter lista de produtos no estoque
 * - Controlar ativação/desativação
 */
public class Estoque {

    private final EstoqueId id;
    private String nome;
    private String endereco;
    private double capacidadeMaxima;
    private boolean ativo;
    private ClienteId cliente;
    private List<ProdutoEstoque> produtos;
    private List<PedidoId> pedidosPendentes;

    public Estoque(String nome, String endereco, double capacidadeMaxima, ClienteId cliente) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(endereco, "Endereço é obrigatório");
        isTrue(capacidadeMaxima > 0, "Capacidade máxima deve ser positiva");
        notNull(cliente, "Cliente é obrigatório");

        this.id = new EstoqueId();
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativo = true;
        this.cliente = cliente;
        this.produtos = List.of();
        this.pedidosPendentes = List.of();
    }

    public Estoque(EstoqueId id, String nome, String endereco, double capacidadeMaxima, 
                   boolean ativo, ClienteId cliente, List<ProdutoEstoque> produtos, 
                   List<PedidoId> pedidosPendentes) {
        notNull(id, "ID é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(endereco, "Endereço é obrigatório");
        isTrue(capacidadeMaxima > 0, "Capacidade máxima deve ser positiva");
        notNull(cliente, "Cliente é obrigatório");
        notNull(produtos, "Lista de produtos é obrigatória");
        notNull(pedidosPendentes, "Lista de pedidos pendentes é obrigatória");

        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativo = ativo;
        this.cliente = cliente;
        this.produtos = List.copyOf(produtos);
        this.pedidosPendentes = List.copyOf(pedidosPendentes);
    }

    /**
     * Registra um produto no estoque
     */
    public void registrarProduto(ProdutoId produto, double quantidade) {
        notNull(produto, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");

        ProdutoEstoque produtoEstoque = new ProdutoEstoque(produto, quantidade);
        var novaLista = new ArrayList<>(produtos);
        novaLista.add(produtoEstoque);
        this.produtos = List.copyOf(novaLista);
    }

    /**
     * Atualiza a capacidade máxima do estoque
     * R1H3: O tamanho de um estoque não pode ser diminuído caso o mesmo esteja com produtos ocupando a capacidade máxima
     */
    public void atualizarCapacidade(double novaCapacidade) {
        isTrue(novaCapacidade > 0, "Capacidade máxima deve ser positiva");
        
        // R1H3: Verifica se a nova capacidade é menor que a atual e se há produtos ocupando a capacidade máxima
        if (novaCapacidade < capacidadeMaxima) {
            double capacidadeOcupada = calcularCapacidadeOcupada();
            if (capacidadeOcupada >= capacidadeMaxima) {
                throw new IllegalStateException("Não é possível diminuir a capacidade de um estoque que está ocupando sua capacidade máxima");
            }
        }
        
        this.capacidadeMaxima = novaCapacidade;
    }

    /**
     * Calcula a capacidade ocupada pelo estoque
     */
    private double calcularCapacidadeOcupada() {
        return produtos.stream()
                .mapToDouble(ProdutoEstoque::getQuantidade)
                .sum();
    }

    /**
     * Verifica se o estoque tem capacidade disponível
     */
    public boolean temCapacidadeDisponivel(double quantidade) {
        double capacidadeOcupada = calcularCapacidadeOcupada();
        return (capacidadeOcupada + quantidade) <= capacidadeMaxima;
    }

    /**
     * Verifica se o estoque está na capacidade máxima
     */
    public boolean isCapacidadeMaxima() {
        return calcularCapacidadeOcupada() >= capacidadeMaxima;
    }

    /**
     * Desativa o estoque
     * R1H2: Um estoque que ainda possui produtos não pode ser removido
     * R2H2: Um estoque que possui um pedido alocado em andamento não pode ser removido
     */
    public void desativar() {
        // R1H2: Verifica se há produtos no estoque
        if (!produtos.isEmpty()) {
            throw new IllegalStateException("Não é possível desativar um estoque que ainda possui produtos");
        }
        
        // R2H2: Verifica se há pedidos pendentes
        if (!pedidosPendentes.isEmpty()) {
            throw new IllegalStateException("Não é possível desativar um estoque que possui pedidos pendentes");
        }
        
        this.ativo = false;
    }

    /**
     * Adiciona um pedido pendente ao estoque
     */
    public void adicionarPedidoPendente(PedidoId pedidoId) {
        notNull(pedidoId, "Pedido é obrigatório");
        if (!pedidosPendentes.contains(pedidoId)) {
            var novaLista = new ArrayList<>(pedidosPendentes);
            novaLista.add(pedidoId);
            this.pedidosPendentes = List.copyOf(novaLista);
        }
    }

    /**
     * Remove um pedido pendente do estoque
     */
    public void removerPedidoPendente(PedidoId pedidoId) {
        notNull(pedidoId, "Pedido é obrigatório");
        this.pedidosPendentes = pedidosPendentes.stream()
                .filter(id -> !id.equals(pedidoId))
                .toList();
    }

    /**
     * Verifica se o estoque está ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Verifica se o estoque está inativo
     */
    public boolean isInativo() {
        return !ativo;
    }

    // Getters
    public EstoqueId getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public double getCapacidadeMaxima() { return capacidadeMaxima; }
    public ClienteId getCliente() { return cliente; }
    public List<ProdutoEstoque> getProdutos() { return List.copyOf(produtos); }
    public List<PedidoId> getPedidosPendentes() { return List.copyOf(pedidosPendentes); }

    @Override
    public String toString() {
        return String.format("Estoque[%s] - %s (%s)", id, nome, endereco);
    }
}