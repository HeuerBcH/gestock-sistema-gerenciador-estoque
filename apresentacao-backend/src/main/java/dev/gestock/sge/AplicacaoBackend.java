package dev.gestock.sge;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.gestock.dominio.analise.emprestimo.EmprestimoRegistroRepositorio;
import dev.gestock.sge.aplicacao.acervo.autor.AutorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.acervo.autor.AutorServicoAplicacao;
import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarRepositorioAplicacao;
import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
import dev.gestock.sge.aplicacao.acervo.livro.LivroRepositorioAplicacao;
import dev.gestock.sge.aplicacao.acervo.livro.LivroServicoAplicacao;
import dev.gestock.sge.aplicacao.analise.EmprestimoRegistroRepositorioAplicacao;
import dev.gestock.sge.aplicacao.analise.EmprestimoRegistroServicoAplicacao;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.livro.LivroRepositorio;
import dev.gestock.sge.dominio.principal.livro.LivroServico;
import dev.gestock.sge.dominio.evento.EventoBarramento;

@SpringBootApplication
public class AplicacaoBackend {
	@Bean
	public ClienteServico autorServico(ClienteRepositorio repositorio) {
		return new ClienteServico(repositorio);
	}

	@Bean
	public AutorServicoAplicacao autorServicoAplicacao(AutorRepositorioAplicacao repositorio) {
		return new AutorServicoAplicacao(repositorio);
	}

	@Bean
	public EstoqueServico exemplarServico(EstoqueRepositorio repositorio) {
		return new EstoqueServico(repositorio);
	}

	@Bean
	public ExemplarServicoAplicacao exemplarServicoAplicacao(ExemplarRepositorioAplicacao repositorio) {
		return new ExemplarServicoAplicacao(repositorio);
	}

	@Bean
	public EstoqueId emprestimoServico(EstoqueRepositorio exemplarRepositorio, EventoBarramento barramento) {
		return new EstoqueId(exemplarRepositorio, barramento);
	}

	@Bean
	public LivroServico livroServico(LivroRepositorio repositorio) {
		return new LivroServico(repositorio);
	}

	@Bean
	public LivroServicoAplicacao livroServicoAplicacao(LivroRepositorioAplicacao repositorio) {
		return new LivroServicoAplicacao(repositorio);
	}

	@Bean
	public EmprestimoRegistroServicoAplicacao emprestimoRegistroServicoAplicacao(
			EmprestimoRegistroRepositorio repositorio, EmprestimoRegistroRepositorioAplicacao repositorioAplicacao,
			EventoBarramento servico) {
		return new EmprestimoRegistroServicoAplicacao(repositorio, repositorioAplicacao, servico);
	}

	public static void main(String[] args) throws IOException {
		run(AplicacaoBackend.class, args);
	}
}