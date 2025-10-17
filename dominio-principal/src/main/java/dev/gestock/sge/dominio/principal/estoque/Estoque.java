package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

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
    }

    public Estoque(EstoqueId id, String nome, String endereco, double capacidadeMaxima, 
                   boolean ativo, ClienteId cliente, List<ProdutoEstoque> produtos) {
        notNull(id, "ID é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(endereco, "Endereço é obrigatório");
        isTrue(capacidadeMaxima > 0, "Capacidade máxima deve ser positiva");
        notNull(cliente, "Cliente é obrigatório");
        notNull(produtos, "Lista de produtos é obrigatória");

        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativo = ativo;
        this.cliente = cliente;
        this.produtos = List.copyOf(produtos);
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
     */
    public void atualizarCapacidade(double novaCapacidade) {
        isTrue(novaCapacidade > 0, "Capacidade máxima deve ser positiva");
        this.capacidadeMaxima = novaCapacidade;
    }

    /**
     * Desativa o estoque
     */
    public void desativar() {
        this.ativo = false;
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

    @Override
    public String toString() {
        return String.format("Estoque[%s] - %s (%s)", id, nome, endereco);
    }
}