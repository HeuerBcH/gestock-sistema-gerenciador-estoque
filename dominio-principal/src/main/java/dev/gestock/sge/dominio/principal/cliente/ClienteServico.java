package dev.gestock.sge.dominio.principal.cliente;

/**
 * Serviço de Domínio: Cliente
 *
 * Responsabilidade:
 * - Agrupar regras e operações que envolvem clientes, mas não pertencem diretamente à entidade.
 * - Exemplo: registrar um cliente, validar se ele tem ao menos um estoque, etc.
 *
 * Ligação com histórias e regras:
 * - História 1 (registro de estoques)
 * - Regra R1 (todo cliente precisa ter ao menos 1 estoque)
 */
public class ClienteServico {

	private final ClienteRepositorio repositorio;

	public ClienteServico(ClienteRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	/**
	 * Cadastra um novo cliente no sistema.
	 * → Aplica validações antes de salvar.
	 */
	public void registrarCliente(Cliente cliente) {
		if (cliente == null)
			throw new IllegalArgumentException("Cliente não pode ser nulo");

		repositorio.salvar(cliente);
	}

	/**
	 * Valida se o cliente cumpre a regra R1:
	 * deve possuir pelo menos um estoque cadastrado.
	 */
	public boolean validarPossuiEstoques(Cliente cliente) {
		return cliente.possuiEstoques();
	}
}
