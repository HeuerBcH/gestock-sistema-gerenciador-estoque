package dev.gestock.sge.dominio.autenticacao.cliente;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClienteSteps {
	
	@Mock
	private ClienteRepositorio repositorio;
	
	private ClienteServico servico;
	private Cliente cliente;
	private Cliente clienteRetornado;
	private Exception excecao;
	private int proximoId = 1;
	
	public ClienteSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new ClienteServico(repositorio);
	}
	
	@Dado("que não existe nenhum cliente cadastrado")
	public void que_nao_existe_nenhum_cliente_cadastrado() {
		when(repositorio.obterPorEmail(any(Email.class))).thenReturn(null);
		when(repositorio.obterPorDocumento(any(CpfCnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Cliente.class))).thenAnswer(invocation -> {
			Cliente c = invocation.getArgument(0);
			return c;
		});
	}
	
	@Dado("que existe um cliente cadastrado com email {string} e senha {string}")
	public void que_existe_um_cliente_cadastrado_com_email_e_senha(String email, String senha) {
		var clienteId = new ClienteId(proximoId++);
		var emailObj = new Email(email);
		var documento = new CpfCnpj("12345678900");
		var senhaObj = new Senha(senha);
		var clienteExistente = new Cliente(clienteId, "Cliente Teste", emailObj, documento, senhaObj);
		
		when(repositorio.obterPorEmail(emailObj)).thenReturn(clienteExistente);
		when(repositorio.obterPorDocumento(any(CpfCnpj.class))).thenReturn(null);
		when(repositorio.salvar(any(Cliente.class))).thenAnswer(invocation -> {
			Cliente c = invocation.getArgument(0);
			return c;
		});
		this.clienteRetornado = clienteExistente;
	}
	
	@Dado("que existe um cliente cadastrado com email {string}")
	public void que_existe_um_cliente_cadastrado_com_email(String email) {
		var clienteId = new ClienteId(proximoId++);
		var emailObj = new Email(email);
		var documento = new CpfCnpj("12345678900");
		var senhaObj = new Senha("senha123");
		var clienteExistente = new Cliente(clienteId, "Cliente Teste", emailObj, documento, senhaObj);
		
		when(repositorio.obterPorEmail(emailObj)).thenReturn(clienteExistente);
		when(repositorio.salvar(any(Cliente.class))).thenAnswer(invocation -> {
			Cliente c = invocation.getArgument(0);
			return c;
		});
	}
	
	@Dado("que existe um cliente cadastrado com documento {string}")
	public void que_existe_um_cliente_cadastrado_com_documento(String documento) {
		var clienteId = new ClienteId(proximoId++);
		var emailObj = new Email("teste@example.com");
		var documentoObj = new CpfCnpj(documento);
		var senhaObj = new Senha("senha123");
		var clienteExistente = new Cliente(clienteId, "Cliente Teste", emailObj, documentoObj, senhaObj);
		
		when(repositorio.obterPorEmail(any(Email.class))).thenReturn(null);
		when(repositorio.obterPorDocumento(documentoObj)).thenReturn(clienteExistente);
		when(repositorio.salvar(any(Cliente.class))).thenAnswer(invocation -> {
			Cliente c = invocation.getArgument(0);
			return c;
		});
	}
	
	@Quando("eu registro um cliente com nome {string}, email {string}, documento {string} e senha {string}")
	public void eu_registro_um_cliente_com_nome_email_documento_e_senha(String nome, String email, String documento, String senha) {
		try {
			var clienteId = new ClienteId(proximoId++);
			var emailObj = new Email(email);
			var documentoObj = new CpfCnpj(documento);
			var senhaObj = new Senha(senha);
			cliente = new Cliente(clienteId, nome, emailObj, documentoObj, senhaObj);
			clienteRetornado = servico.registrarERetornar(cliente);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento registrar um cliente com nome {string}, email {string}, documento {string} e senha {string}")
	public void eu_tento_registrar_um_cliente_com_nome_email_documento_e_senha(String nome, String email, String documento, String senha) {
		try {
			var clienteId = new ClienteId(proximoId++);
			var emailObj = new Email(email);
			var documentoObj = new CpfCnpj(documento);
			var senhaObj = new Senha(senha);
			cliente = new Cliente(clienteId, nome, emailObj, documentoObj, senhaObj);
			clienteRetornado = servico.registrarERetornar(cliente);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu autentico com email {string} e senha {string}")
	public void eu_autentico_com_email_e_senha(String email, String senha) {
		try {
			var emailObj = new Email(email);
			var senhaObj = new Senha(senha);
			clienteRetornado = servico.autenticar(emailObj, senhaObj);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento autenticar com email {string} e senha {string}")
	public void eu_tento_autenticar_com_email_e_senha(String email, String senha) {
		try {
			var emailObj = new Email(email);
			var senhaObj = new Senha(senha);
			clienteRetornado = servico.autenticar(emailObj, senhaObj);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu altero a senha do cliente para {string}")
	public void eu_altero_a_senha_do_cliente_para(String novaSenha) {
		if (clienteRetornado == null) {
			// Se não foi retornado pela autenticação, usa o cliente do mock
			var emailObj = new Email("joao@example.com");
			clienteRetornado = repositorio.obterPorEmail(emailObj);
		}
		var novaSenhaObj = new Senha(novaSenha);
		clienteRetornado.alterarSenha(novaSenhaObj);
	}
	
	@Então("o cliente deve ser registrado com sucesso")
	public void o_cliente_deve_ser_registrado_com_sucesso() {
		assertThat(clienteRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Cliente.class));
	}
	
	@Então("o cliente deve ter o nome {string}")
	public void o_cliente_deve_ter_o_nome(String nome) {
		assertThat(clienteRetornado.getNome()).isEqualTo(nome);
	}
	
	@Então("o cliente deve ter o email {string}")
	public void o_cliente_deve_ter_o_email(String email) {
		assertThat(clienteRetornado.getEmail().getEndereco()).isEqualTo(email);
	}
	
	@Então("deve ocorrer um erro informando que o email do cliente é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_email_do_cliente_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("e-mail");
	}
	
	@Então("deve ocorrer um erro informando que o documento é inválido")
	public void deve_ocorrer_um_erro_informando_que_o_documento_e_invalido() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("Documento");
	}
	
	@Então("deve ocorrer um erro informando que já existe um cliente com este email")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_cliente_com_este_email() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("e-mail");
	}
	
	@Então("deve ocorrer um erro informando que já existe um cliente com este documento")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_cliente_com_este_documento() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("documento");
	}
	
	@Então("a autenticação deve ser bem-sucedida")
	public void a_autenticacao_deve_ser_bem_sucedida() {
		assertThat(clienteRetornado).isNotNull();
		assertThat(excecao).isNull();
	}
	
	@Então("o cliente retornado deve ter o email {string}")
	public void o_cliente_retornado_deve_ter_o_email(String email) {
		assertThat(clienteRetornado.getEmail().getEndereco()).isEqualTo(email);
	}
	
	@Então("deve ocorrer um erro informando que o email ou senha são inválidos")
	public void deve_ocorrer_um_erro_informando_que_o_email_ou_senha_sao_invalidos() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("inválidos");
	}
	
	@Então("o documento do cliente deve ser identificado como CNPJ")
	public void o_documento_do_cliente_deve_ser_identificado_como_cnpj() {
		assertThat(clienteRetornado.getDocumento().isCnpj()).isTrue();
		assertThat(clienteRetornado.getDocumento().isCpf()).isFalse();
	}
	
	@Então("o cliente deve aceitar a nova senha {string}")
	public void o_cliente_deve_aceitar_a_nova_senha(String senha) {
		assertThat(clienteRetornado.validarSenha(senha)).isTrue();
	}
	
	@Então("o cliente não deve mais aceitar a senha antiga {string}")
	public void o_cliente_nao_deve_mais_aceitar_a_senha_antiga(String senhaAntiga) {
		assertThat(clienteRetornado.validarSenha(senhaAntiga)).isFalse();
	}
}

