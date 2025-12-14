package dev.gestock.sge.dominio.fornecedor;

import dev.gestock.sge.dominio.autenticacao.cliente.Email;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FornecedorSteps {
	
	@Mock
	private FornecedorRepositorio repositorio;
	
	private FornecedorServico servico;
	private Fornecedor fornecedor;
	private Fornecedor fornecedorRetornado;
	private Exception excecao;
	private int proximoId = 1;
	
	public FornecedorSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new FornecedorServico(repositorio);
	}
	
	@Dado("que não existe nenhum fornecedor cadastrado")
	public void que_nao_existe_nenhum_fornecedor_cadastrado() {
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.obter(any(FornecedorId.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
	}
	
	@Dado("que existe um fornecedor cadastrado com CNPJ {string}")
	public void que_existe_um_fornecedor_cadastrado_com_cnpj(String cnpj) {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpjObj = new Cnpj(cnpj);
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(7);
		var custo = new Custo(10.50);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpjObj, email, leadTime, custo, Status.ATIVO);
		
		when(repositorio.obterPorCnpj(cnpjObj)).thenReturn(fornecedorExistente);
		when(repositorio.obter(any(FornecedorId.class))).thenReturn(fornecedorExistente);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
	}
	
	@Dado("que existe um fornecedor cadastrado com status {string}")
	public void que_existe_um_fornecedor_cadastrado_com_status(String status) {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpj = new Cnpj("12345678000190");
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(7);
		var custo = new Custo(10.50);
		var statusEnum = Status.valueOf(status);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpj, email, leadTime, custo, statusEnum);
		
		when(repositorio.obter(fornecedorId)).thenReturn(fornecedorExistente);
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
		
		this.fornecedor = fornecedorExistente;
	}
	
	@Dado("que existe um fornecedor cadastrado com status INATIVO")
	public void que_existe_um_fornecedor_cadastrado_com_status_inativo() {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpj = new Cnpj("12345678000190");
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(7);
		var custo = new Custo(10.50);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpj, email, leadTime, custo, Status.INATIVO);
		
		when(repositorio.obter(fornecedorId)).thenReturn(fornecedorExistente);
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
		
		this.fornecedor = fornecedorExistente;
	}
	
	@Dado("que existe um fornecedor cadastrado com status ATIVO")
	public void que_existe_um_fornecedor_cadastrado_com_status_ativo() {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpj = new Cnpj("12345678000190");
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(7);
		var custo = new Custo(10.50);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpj, email, leadTime, custo, Status.ATIVO);
		
		when(repositorio.obter(fornecedorId)).thenReturn(fornecedorExistente);
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
		
		this.fornecedor = fornecedorExistente;
	}
	
	@Dado("que existe um fornecedor cadastrado com lead time {int} dias")
	public void que_existe_um_fornecedor_cadastrado_com_lead_time_dias(Integer leadTimeDias) {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpj = new Cnpj("12345678000190");
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(leadTimeDias);
		var custo = new Custo(10.50);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpj, email, leadTime, custo, Status.ATIVO);
		
		when(repositorio.obter(fornecedorId)).thenReturn(fornecedorExistente);
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
		
		this.fornecedor = fornecedorExistente;
	}
	
	@Dado("que existe um fornecedor cadastrado com custo {double}")
	public void que_existe_um_fornecedor_cadastrado_com_custo(Double custoValor) {
		var fornecedorId = new FornecedorId(proximoId++);
		var cnpj = new Cnpj("12345678000190");
		var email = new Email("contato@teste.com");
		var leadTime = new LeadTime(7);
		var custo = new Custo(custoValor);
		var fornecedorExistente = new Fornecedor(fornecedorId, "Fornecedor Teste", cnpj, email, leadTime, custo, Status.ATIVO);
		
		when(repositorio.obter(fornecedorId)).thenReturn(fornecedorExistente);
		when(repositorio.obterPorCnpj(any(Cnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Fornecedor.class))).thenAnswer(invocation -> {
			Fornecedor f = invocation.getArgument(0);
			return f;
		});
		
		this.fornecedor = fornecedorExistente;
	}
	
	@Dado("que o fornecedor não possui pedidos pendentes")
	public void que_o_fornecedor_nao_possui_pedidos_pendentes() {
		when(repositorio.possuiPedidosPendentes(anyInt())).thenReturn(false);
	}
	
	@Dado("que o fornecedor possui pedidos pendentes")
	public void que_o_fornecedor_possui_pedidos_pendentes() {
		when(repositorio.possuiPedidosPendentes(anyInt())).thenReturn(true);
	}
	
	@Quando("eu crio um fornecedor com nome {string}, CNPJ {string}, email {string}, lead time {int} dias e custo {double}")
	public void eu_crio_um_fornecedor_com_nome_cnpj_email_lead_time_dias_e_custo(String nome, String cnpj, String email, Integer leadTimeDias, Double custoValor) {
		try {
			var fornecedorId = new FornecedorId(proximoId++);
			var cnpjObj = new Cnpj(cnpj);
			var emailObj = new Email(email);
			var leadTime = new LeadTime(leadTimeDias);
			var custo = new Custo(custoValor);
			fornecedor = new Fornecedor(fornecedorId, nome, cnpjObj, emailObj, leadTime, custo, Status.ATIVO);
			fornecedorRetornado = servico.salvar(fornecedor);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar um fornecedor com nome {string}, CNPJ {string}, email {string}, lead time {int} dias e custo {double}")
	public void eu_tento_criar_um_fornecedor_com_nome_cnpj_email_lead_time_dias_e_custo(String nome, String cnpj, String email, Integer leadTimeDias, Double custoValor) {
		try {
			var fornecedorId = new FornecedorId(proximoId++);
			var cnpjObj = new Cnpj(cnpj);
			var emailObj = new Email(email);
			var leadTime = new LeadTime(leadTimeDias);
			var custo = new Custo(custoValor);
			fornecedor = new Fornecedor(fornecedorId, nome, cnpjObj, emailObj, leadTime, custo, Status.ATIVO);
			fornecedorRetornado = servico.salvar(fornecedor);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu ativo o fornecedor")
	public void eu_ativo_o_fornecedor() {
		try {
			servico.ativar(fornecedor.getId());
			fornecedorRetornado = repositorio.obter(fornecedor.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu inativo o fornecedor")
	public void eu_inativo_o_fornecedor() {
		try {
			servico.inativar(fornecedor.getId());
			fornecedorRetornado = repositorio.obter(fornecedor.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento inativar o fornecedor")
	public void eu_tento_inativar_o_fornecedor() {
		try {
			servico.inativar(fornecedor.getId());
			fornecedorRetornado = repositorio.obter(fornecedor.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu atualizo o lead time do fornecedor para {int} dias")
	public void eu_atualizo_o_lead_time_do_fornecedor_para_dias(Integer novoLeadTime) {
		fornecedor.setLeadTime(new LeadTime(novoLeadTime));
		fornecedorRetornado = servico.salvar(fornecedor);
	}
	
	@Quando("eu atualizo o custo do fornecedor para {double}")
	public void eu_atualizo_o_custo_do_fornecedor_para(Double novoCusto) {
		fornecedor.setCusto(new Custo(novoCusto));
		fornecedorRetornado = servico.salvar(fornecedor);
	}
	
	@Então("o fornecedor deve ser criado com sucesso")
	public void o_fornecedor_deve_ser_criado_com_sucesso() {
		assertThat(fornecedorRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Fornecedor.class));
	}
	
	@Então("o fornecedor deve ter o nome {string}")
	public void o_fornecedor_deve_ter_o_nome(String nome) {
		assertThat(fornecedorRetornado.getNome()).isEqualTo(nome);
	}
	
	@Então("o fornecedor deve ter o CNPJ {string}")
	public void o_fornecedor_deve_ter_o_cnpj(String cnpj) {
		assertThat(fornecedorRetornado.getCnpj().getNumero()).isEqualTo(cnpj.replaceAll("[^0-9]", ""));
	}
	
	@Então("o fornecedor deve ter status {string}")
	public void o_fornecedor_deve_ter_status(String status) {
		assertThat(fornecedorRetornado.getStatus()).isEqualTo(Status.valueOf(status));
	}
	
	@Então("o fornecedor deve ter status ATIVO")
	public void o_fornecedor_deve_ter_status_ativo() {
		assertThat(fornecedorRetornado.getStatus()).isEqualTo(Status.ATIVO);
	}
	
	@Então("o fornecedor deve ter status INATIVO")
	public void o_fornecedor_deve_ter_status_inativo() {
		assertThat(fornecedorRetornado.getStatus()).isEqualTo(Status.INATIVO);
	}
	
	@Então("deve ocorrer um erro informando que o CNPJ é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_cnpj_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("CNPJ");
	}
	
	@Então("deve ocorrer um erro informando que o email do fornecedor é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_email_do_fornecedor_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("e-mail");
	}
	
	@Então("deve ocorrer um erro informando que já existe um fornecedor com este CNPJ")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_fornecedor_com_este_cnpj() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("CNPJ");
	}
	
	@Então("deve ocorrer um erro informando que o lead time é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_lead_time_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("lead time");
	}
	
	@Então("deve ocorrer um erro informando que o custo é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_custo_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("custo");
	}
	
	@Então("deve ocorrer um erro informando que não é possível inativar o fornecedor pois existem pedidos pendentes")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_inativar_o_fornecedor_pois_existem_pedidos_pendentes() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("pedidos pendentes");
	}
	
	@Então("o fornecedor deve ter lead time de {int} dias")
	public void o_fornecedor_deve_ter_lead_time_de_dias(Integer leadTimeEsperado) {
		assertThat(fornecedorRetornado.getLeadTime().getDias()).isEqualTo(leadTimeEsperado);
	}
	
	@Então("o fornecedor deve ter custo {double}")
	public void o_fornecedor_deve_ter_custo(Double custoEsperado) {
		assertThat(fornecedorRetornado.getCusto().getValor().doubleValue()).isEqualTo(custoEsperado);
	}
}

