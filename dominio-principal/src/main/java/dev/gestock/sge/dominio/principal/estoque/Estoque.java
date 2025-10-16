package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregate Root: Estoque
 *
 * Responsabilidades:
 * - Pertencer a um Cliente (clienteId).
 * - Manter saldos por Produto (fisico e reservado).
 * - Registrar Movimentacoes (Entrada, Saida, Ajuste) com auditoria.
 * - Expor operações de domínio: entrada/saída/ajuste, reservar/liberar, consumirReserva.
 *
 * Regras atendidas:
 * - R10: Toda baixa = Movimentacao de Saida.
 * - R11: Saldo disponível nunca negativo (validado em Saida/Reserva).
 * - R12/R13: Ajuste exige trilha (responsável, data, motivo).
 * - R15: SaldoDisponivel = fisico - reservado.
 */
public class Estoque {

	private final EstoqueId id;                    // Identidade imutável do estoque
	private final ClienteId clienteId;             // FK lógica: o dono deste estoque
	private String nome;                           // Nome de exibição do estoque

	// Saldos por produto (mantidos dentro do agregado Estoque)
	private final Map<ProdutoId, SaldoProduto> saldos = new HashMap<>();

	// Log de movimentações (auditoria de domínio)
	private final List<Movimentacao> movimentacoes = new ArrayList<>();

	// ------------------ Construtores ------------------

	public Estoque(ClienteId clienteId, String nome) {
		notNull(clienteId, "Cliente do estoque é obrigatório");
		notBlank(nome, "Nome do estoque é obrigatório");
		this.id = new EstoqueId();
		this.clienteId = clienteId;
		this.nome = nome;
	}

	public Estoque(EstoqueId id, ClienteId clienteId, String nome) {
		notNull(id, "Id do estoque é obrigatório");
		notNull(clienteId, "Cliente do estoque é obrigatório");
		notBlank(nome, "Nome do estoque é obrigatório");
		this.id = id;
		this.clienteId = clienteId;
		this.nome = nome;
	}

	// ------------------ Getters básicos ------------------

	public EstoqueId getId() {
		return id;
	}
	public ClienteId getClienteId() {
		return clienteId;
	}
	public String getNome() {
		return nome;
	}

	public void renomear(String novoNome) {
		notBlank(novoNome, "Nome do estoque é obrigatório");
		this.nome = novoNome;
	}

	public Map<ProdutoId, SaldoProduto> getSaldosSnapshot() {
		// snapshot imutável (protege invariantes)
		return Map.copyOf(saldos);
	}

	public List<Movimentacao> getMovimentacoesSnapshot() {
		return List.copyOf(movimentacoes);
	}

	// ------------------ Consultas de saldo ------------------

	public int getSaldoFisico(ProdutoId produtoId) {
		return saldos.getOrDefault(produtoId, SaldoProduto.zero()).fisico();
	}

	public int getSaldoReservado(ProdutoId produtoId) {
		return saldos.getOrDefault(produtoId, SaldoProduto.zero()).reservado();
	}

	public int getSaldoDisponivel(ProdutoId produtoId) {
		// R15: disponível = físico − reservado
		SaldoProduto sp = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		return sp.disponivel();
	}

	// ------------------ Operações de domínio ------------------

	/**
	 * ENTRADA de mercadorias (R8).
	 * - aumenta o saldo físico.
	 * - registra movimentação de ENTRADA com auditoria.
	 * - pode carregar metadata (ex.: lote/validade) via campos opcionais.
	 */
	public void registrarEntrada(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional, Map<String, String> metaOpcional) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");
		notBlank(responsavel, "Responsável é obrigatório");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		SaldoProduto novo  = atual.comEntrada(quantidade);
		saldos.put(produtoId, novo);

