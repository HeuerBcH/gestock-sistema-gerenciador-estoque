package dev.gestock.sge.dominio.estoque;

/**
 * Interface comum para o serviço de estoque.
 * 
 * Esta interface é parte da implementação do padrão Decorator (GoF),
 * permitindo que decorators adicionem funcionalidades ao EstoqueServico
 * sem modificar sua implementação original.
 * 
 * O padrão Decorator permite:
 * - Adicionar responsabilidades a objetos dinamicamente
 * - Fornecer alternativa flexível à herança para estender funcionalidades
 * - Manter a mesma interface para componente e decorators
 * 
 * @see EstoqueServico Implementação base do serviço (Componente Concreto)
 * @see EstoqueServicoDecorator Classe base abstrata para decorators (Decorator Abstrato)
 * @see EstoqueServicoComLogging Decorator concreto que adiciona logging (Decorator Concreto)
 */
public interface IEstoqueServico {
	
	/**
	 * Salva um estoque no repositório.
	 * 
	 * @param estoque O estoque a ser salvo
	 * @return O estoque salvo (pode ter ID atualizado)
	 */
	Estoque salvar(Estoque estoque);
	
	/**
	 * Remove um estoque do repositório.
	 * 
	 * @param id O identificador do estoque a ser removido
	 */
	void remover(EstoqueId id);
	
	/**
	 * Ativa um estoque.
	 * 
	 * @param id O identificador do estoque a ser ativado
	 */
	void ativar(EstoqueId id);
	
	/**
	 * Inativa um estoque.
	 * 
	 * @param id O identificador do estoque a ser inativado
	 */
	void inativar(EstoqueId id);
}

