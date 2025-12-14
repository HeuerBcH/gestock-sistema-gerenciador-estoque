package dev.gestock.sge.dominio.estoque;

import dev.gestock.sge.dominio.fornecedor.Status;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class EstoqueSteps {
	
	@Mock
	private EstoqueRepositorio repositorio;
	
	private EstoqueServico servico;
	private Estoque estoque;
	private Estoque estoqueRetornado;
	private Exception excecao;
	private int proximoId = 1;
	
	public EstoqueSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new EstoqueServico(repositorio);
	}
	
	@Dado("que não existe nenhum estoque cadastrado")
	public void que_nao_existe_nenhum_estoque_cadastrado() {
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
	}
	
	@Dado("que existe um estoque cadastrado com endereço {string}")
	public void que_existe_um_estoque_cadastrado_com_endereco(String endereco) {
		var estoqueId = new EstoqueId(proximoId++);
		var enderecoObj = new Endereco(endereco);
		var capacidade = new Capacidade(10000);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", enderecoObj, capacidade, Status.ATIVO);
		
		// Quando tentar criar um novo estoque (id diferente), deve retornar true (já existe)
		when(repositorio.existePorEndereco(eq(endereco), anyInt())).thenAnswer(invocation -> {
			Integer id = invocation.getArgument(1);
			return !id.equals(estoqueId.getId()); // Retorna true se o id for diferente
		});
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
	}
	
	@Dado("que existe um estoque cadastrado com nome {string}")
	public void que_existe_um_estoque_cadastrado_com_nome(String nome) {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidade = new Capacidade(10000);
		var estoqueExistente = new Estoque(estoqueId, nome, endereco, capacidade, Status.ATIVO);
		
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		// Quando tentar criar um novo estoque (id diferente), deve retornar true (já existe)
		when(repositorio.existePorNome(eq(nome), anyInt())).thenAnswer(invocation -> {
			Integer id = invocation.getArgument(1);
			return !id.equals(estoqueId.getId()); // Retorna true se o id for diferente
		});
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
	}
	
	@Dado("que existe um estoque cadastrado com status {string}")
	public void que_existe_um_estoque_cadastrado_com_status(String status) {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidade = new Capacidade(10000);
		var statusEnum = Status.valueOf(status);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", endereco, capacidade, statusEnum);
		
		when(repositorio.obter(estoqueId)).thenReturn(estoqueExistente);
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
		
		this.estoque = estoqueExistente;
	}
	
	@Dado("que existe um estoque cadastrado com status INATIVO")
	public void que_existe_um_estoque_cadastrado_com_status_inativo() {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidade = new Capacidade(10000);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", endereco, capacidade, Status.INATIVO);
		
		when(repositorio.obter(estoqueId)).thenReturn(estoqueExistente);
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
		
		this.estoque = estoqueExistente;
	}
	
	@Dado("que existe um estoque cadastrado com status ATIVO")
	public void que_existe_um_estoque_cadastrado_com_status_ativo() {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidade = new Capacidade(10000);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", endereco, capacidade, Status.ATIVO);
		
		when(repositorio.obter(estoqueId)).thenReturn(estoqueExistente);
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
		
		this.estoque = estoqueExistente;
	}
	
	@Dado("que existe um estoque cadastrado")
	public void que_existe_um_estoque_cadastrado() {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidade = new Capacidade(10000);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", endereco, capacidade, Status.ATIVO);
		
		when(repositorio.obter(estoqueId)).thenReturn(estoqueExistente);
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
		
		this.estoque = estoqueExistente;
	}
	
	@Dado("que o estoque não possui produtos")
	public void que_o_estoque_nao_possui_produtos() {
		when(repositorio.possuiProdutos(estoque.getId().getId())).thenReturn(false);
	}
	
	@Dado("que o estoque possui produtos")
	public void que_o_estoque_possui_produtos() {
		when(repositorio.possuiProdutos(estoque.getId().getId())).thenReturn(true);
	}
	
	@Dado("que o estoque não possui pedidos em andamento")
	public void que_o_estoque_nao_possui_pedidos_em_andamento() {
		when(repositorio.possuiPedidosEmAndamento(estoque.getId().getId())).thenReturn(false);
	}
	
	@Dado("que o estoque possui pedidos em andamento")
	public void que_o_estoque_possui_pedidos_em_andamento() {
		when(repositorio.possuiPedidosEmAndamento(estoque.getId().getId())).thenReturn(true);
	}
	
	@Dado("que existe um estoque cadastrado com capacidade {int}")
	public void que_existe_um_estoque_cadastrado_com_capacidade(Integer capacidade) {
		var estoqueId = new EstoqueId(proximoId++);
		var endereco = new Endereco("Rua Teste, 123");
		var capacidadeObj = new Capacidade(capacidade);
		var estoqueExistente = new Estoque(estoqueId, "Estoque Teste", endereco, capacidadeObj, Status.ATIVO);
		
		when(repositorio.obter(estoqueId)).thenReturn(estoqueExistente);
		when(repositorio.existePorEndereco(anyString(), anyInt())).thenReturn(false);
		when(repositorio.existePorNome(anyString(), anyInt())).thenReturn(false);
		when(repositorio.salvar(any(Estoque.class))).thenAnswer(invocation -> {
			Estoque e = invocation.getArgument(0);
			return e;
		});
		
		this.estoque = estoqueExistente;
	}
	
	@Dado("que o estoque possui ocupação atual de {int} unidades")
	public void que_o_estoque_possui_ocupacao_atual_de_unidades(Integer ocupacao) {
		when(repositorio.obterOcupacaoAtual(estoque.getId().getId())).thenReturn(ocupacao);
	}
	
	@Quando("eu crio um estoque com nome {string}, endereço {string} e capacidade {int}")
	public void eu_crio_um_estoque_com_nome_endereco_e_capacidade(String nome, String endereco, Integer capacidade) {
		try {
			var estoqueId = new EstoqueId(proximoId++);
			var enderecoObj = new Endereco(endereco);
			var capacidadeObj = new Capacidade(capacidade);
			estoque = new Estoque(estoqueId, nome, enderecoObj, capacidadeObj, Status.ATIVO);
			estoqueRetornado = servico.salvar(estoque);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento criar um estoque com nome {string}, endereço {string} e capacidade {int}")
	public void eu_tento_criar_um_estoque_com_nome_endereco_e_capacidade(String nome, String endereco, Integer capacidade) {
		try {
			var estoqueId = new EstoqueId(proximoId++);
			var enderecoObj = new Endereco(endereco);
			var capacidadeObj = new Capacidade(capacidade);
			estoque = new Estoque(estoqueId, nome, enderecoObj, capacidadeObj, Status.ATIVO);
			estoqueRetornado = servico.salvar(estoque);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu ativo o estoque")
	public void eu_ativo_o_estoque() {
		try {
			servico.ativar(estoque.getId());
			estoqueRetornado = repositorio.obter(estoque.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu inativo o estoque")
	public void eu_inativo_o_estoque() {
		try {
			servico.inativar(estoque.getId());
			estoqueRetornado = repositorio.obter(estoque.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu removo o estoque")
	public void eu_removo_o_estoque() {
		try {
			servico.remover(estoque.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento remover o estoque")
	public void eu_tento_remover_o_estoque() {
		try {
			servico.remover(estoque.getId());
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu tento atualizar a capacidade do estoque para {int}")
	public void eu_tento_atualizar_a_capacidade_do_estoque_para(Integer novaCapacidade) {
		try {
			estoque.setCapacidade(new Capacidade(novaCapacidade));
			estoqueRetornado = servico.salvar(estoque);
			excecao = null;
		} catch (Exception e) {
			excecao = e;
		}
	}
	
	@Quando("eu atualizo a capacidade do estoque para {int}")
	public void eu_atualizo_a_capacidade_do_estoque_para(Integer novaCapacidade) {
		estoque.setCapacidade(new Capacidade(novaCapacidade));
		estoqueRetornado = servico.salvar(estoque);
	}
	
	@Então("o estoque deve ser criado com sucesso")
	public void o_estoque_deve_ser_criado_com_sucesso() {
		assertThat(estoqueRetornado).isNotNull();
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).salvar(any(Estoque.class));
	}
	
	@Então("o estoque deve ter o nome {string}")
	public void o_estoque_deve_ter_o_nome(String nome) {
		assertThat(estoqueRetornado.getNome()).isEqualTo(nome);
	}
	
	@Então("o estoque deve ter o endereço {string}")
	public void o_estoque_deve_ter_o_endereco(String endereco) {
		assertThat(estoqueRetornado.getEndereco().getValor()).isEqualTo(endereco);
	}
	
	@Então("o estoque deve ter capacidade de {int} unidades")
	public void o_estoque_deve_ter_capacidade_de_unidades(Integer capacidade) {
		assertThat(estoqueRetornado.getCapacidade().getValor()).isEqualTo(capacidade);
	}
	
	@Então("deve ocorrer um erro informando que já existe um estoque neste endereço")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_estoque_neste_endereco() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("endereço");
	}
	
	@Então("deve ocorrer um erro informando que já existe um estoque com este nome")
	public void deve_ocorrer_um_erro_informando_que_ja_existe_um_estoque_com_este_nome() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("nome");
	}
	
	@Então("deve ocorrer um erro informando que a capacidade é inválida")
	public void deve_ocorrer_um_erro_informando_que_a_capacidade_e_invalida() {
		assertThat(excecao).isNotNull();
		assertThat(excecao).isInstanceOf(IllegalArgumentException.class);
		assertThat(excecao.getMessage()).contains("capacidade");
	}
	
	@Então("o estoque deve ter status {string}")
	public void o_estoque_deve_ter_status(String status) {
		assertThat(estoqueRetornado.getStatus()).isEqualTo(Status.valueOf(status));
	}
	
	@Então("o estoque deve ter status ATIVO")
	public void o_estoque_deve_ter_status_ativo() {
		assertThat(estoqueRetornado.getStatus()).isEqualTo(Status.ATIVO);
	}
	
	@Então("o estoque deve ter status INATIVO")
	public void o_estoque_deve_ter_status_inativo() {
		assertThat(estoqueRetornado.getStatus()).isEqualTo(Status.INATIVO);
	}
	
	@Então("o estoque deve ser removido com sucesso")
	public void o_estoque_deve_ser_removido_com_sucesso() {
		assertThat(excecao).isNull();
		verify(repositorio, times(1)).remover(estoque.getId());
	}
	
	@Então("deve ocorrer um erro informando que não é possível remover o estoque pois ele ainda possui produtos")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_remover_o_estoque_pois_ele_ainda_possui_produtos() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("produtos");
	}
	
	@Então("deve ocorrer um erro informando que não é possível remover o estoque pois ele possui pedidos em andamento")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_remover_o_estoque_pois_ele_possui_pedidos_em_andamento() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("pedidos em andamento");
	}
	
	@Então("deve ocorrer um erro informando que não é possível diminuir a capacidade abaixo da ocupação atual")
	public void deve_ocorrer_um_erro_informando_que_nao_e_possivel_diminuir_a_capacidade_abaixo_da_ocupacao_atual() {
		assertThat(excecao).isNotNull();
		assertThat(excecao.getMessage()).contains("capacidade");
	}
}

