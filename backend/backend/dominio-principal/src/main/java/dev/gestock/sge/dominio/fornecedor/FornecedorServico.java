package dev.gestock.sge.dominio.fornecedor;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.comum.RegraVioladaException;

public class FornecedorServico {
	private final FornecedorRepositorio repositorio;
	private LeadTimeAlteradoCallback leadTimeAlteradoCallback;

	/**
	 * Interface para callback quando o lead time de um fornecedor é alterado.
	 * R1H6: Alterar o Lead Time de um fornecedor recalcula o ponto de ressuprimento.
	 */
	public interface LeadTimeAlteradoCallback {
		void onLeadTimeAlterado(int fornecedorId);
	}

	public FornecedorServico(FornecedorRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public void setLeadTimeAlteradoCallback(LeadTimeAlteradoCallback callback) {
		this.leadTimeAlteradoCallback = callback;
	}

	public Fornecedor salvar(Fornecedor fornecedor) {
		notNull(fornecedor, "O fornecedor não pode ser nulo");

		// Verificar se já existe fornecedor com o mesmo CNPJ (exceto se for o mesmo)
		var fornecedorExistente = repositorio.obterPorCnpj(fornecedor.getCnpj());
		if (fornecedorExistente != null && !fornecedorExistente.getId().equals(fornecedor.getId())) {
			throw new IllegalArgumentException("Já existe um fornecedor cadastrado com este CNPJ");
		}

		// R1H6: Verificar se o lead time foi alterado para recalcular ROP
		boolean leadTimeAlterado = false;
		int fornecedorId = fornecedor.getId().getId();
		
		if (fornecedorId > 0) {
			var existente = repositorio.obter(fornecedor.getId());
			if (existente != null && !existente.getLeadTime().equals(fornecedor.getLeadTime())) {
				leadTimeAlterado = true;
			}
		}

		var resultado = repositorio.salvar(fornecedor);

		// R1H6: Notificar sobre alteração de lead time para recalcular ROP
		if (leadTimeAlterado && leadTimeAlteradoCallback != null) {
			leadTimeAlteradoCallback.onLeadTimeAlterado(resultado.getId().getId());
		}

		return resultado;
	}

	public void remover(FornecedorId id) {
		notNull(id, "O id não pode ser nulo");
		repositorio.remover(id);
	}

	public void ativar(FornecedorId id) {
		notNull(id, "O id não pode ser nulo");
		var fornecedor = repositorio.obter(id);
		if (fornecedor == null) {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}
		fornecedor.ativar();
		repositorio.salvar(fornecedor);
	}

	public void inativar(FornecedorId id) {
		notNull(id, "O id não pode ser nulo");
		var fornecedor = repositorio.obter(id);
		if (fornecedor == null) {
			throw new IllegalArgumentException("Fornecedor não encontrado");
		}
		
		// R1H7: Um fornecedor não pode ser inativado se houver pedidos pendentes com ele
		if (repositorio.possuiPedidosPendentes(id.getId())) {
			throw new RegraVioladaException("R1H7", 
				"Não é possível inativar o fornecedor pois existem pedidos pendentes");
		}
		
		fornecedor.inativar();
		repositorio.salvar(fornecedor);
	}
}

