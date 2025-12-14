package dev.gestock.sge.dominio.estoque;

/**
 * Classe base abstrata para decorators do EstoqueServico.
 * 
 * Implementa o padrão Decorator (GoF) permitindo adicionar funcionalidades
 * ao EstoqueServico de forma dinâmica sem modificar sua implementação original.
 * 
 * Pattern: Decorator (Decorator Abstrato)
 * Funcionalidade: Gerenciar Estoques (SILVIO)
 * 
 * Como funciona o Decorator:
 * - Esta classe implementa IEstoqueServico (mesma interface do componente)
 * - Mantém referência para IEstoqueServico (pode ser o componente ou outro decorator)
 * - Delega chamadas para o serviço decorado
 * - Subclasses concretas adicionam comportamentos antes/depois da delegação
 * 
 * Características do padrão Decorator:
 * - Permite adicionar responsabilidades a objetos dinamicamente
 * - Fornece alternativa flexível à herança para estender funcionalidades
 * - Mantém a mesma interface para componente e decorators (substituição transparente)
 * - Permite encadear múltiplos decorators
 * 
 * Exemplo de uso:
 * <pre>
 * // Criar o componente base
 * IEstoqueServico servicoBase = new EstoqueServico(repositorio);
 * 
 * // Aplicar decorator para adicionar logging
 * IEstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
 * servicoComLog.salvar(estoque); // Operação com logging automático
 * 
 * // Decorators podem ser encadeados (se houver outros decorators)
 * // IEstoqueServico servicoComLogECache = new EstoqueServicoComCache(servicoComLog);
 * </pre>
 * 
 * @see IEstoqueServico Interface comum do padrão
 * @see EstoqueServico Componente concreto que pode ser decorado
 * @see EstoqueServicoComLogging Decorator concreto de exemplo
 */
public abstract class EstoqueServicoDecorator implements IEstoqueServico {
	protected final IEstoqueServico servicoDecorado;

	public EstoqueServicoDecorator(IEstoqueServico servicoDecorado) {
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

