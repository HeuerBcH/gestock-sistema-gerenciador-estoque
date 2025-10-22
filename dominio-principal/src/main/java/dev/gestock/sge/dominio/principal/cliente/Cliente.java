package dev.gestock.sge.dominio.principal.cliente;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.Estoque;

import java.util.ArrayList;
import java.util.List;

// ENTIDADE: Cliente (Aggregate Root)

public class Cliente {

    private final ClienteId id;
    private String nome;
    private String documento;
    private String email;

    private final List<Estoque> estoques = new ArrayList<>();
    // Relação 1:N com Estoque → garante a regra R1 (todo cliente tem pelo menos 1 estoque)

    public Cliente(ClienteId id, String nome, String documento, String email) {
        notNull(id, "O id do cliente não pode ser nulo");
        this.id = id;
        setNome(nome);
        setDocumento(documento);
        setEmail(email);
    }

    // Getters

    public ClienteId getId() {
        return id;
    }
    public String getNome() {
        return nome;
    }
    public String getDocumento() {
        return documento;
    }
    public String getEmail() {
        return email;
    }

    // Exposição segura da lista de estoques → Retorna uma cópia imutável da lista.
    public List<Estoque> getEstoques() {
        return List.copyOf(estoques);
    }

    public void adicionarEstoque(Estoque estoque) {
        notNull(estoque, "O estoque não pode ser nulo");

        // Garante integridade: o estoque precisa estar associado ao mesmo cliente
        if (!estoque.getClienteId().equals(this.id)) {
            throw new IllegalArgumentException("O estoque pertence a outro cliente");
        }

        estoques.add(estoque);
    }

    // Verifica se o cliente tem ao menos um estoque cadastrado.
    public boolean possuiEstoques() {
        return !estoques.isEmpty();
    }


    // Validações internas
    private void setNome(String nome) {
        notBlank(nome, "Nome do cliente é obrigatório");
        this.nome = nome;
    }

    private void setDocumento(String documento) {
        notBlank(documento, "Documento (CPF/CNPJ) é obrigatório");
        this.documento = documento;
    }

    private void setEmail(String email) {
        notBlank(email, "E-mail é obrigatório");
        this.email = email;
    }


    // Representação textual para logs, debug, UI
    @Override
    public String toString() {
        return nome + " (" + documento + ")";
    }
}
