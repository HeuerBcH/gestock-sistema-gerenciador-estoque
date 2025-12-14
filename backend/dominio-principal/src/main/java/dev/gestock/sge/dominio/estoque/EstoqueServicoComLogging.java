package dev.gestock.sge.dominio.estoque;

/**
 * Decorator concreto que adiciona funcionalidade de logging ao EstoqueServico.
 * 
 * Esta classe é uma implementação concreta do padrão Decorator que adiciona
 * logging automático a todas as operações do serviço de estoque.
 * 
 * Pattern: Decorator (Decorator Concreto)
 * Funcionalidade: Gerenciar Estoques (SILVIO)
 * 
 * Funcionalidade adicionada:
 * - Registra início e fim de cada operação
 * - Captura e registra erros durante as operações
 * - Mantém compatibilidade total com IEstoqueServico
 * - Permite substituição transparente do componente base
 * 
 * Como funciona:
 * - Envolve (decora) uma instância de IEstoqueServico
 * - Intercepta todas as chamadas aos métodos da interface
 * - Adiciona logging antes e depois de delegar para o serviço decorado
 * - Propaga exceções mantendo o comportamento original
 * 
 * Exemplo de uso:
 * <pre>
 * // Criar o componente base
 * IEstoqueServico servicoBase = new EstoqueServico(repositorio);
 * 
 * // Aplicar este decorator para adicionar logging
 * IEstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
 * servicoComLog.salvar(estoque); // Salva e registra logs automaticamente
 * 
 * // Pode ser usado em qualquer lugar que espera IEstoqueServico
 * public void processarEstoque(IEstoqueServico servico, Estoque estoque) {
 *     servico.salvar(estoque); // Aceita tanto EstoqueServico quanto decorators
 * }
 * </pre>
 * 
 * @see IEstoqueServico Interface comum do padrão Decorator
 * @see EstoqueServicoDecorator Classe base abstrata
 * @see EstoqueServico Componente que está sendo decorado
 */
public class EstoqueServicoComLogging extends EstoqueServicoDecorator {

	public EstoqueServicoComLogging(IEstoqueServico servicoDecorado) {
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

