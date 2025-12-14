package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.pedido.ItemPedidoResumo;
import dev.gestock.sge.aplicacao.pedido.PedidoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.pedido.PedidoResumo;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.PedidoRepositorio;

@Repository
class PedidoRepositorioImpl implements PedidoRepositorio, PedidoRepositorioAplicacao {
	@Autowired
	PedidoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Autowired
	FornecedorJpaRepository fornecedorRepositorio;

	@Autowired
	EstoqueJpaRepository estoqueRepositorio;

	@Autowired
	ProdutoJpaRepository produtoRepositorio;

	@Override
	public Pedido salvar(Pedido pedido) {
		var pedidoJpa = mapeador.map(pedido, PedidoJpa.class);
		// Carregar fornecedor do banco
		pedidoJpa.fornecedor = fornecedorRepositorio.findById(pedido.getFornecedorId().getId())
			.orElseThrow(() -> new IllegalArgumentException("Fornecedor não encontrado"));
		// Carregar estoque do banco
		if (pedido.getEstoqueId() != null && pedido.getEstoqueId().getId() > 0) {
			pedidoJpa.estoque = estoqueRepositorio.findById(pedido.getEstoqueId().getId())
				.orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado"));
		}
		pedidoJpa = repositorio.save(pedidoJpa);
		return mapeador.map(pedidoJpa, Pedido.class);
	}

	@Transactional
	@Override
	public Pedido obter(PedidoId id) {
		var pedidoJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(pedidoJpa, Pedido.class);
	}

	@Override
	public List<Pedido> obterTodos() {
		var pedidosJpa = repositorio.findAll();
		return pedidosJpa.stream()
			.map(p -> mapeador.map(p, Pedido.class))
			.toList();
	}

	@Override
	public void remover(PedidoId id) {
		repositorio.deleteById(id.getId());
	}

	@Transactional
	@Override
	public List<PedidoResumo> pesquisarResumos() {
		var pedidosJpa = repositorio.findAll();
		return criarResumos(pedidosJpa);
	}

	@Transactional
	@Override
	public PedidoResumo obterResumo(int id) {
		var pedidoJpa = repositorio.findById(id).orElse(null);
		if (pedidoJpa == null) {
			return null;
		}
		return criarResumo(pedidoJpa);
	}

	private List<PedidoResumo> criarResumos(List<PedidoJpa> pedidosJpa) {
		return pedidosJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	private PedidoResumo criarResumo(PedidoJpa pedidoJpa) {
		var itensResumo = new ArrayList<ItemPedidoResumo>();
		if (pedidoJpa.itens != null) {
			for (var itemJpa : pedidoJpa.itens) {
				itensResumo.add(new ItemPedidoResumo() {
					@Override
					public int getProdutoId() {
						return itemJpa.produtoId;
					}

					@Override
					public String getProdutoNome() {
						var produtoJpa = produtoRepositorio.findById(itemJpa.produtoId).orElse(null);
						return produtoJpa != null ? produtoJpa.nome : "";
					}

					@Override
					public int getQuantidade() {
						return itemJpa.quantidade;
					}

					@Override
					public BigDecimal getPrecoUnitario() {
						return itemJpa.precoUnitario;
					}

					@Override
					public BigDecimal getSubtotal() {
						return itemJpa.precoUnitario.multiply(BigDecimal.valueOf(itemJpa.quantidade));
					}
				});
			}
		}

		return new PedidoResumo() {
			@Override
			public int getId() {
				return pedidoJpa.id;
			}

			@Override
			public int getFornecedorId() {
				return pedidoJpa.fornecedor != null ? pedidoJpa.fornecedor.id : 0;
			}

			@Override
			public String getFornecedorNome() {
				return pedidoJpa.fornecedor != null ? pedidoJpa.fornecedor.nome : "";
			}

			@Override
			public int getEstoqueId() {
				return pedidoJpa.estoque != null ? pedidoJpa.estoque.id : 0;
			}

			@Override
			public String getEstoqueNome() {
				return pedidoJpa.estoque != null ? pedidoJpa.estoque.nome : "";
			}

			@Override
			public List<ItemPedidoResumo> getItens() {
				return itensResumo;
			}

			@Override
			public BigDecimal getValorTotal() {
				return pedidoJpa.valorTotal;
			}

			@Override
			public LocalDate getDataPedido() {
				return pedidoJpa.dataPedido;
			}

			@Override
			public LocalDate getDataPrevista() {
				return pedidoJpa.dataPrevista;
			}

			@Override
			public String getStatus() {
				return pedidoJpa.status;
			}
		};
	}
}
