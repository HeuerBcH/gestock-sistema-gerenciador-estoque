package dev.gestock.sge.dominio.produto;

import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.Status;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ProdutoSteps {
	
	@Mock
	private ProdutoRepositorio repositorio;
	
	private ProdutoServico servico;
	private Produto produto;
	private Produto produtoRetornado;
	private Exception excecao;
	private int proximoId = 1;
	
	public ProdutoSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new ProdutoServico(repositorio);
	}
	
	@Dado("que não existe nenhum produto cadastrado")
	public void que_nao_existe_nenhum_produto_cadastrado() {
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
	}
	
	@Dado("que existe um produto cadastrado com código {string}")
	public void que_existe_um_produto_cadastrado_com_codigo(String codigo) {
		var produtoId = new ProdutoId(proximoId++);
		var codigoObj = new Codigo(codigo);
		var peso = new Peso(1000);
		var fornecedores = new ArrayList<FornecedorId>();
		var produtoExistente = new Produto(produtoId, codigoObj, "Produto Teste", peso, Perecivel.NAO, Status.ATIVO, fornecedores);
		
		// Quando tentar criar um novo produto (id diferente), deve retornar true (já existe)
		when(repositorio.existePorCodigo(eq(codigo), anyInt())).thenAnswer(invocation -> {
			Integer id = invocation.getArgument(1);
			return !id.equals(produtoId.getId()); // Retorna true se o id for diferente
		});
		when(repositorio.obterPorCodigo(codigoObj)).thenReturn(produtoExistente);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
	}
	
	@Dado("que existe um produto cadastrado com status {string}")
	public void que_existe_um_produto_cadastrado_com_status(String status) {
		var produtoId = new ProdutoId(proximoId++);
		var codigo = new Codigo("PROD001");
		var peso = new Peso(1000);
		var fornecedores = new ArrayList<FornecedorId>();
		var statusEnum = Status.valueOf(status);
		var produtoExistente = new Produto(produtoId, codigo, "Produto Teste", peso, Perecivel.NAO, statusEnum, fornecedores);
		
		when(repositorio.obter(produtoId)).thenReturn(produtoExistente);
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
		
		this.produto = produtoExistente;
	}
	
	@Dado("que existe um produto cadastrado com status INATIVO")
	public void que_existe_um_produto_cadastrado_com_status_inativo() {
		var produtoId = new ProdutoId(proximoId++);
		var codigo = new Codigo("PROD001");
		var peso = new Peso(1000);
		var fornecedores = new ArrayList<FornecedorId>();
		var produtoExistente = new Produto(produtoId, codigo, "Produto Teste", peso, Perecivel.NAO, Status.INATIVO, fornecedores);
		
		when(repositorio.obter(produtoId)).thenReturn(produtoExistente);
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
		
		this.produto = produtoExistente;
	}
	
	@Dado("que existe um produto cadastrado com status ATIVO")
	public void que_existe_um_produto_cadastrado_com_status_ativo() {
		var produtoId = new ProdutoId(proximoId++);
		var codigo = new Codigo("PROD001");
		var peso = new Peso(1000);
		var fornecedores = new ArrayList<FornecedorId>();
		var produtoExistente = new Produto(produtoId, codigo, "Produto Teste", peso, Perecivel.NAO, Status.ATIVO, fornecedores);
		
		when(repositorio.obter(produtoId)).thenReturn(produtoExistente);
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
		
		this.produto = produtoExistente;
	}
	
	@Dado("que o produto está vinculado a um estoque ativo")
	public void que_o_produto_esta_vinculado_a_um_estoque_ativo() {
		when(repositorio.possuiEstoqueAtivo(produto.getId().getId())).thenReturn(true);
	}
	
	@Dado("que o produto não está vinculado a nenhum estoque ativo")
	public void que_o_produto_nao_esta_vinculado_a_nenhum_estoque_ativo() {
		when(repositorio.possuiEstoqueAtivo(produto.getId().getId())).thenReturn(false);
	}
	
	@Dado("que o produto não possui saldo em estoque")
	public void que_o_produto_nao_possui_saldo_em_estoque() {
		when(repositorio.possuiSaldoEmEstoque(produto.getId().getId())).thenReturn(false);
	}
	
	@Dado("que o produto possui saldo em estoque")
	public void que_o_produto_possui_saldo_em_estoque() {
		when(repositorio.possuiSaldoEmEstoque(produto.getId().getId())).thenReturn(true);
	}
	
	@Dado("que o produto não possui pedidos em andamento")
	public void que_o_produto_nao_possui_pedidos_em_andamento() {
		when(repositorio.possuiPedidosEmAndamento(produto.getId().getId())).thenReturn(false);
	}
	
	@Dado("que o produto possui pedidos em andamento")
	public void que_o_produto_possui_pedidos_em_andamento() {
		when(repositorio.possuiPedidosEmAndamento(produto.getId().getId())).thenReturn(true);
	}
	
	@Dado("que existe um produto cadastrado com nome {string}")
	public void que_existe_um_produto_cadastrado_com_nome(String nome) {
		var produtoId = new ProdutoId(proximoId++);
		var codigo = new Codigo("PROD001");
		var peso = new Peso(1000);
		var fornecedores = new ArrayList<FornecedorId>();
		var produtoExistente = new Produto(produtoId, codigo, nome, peso, Perecivel.NAO, Status.ATIVO, fornecedores);
		
		when(repositorio.obter(produtoId)).thenReturn(produtoExistente);
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
		
		this.produto = produtoExistente;
	}
	
	@Dado("que existe um produto cadastrado com peso {int} gramas")
	public void que_existe_um_produto_cadastrado_com_peso_gramas(Integer pesoGramas) {
		var produtoId = new ProdutoId(proximoId++);
		var codigo = new Codigo("PROD001");
		var peso = new Peso(pesoGramas);
		var fornecedores = new ArrayList<FornecedorId>();
		var produtoExistente = new Produto(produtoId, codigo, "Produto Teste", peso, Perecivel.NAO, Status.ATIVO, fornecedores);
		
		when(repositorio.obter(produtoId)).thenReturn(produtoExistente);
		when(repositorio.existePorCodigo(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Produto.class))).thenAnswer(invocation -> {
			Produto p = invocation.getArgument(0);
			return p;
		});
		
		this.produto = produtoExistente;
	}
	
	@Quando("eu crio um produto com código {string}, nome {string}, peso {int} gramas, perecível {string} e fornecedores")
	public void eu_crio_um_produto_com_codigo_nome_peso_gramas_perecivel_e_fornecedores(String codigo, String nome, Integer pesoGramas, String perecivel) {
		try {
			var produtoId = new ProdutoId(proximoId++);
			var codigoObj = new Codigo(codigo);
			var peso = new Peso(pesoGramas);
			var perecivelEnum = Perecivel.valueOf(perecivel);
			var fornecedores = new ArrayList<FornecedorId>();
			produto = new Produto(produtoId, codigoObj, nome, peso, perecivelEnum, Status.ATIVO, fornecedores);
			produtoRetornado = servico.salvar(produto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu crio um produto com código {string}, nome {string}, peso {int} gramas, perecível NAO e fornecedores")
	public void eu_crio_um_produto_com_codigo_nome_peso_gramas_perecivel_nao_e_fornecedores(String codigo, String nome, Integer pesoGramas) {
		try {
			var produtoId = new ProdutoId(proximoId++);
			var codigoObj = new Codigo(codigo);
			var peso = new Peso(pesoGramas);
			var fornecedores = new ArrayList<FornecedorId>();
			produto = new Produto(produtoId, codigoObj, nome, peso, Perecivel.NAO, Status.ATIVO, fornecedores);
			produtoRetornado = servico.salvar(produto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar um produto com código {string}, nome {string}, peso {int} gramas, perecível {string} e fornecedores")
	public void eu_tento_criar_um_produto_com_codigo_nome_peso_gramas_perecivel_e_fornecedores(String codigo, String nome, Integer pesoGramas, String perecivel) {
		try {
			var produtoId = new ProdutoId(proximoId++);
			var codigoObj = new Codigo(codigo);
			var peso = new Peso(pesoGramas);
			var perecivelEnum = Perecivel.valueOf(perecivel);
			var fornecedores = new ArrayList<FornecedorId>();
			produto = new Produto(produtoId, codigoObj, nome, peso, perecivelEnum, Status.ATIVO, fornecedores);
			produtoRetornado = servico.salvar(produto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar um produto com código {string}, nome {string}, peso {int} gramas, perecível NAO e fornecedores")
	public void eu_tento_criar_um_produto_com_codigo_nome_peso_gramas_perecivel_nao_e_fornecedores(String codigo, String nome, Integer pesoGramas) {
		try {
			var produtoId = new ProdutoId(proximoId++);
			var codigoObj = new Codigo(codigo);
			var peso = new Peso(pesoGramas);
			var fornecedores = new ArrayList<FornecedorId>();
			produto = new Produto(produtoId, codigoObj, nome, peso, Perecivel.NAO, Status.ATIVO, fornecedores);
			produtoRetornado = servico.salvar(produto);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu ativo o produto")
	public void eu_ativo_o_produto() {
		try {
			servico.ativar(produto.getId());
			produtoRetornado = repositorio.obter(produto.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento ativar o produto")
	public void eu_tento_ativar_o_produto() {
		try {
			servico.ativar(produto.getId());
			produtoRetornado = repositorio.obter(produto.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu inativo o produto")
	public void eu_inativo_o_produto() {
		try {
			servico.inativar(produto.getId());
			produtoRetornado = repositorio.obter(produto.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento inativar o produto")
	public void eu_tento_inativar_o_produto() {
		try {
			servico.inativar(produto.getId());
			produtoRetornado = repositorio.obter(produto.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu atualizo o nome do produto para {string}")
	public void eu_atualizo_o_nome_do_produto_para(String novoNome) {
		produto.setNome(novoNome);
		produtoRetornado = servico.salvar(produto);
	}
	
	@Quando("eu atualizo o peso do produto para {int} gramas")
	public void eu_atualizo_o_peso_do_produto_para_gramas(Integer novoPeso) {
		produto.setPeso(new Peso(novoPeso));
		produtoRetornado = servico.salvar(produto);
	}
	
	@Então("o produto deve ser criado com sucesso")
	public void o_produto_deve_ser_criado_com_sucesso() {
		assertThat(produtoRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Produto.class));
	}
	
	@Então("o produto deve ter o código {string}")
	public void o_produto_deve_ter_o_codigo(String codigo) {
		assertThat(produtoRetornado.getCodigo().getValor()).isEqualTo(codigo);
	}
	
	@Então("o produto deve ter o nome {string}")
	public void o_produto_deve_ter_o_nome(String nome) {
		assertThat(produtoRetornado.getNome()).isEqualTo(nome);
	}
	
	@Então("o produto deve ter peso de {int} gramas")
	public void o_produto_deve_ter_peso_de_gramas(Integer pesoEsperado) {
		assertThat(produtoRetornado.getPeso().getGramas()).isEqualTo(pesoEsperado);
	}
	
	@Então("deve ocorrer um erro informando que já existe um produto com este código")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_produto_com_este_codigo() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("código");
	}
	
	@Então("deve ocorrer um erro informando que o peso é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_peso_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("peso");
	}
	
	@Então("o produto deve ter status {string}")
	public void o_produto_deve_ter_status(String status) {
		assertThat(produtoRetornado.getStatus()).isEqualTo(Status.valueOf(status));
	}
	
	@Então("o produto deve ter status ATIVO")
	public void o_produto_deve_ter_status_ativo() {
		assertThat(produtoRetornado.getStatus()).isEqualTo(Status.ATIVO);
	}
	
	@Então("o produto deve ter status INATIVO")
	public void o_produto_deve_ter_status_inativo() {
		assertThat(produtoRetornado.getStatus()).isEqualTo(Status.INATIVO);
	}
	
	@Então("deve ocorrer um erro informando que o produto deve estar vinculado a pelo menos um estoque ativo")
	public void deve_ocorrer_um_erro_informando_que_o_produto_deve_estar_vinculado_a_pelo_menos_um_estoque_ativo() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("estoque ativo");
	}
	
	@Então("deve ocorrer um erro informando que não é possível inativar o produto pois ele possui saldo em estoque")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_inativar_o_produto_pois_ele_possui_saldo_em_estoque() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("saldo em estoque");
	}
	
	@Então("deve ocorrer um erro informando que não é possível inativar o produto pois existem pedidos em andamento")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_inativar_o_produto_pois_existem_pedidos_em_andamento() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("pedidos em andamento");
	}
}

