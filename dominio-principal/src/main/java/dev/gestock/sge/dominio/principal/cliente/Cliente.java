package dev.gestock.sge.dominio.principal.cliente;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.Estoque;

import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDADE: Cliente (Aggregate Root)
 *
 * Responsabilidade:
 * - Representa o dono dos estoques.
 * - Centraliza a criação e o gerenciamento dos estoques do cliente.
 * - Garante que nenhum estoque exista sem estar vinculado a um cliente.
 *
 * Regras relacionadas:
 * R1 → Cada usuário deve possuir pelo menos um estoque cadastrado.
 *
 * Ligação com Histórias de Usuário:
 * História 1 → "Como usuário, quero poder registrar um ou mais estoques..."
 */
public class Cliente {

	// -----------------------------
	// Atributos de domínio
	// -----------------------------
	private final ClienteId id;     // Identidade única (Value Object, imutável)
	private String nome;            // Nome do cliente
	private String documento;       // CPF/CNPJ
	private String email;           // E-mail de contato

	private final List<Estoque> estoques = new ArrayList<>();
	// Relação 1:N com Estoque → garante a regra R1 (todo cliente tem pelo menos 1 estoque)

	// -----------------------------
	// Construtores
	// -----------------------------

	/**
	 * Construtor - ID será atribuído pela camada de persistência.
	 */
	public Cliente(ClienteId id, String nome, String documento, String email) {
		notNull(id, "O id do cliente não pode ser nulo");
		this.id = id;
		setNome(nome);
		setDocumento(documento);
		setEmail(email);
	}

	// -----------------------------
	// Getters (exposição controlada)
	// -----------------------------
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

	/**
	 * Exposição segura da lista de estoques.
	 * → Retorna uma cópia imutável da lista.
	 */
	public List<Estoque> getEstoques() {
		return List.copyOf(estoques);
	}

	// -----------------------------
	// Métodos de negócio (comportamentos)
	// -----------------------------

	/**
	 * Adiciona um novo estoque vinculado a este cliente.
	 *
	 * Validações:
	 * - O estoque não pode ser nulo.
	 * - O estoque deve pertencer ao mesmo cliente.
	 *
	 * Regra associada:
	 * R1 → Cada usuário deve possuir pelo menos um estoque cadastrado.
	 *
	 * Histórias associadas:
	 * História 1 → "Registrar estoques com produtos"
	 */
	public void adicionarEstoque(Estoque estoque) {
		notNull(estoque, "O estoque não pode ser nulo");

		// Garante integridade: o estoque precisa estar associado ao mesmo cliente
		if (!estoque.getClienteId().equals(this.id)) {
			throw new IllegalArgumentException("O estoque pertence a outro cliente");
		}

		estoques.add(estoque);
	}

	/**
	 * Verifica se o cliente tem ao menos um estoque cadastrado.
	 *
	 * Útil para enforcing rule R1 em um serviço de domínio ou regra de negócio.
	 */
	public boolean possuiEstoques() {
		return !estoques.isEmpty();
	}

	// -----------------------------
	// Validações internas (invariantes)
	// -----------------------------

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

	// -----------------------------
	// Representação textual (útil para logs, debug, UI)
	// -----------------------------
	@Override
	public String toString() {
		return nome + " (" + documento + ")";
	}
}
