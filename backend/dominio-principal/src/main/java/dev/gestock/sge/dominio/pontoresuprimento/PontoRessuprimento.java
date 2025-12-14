package dev.gestock.sge.dominio.pontoresuprimento;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.produto.ProdutoId;

public class PontoRessuprimento {
	private final PontoRessuprimentoId id;
	private final EstoqueId estoqueId;
	private final ProdutoId produtoId;
	private EstoqueSeguranca estoqueSeguranca;

	public PontoRessuprimento(PontoRessuprimentoId id, EstoqueId estoqueId, ProdutoId produtoId,
			EstoqueSeguranca estoqueSeguranca) {
		notNull(id, "O id não pode ser nulo");
		notNull(estoqueId, "O id do estoque não pode ser nulo");
		notNull(produtoId, "O id do produto não pode ser nulo");
		notNull(estoqueSeguranca, "O estoque de segurança não pode ser nulo");

		this.id = id;
		this.estoqueId = estoqueId;
		this.produtoId = produtoId;
		this.estoqueSeguranca = estoqueSeguranca;
	}

	public PontoRessuprimentoId getId() {
		return id;
	}

	public EstoqueId getEstoqueId() {
		return estoqueId;
	}

	public ProdutoId getProdutoId() {
		return produtoId;
	}

	public EstoqueSeguranca getEstoqueSeguranca() {
		return estoqueSeguranca;
	}

	public void setEstoqueSeguranca(EstoqueSeguranca estoqueSeguranca) {
		notNull(estoqueSeguranca, "O estoque de segurança não pode ser nulo");
		this.estoqueSeguranca = estoqueSeguranca;
	}

	/**
	 * Calcula o ROP (Reorder Point) baseado no consumo médio diário e lead time.
	 * Fórmula: ROP = (Consumo Médio Diário x Lead Time) + Estoque de Segurança
	 */
	public RopCalculado calcularRop(ConsumoMedioDiario consumo, LeadTime leadTime) {
		notNull(consumo, "O consumo médio diário não pode ser nulo");
		notNull(leadTime, "O lead time não pode ser nulo");

		var rop = (int) Math.round((consumo.getValor() * leadTime.getDias()) + estoqueSeguranca.getValor());
		return new RopCalculado(Math.max(0, rop));
	}

	/**
	 * Determina o status do ROP baseado no saldo atual e ROP calculado.
	 */
	public StatusRop determinarStatus(SaldoAtual saldo, RopCalculado rop) {
		notNull(saldo, "O saldo atual não pode ser nulo");
		notNull(rop, "O ROP calculado não pode ser nulo");

		return saldo.getValor() >= rop.getValor() ? StatusRop.ADEQUADO : StatusRop.INADEQUADO;
	}
}

