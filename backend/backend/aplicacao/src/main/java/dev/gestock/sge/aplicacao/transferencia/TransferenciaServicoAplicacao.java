package dev.gestock.sge.aplicacao.transferencia;

import static org.apache.commons.lang3.Validate.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;
import dev.gestock.sge.dominio.movimentacao.Movimentacao.MovimentacaoCriadaEvento;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoRepositorio;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoServico;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Responsavel;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.Quantidade;
import dev.gestock.sge.dominio.transferencia.TransferenciaServico;

public class TransferenciaServicoAplicacao {
	private final TransferenciaRepositorioAplicacao repositorioAplicacao;
	private final TransferenciaServico transferenciaServico;
	private final MovimentacaoRepositorio movimentacaoRepositorio;
	private final MovimentacaoServico movimentacaoServico;

	public TransferenciaServicoAplicacao(TransferenciaRepositorioAplicacao repositorioAplicacao,
			TransferenciaServico transferenciaServico, MovimentacaoRepositorio movimentacaoRepositorio,
			MovimentacaoServico movimentacaoServico, EventoBarramento barramento) {
		notNull(repositorioAplicacao, "O repositório de aplicação não pode ser nulo");
		notNull(transferenciaServico, "O serviço de transferência não pode ser nulo");
		notNull(movimentacaoRepositorio, "O repositório de movimentação não pode ser nulo");
		notNull(movimentacaoServico, "O serviço de movimentação não pode ser nulo");
		notNull(barramento, "O barramento de eventos não pode ser nulo");

		this.repositorioAplicacao = repositorioAplicacao;
		this.transferenciaServico = transferenciaServico;
		this.movimentacaoRepositorio = movimentacaoRepositorio;
		this.movimentacaoServico = movimentacaoServico;

		// Registrar observador de eventos
		barramento.adicionar(new dev.gestock.sge.dominio.evento.EventoObservador<MovimentacaoCriadaEvento>() {
			@Override
			public void observarEvento(MovimentacaoCriadaEvento evento) {
				tratarMovimentacaoCriada(evento);
			}
		});
	}

	private void tratarMovimentacaoCriada(MovimentacaoCriadaEvento evento) {
		var movimentacao = evento.getMovimentacao();

		// Apenas processar movimentações de ENTRADA
		if (movimentacao.getTipo() != TipoMovimentacao.ENTRADA) {
			return;
		}

		// Buscar movimentação de SAÍDA correspondente recente
		var movimentacaoSaida = buscarMovimentacaoSaidaCorrespondente(movimentacao);
		if (movimentacaoSaida != null) {
			// Criar transferência
			transferenciaServico.criarTransferencia(movimentacaoSaida, movimentacao);
		}
	}

	/**
	 * Busca uma movimentação de SAÍDA correspondente à movimentação de ENTRADA.
	 * Critérios de correspondência:
	 * - Mesmo produto
	 * - Mesma quantidade
	 * - Mesmo responsável
	 * - Mesmo motivo
	 * - Data/hora próxima (dentro de 1 hora)
	 * - Estoque diferente
	 */
	private Movimentacao buscarMovimentacaoSaidaCorrespondente(Movimentacao movimentacaoEntrada) {
		// Buscar movimentações de SAÍDA do mesmo produto, quantidade, responsável e motivo
		// que ocorreram nas últimas 1 hora
		var dataHoraLimite = movimentacaoEntrada.getDataHora().minusHours(1);
		var dataInicio = dataHoraLimite.toLocalDate();
		var dataFim = movimentacaoEntrada.getDataHora().toLocalDate().plusDays(1);
		var todasMovimentacoes = movimentacaoRepositorio.obterPorPeriodo(dataInicio, dataFim);

		// Filtrar movimentações de SAÍDA correspondentes
		for (var movimentacao : todasMovimentacoes) {
			if (movimentacao.getTipo() != TipoMovimentacao.SAIDA) {
				continue;
			}

			// Verificar correspondência
			if (!movimentacao.getProdutoId().equals(movimentacaoEntrada.getProdutoId())) {
				continue;
			}
			if (movimentacao.getQuantidade().getValor() != movimentacaoEntrada.getQuantidade().getValor()) {
				continue;
			}
			if (!movimentacao.getResponsavel().equals(movimentacaoEntrada.getResponsavel())) {
				continue;
			}
			if (!movimentacao.getMotivo().equals(movimentacaoEntrada.getMotivo())) {
				continue;
			}
			if (movimentacao.getEstoqueId().equals(movimentacaoEntrada.getEstoqueId())) {
				continue; // Estoque deve ser diferente
			}

			// Verificar se está dentro do intervalo de 1 hora
			var diferenca = Duration.between(movimentacao.getDataHora(), movimentacaoEntrada.getDataHora());
			if (diferenca.toHours() > 1 || diferenca.isNegative()) {
				continue;
			}

			// Verificar se já não foi usada em outra transferência
			// (isso será verificado na implementação do repositório se necessário)

			return movimentacao;
		}

		return null;
	}

