package dev.gestock.sge.dominio.principal.fornecedor;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: Fornecedor
 *
 * Responsabilidades:
 * - Gerenciar informações do fornecedor
 * - Controlar lead time
 * - Gerenciar cotações
 * - Controlar ativação/desativação
 */
public class Fornecedor {

    private final FornecedorId id;
    private String nome;
    private String cnpj;
    private String contato;
    private int leadTime; // em dias
    private boolean ativo;
    private List<Cotacao> cotacoes;

    public Fornecedor(String nome, String cnpj, String contato, int leadTime) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");
        notBlank(contato, "Contato é obrigatório");
        isTrue(leadTime > 0, "Lead time deve ser positivo");

        this.id = new FornecedorId();
        this.nome = nome;
        this.cnpj = cnpj;
        this.contato = contato;
        this.leadTime = leadTime;
        this.ativo = true;
        this.cotacoes = List.of();
    }

    public Fornecedor(FornecedorId id, String nome, String cnpj, String contato, 
                      int leadTime, boolean ativo, List<Cotacao> cotacoes) {
        notNull(id, "ID é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");
        notBlank(contato, "Contato é obrigatório");
        isTrue(leadTime > 0, "Lead time deve ser positivo");
        notNull(cotacoes, "Lista de cotações é obrigatória");

        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.contato = contato;
        this.leadTime = leadTime;
        this.ativo = ativo;
        this.cotacoes = List.copyOf(cotacoes);
    }

    /**
     * Atualiza cotação para um produto
     */
    public void atualizarCotacao(ProdutoId produto, double preco, LocalDate validade) {
        notNull(produto, "Produto é obrigatório");
        isTrue(preco > 0, "Preço deve ser positivo");
        notNull(validade, "Validade é obrigatória");

        Cotacao novaCotacao = new Cotacao(produto, preco, validade);
        var novaLista = new ArrayList<>(cotacoes);
        novaLista.add(novaCotacao);
        this.cotacoes = List.copyOf(novaLista);
    }

    /**
     * Desativa o fornecedor
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Verifica se o fornecedor está ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Verifica se o fornecedor está inativo
     */
    public boolean isInativo() {
        return !ativo;
    }

    // Getters
    public FornecedorId getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getContato() { return contato; }
    public int getLeadTime() { return leadTime; }
    public List<Cotacao> getCotacoes() { return List.copyOf(cotacoes); }

    @Override
    public String toString() {
        return String.format("Fornecedor[%s] - %s (%s)", id, nome, cnpj);
    }
}