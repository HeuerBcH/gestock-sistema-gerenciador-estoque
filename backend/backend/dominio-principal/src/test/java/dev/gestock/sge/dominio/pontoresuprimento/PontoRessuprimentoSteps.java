package dev.gestock.sge.dominio.pontoresuprimento;

import dev.gestock.sge.dominio.estoque.Estoque;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PontoRessuprimentoSteps {
	
	@Mock
	private PontoRessuprimentoRepositorio repositorio;
	
	@Mock
	private ProdutoRepositorio produtoRepositorio;
	
	@Mock
	private EstoqueRepositorio estoqueRepositorio;
	
	private PontoRessuprimentoServico servico;
	private PontoRessuprimento ponto;
	private PontoRessuprimento pontoRetornado;
	private RopCalculado ropCalculado;
	private SaldoAtual saldoAtual;
	private StatusRop status;
	private Exception excecao;
	private int proximoId = 1;
	private ProdutoId produtoId;
	private EstoqueId estoqueId;
	
	public PontoRessuprimentoSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new PontoRessuprimentoServico(repositorio, produtoRepositorio, estoqueRepositorio);
		this.produtoId = new ProdutoId(1);
		this.estoqueId = new EstoqueId(1);
	}
	
	@Dado("que existe um produto cadastrado para ponto de ressuprimento")
	public void que_existe_um_produto_cadastrado_para_ponto_de_ressuprimento() {
		var produto = mock(Produto.class);
		when(produtoRepositorio.obter(produtoId)).thenReturn(produto);
	}
	
	@Dado("que existe um estoque cadastrado para ponto de ressuprimento")
	public void que_existe_um_estoque_cadastrado_para_ponto_de_ressuprimento() {
		var estoque = mock(Estoque.class);
		when(estoqueRepositorio.obter(estoqueId)).thenReturn(estoque);
	}
	
	@Dado("que existe um ponto de ressuprimento com estoque de segurança {int}")
	public void que_existe_um_ponto_de_ressuprimento_com_estoque_de_seguranca(Integer estoqueSeguranca) {
		var pontoId = new PontoRessuprimentoId(proximoId++);
		var estoqueSegurancaObj = new EstoqueSeguranca(estoqueSeguranca);
		var pontoExistente = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSegurancaObj);
		
		when(repositorio.obter(pontoId)).thenReturn(pontoExistente);
		when(repositorio.salvar(any(PontoRessuprimento.class))).thenAnswer(invocation -> {
			PontoRessuprimento p = invocation.getArgument(0);
			return p;
		});
		
		this.ponto = pontoExistente;
	}
	
	@Dado("que existe um ponto de ressuprimento")
	public void que_existe_um_ponto_de_ressuprimento() {
		var pontoId = new PontoRessuprimentoId(proximoId++);
		var estoqueSeguranca = new EstoqueSeguranca(20);
		var pontoExistente = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSeguranca);
		
		when(repositorio.obter(pontoId)).thenReturn(pontoExistente);
		when(repositorio.salvar(any(PontoRessuprimento.class))).thenAnswer(invocation -> {
			PontoRessuprimento p = invocation.getArgument(0);
			return p;
		});
		
		this.ponto = pontoExistente;
	}
	
	@Dado("que o saldo atual é {int}")
	public void que_o_saldo_atual_e(Integer saldo) {
		this.saldoAtual = new SaldoAtual(saldo);
	}
	
	@Dado("que o ROP calculado é {int}")
	public void que_o_rop_calculado_e(Integer rop) {
		this.ropCalculado = new RopCalculado(rop);
	}
	
	@Dado("que existe um ponto de ressuprimento para o estoque e produto")
	public void que_existe_um_ponto_de_ressuprimento_para_o_estoque_e_produto() {
		var pontoId = new PontoRessuprimentoId(proximoId++);
		var estoqueSeguranca = new EstoqueSeguranca(20);
		var pontoExistente = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSeguranca);
		
		when(repositorio.obterPorEstoqueEProduto(estoqueId, produtoId)).thenReturn(pontoExistente);
		when(repositorio.salvar(any(PontoRessuprimento.class))).thenAnswer(invocation -> {
			PontoRessuprimento p = invocation.getArgument(0);
			return p;
		});
	}
	
	@Quando("eu registro um ponto de ressuprimento com estoque de segurança {int}")
	public void eu_registro_um_ponto_de_ressuprimento_com_estoque_de_seguranca(Integer estoqueSeguranca) {
		try {
			var pontoId = new PontoRessuprimentoId(proximoId++);
			var estoqueSegurancaObj = new EstoqueSeguranca(estoqueSeguranca);
			ponto = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSegurancaObj);
			when(repositorio.salvar(any(PontoRessuprimento.class))).thenAnswer(invocation -> {
				PontoRessuprimento p = invocation.getArgument(0);
				return p;
			});
			pontoRetornado = servico.registrar(ponto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu calculo o ROP com consumo médio diário {double} e lead time {int} dias")
	public void eu_calculo_o_rop_com_consumo_medio_diario_e_lead_time_dias(Double consumo, Integer leadTime) {
		assertThat(ponto).isNotNull(); // Garante que o ponto foi inicializado no step anterior
		// Garante que o estoque de segurança está correto
		assertThat(ponto.getEstoqueSeguranca()).isNotNull();
		var consumoObj = new ConsumoMedioDiario(consumo);
		var leadTimeObj = new LeadTime(leadTime);
		ropCalculado = ponto.calcularRop(consumoObj, leadTimeObj);
	}
	
	@Quando("eu determino o status do ROP")
	public void eu_determino_o_status_do_rop() {
		if (saldoAtual == null) {
			saldoAtual = new SaldoAtual(100);
		}
		if (ropCalculado == null) {
			ropCalculado = new RopCalculado(58);
		}
		status = ponto.determinarStatus(saldoAtual, ropCalculado);
	}
	
	@Quando("eu atualizo o estoque de segurança para {int}")
	public void eu_atualizo_o_estoque_de_seguranca_para(Integer novoEstoqueSeguranca) {
		try {
			servico.atualizarEstoqueSeguranca(ponto.getId(), new EstoqueSeguranca(novoEstoqueSeguranca));
			pontoRetornado = repositorio.obter(ponto.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento registrar outro ponto de ressuprimento para o mesmo estoque e produto")
	public void eu_tento_registrar_outro_ponto_de_ressuprimento_para_o_mesmo_estoque_e_produto() {
		try {
			var pontoId = new PontoRessuprimentoId(proximoId++);
			var estoqueSeguranca = new EstoqueSeguranca(25);
			ponto = new PontoRessuprimento(pontoId, estoqueId, produtoId, estoqueSeguranca);
			pontoRetornado = servico.registrar(ponto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Então("o ponto de ressuprimento deve ser registrado com sucesso")
	public void o_ponto_de_ressuprimento_deve_ser_registrado_com_sucesso() {
		assertThat(pontoRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(PontoRessuprimento.class));
	}
	
	@Então("o ponto de ressuprimento deve ter estoque de segurança {int}")
	public void o_ponto_de_ressuprimento_deve_ter_estoque_de_seguranca(Integer estoqueSeguranca) {
		assertThat(pontoRetornado.getEstoqueSeguranca().getValor()).isEqualTo(estoqueSeguranca);
	}
	
	@Então("o ROP calculado deve ser {int}")
	public void o_rop_calculado_deve_ser(Integer ropEsperado) {
		assertThat(ropCalculado.getValor()).isEqualTo(ropEsperado);
	}
	
	@Então("o status deve ser {string}")
	public void o_status_deve_ser(String statusEsperado) {
		assertThat(status).isEqualTo(StatusRop.valueOf(statusEsperado));
	}
	
	@Então("o status deve ser ADEQUADO")
	public void o_status_deve_ser_adequado() {
		assertThat(status).isEqualTo(StatusRop.ADEQUADO);
	}
	
	@Então("o status deve ser INADEQUADO")
	public void o_status_deve_ser_inadequado() {
		assertThat(status).isEqualTo(StatusRop.INADEQUADO);
	}
	
	@Então("deve ocorrer um erro informando que já existe um ponto de ressuprimento")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_ponto_de_ressuprimento() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("ponto de ressuprimento");
	}
}

