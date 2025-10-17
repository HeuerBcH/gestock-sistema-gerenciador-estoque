package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;

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
    private String nome;
    private String descricao;
    private String unidadeMedida;
    private boolean ativo;
    private EstoqueId estoque;
    private List<FornecedorId> fornecedores;
    private double pontoRessuprimento;

    public Produto(String nome, String descricao, String unidadeMedida, EstoqueId estoque) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(descricao, "Descrição é obrigatória");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        notNull(estoque, "Estoque é obrigatório");

        this.id = new ProdutoId();
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.ativo = true;
        this.estoque = estoque;
        this.fornecedores = List.of();
        this.pontoRessuprimento = 0.0;
    }

    public Produto(ProdutoId id, String nome, String descricao, String unidadeMedida, 
                   boolean ativo, EstoqueId estoque, List<FornecedorId> fornecedores, 
                   double pontoRessuprimento) {
        notNull(id, "ID é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(descricao, "Descrição é obrigatória");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        notNull(estoque, "Estoque é obrigatório");
        notNull(fornecedores, "Lista de fornecedores é obrigatória");
        isTrue(pontoRessuprimento >= 0, "Ponto de ressuprimento não pode ser negativo");

        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.unidadeMedida = unidadeMedida;
        this.ativo = ativo;
        this.estoque = estoque;
        this.fornecedores = List.copyOf(fornecedores);
        this.pontoRessuprimento = pontoRessuprimento;
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
     * Desativa o produto
     */
    public void desativar() {
        this.ativo = false;
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
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public EstoqueId getEstoque() { return estoque; }
    public List<FornecedorId> getFornecedores() { return List.copyOf(fornecedores); }
    public double getPontoRessuprimento() { return pontoRessuprimento; }

    @Override
    public String toString() {
        return String.format("Produto[%s] - %s (%s)", id, nome, unidadeMedida);
    }
}