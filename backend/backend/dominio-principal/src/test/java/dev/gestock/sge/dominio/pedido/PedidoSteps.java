package dev.gestock.sge.dominio.pedido;

import dev.gestock.sge.dominio.cotacao.Cotacao;
import dev.gestock.sge.dominio.cotacao.CotacaoId;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;
import dev.gestock.sge.dominio.cotacao.StatusAprovacao;
import dev.gestock.sge.dominio.cotacao.Validade;
import dev.gestock.sge.dominio.estoque.Capacidade;
import dev.gestock.sge.dominio.estoque.Estoque;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.fornecedor.Status;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoServico;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.produto.Quantidade;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PedidoSteps {
	
	@Mock
	private PedidoRepositorio repositorio;
	
	@Mock
	private FornecedorRepositorio fornecedorRepositorio;
	
	@Mock
	private ProdutoRepositorio produtoRepositorio;
	
	@Mock
	private EstoqueRepositorio estoqueRepositorio;
	
	@Mock
	private CotacaoRepositorio cotacaoRepositorio;
	
	@Mock
	private CotacaoServico cotacaoServico;
	
	@Mock
	private MovimentacaoServico movimentacaoServico;
	
	@Mock
	private EventoBarramento barramento;
	
	private PedidoServico servico;
	private Pedido pedido;
	private Pedido pedidoRetornado;
	private Exception excecao;
	private int proximoId = 1;
	private FornecedorId fornecedorId;
	private EstoqueId estoqueId;
	private ProdutoId produtoId;
	
	public PedidoSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new PedidoServico(repositorio, fornecedorRepositorio, produtoRepositorio, 
				estoqueRepositorio, cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
		this.fornecedorId = new FornecedorId(1);
		this.estoqueId = new EstoqueId(1);
		this.produtoId = new ProdutoId(1);
	}
	
	@Dado("que existe um fornecedor ativo cadastrado")
	public void que_existe_um_fornecedor_ativo_cadastrado() {
		var fornecedor = mock(Fornecedor.class);
		when(fornecedor.getStatus()).thenReturn(Status.ATIVO);
		when(fornecedor.getLeadTime()).thenReturn(new LeadTime(7));
		when(fornecedorRepositorio.obter(fornecedorId)).thenReturn(fornecedor);
	}
	
	@Dado("que existe um estoque ativo cadastrado")
	public void que_existe_um_estoque_ativo_cadastrado() {
		var estoque = mock(Estoque.class);
		when(estoque.getStatus()).thenReturn(Status.ATIVO);
		when(estoque.getCapacidade()).thenReturn(new Capacidade(10000));
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que existe um produto ativo cadastrado")
	public void que_existe_um_produto_ativo_cadastrado() {
		var produto = mock(Produto.class);
		when(produto.getStatus()).thenReturn(Status.ATIVO);
		when(produtoRepositorio.obter(produtoId)).thenReturn(produto);
	}
	
	@Dado("que existe uma cotação aprovada para o produto")
	public void que_existe_uma_cotacao_aprovada_para_o_produto() {
		var cotacao = mock(Cotacao.class);
		when(cotacao.getPreco()).thenReturn(new Custo(10.50));
		when(cotacao.getFornecedorId()).thenReturn(fornecedorId);
		when(cotacaoRepositorio.obterPorProduto(produtoId)).thenReturn(List.of(cotacao));
		when(cotacaoServico.obterMaisVantajosa(produtoId)).thenReturn(cotacao);
	}
	
	@Dado("que o fornecedor está inativo")
	public void que_o_fornecedor_esta_inativo() {
		var fornecedor = mock(Fornecedor.class);
		when(fornecedor.getStatus()).thenReturn(Status.INATIVO);
		when(fornecedorRepositorio.obter(fornecedorId)).thenReturn(fornecedor);
	}
	
	@Dado("que o estoque está inativo")
	public void que_o_estoque_esta_inativo() {
		var estoque = mock(Estoque.class);
		when(estoque.getStatus()).thenReturn(Status.INATIVO);
		when(estoque.getCapacidade()).thenReturn(new Capacidade(10000)); // Adiciona capacidade para evitar NullPointerException
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que o estoque tem capacidade {int}")
	public void que_o_estoque_tem_capacidade(Integer capacidade) {
		var estoque = mock(Estoque.class);
		when(estoque.getStatus()).thenReturn(Status.ATIVO);
		when(estoque.getCapacidade()).thenReturn(new Capacidade(capacidade));
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que o pedido tem quantidade total {int}")
	public void que_o_pedido_tem_quantidade_total(Integer quantidadeTotal) {
		// A quantidade será usada no step de criação do pedido
		// O estoque será configurado com capacidade menor que a quantidade
		var estoque = mock(Estoque.class);
		when(estoque.getStatus()).thenReturn(Status.ATIVO);
		when(estoque.getCapacidade()).thenReturn(new Capacidade(quantidadeTotal - 1)); // Capacidade menor que a quantidade
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que existe um pedido cadastrado com status {string}")
	public void que_existe_um_pedido_cadastrado_com_status(String status) {
		var pedidoId = new PedidoId(proximoId++);
		var itens = new ArrayList<ItemPedido>();
		itens.add(new ItemPedido(produtoId, new Quantidade(100), new Custo(10.50)));
		var dataPedido = new DataPedido(LocalDate.now());
		var statusEnum = StatusPedido.valueOf(status);
		var pedidoExistente = new Pedido(pedidoId, fornecedorId, estoqueId, itens, dataPedido, statusEnum);
		
		when(repositorio.obter(pedidoId)).thenReturn(pedidoExistente);
		when(repositorio.salvar(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido p = invocation.getArgument(0);
			return p;
		});
		
		this.pedido = pedidoExistente;
	}
	
	@Dado("que existe um pedido cadastrado com status CRIADO")
	public void que_existe_um_pedido_cadastrado_com_status_criado() {
		var pedidoId = new PedidoId(proximoId++);
		var itens = new ArrayList<ItemPedido>();
		itens.add(new ItemPedido(produtoId, new Quantidade(100), new Custo(10.50)));
		var dataPedido = new DataPedido(LocalDate.now());
		var pedidoExistente = new Pedido(pedidoId, fornecedorId, estoqueId, itens, dataPedido, StatusPedido.CRIADO);
		
		when(repositorio.obter(pedidoId)).thenReturn(pedidoExistente);
		when(repositorio.salvar(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido p = invocation.getArgument(0);
			return p;
		});
		
		this.pedido = pedidoExistente;
	}
	
	@Dado("que existe um pedido cadastrado com status EM_TRANSPORTE")
	public void que_existe_um_pedido_cadastrado_com_status_em_transporte() {
		var pedidoId = new PedidoId(proximoId++);
		var itens = new ArrayList<ItemPedido>();
		itens.add(new ItemPedido(produtoId, new Quantidade(100), new Custo(10.50)));
		var dataPedido = new DataPedido(LocalDate.now());
		var pedidoExistente = new Pedido(pedidoId, fornecedorId, estoqueId, itens, dataPedido, StatusPedido.EM_TRANSPORTE);
		
		when(repositorio.obter(pedidoId)).thenReturn(pedidoExistente);
		when(repositorio.salvar(any(Pedido.class))).thenAnswer(invocation -> {
			Pedido p = invocation.getArgument(0);
			return p;
		});
		
		this.pedido = pedidoExistente;
	}
	
	@Quando("eu crio um pedido com itens")
	public void eu_crio_um_pedido_com_itens() {
		try {
			var pedidoId = new PedidoId(proximoId++);
			var itens = new ArrayList<ItemPedido>();
			itens.add(new ItemPedido(produtoId, new Quantidade(100), new Custo(10.50)));
			var dataPedido = new DataPedido(LocalDate.now());
			pedido = new Pedido(pedidoId, fornecedorId, estoqueId, itens, dataPedido, StatusPedido.CRIADO);
			when(repositorio.salvar(any(Pedido.class))).thenAnswer(invocation -> {
				Pedido p = invocation.getArgument(0);
				return p;
			});
			pedidoRetornado = servico.criar(pedido);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar um pedido com itens")
	public void eu_tento_criar_um_pedido_com_itens() {
		try {
			var pedidoId = new PedidoId(proximoId++);
			var itens = new ArrayList<ItemPedido>();
			// Usa quantidade que excede a capacidade configurada no step anterior
			var estoque = estoqueRepositorio.obter(estoqueId);
			int quantidadeExcedente = estoque != null ? estoque.getCapacidade().getValor() + 100 : 1500;
			itens.add(new ItemPedido(produtoId, new Quantidade(quantidadeExcedente), new Custo(10.50)));
			var dataPedido = new DataPedido(LocalDate.now());
			pedido = new Pedido(pedidoId, fornecedorId, estoqueId, itens, dataPedido, StatusPedido.CRIADO);
			pedidoRetornado = servico.criar(pedido);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu confirmo o recebimento do pedido")
	public void eu_confirmo_o_recebimento_do_pedido() {
		try {
			servico.confirmarRecebimento(pedido.getId());
			pedidoRetornado = repositorio.obter(pedido.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu cancelo o pedido")
	public void eu_cancelo_o_pedido() {
		try {
			servico.cancelar(pedido.getId());
			pedidoRetornado = repositorio.obter(pedido.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento cancelar o pedido")
	public void eu_tento_cancelar_o_pedido() {
		try {
			servico.cancelar(pedido.getId());
			pedidoRetornado = repositorio.obter(pedido.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Então("o pedido deve ser criado com sucesso")
	public void o_pedido_deve_ser_criado_com_sucesso() {
		assertThat(pedidoRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Pedido.class));
	}
	
	@Então("o valor total do pedido deve ser calculado corretamente")
	public void o_valor_total_do_pedido_deve_ser_calculado_corretamente() {
		assertThat(pedidoRetornado.getValorTotal()).isNotNull();
		assertThat(pedidoRetornado.getValorTotal().getValor()).isGreaterThan(BigDecimal.ZERO);
	}
	
	@Então("a data prevista do pedido deve ser calculada baseada no lead time do fornecedor")
	public void a_data_prevista_do_pedido_deve_ser_calculada_baseada_no_lead_time_do_fornecedor() {
		assertThat(pedidoRetornado.getDataPrevista()).isNotNull();
	}
	
	@Então("deve ocorrer um erro informando que o fornecedor deve estar ativo")
	public void deve_ocorrer_um_erro_informando_que_o_fornecedor_deve_estar_ativo() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("ativo");
	}
	
	@Então("deve ocorrer um erro informando que o estoque deve estar ativo")
	public void deve_ocorrer_um_erro_informando_que_o_estoque_deve_estar_ativo() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("ativo");
	}
	
	@Então("deve ocorrer um erro informando que a quantidade excede a capacidade do estoque")
	public void deve_ocorrer_um_erro_informando_que_a_quantidade_excede_a_capacidade_do_estoque() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("capacidade");
	}
	
	@Então("o pedido deve ter status {string}")
	public void o_pedido_deve_ter_status(String status) {
		assertThat(pedidoRetornado.getStatus()).isEqualTo(StatusPedido.valueOf(status));
	}
	
	@Então("o pedido deve ter status RECEBIDO")
	public void o_pedido_deve_ter_status_recebido() {
		assertThat(pedidoRetornado.getStatus()).isEqualTo(StatusPedido.RECEBIDO);
	}
	
	@Então("o pedido deve ter status CANCELADO")
	public void o_pedido_deve_ter_status_cancelado() {
		assertThat(pedidoRetornado.getStatus()).isEqualTo(StatusPedido.CANCELADO);
	}
	
	@Então("devem ser criadas movimentações de ENTRADA para cada item")
	public void devem_ser_criadas_movimentacoes_de_entrada_para_cada_item() {
		verify(movimentacaoServico, atLeastOnce()).registrar(any());
	}
	
	@Então("deve ocorrer um erro informando que não é possível cancelar um pedido em transporte")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_cancelar_um_pedido_em_transporte() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("transporte");
	}
}

