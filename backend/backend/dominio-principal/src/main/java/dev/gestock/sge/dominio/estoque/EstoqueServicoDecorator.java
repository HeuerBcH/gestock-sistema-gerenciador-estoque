package dev.gestock.sge.dominio.estoque;

/**
 * Decorator base para EstoqueServico.
 * Permite adicionar funcionalidades extras ao serviço de estoque sem modificar sua implementação original.
 * 
 * Pattern: Decorator
 * Funcionalidade: Gerenciar Estoques (SILVIO)
 */
public abstract class EstoqueServicoDecorator {
	protected final EstoqueServico servicoDecorado;

	public EstoqueServicoDecorator(EstoqueServico servicoDecorado) {
		if (servicoDecorado == null) {
			throw new IllegalArgumentException("O serviço decorado não pode ser nulo");
		}
		this.servicoDecorado = servicoDecorado;
	}

	public Estoque salvar(Estoque estoque) {
		return servicoDecorado.salvar(estoque);
	}

	public void remover(EstoqueId id) {
		servicoDecorado.remover(id);
	}

	public void ativar(EstoqueId id) {
		servicoDecorado.ativar(id);
	}

	public void inativar(EstoqueId id) {
		servicoDecorado.inativar(id);
	}
}

