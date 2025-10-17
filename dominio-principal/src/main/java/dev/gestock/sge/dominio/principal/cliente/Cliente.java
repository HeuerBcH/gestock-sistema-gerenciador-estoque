package dev.gestock.sge.dominio.principal.cliente;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate Root: Cliente
 *
 * Responsabilidades:
 * - Gerenciar informações do cliente
 * - Controlar ativação/desativação
 * - Manter dados de contato e identificação
 * - Gerenciar lista de estoques
 */
public class Cliente {

    private final ClienteId id;
    private String nome;
    private String email;
    private String cnpj;
    private boolean ativo;
    private List<EstoqueId> estoques;

    public Cliente(String nome, String email, String cnpj) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(email, "Email é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");

        this.id = new ClienteId();
        this.nome = nome;
        this.email = email;
        this.cnpj = cnpj;
        this.ativo = true;
        this.estoques = List.of();
    }

    public Cliente(ClienteId id, String nome, String email, String cnpj, boolean ativo, List<EstoqueId> estoques) {
        notNull(id, "ID é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(email, "Email é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");
        notNull(estoques, "Lista de estoques é obrigatória");

        this.id = id;
        this.nome = nome;
        this.email = email;
        this.cnpj = cnpj;
        this.ativo = ativo;
        this.estoques = List.copyOf(estoques);
    }

    /**
     * Ativa o cliente
     */
    public void ativar() {
        this.ativo = true;
    }

    /**
     * Desativa o cliente
     */
    public void desativar() {
        this.ativo = false;
    }

    /**
     * Adiciona um estoque à lista do cliente
     */
    public void adicionarEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        if (!estoques.contains(estoqueId)) {
            var novaLista = new ArrayList<>(estoques);
            novaLista.add(estoqueId);
            this.estoques = List.copyOf(novaLista);
        }
    }

    /**
     * Remove um estoque da lista do cliente
     */
    public void removerEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        this.estoques = estoques.stream()
                .filter(id -> !id.equals(estoqueId))
                .toList();
    }

    /**
     * Verifica se o cliente está ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Verifica se o cliente está inativo
     */
    public boolean isInativo() {
        return !ativo;
    }

    // Getters
    public ClienteId getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getCnpj() { return cnpj; }
    public List<EstoqueId> getEstoques() { return List.copyOf(estoques); }

    @Override
    public String toString() {
        return String.format("Cliente[%s] - %s (%s)", id, nome, cnpj);
    }
}