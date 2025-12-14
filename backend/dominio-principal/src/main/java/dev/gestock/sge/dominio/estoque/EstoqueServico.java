package dev.gestock.sge.dominio.estoque;

import static org.apache.commons.lang3.Validate.*;
import dev.gestock.sge.dominio.comum.RegraVioladaException;

/**
 * Implementação base do serviço de estoque.
 * 
 * Esta classe implementa IEstoqueServico e pode ser decorada usando
 * o padrão Decorator através de EstoqueServicoDecorator.
 * 
 * Pattern: Decorator (Componente Concreto)
 * Funcionalidade: Gerenciar Estoques (SILVIO)
 * 
 * No padrão Decorator, esta classe representa o "Componente Concreto",
 * que é a implementação base que pode ser decorada dinamicamente.
 * Decorators podem adicionar funcionalidades (como logging, cache, validações extras)
 * sem modificar esta implementação.
 * 
 * Exemplo de uso com decorator:
 * <pre>
 * IEstoqueServico servicoBase = new EstoqueServico(repositorio);
 * IEstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
 * servicoComLog.salvar(estoque); // Usa o serviço com logging automático
 * </pre>
 * 
 * @see IEstoqueServico Interface comum do padrão Decorator
 * @see EstoqueServicoDecorator Classe base abstrata para decorators
 * @see EstoqueServicoComLogging Decorator concreto que adiciona logging
 */
public class EstoqueServico implements IEstoqueServico {
	private final EstoqueRepositorio repositorio;

	public EstoqueServico(EstoqueRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	public Estoque salvar(Estoque estoque) {
		notNull(estoque, "O estoque não pode ser nulo");
		
		int estoqueId = estoque.getId().getId();
		
		// R2H1: Não pode haver mais de um estoque cadastrado em um mesmo endereço
		if (repositorio.existePorEndereco(estoque.getEndereco().getValor(), estoqueId)) {
			throw new RegraVioladaException("R2H1", 
				"Já existe um estoque cadastrado neste endereço: " + estoque.getEndereco().getValor());
		}
		
		// R3H1: Dois ou mais estoques não podem ter o mesmo nome
		if (repositorio.existePorNome(estoque.getNome(), estoqueId)) {
			throw new RegraVioladaException("R3H1", 
				"Já existe um estoque com este nome: " + estoque.getNome());
		}
		
		// R1H3: O tamanho de um estoque não pode ser diminuído caso o mesmo esteja com produtos ocupando a capacidade máxima
		if (estoqueId > 0) {
			var estoqueExistente = repositorio.obter(estoque.getId());
			if (estoqueExistente != null) {
				int novaCapacidade = estoque.getCapacidade().getValor();
				int ocupacaoAtual = repositorio.obterOcupacaoAtual(estoqueId);
				
				if (novaCapacidade < ocupacaoAtual) {
					throw new RegraVioladaException("R1H3", 
						"Não é possível diminuir a capacidade para " + novaCapacidade + 
						" pois o estoque possui " + ocupacaoAtual + " unidades ocupadas");
				}
			}
		}
		
		return repositorio.salvar(estoque);
	}

	public void remover(EstoqueId id) {
		notNull(id, "O id não pode ser nulo");
		
		// R1H2: Um estoque que ainda possui produtos não pode ser removido
		if (repositorio.possuiProdutos(id.getId())) {
			throw new RegraVioladaException("R1H2", 
				"Não é possível remover o estoque pois ele ainda possui produtos");
		}
		
		// R2H2: Um estoque que possui um pedido alocado em andamento não pode ser removido
		if (repositorio.possuiPedidosEmAndamento(id.getId())) {
			throw new RegraVioladaException("R2H2", 
				"Não é possível remover o estoque pois ele possui pedidos em andamento");
		}
		
		repositorio.remover(id);
	}

	public void ativar(EstoqueId id) {
		notNull(id, "O id não pode ser nulo");
		var estoque = repositorio.obter(id);
		if (estoque == null) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}
		estoque.ativar();
		repositorio.salvar(estoque);
	}

	public void inativar(EstoqueId id) {
		notNull(id, "O id não pode ser nulo");
		var estoque = repositorio.obter(id);
		if (estoque == null) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}
		estoque.inativar();
		repositorio.salvar(estoque);
	}
}

