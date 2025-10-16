package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque.ExemplarDevolvidoEvento;
import dev.gestock.sge.dominio.principal.livro.Isbn;
import dev.gestock.sge.dominio.principal.livro.Livro;
import dev.gestock.sge.dominio.administracao.socio.SocioId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DevolverFuncionalidade extends AcervoFuncionalidade {
	private ClienteId autorId = new ClienteId(1);
	private Isbn livroId = isbnFabrica.construir("9788582606162");
	private ExemplarId exemplarId = new ExemplarId(1);
	private SocioId socioId = new SocioId(1);

	private RuntimeException excecao;

	@Given("um exemplar {string} emprestado")
	public void um_exemplar_p1_emprestado(String disponibilidade) {
		var autor = new Cliente(autorId, "Andrew Tanenbaum");
		autorServico.salvar(autor);

		var livro = new Livro(livroId, "Sistemas operacionais modernos", null, Arrays.asList(new ClienteId(1)));
		livroServico.salvar(livro);

		var exemplar = new Estoque(exemplarId, livroId, null);
		exemplarServico.salvar(exemplar);

		if ("foi".startsWith(disponibilidade)) {
			emprestimoServico.realizarEmprestimo(exemplarId, socioId);
			eventos.clear();
		}
	}

	@When("o exemplar for devolvido")
	public void o_exemplar_for_devolvido() {
		try {
			emprestimoServico.devolver(exemplarId);
		} catch (IllegalArgumentException | IllegalStateException excecao) {
			this.excecao = excecao;
		}
	}

	@Then("o empréstimo é concluído com sucesso")
	public void o_empréstimo_é_concluído_com_sucesso() {
		var exemplar = exemplarServico.obter(exemplarId);
		assertTrue(exemplar.disponivel());
	}

	@Then("o sistema notifica a devolução do exemplar")
	public void o_sistema_notifica_a_devolução_do_exemplar() {
		assertEquals(1, eventos.size());

		var evento = eventos.get(0);
		assertTrue(evento instanceof ExemplarDevolvidoEvento);

		var exemplarDevolvido = (ExemplarDevolvidoEvento) evento;
		var exemplar = exemplarDevolvido.getExemplar();
		var exemplarId = exemplar.getId();
		assertTrue(this.exemplarId.equals(exemplarId));
	}

	@Then("o sistema informa que o exemplar não está emprestado")
	public void o_sistema_informa_que_o_exemplar_não_está_emprestado() {
		assertNotNull(excecao);
	}
}