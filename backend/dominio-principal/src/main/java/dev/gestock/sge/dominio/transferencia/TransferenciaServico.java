package dev.gestock.sge.dominio.transferencia;

import static org.apache.commons.lang3.Validate.*;
import java.time.LocalDateTime;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;

public class TransferenciaServico {
	private final TransferenciaRepositorio repositorio;

	public TransferenciaServico(TransferenciaRepositorio repositorio) {
		notNull(repositorio, "O repositório não pode ser nulo");
		this.repositorio = repositorio;
	}

	/**
	 * Cria uma transferência a partir de duas movimentações (SAÍDA e ENTRADA).
	 */
	public void criarTransferencia(Movimentacao movimentacaoSaida, Movimentacao movimentacaoEntrada) {
		notNull(movimentacaoSaida, "A movimentação de saída não pode ser nula");
		notNull(movimentacaoEntrada, "A movimentação de entrada não pode ser nula");

		// Validar que são tipos corretos
		if (movimentacaoSaida.getTipo() != dev.gestock.sge.dominio.movimentacao.TipoMovimentacao.SAIDA) {
			throw new IllegalArgumentException("A primeira movimentação deve ser do tipo SAÍDA");
		}
		if (movimentacaoEntrada.getTipo() != dev.gestock.sge.dominio.movimentacao.TipoMovimentacao.ENTRADA) {
			throw new IllegalArgumentException("A segunda movimentação deve ser do tipo ENTRADA");
		}

		// Validar correspondência
		if (!movimentacaoSaida.getProdutoId().equals(movimentacaoEntrada.getProdutoId())) {
			throw new IllegalArgumentException("As movimentações devem ser do mesmo produto");
		}
		if (movimentacaoSaida.getQuantidade().getValor() != movimentacaoEntrada.getQuantidade().getValor()) {
			throw new IllegalArgumentException("As movimentações devem ter a mesma quantidade");
		}
		if (!movimentacaoSaida.getResponsavel().equals(movimentacaoEntrada.getResponsavel())) {
			throw new IllegalArgumentException("As movimentações devem ter o mesmo responsável");
		}
		if (!movimentacaoSaida.getMotivo().equals(movimentacaoEntrada.getMotivo())) {
			throw new IllegalArgumentException("As movimentações devem ter o mesmo motivo");
		}
		if (movimentacaoSaida.getEstoqueId().equals(movimentacaoEntrada.getEstoqueId())) {
			throw new IllegalArgumentException("Os estoques de origem e destino devem ser diferentes");
		}

		// Criar ID temporário (será gerado pelo banco)
		var transferenciaId = new TransferenciaId(0);
		var dataHora = LocalDateTime.now();
		var transferencia = new Transferencia(transferenciaId, movimentacaoEntrada.getProdutoId(),
				movimentacaoEntrada.getQuantidade(), movimentacaoSaida.getEstoqueId(),
				movimentacaoEntrada.getEstoqueId(), new DataHoraTransferencia(dataHora),
				movimentacaoEntrada.getResponsavel(), movimentacaoEntrada.getMotivo(),
				movimentacaoSaida.getId(), movimentacaoEntrada.getId());

		repositorio.salvar(transferencia);
	}
}

