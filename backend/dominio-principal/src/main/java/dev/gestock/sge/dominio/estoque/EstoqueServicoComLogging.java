package dev.gestock.sge.dominio.estoque;

/**
 * Decorator concreto que adiciona funcionalidade de logging ao EstoqueServico.
 * 
 * Pattern: Decorator
 * Funcionalidade: Gerenciar Estoques (SILVIO)
 */
public class EstoqueServicoComLogging extends EstoqueServicoDecorator {

	public EstoqueServicoComLogging(EstoqueServico servicoDecorado) {
		super(servicoDecorado);
	}

	@Override
	public Estoque salvar(Estoque estoque) {
		log("Iniciando salvamento de estoque: " + estoque.getNome());
		try {
			Estoque resultado = servicoDecorado.salvar(estoque);
			log("Estoque salvo com sucesso. ID: " + resultado.getId().getId());
			return resultado;
		} catch (Exception e) {
			log("Erro ao salvar estoque: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public void remover(EstoqueId id) {
		log("Iniciando remoção de estoque. ID: " + id.getId());
		try {
			servicoDecorado.remover(id);
			log("Estoque removido com sucesso. ID: " + id.getId());
		} catch (Exception e) {
			log("Erro ao remover estoque: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public void ativar(EstoqueId id) {
		log("Ativando estoque. ID: " + id.getId());
		servicoDecorado.ativar(id);
		log("Estoque ativado com sucesso. ID: " + id.getId());
	}

	@Override
	public void inativar(EstoqueId id) {
		log("Inativando estoque. ID: " + id.getId());
		servicoDecorado.inativar(id);
		log("Estoque inativado com sucesso. ID: " + id.getId());
	}

	private void log(String mensagem) {
		// Em produção, usar um framework de logging como SLF4J
		System.out.println("[ESTOQUE-LOG] " + java.time.LocalDateTime.now() + " - " + mensagem);
	}
}

