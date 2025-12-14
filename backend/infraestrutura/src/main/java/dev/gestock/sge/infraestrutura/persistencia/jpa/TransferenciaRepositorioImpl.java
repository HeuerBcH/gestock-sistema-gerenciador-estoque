package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaResumo;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaTotais;
import dev.gestock.sge.dominio.transferencia.Transferencia;
import dev.gestock.sge.dominio.transferencia.TransferenciaId;
import dev.gestock.sge.dominio.transferencia.TransferenciaRepositorio;
import dev.gestock.sge.infraestrutura.persistencia.jpa.JpaMapeador;

@Repository
class TransferenciaRepositorioImpl implements TransferenciaRepositorio, TransferenciaRepositorioAplicacao {
	@Autowired
	TransferenciaJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Autowired
	EstoqueJpaRepository estoqueRepositorio;

	@Autowired
	MovimentacaoJpaRepository movimentacaoRepositorio;

	@Autowired
	EstoqueProdutoJpaRepository estoqueProdutoRepositorio;

	@Override
	public void salvar(Transferencia transferencia) {
		var transferenciaJpa = mapeador.map(transferencia, TransferenciaJpa.class);
		// Carregar produto, estoques e movimentações do banco
		transferenciaJpa.produto = produtoRepositorio.findById(transferencia.getProdutoId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
		transferenciaJpa.estoqueOrigem = estoqueRepositorio.findById(transferencia.getEstoqueOrigem().getId())
			.orElseThrow(() -> new IllegalArgumentException("Estoque de origem não encontrado"));
		transferenciaJpa.estoqueDestino = estoqueRepositorio.findById(transferencia.getEstoqueDestino().getId())
			.orElseThrow(() -> new IllegalArgumentException("Estoque de destino não encontrado"));
		if (transferencia.getMovimentacaoSaidaId() != null) {
			transferenciaJpa.movimentacaoSaida = movimentacaoRepositorio
				.findById(transferencia.getMovimentacaoSaidaId().getId()).orElse(null);
		}
		if (transferencia.getMovimentacaoEntradaId() != null) {
			transferenciaJpa.movimentacaoEntrada = movimentacaoRepositorio
				.findById(transferencia.getMovimentacaoEntradaId().getId()).orElse(null);
		}
		repositorio.save(transferenciaJpa);
	}

	@Override
	public Transferencia obter(TransferenciaId id) {
		var transferenciaJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(transferenciaJpa, Transferencia.class);
	}

	@Override
	public List<TransferenciaResumo> pesquisarResumos(String busca) {
		var transferenciasJpa = repositorio.pesquisarResumosJpa(busca);
		var resumos = new ArrayList<TransferenciaResumo>();
		for (var transferenciaJpa : transferenciasJpa) {
			resumos.add(new TransferenciaResumo() {
				@Override
				public int getId() {
					return transferenciaJpa.id;
				}

				@Override
				public LocalDateTime getDataHoraTransferencia() {
					return transferenciaJpa.dataHoraTransferencia;
				}

				@Override
				public int getProdutoId() {
					return transferenciaJpa.produto != null ? transferenciaJpa.produto.id : 0;
				}

				@Override
				public String getProdutoNome() {
					return transferenciaJpa.produto != null ? transferenciaJpa.produto.nome : "";
				}

				@Override
				public int getQuantidade() {
					return transferenciaJpa.quantidade;
				}

				@Override
				public int getEstoqueOrigemId() {
					return transferenciaJpa.estoqueOrigem != null ? transferenciaJpa.estoqueOrigem.id : 0;
				}

				@Override
				public String getEstoqueOrigemNome() {
					return transferenciaJpa.estoqueOrigem != null ? transferenciaJpa.estoqueOrigem.nome : "";
				}

				@Override
				public int getEstoqueDestinoId() {
					return transferenciaJpa.estoqueDestino != null ? transferenciaJpa.estoqueDestino.id : 0;
				}

				@Override
				public String getEstoqueDestinoNome() {
					return transferenciaJpa.estoqueDestino != null ? transferenciaJpa.estoqueDestino.nome : "";
				}

				@Override
				public String getResponsavel() {
					return transferenciaJpa.responsavel;
				}

				@Override
				public String getMotivo() {
					return transferenciaJpa.motivo;
				}
			});
		}
		return resumos;
	}

	@Override
	public TransferenciaTotais obterTotais() {
		var totalTransferencias = repositorio.contarTotal();
		var unidadesMovidas = repositorio.somarUnidadesMovidas();
		var produtosDistintos = repositorio.contarProdutosDistintos();

		return new TransferenciaTotais() {
			@Override
			public int getTotalTransferencias() {
				return (int) totalTransferencias;
			}

			@Override
			public int getUnidadesMovidas() {
				return (int) unidadesMovidas;
			}

			@Override
			public int getProdutosDistintos() {
				return (int) produtosDistintos;
			}
		};
	}

	@Override
	public int obterQuantidadeProdutoNoEstoque(int estoqueId, int produtoId) {
		var estoqueProduto = estoqueProdutoRepositorio.findByEstoqueIdAndProdutoId(estoqueId, produtoId);
		return estoqueProduto.map(ep -> ep.quantidade).orElse(0);
	}

	@Override
	public int obterCapacidadeDisponivel(int estoqueId) {
		var estoqueOpt = estoqueRepositorio.findById(estoqueId);
		if (estoqueOpt.isEmpty()) {
			return 0;
		}
		var estoque = estoqueOpt.get();
		var ocupacaoAtual = estoqueRepositorio.calcularOcupacao(estoqueId);
		return Math.max(0, estoque.capacidade - ocupacaoAtual);
	}
}

