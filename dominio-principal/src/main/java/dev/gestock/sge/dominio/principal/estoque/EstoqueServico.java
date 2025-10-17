package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço de domínio para operações que envolvem mais de um Estoque
 * ou orquestram múltiplas chamadas atômicas.
 *
 * Responsabilidades:
 * - Transferência entre estoques
 * - Validação de regras de negócio complexas
 * - Operações que envolvem múltiplos estoques
 * - Validação de inativação de estoques
 */
public class EstoqueServico {

	private final EstoqueRepositorio repositorio;

	public EstoqueServico(EstoqueRepositorio repositorio) {
		notNull(repositorio, "Repositório é obrigatório");
		this.repositorio = repositorio;
	}

	/**
	 * Transfere produtos entre estoques (R17/R18):
	 * - Gera SAIDA no estoque de origem e ENTRADA no destino.
	 * - Opera de forma atômica no nível de aplicação (transação deve envolver este método).
	 */
	public void transferir(Estoque origem,
						   Estoque destino,
						   ProdutoId produtoId,
						   int quantidade,
						   String responsavel,
						   String motivo) {

		notNull(origem, "Estoque de origem é obrigatório");
		notNull(destino, "Estoque de destino é obrigatório");
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");
		notBlank(responsavel, "Responsável é obrigatório");

		// 1) saída na origem (valida saldo disponível)
		origem.registrarSaida(produtoId, quantidade, responsavel, motivo);

		// 2) entrada no destino (metadados opcionais)
		destino.registrarEntrada(produtoId, quantidade, responsavel, "Transferência de estoque", Map.of(
				"transferencia", "true",
				"origem", origem.getId().toString()
		));

		// 3) persistir ambos os agregados
		repositorio.salvar(origem);
		repositorio.salvar(destino);
	}

	/**
	 * Cria um novo estoque com validações (R2H1, R3H1)
	 * 
	 * @param clienteId ID do cliente
	 * @param nome Nome do estoque
	 * @param endereco Endereço do estoque
	 * @param descricao Descrição do estoque
	 * @param capacidadeMaxima Capacidade máxima
	 * @param responsavelCriacao Responsável pela criação
	 * @return Estoque criado
	 */
	public Estoque criarEstoque(ClienteId clienteId, String nome, String endereco, 
	                           String descricao, int capacidadeMaxima, String responsavelCriacao) {
		notNull(clienteId, "Cliente é obrigatório");
		notBlank(nome, "Nome é obrigatório");
		notBlank(endereco, "Endereço é obrigatório");
		notBlank(descricao, "Descrição é obrigatória");
		isTrue(capacidadeMaxima > 0, "Capacidade máxima deve ser positiva");
		notBlank(responsavelCriacao, "Responsável pela criação é obrigatório");

		// R3H1: Verifica se já existe estoque com o mesmo nome
		if (repositorio.existeEstoqueComNome(nome)) {
			throw new IllegalArgumentException("Já existe um estoque com o nome: " + nome);
		}

		// R2H1: Verifica se já existe estoque no mesmo endereço
		if (repositorio.existeEstoqueComEndereco(endereco)) {
			throw new IllegalArgumentException("Já existe um estoque no endereço: " + endereco);
		}

		Estoque estoque = new Estoque(clienteId, nome, endereco, descricao, capacidadeMaxima, responsavelCriacao);
		repositorio.salvar(estoque);

		return estoque;
	}

