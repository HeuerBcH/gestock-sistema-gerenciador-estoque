package dev.gestock.sge.dominio.transferencia;

import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.Responsavel;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;
import dev.gestock.sge.dominio.produto.ProdutoId;
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

public class TransferenciaSteps {
	
	@Mock
	private TransferenciaRepositorio repositorio;
	
	private TransferenciaServico servico;
	private Movimentacao movimentacaoSaida;
	private Movimentacao movimentacaoEntrada;
	private Exception excecao;
	
	public TransferenciaSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new TransferenciaServico(repositorio);
	}
	
	@Dado("que existem duas movimentações correspondentes")
	public void que_existem_duas_movimentacoes_correspondentes() {
		var produtoId = new ProdutoId(1);
		var estoqueOrigem = new EstoqueId(1);
		var estoqueDestino = new EstoqueId(2);
		var quantidade = new Quantidade(50);
		var motivo = new Motivo("Transferência");
		var responsavel = new Responsavel("João Silva");
		
		movimentacaoSaida = new Movimentacao(new MovimentacaoId(1), LocalDateTime.now(), produtoId, estoqueOrigem,
				quantidade, TipoMovimentacao.SAIDA, motivo, responsavel);
		movimentacaoEntrada = new Movimentacao(new MovimentacaoId(2), LocalDateTime.now(), produtoId, estoqueDestino,
				quantidade, TipoMovimentacao.ENTRADA, motivo, responsavel);
	}
	
	@Dado("que as movimentações têm o mesmo estoque")
	public void que_as_movimentacoes_tem_o_mesmo_estoque() {
		var produtoId = new ProdutoId(1);
		var estoque = new EstoqueId(1);
		var quantidade = new Quantidade(50);
		var motivo = new Motivo("Transferência");
		var responsavel = new Responsavel("João Silva");
		
		movimentacaoSaida = new Movimentacao(new MovimentacaoId(1), LocalDateTime.now(), produtoId, estoque,
				quantidade, TipoMovimentacao.SAIDA, motivo, responsavel);
		movimentacaoEntrada = new Movimentacao(new MovimentacaoId(2), LocalDateTime.now(), produtoId, estoque,
				quantidade, TipoMovimentacao.ENTRADA, motivo, responsavel);
	}
	
	@Dado("que as movimentações têm produtos diferentes")
	public void que_as_movimentacoes_tem_produtos_diferentes() {
		var produto1 = new ProdutoId(1);
		var produto2 = new ProdutoId(2);
		var estoqueOrigem = new EstoqueId(1);
		var estoqueDestino = new EstoqueId(2);
		var quantidade = new Quantidade(50);
		var motivo = new Motivo("Transferência");
		var responsavel = new Responsavel("João Silva");
		
		movimentacaoSaida = new Movimentacao(new MovimentacaoId(1), LocalDateTime.now(), produto1, estoqueOrigem,
				quantidade, TipoMovimentacao.SAIDA, motivo, responsavel);
		movimentacaoEntrada = new Movimentacao(new MovimentacaoId(2), LocalDateTime.now(), produto2, estoqueDestino,
				quantidade, TipoMovimentacao.ENTRADA, motivo, responsavel);
	}
	
	@Quando("eu crio uma transferência a partir das movimentações")
	public void eu_crio_uma_transferencia_a_partir_das_movimentacoes() {
		try {
			servico.criarTransferencia(movimentacaoSaida, movimentacaoEntrada);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar uma transferência a partir das movimentações")
	public void eu_tento_criar_uma_transferencia_a_partir_das_movimentacoes() {
		try {
			servico.criarTransferencia(movimentacaoSaida, movimentacaoEntrada);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Então("a transferência deve ser criada com sucesso")
	public void a_transferencia_deve_ser_criada_com_sucesso() {
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Transferencia.class));
	}
	
	@Então("a transferência deve ter estoque de origem e destino diferentes")
	public void a_transferencia_deve_ter_estoque_de_origem_e_destino_diferentes() {
		verify(repositorio, times(1)).salvar(argThat(transferencia -> 
			!transferencia.getEstoqueOrigem().equals(transferencia.getEstoqueDestino())));
	}
	
	@Então("deve ocorrer um erro informando que os estoques devem ser diferentes")
	public void deve_ocorrer_um_erro_informando_que_os_estoques_devem_ser_diferentes() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("diferentes");
	}
	
	@Então("deve ocorrer um erro informando que as movimentações devem ser do mesmo produto")
	public void deve_ocorrer_um_erro_informando_que_as_movimentacoes_devem_ser_do_mesmo_produto() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("produto");
	}
}

