package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: Produto
 *
 * Responsabilidades:
 * - Gerenciar informações do produto
 * - Controlar ponto de ressuprimento
 * - Gerenciar lista de fornecedores
 * - Controlar ativação/desativação
 */
public class Produto {

    private final ProdutoId id;
    private String codigo;
    private String nome;
    private String descricao;
    private String unidadeMedida;
    private boolean ativo;
    private List<EstoqueId> estoques;
    private List<FornecedorId> fornecedores;
    private double pontoRessuprimento;
    private List<PedidoId> pedidosPendentes;

    public Produto(String codigo, String nome, String descricao, String unidadeMedida, EstoqueId estoque) {
        notBlank(codigo, "Código é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(descricao, "Descrição é obrigatória");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        notNull(estoque, "Estoque é obrigatório");

        this.id = new ProdutoId();
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.ativo = true;
        this.estoques = List.of(estoque);
        this.fornecedores = List.of();
        this.pontoRessuprimento = 0.0;
        this.pedidosPendentes = List.of();
    }

    public Produto(ProdutoId id, String codigo, String nome, String descricao, String unidadeMedida, 
                   boolean ativo, List<EstoqueId> estoques, List<FornecedorId> fornecedores, 
                   double pontoRessuprimento, List<PedidoId> pedidosPendentes) {
        notNull(id, "ID é obrigatório");
        notBlank(codigo, "Código é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(descricao, "Descrição é obrigatória");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        notNull(estoques, "Lista de estoques é obrigatória");
        notNull(fornecedores, "Lista de fornecedores é obrigatória");
        isTrue(pontoRessuprimento >= 0, "Ponto de ressuprimento não pode ser negativo");
        notNull(pedidosPendentes, "Lista de pedidos pendentes é obrigatória");

        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.ativo = ativo;
        this.estoques = List.copyOf(estoques);
        this.fornecedores = List.copyOf(fornecedores);
        this.pontoRessuprimento = pontoRessuprimento;
        this.pedidosPendentes = List.copyOf(pedidosPendentes);
    }

    /**
     * Define o ponto de ressuprimento
     */
    public void definirPontoRessuprimento(double novoPonto) {
        isTrue(novoPonto >= 0, "Ponto de ressuprimento não pode ser negativo");
        this.pontoRessuprimento = novoPonto;
    }

    /**
     * Vincula um fornecedor ao produto
     */
    public void vincularFornecedor(FornecedorId fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        if (!fornecedores.contains(fornecedor)) {
            var novaLista = new ArrayList<>(fornecedores);
            novaLista.add(fornecedor);
            this.fornecedores = List.copyOf(novaLista);
        }
    }

    /**
     * Desvincula um fornecedor do produto
     */
    public void desvincularFornecedor(FornecedorId fornecedor) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        this.fornecedores = fornecedores.stream()
                .filter(id -> !id.equals(fornecedor))
                .toList();
    }

    /**
     * Adiciona um estoque ao produto
     * R3H8: Todo produto deve estar vinculado a pelo menos um estoque ativo
     */
    public void adicionarEstoque(EstoqueId estoque) {
        notNull(estoque, "Estoque é obrigatório");
        if (!estoques.contains(estoque)) {
            var novaLista = new ArrayList<>(estoques);
            novaLista.add(estoque);
            this.estoques = List.copyOf(novaLista);
        }
    }

    /**
     * Remove um estoque do produto
     * R3H8: Todo produto deve estar vinculado a pelo menos um estoque ativo
     */
    public void removerEstoque(EstoqueId estoque) {
        notNull(estoque, "Estoque é obrigatório");
        if (estoques.size() <= 1) {
            throw new IllegalStateException("Produto deve estar vinculado a pelo menos um estoque");
        }
        this.estoques = estoques.stream()
                .filter(id -> !id.equals(estoque))
                .toList();
    }

    /**
     * Desativa o produto
     * R1H10: Um produto não pode ser inativado se houver saldo positivo em qualquer estoque ou pedidos em andamento associados a ele
     * R2H10: Ao inativar um produto, todas as novas cotações e pedidos para ele devem ser bloqueados
     */
    public void desativar() {
        // R1H10: Verifica se há pedidos pendentes
        if (!pedidosPendentes.isEmpty()) {
            throw new IllegalStateException("Não é possível desativar um produto que possui pedidos em andamento");
        }
        
        // R2H10: Bloqueia novas cotações e pedidos
        this.ativo = false;
    }

    /**
     * Adiciona um pedido pendente ao produto
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
     * Remove um pedido pendente do produto
     */
    public void removerPedidoPendente(PedidoId pedidoId) {
        notNull(pedidoId, "Pedido é obrigatório");
        this.pedidosPendentes = pedidosPendentes.stream()
                .filter(id -> !id.equals(pedidoId))
                .toList();
    }

    /**
     * Verifica se o produto está ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Verifica se o produto está inativo
     */
    public boolean isInativo() {
        return !ativo;
    }

    // Getters
    public ProdutoId getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public List<EstoqueId> getEstoques() { return List.copyOf(estoques); }
    public List<FornecedorId> getFornecedores() { return List.copyOf(fornecedores); }
    public double getPontoRessuprimento() { return pontoRessuprimento; }
    public List<PedidoId> getPedidosPendentes() { return List.copyOf(pedidosPendentes); }

    @Override
    public String toString() {
        return String.format("Produto[%s] - %s (%s) - %s", id, codigo, nome, unidadeMedida);
    }
}