	/**
	 * Valida se um estoque pode ser inativado (R1H2, R2H2)
	 * 
	 * @param estoqueId ID do estoque
	 * @param pedidosEmAndamento Quantidade de pedidos em andamento
	 * @return true se pode ser inativado
	 */
	public boolean podeInativarEstoque(EstoqueId estoqueId, int pedidosEmAndamento) {
		notNull(estoqueId, "ID do estoque é obrigatório");
		isTrue(pedidosEmAndamento >= 0, "Quantidade de pedidos não pode ser negativa");

		Optional<Estoque> estoqueOpt = repositorio.buscarPorId(estoqueId);
		if (estoqueOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		Estoque estoque = estoqueOpt.get();
		
		// R1H2: Verifica se há produtos no estoque
		if (estoque.getCapacidadeAtual() > 0) {
			return false;
		}

		// R2H2: Verifica se há pedidos em andamento
		if (pedidosEmAndamento > 0) {
			return false;
		}

		return true;
	}

	/**
	 * Inativa um estoque com validação (R1H2, R2H2)
	 * 
	 * @param estoqueId ID do estoque
	 * @param pedidosEmAndamento Quantidade de pedidos em andamento
	 * @param responsavel Responsável pela inativação
	 */
	public void inativarEstoque(EstoqueId estoqueId, int pedidosEmAndamento, String responsavel) {
		notNull(estoqueId, "ID do estoque é obrigatório");
		notBlank(responsavel, "Responsável é obrigatório");

		Optional<Estoque> estoqueOpt = repositorio.buscarPorId(estoqueId);
		if (estoqueOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		Estoque estoque = estoqueOpt.get();

		if (!estoque.isAtivo()) {
			throw new IllegalStateException("Estoque já está inativo");
		}

		if (!podeInativarEstoque(estoqueId, pedidosEmAndamento)) {
			throw new IllegalStateException("Estoque não pode ser inativado: há produtos ou pedidos em andamento");
		}

		estoque.inativar(responsavel);
		repositorio.salvar(estoque);
	}

	/**
	 * Reativa um estoque
	 * 
	 * @param estoqueId ID do estoque
	 * @param responsavel Responsável pela reativação
	 */
	public void reativarEstoque(EstoqueId estoqueId, String responsavel) {
		notNull(estoqueId, "ID do estoque é obrigatório");
		notBlank(responsavel, "Responsável é obrigatório");

		Optional<Estoque> estoqueOpt = repositorio.buscarPorId(estoqueId);
		if (estoqueOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		Estoque estoque = estoqueOpt.get();

		if (estoque.isAtivo()) {
			throw new IllegalStateException("Estoque já está ativo");
		}

		estoque.reativar(responsavel);
		repositorio.salvar(estoque);
	}

	/**
	 * Atualiza informações de um estoque (R1H3)
	 * 
	 * @param estoqueId ID do estoque
	 * @param nome Novo nome
	 * @param endereco Novo endereço
	 * @param descricao Nova descrição
	 * @param responsavel Responsável pela atualização
	 */
	public void atualizarEstoque(EstoqueId estoqueId, String nome, String endereco, 
	                             String descricao, String responsavel) {
		notNull(estoqueId, "ID do estoque é obrigatório");
		notBlank(nome, "Nome é obrigatório");
		notBlank(endereco, "Endereço é obrigatório");
		notBlank(descricao, "Descrição é obrigatória");
		notBlank(responsavel, "Responsável é obrigatório");

		Optional<Estoque> estoqueOpt = repositorio.buscarPorId(estoqueId);
		if (estoqueOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		Estoque estoque = estoqueOpt.get();
		estoque.atualizarInformacoes(nome, endereco, descricao, responsavel);
		repositorio.salvar(estoque);
	}

	/**
	 * Atualiza a capacidade de um estoque (R1H3)
	 * 
	 * @param estoqueId ID do estoque
	 * @param novaCapacidade Nova capacidade máxima
	 * @param responsavel Responsável pela atualização
	 */
	public void atualizarCapacidade(EstoqueId estoqueId, int novaCapacidade, String responsavel) {
		notNull(estoqueId, "ID do estoque é obrigatório");
		isTrue(novaCapacidade > 0, "Capacidade máxima deve ser positiva");
		notBlank(responsavel, "Responsável é obrigatório");

		Optional<Estoque> estoqueOpt = repositorio.buscarPorId(estoqueId);
		if (estoqueOpt.isEmpty()) {
			throw new IllegalArgumentException("Estoque não encontrado");
		}

		Estoque estoque = estoqueOpt.get();
		estoque.atualizarCapacidade(novaCapacidade, responsavel);
		repositorio.salvar(estoque);
	}

	/**
	 * Lista estoques de um cliente
	 * 
	 * @param clienteId ID do cliente
	 * @return Lista de estoques
	 */
	public List<Estoque> listarEstoquesPorCliente(ClienteId clienteId) {
		notNull(clienteId, "Cliente é obrigatório");
		return repositorio.buscarEstoquesPorCliente(clienteId);
	}

	/**
	 * Lista estoques ativos
	 * 
	 * @return Lista de estoques ativos
	 */
	public List<Estoque> listarEstoquesAtivos() {
		return repositorio.buscarEstoquesAtivos();
	}

	/**
	 * Lista estoques inativos
	 * 
	 * @return Lista de estoques inativos
	 */
	public List<Estoque> listarEstoquesInativos() {
		return repositorio.buscarEstoquesInativos();
	}

	/**
	 * Lista estoques por nome
	 * 
	 * @param nome Nome do estoque
	 * @return Lista de estoques
	 */
	public List<Estoque> listarEstoquesPorNome(String nome) {
		notBlank(nome, "Nome é obrigatório");
		return repositorio.buscarEstoquesPorNome(nome);
	}

	/**
	 * Lista estoques por endereço
	 * 
	 * @param endereco Endereço do estoque
	 * @return Lista de estoques
	 */
	public List<Estoque> listarEstoquesPorEndereco(String endereco) {
		notBlank(endereco, "Endereço é obrigatório");
		return repositorio.buscarEstoquesPorEndereco(endereco);
	}
}