	public List<TransferenciaResumo> pesquisarResumos(String busca) {
		return repositorioAplicacao.pesquisarResumos(busca);
	}

	public TransferenciaTotais obterTotais() {
		return repositorioAplicacao.obterTotais();
	}

	/**
	 * Registra uma nova transferência de produto entre estoques.
	 * 
	 * @param produtoId ID do produto a ser transferido
	 * @param estoqueOrigemId ID do estoque de origem
	 * @param estoqueDestinoId ID do estoque de destino
	 * @param quantidade Quantidade a ser transferida
	 * @param responsavel Responsável pela transferência
	 * @param motivo Motivo da transferência
	 * @return TransferenciaResumo com os dados da transferência criada
	 */
	public TransferenciaResumo registrar(int produtoId, int estoqueOrigemId, int estoqueDestinoId,
			int quantidade, String responsavel, String motivo) {
		
		// Validar que os estoques são diferentes
		if (estoqueOrigemId == estoqueDestinoId) {
			throw new IllegalArgumentException("O estoque de origem deve ser diferente do estoque de destino");
		}

		// Validar quantidade positiva
		if (quantidade <= 0) {
			throw new IllegalArgumentException("A quantidade deve ser maior que zero");
		}

		// Validar quantidade disponível no estoque de origem
		var quantidadeDisponivel = repositorioAplicacao.obterQuantidadeProdutoNoEstoque(estoqueOrigemId, produtoId);
		if (quantidade > quantidadeDisponivel) {
			throw new IllegalArgumentException(
				String.format("Quantidade insuficiente no estoque de origem. Disponível: %d, Solicitado: %d", 
					quantidadeDisponivel, quantidade));
		}

		// Validar capacidade do estoque de destino
		var capacidadeDisponivel = repositorioAplicacao.obterCapacidadeDisponivel(estoqueDestinoId);
		if (quantidade > capacidadeDisponivel) {
			throw new IllegalArgumentException(
				String.format("Capacidade insuficiente no estoque de destino. Disponível: %d, Solicitado: %d", 
					capacidadeDisponivel, quantidade));
		}

		// Criar movimentação de SAÍDA do estoque de origem
		var motivoTransferencia = new Motivo("[TRANSFERÊNCIA] " + motivo);
		var responsavelTransferencia = new Responsavel(responsavel);
		var produtoIdObj = new ProdutoId(produtoId);
		var quantidadeObj = new Quantidade(quantidade);
		
		var movimentacaoSaida = new Movimentacao(
			new MovimentacaoId(0),
			LocalDateTime.now(),
			produtoIdObj,
			new EstoqueId(estoqueOrigemId),
			quantidadeObj,
			TipoMovimentacao.SAIDA,
			motivoTransferencia,
			responsavelTransferencia
		);
		
		var movSaidaSalva = movimentacaoServico.registrar(movimentacaoSaida);
		
		// Criar movimentação de ENTRADA no estoque de destino
		var movimentacaoEntrada = new Movimentacao(
			new MovimentacaoId(0),
			LocalDateTime.now(),
			produtoIdObj,
			new EstoqueId(estoqueDestinoId),
			quantidadeObj,
			TipoMovimentacao.ENTRADA,
			motivoTransferencia,
			responsavelTransferencia
		);
		
		movimentacaoServico.registrar(movimentacaoEntrada);
		
		// A transferência será criada automaticamente pelo evento de movimentação
		// Retornar o resumo mais recente
		var resumos = pesquisarResumos("");
		if (!resumos.isEmpty()) {
			return resumos.get(0);
		}
		
		return null;
	}
}

