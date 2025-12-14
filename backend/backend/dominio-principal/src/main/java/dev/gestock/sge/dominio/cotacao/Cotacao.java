package dev.gestock.sge.dominio.cotacao;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.produto.ProdutoId;

public class Cotacao {
	private final CotacaoId id;
	private final ProdutoId produtoId;
	private final FornecedorId fornecedorId;
	private Custo preco;
	private LeadTime leadTime;
	private Validade validade;
	private StatusAprovacao statusAprovacao;

	public Cotacao(CotacaoId id, ProdutoId produtoId, FornecedorId fornecedorId, Custo preco, 
			LeadTime leadTime, Validade validade, StatusAprovacao statusAprovacao) {
		notNull(id, "O id não pode ser nulo");
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(fornecedorId, "O id do fornecedor não pode ser nulo");
		notNull(preco, "O preço não pode ser nulo");
		notNull(leadTime, "O lead time não pode ser nulo");
		notNull(validade, "A validade não pode ser nula");
		notNull(statusAprovacao, "O status de aprovação não pode ser nulo");

		this.id = id;
		this.produtoId = produtoId;
		this.fornecedorId = fornecedorId;
		this.preco = preco;
		this.leadTime = leadTime;
		this.validade = validade;
		this.statusAprovacao = statusAprovacao;
	}

	public CotacaoId getId() {
		return id;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public FornecedorId getFornecedorId() {
		return fornecedorId;
	}

	public Custo getPreco() {
		return preco;
	}

	public void setPreco(Custo preco) {
		notNull(preco, "O preço não pode ser nulo");
		this.preco = preco;
	}

	public LeadTime getLeadTime() {
		return leadTime;
	}

	public void setLeadTime(LeadTime leadTime) {
		notNull(leadTime, "O lead time não pode ser nulo");
		this.leadTime = leadTime;
	}

	public Validade getValidade() {
		return validade;
	}

	public void setValidade(Validade validade) {
		notNull(validade, "A validade não pode ser nula");
		this.validade = validade;
	}

	public StatusAprovacao getStatusAprovacao() {
		return statusAprovacao;
	}

	public void aprovar() {
		this.statusAprovacao = StatusAprovacao.APROVADA;
	}

	public void desaprovar() {
		this.statusAprovacao = StatusAprovacao.PENDENTE;
	}
}