		Movimentacao mov = new Movimentacao(
				TipoMovimentacao.ENTRADA,
				produtoId,
				quantidade,
				LocalDateTime.now(),
				responsavel,
				motivoOpcional,
				metaOpcional == null ? Map.of() : Map.copyOf(metaOpcional)
		);
		movimentacoes.add(mov);
	}

	/**
	 * SAÍDA (R10, R11).
	 * - valida saldo disponível (não pode negativar).
	 * - diminui físico diretamente (saída imediata).
	 * - registra movimentação de SAIDA.
	 */
	public void registrarSaida(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");
		notBlank(responsavel, "Responsável é obrigatório");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		// impede saldo disponível negativo (R11)
		isTrue(atual.disponivel() >= quantidade, "Saldo disponível insuficiente para saída");
		SaldoProduto novo = atual.comSaida(quantidade);
		saldos.put(produtoId, novo);

		Movimentacao mov = new Movimentacao(
				TipoMovimentacao.SAIDA,
				produtoId,
				quantidade,
				LocalDateTime.now(),
				responsavel,
				motivoOpcional,
				Map.of()
		);
		movimentacoes.add(mov);
	}

	/**
	 * AJUSTE (R12, R13).
	 * - usado para correções de inventário.
	 * - exige responsável e motivo para garantir rastreabilidade (R13).
	 * - quantidade > 0; sinal indicado pelo tipoAjuste (ENTRADA/SAIDA).
	 */
	public void registrarAjuste(ProdutoId produtoId, int quantidade, TipoMovimentacao tipoAjuste, String responsavel, String motivo) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade do ajuste deve ser positiva");
		notNull(tipoAjuste, "Tipo de ajuste é obrigatório");
		isTrue(tipoAjuste == TipoMovimentacao.ENTRADA || tipoAjuste == TipoMovimentacao.SAIDA, "Ajuste só pode ser ENTRADA ou SAIDA");
		notBlank(responsavel, "Responsável é obrigatório");
		notBlank(motivo, "Motivo do ajuste é obrigatório");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		SaldoProduto novo;
		if (tipoAjuste == TipoMovimentacao.ENTRADA) {
			novo = atual.comEntrada(quantidade);
		} else {
			isTrue(atual.disponivel() >= quantidade, "Saldo disponível insuficiente para ajuste de saída");
			novo = atual.comSaida(quantidade);
		}
		saldos.put(produtoId, novo);

		Movimentacao mov = new Movimentacao(
				TipoMovimentacao.AJUSTE,
				produtoId,
				(tipoAjuste == TipoMovimentacao.ENTRADA ? quantidade : -quantidade), // registra sinal no histórico
				LocalDateTime.now(),
				responsavel,
				motivo,
				Map.of("tipoAjuste", tipoAjuste.name())
		);
		movimentacoes.add(mov);
	}

	/**
	 * Reserva preventiva (R15, R16): diminui o disponível sem mexer no físico.
	 * - usado quando um Pedido em andamento deve “segurar” o item.
	 */
	public void reservar(ProdutoId produtoId, int quantidade) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		isTrue(atual.disponivel() >= quantidade, "Saldo disponível insuficiente para reserva");
		SaldoProduto novo = atual.comReserva(quantidade);
		saldos.put(produtoId, novo);
	}

	/**
	 * Liberação de reserva (R16): reverte a reserva (ex.: cancelamento de pedido).
	 */
	public void liberarReserva(ProdutoId produtoId, int quantidade) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		isTrue(atual.reservado() >= quantidade, "Quantidade a liberar excede o reservado");
		SaldoProduto novo = atual.comLiberacao(quantidade);
		saldos.put(produtoId, novo);
	}

	/**
	 * Consumo de reserva: quando a saída é para atender a reserva:
	 * - reduz o reservado.
	 * - reduz o físico na mesma quantidade.
	 * - mantém disponível coerente (não negativando).
	 */
	public void consumirReservaComoSaida(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional) {
		notNull(produtoId, "Produto é obrigatório");
		isTrue(quantidade > 0, "Quantidade deve ser positiva");
		notBlank(responsavel, "Responsável é obrigatório");

		SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
		isTrue(atual.reservado() >= quantidade, "Reserva insuficiente para consumo");
		// reduzir reservado e fisico simultaneamente
		SaldoProduto apósReserva = atual.comLiberacao(quantidade);
		isTrue(apósReserva.disponivel() >= quantidade, "Saldo disponível insuficiente para saída");
		SaldoProduto novo = apósReserva.comSaida(quantidade);
		saldos.put(produtoId, novo);

		Movimentacao mov = new Movimentacao(
				TipoMovimentacao.SAIDA,
				produtoId,
				quantidade,
				LocalDateTime.now(),
				responsavel,
				motivoOpcional,
				Map.of("consumoReserva", "true")
		);
		movimentacoes.add(mov);
	}
}
