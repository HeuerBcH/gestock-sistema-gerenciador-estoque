package dev.gestock.sge.dominio.principal;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.ArrayList;
import java.util.List;

import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.livro.IsbnFabrica;
import dev.gestock.sge.dominio.principal.livro.LivroServico;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.dominio.evento.EventoObservador;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

public class AcervoFuncionalidade implements EventoBarramento {
	protected IsbnFabrica isbnFabrica;
	protected ClienteServico autorServico;
	protected LivroServico livroServico;
	protected EstoqueServico exemplarServico;
	protected EstoqueId emprestimoServico;
	protected List<Object> eventos;

	public AcervoFuncionalidade() {
		isbnFabrica = new IsbnFabrica();

		var repositorio = new Repositorio();

		autorServico = new ClienteServico(repositorio);
		livroServico = new LivroServico(repositorio);
		exemplarServico = new EstoqueServico(repositorio);
		emprestimoServico = new EstoqueId(repositorio, this);

		eventos = new ArrayList<>();
	}

	@Override
	public <E> void adicionar(EventoObservador<E> observador) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void postar(Object evento) {
		notNull(evento, "O evento não pode ser nulo");

		eventos.add(evento);
	}
}