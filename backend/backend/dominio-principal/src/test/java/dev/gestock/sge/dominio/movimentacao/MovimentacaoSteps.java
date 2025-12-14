package dev.gestock.sge.dominio.movimentacao;

import dev.gestock.sge.dominio.estoque.Estoque;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.produto.Quantidade;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MovimentacaoSteps {
	
	@Mock
	private MovimentacaoRepositorio repositorio;
	
	@Mock
	private ProdutoRepositorio produtoRepositorio;
	
	@Mock
	private EstoqueRepositorio estoqueRepositorio;
	
	@Mock
	private EventoBarramento barramento;
	
	private MovimentacaoServico servico;
	private Movimentacao movimentacao;
	private Movimentacao movimentacaoRetornada;
	private Exception excecao;
	private int proximoId = 1;
	private ProdutoId produtoId;
	private EstoqueId estoqueId;
	
	public MovimentacaoSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new MovimentacaoServico(repositorio, produtoRepositorio, estoqueRepositorio, barramento);
		this.produtoId = new ProdutoId(1);
		this.estoqueId = new EstoqueId(1);
	}
	
	@Dado("que existe um produto cadastrado para movimentação")
	public void que_existe_um_produto_cadastrado_para_movimentacao() {
		var produto = mock(Produto.class);
		when(produtoRepositorio.obter(produtoId)).thenReturn(produto);
	}
	
	@Dado("que existe um estoque cadastrado para movimentação")
	public void que_existe_um_estoque_cadastrado_para_movimentacao() {
		var estoque = mock(Estoque.class);
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que não existe o produto especificado para movimentação")
	public void que_nao_existe_o_produto_especificado_para_movimentacao() {
		when(produtoRepositorio.obter(any(ProdutoId.class))).thenReturn(null);
	}
	
	@Dado("que não existe o estoque especificado")
	public void que_nao_existe_o_estoque_especificado() {
		when(estoqueRepositorio.obter(any(EstoqueId.class))).thenReturn(null);
	}
	
	@Quando("eu registro uma movimentação de tipo {string} com quantidade {int}, motivo {string} e responsável {string}")
	public void eu_registro_uma_movimentacao_de_tipo_com_quantidade_motivo_e_responsavel(String tipo, Integer quantidade, String motivo, String responsavel) {
		try {
			var movimentacaoId = new MovimentacaoId(proximoId++);
			var tipoEnum = TipoMovimentacao.valueOf(tipo);
			var quantidadeObj = new Quantidade(quantidade);
			var motivoObj = new Motivo(motivo);
			var responsavelObj = new Responsavel(responsavel);
			movimentacao = new Movimentacao(movimentacaoId, LocalDateTime.now(), produtoId, estoqueId, quantidadeObj, tipoEnum, motivoObj, responsavelObj);
			movimentacaoRetornada = servico.registrar(movimentacao);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu registro uma movimentação de tipo ENTRADA com quantidade {int}, motivo {string} e responsável {string}")
	public void eu_registro_uma_movimentacao_de_tipo_entrada_com_quantidade_motivo_e_responsavel(Integer quantidade, String motivo, String responsavel) {
		try {
			var movimentacaoId = new MovimentacaoId(proximoId++);
			var quantidadeObj = new Quantidade(quantidade);
			var motivoObj = new Motivo(motivo);
			var responsavelObj = new Responsavel(responsavel);
			movimentacao = new Movimentacao(movimentacaoId, LocalDateTime.now(), produtoId, estoqueId, quantidadeObj, TipoMovimentacao.ENTRADA, motivoObj, responsavelObj);
			when(repositorio.salvar(any(Movimentacao.class))).thenAnswer(invocation -> {
				Movimentacao m = invocation.getArgument(0);
				return m;
			});
			movimentacaoRetornada = servico.registrar(movimentacao);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu registro uma movimentação de tipo SAIDA com quantidade {int}, motivo {string} e responsável {string}")
	public void eu_registro_uma_movimentacao_de_tipo_saida_com_quantidade_motivo_e_responsavel(Integer quantidade, String motivo, String responsavel) {
		try {
			var movimentacaoId = new MovimentacaoId(proximoId++);
			var quantidadeObj = new Quantidade(quantidade);
			var motivoObj = new Motivo(motivo);
			var responsavelObj = new Responsavel(responsavel);
			movimentacao = new Movimentacao(movimentacaoId, LocalDateTime.now(), produtoId, estoqueId, quantidadeObj, TipoMovimentacao.SAIDA, motivoObj, responsavelObj);
			when(repositorio.salvar(any(Movimentacao.class))).thenAnswer(invocation -> {
				Movimentacao m = invocation.getArgument(0);
				return m;
			});
			movimentacaoRetornada = servico.registrar(movimentacao);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento registrar uma movimentação de tipo {string} com quantidade {int}, motivo {string} e responsável {string}")
	public void eu_tento_registrar_uma_movimentacao_de_tipo_com_quantidade_motivo_e_responsavel(String tipo, Integer quantidade, String motivo, String responsavel) {
		try {
			var movimentacaoId = new MovimentacaoId(proximoId++);
			var tipoEnum = TipoMovimentacao.valueOf(tipo);
			var quantidadeObj = new Quantidade(quantidade);
			var motivoObj = new Motivo(motivo);
			var responsavelObj = new Responsavel(responsavel);
			movimentacao = new Movimentacao(movimentacaoId, LocalDateTime.now(), produtoId, estoqueId, quantidadeObj, tipoEnum, motivoObj, responsavelObj);
			movimentacaoRetornada = servico.registrar(movimentacao);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento registrar uma movimentação de tipo ENTRADA com quantidade {int}, motivo {string} e responsável {string}")
	public void eu_tento_registrar_uma_movimentacao_de_tipo_entrada_com_quantidade_motivo_e_responsavel(Integer quantidade, String motivo, String responsavel) {
		try {
			var movimentacaoId = new MovimentacaoId(proximoId++);
			var quantidadeObj = new Quantidade(quantidade);
			var motivoObj = new Motivo(motivo);
			var responsavelObj = new Responsavel(responsavel);
			movimentacao = new Movimentacao(movimentacaoId, LocalDateTime.now(), produtoId, estoqueId, quantidadeObj, TipoMovimentacao.ENTRADA, motivoObj, responsavelObj);
			movimentacaoRetornada = servico.registrar(movimentacao);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Então("a movimentação deve ser registrada com sucesso")
	public void a_movimentacao_deve_ser_registrada_com_sucesso() {
		assertThat(movimentacaoRetornada).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Movimentacao.class));
	}
	
	@Então("a movimentação deve ter tipo {string}")
	public void a_movimentacao_deve_ter_tipo(String tipo) {
		assertThat(movimentacaoRetornada.getTipo()).isEqualTo(TipoMovimentacao.valueOf(tipo));
	}
	
	@Então("a movimentação deve ter tipo ENTRADA")
	public void a_movimentacao_deve_ter_tipo_entrada() {
		assertThat(movimentacaoRetornada.getTipo()).isEqualTo(TipoMovimentacao.ENTRADA);
	}
	
	@Então("a movimentação deve ter tipo SAIDA")
	public void a_movimentacao_deve_ter_tipo_saida() {
		assertThat(movimentacaoRetornada.getTipo()).isEqualTo(TipoMovimentacao.SAIDA);
	}
	
	@Então("a movimentação deve ter quantidade {int}")
	public void a_movimentacao_deve_ter_quantidade(Integer quantidade) {
		assertThat(movimentacaoRetornada.getQuantidade().getValor()).isEqualTo(quantidade);
	}
	
	@Então("deve ocorrer um erro informando que o produto da movimentação não foi encontrado")
	public void deve_ocorrer_um_erro_informando_que_o_produto_da_movimentacao_nao_foi_encontrado() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("Produto não encontrado");
	}
	
	@Então("deve ocorrer um erro informando que o estoque não foi encontrado")
	public void deve_ocorrer_um_erro_informando_que_o_estoque_nao_foi_encontrado() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("Estoque não encontrado");
	}
}

