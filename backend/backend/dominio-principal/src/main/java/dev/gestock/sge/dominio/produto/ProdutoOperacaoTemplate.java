package dev.gestock.sge.dominio.produto;

/**
 * Template Method para operações de produto.
 * Define o esqueleto do algoritmo, deixando alguns passos para as subclasses.
 * 
 * Pattern: Template Method
 * Funcionalidade: Gerenciar Produtos (RONALDO)
 */
public abstract class ProdutoOperacaoTemplate {
	protected final ProdutoRepositorio repositorio;

	public ProdutoOperacaoTemplate(ProdutoRepositorio repositorio) {
		if (repositorio == null) {
			throw new IllegalArgumentException("O repositório não pode ser nulo");
		}
		this.repositorio = repositorio;
	}

	/**
	 * Template method que define o fluxo da operação.
	 */
	public final void executar(ProdutoId id) {
		// Passo 1: Validar entrada
		validarEntrada(id);
		
		// Passo 2: Obter produto
		Produto produto = obterProduto(id);
		
		// Passo 3: Validar regras de negócio (hook method)
		validarRegrasNegocio(produto);
		
		// Passo 4: Executar operação específica (delegado para subclasse)
		executarOperacao(produto);
		
		// Passo 5: Persistir alterações
		persistir(produto);
		
		// Passo 6: Pós-processamento (hook method)
		posProcessar(produto);
	}

	/**
	 * Validação comum de entrada.
	 */
	protected void validarEntrada(ProdutoId id) {
		if (id == null) {
			throw new IllegalArgumentException("O id não pode ser nulo");
		}
	}

	/**
	 * Obtém o produto do repositório.
	 */
	protected Produto obterProduto(ProdutoId id) {
		var produto = repositorio.obter(id);
		if (produto == null) {
			throw new IllegalArgumentException("Produto não encontrado");
		}
		return produto;
	}

	/**
	 * Hook method para validação de regras de negócio específicas.
	 * Pode ser sobrescrito pelas subclasses.
	 */
	protected void validarRegrasNegocio(Produto produto) {
		// Implementação padrão vazia - pode ser sobrescrita
	}

	/**
	 * Método abstrato que deve ser implementado pelas subclasses
	 * para executar a operação específica.
	 */
	protected abstract void executarOperacao(Produto produto);

	/**
	 * Persiste as alterações no repositório.
	 */
	protected void persistir(Produto produto) {
		repositorio.salvar(produto);
	}

	/**
	 * Hook method para pós-processamento.
	 * Pode ser sobrescrito pelas subclasses.
	 */
	protected void posProcessar(Produto produto) {
		// Implementação padrão vazia - pode ser sobrescrita
	}
}

