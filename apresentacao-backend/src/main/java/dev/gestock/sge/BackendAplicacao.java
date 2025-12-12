package dev.gestock.sge;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import dev.gestock.sge.aplicacao.dominio.alerta.AlertaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.alerta.AlertaServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.cliente.ClienteRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.cliente.ClienteServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.pedido.PedidoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.pedido.PedidoServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.dominio.principal.alerta.AlertaRepositorio;
import dev.gestock.sge.dominio.principal.alerta.AlertaServico;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.dominio.principal.estoque.AtualizacaoEstoquePadrao;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.fornecedor.SelecaoCotacaoMenorPreco;
import dev.gestock.sge.dominio.principal.fornecedor.SelecaoCotacaoStrategy;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.pedido.PedidoServico;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoServico;

@SpringBootApplication
@ComponentScan(basePackages = {
	"dev.gestock.sge",
	"dev.gestock.sge.infraestrutura",
	"dev.gestock.sge.aplicacao"
})
@EntityScan(basePackages = "dev.gestock.sge.infraestrutura.persistencia.jpa")
public class BackendAplicacao {

	@Bean
	public ClienteServico clienteServico(ClienteRepositorio repositorio) {
		return new ClienteServico(repositorio);
	}

	@Bean
	public ClienteServicoAplicacao clienteServicoAplicacao(ClienteRepositorioAplicacao repositorio) {
		return new ClienteServicoAplicacao(repositorio);
	}

	@Bean
	public EstoqueServico estoqueServico(EstoqueRepositorio estoqueRepo, 
	                                     PedidoRepositorio pedidoRepo,
	                                     AlertaRepositorio alertaRepo) {
		// Configuração do Template Method com Observer
		var template = new AtualizacaoEstoquePadrao(estoqueRepo, pedidoRepo);
		var alertaServico = new AlertaServico(alertaRepo, estoqueRepo);
		template.registrarObserver(alertaServico); // R1H17: Observer para remover alertas
		return new EstoqueServico(estoqueRepo, pedidoRepo, template);
	}

	@Bean
	public EstoqueServicoAplicacao estoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		return new EstoqueServicoAplicacao(repositorio);
	}

	@Bean
	public ProdutoServico produtoServico(ProdutoRepositorio produtoRepo,
	                                     EstoqueRepositorio estoqueRepo,
	                                     PedidoRepositorio pedidoRepo) {
		return new ProdutoServico(produtoRepo, estoqueRepo, pedidoRepo);
	}

	@Bean
	public ProdutoServicoAplicacao produtoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		return new ProdutoServicoAplicacao(repositorio);
	}

	@Bean
	public SelecaoCotacaoStrategy selecaoCotacaoStrategy() {
		return new SelecaoCotacaoMenorPreco();
	}

	@Bean
	public FornecedorServico fornecedorServico(FornecedorRepositorio fornecedorRepo,
	                                           PedidoRepositorio pedidoRepo,
	                                           SelecaoCotacaoStrategy strategy) {
		return new FornecedorServico(fornecedorRepo, pedidoRepo, strategy);
	}

	@Bean
	public FornecedorServicoAplicacao fornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		return new FornecedorServicoAplicacao(repositorio);
	}

	@Bean
	public PedidoServico pedidoServico(PedidoRepositorio pedidoRepo,
	                                   EstoqueRepositorio estoqueRepo,
	                                   AlertaRepositorio alertaRepo) {
		return new PedidoServico(pedidoRepo, estoqueRepo, alertaRepo);
	}

	@Bean
	public PedidoServicoAplicacao pedidoServicoAplicacao(PedidoRepositorioAplicacao repositorio) {
		return new PedidoServicoAplicacao(repositorio);
	}

	@Bean
	public AlertaServico alertaServico(AlertaRepositorio alertaRepo,
	                                   EstoqueRepositorio estoqueRepo) {
		return new AlertaServico(alertaRepo, estoqueRepo);
	}

	@Bean
	public AlertaServicoAplicacao alertaServicoAplicacao(AlertaRepositorioAplicacao repositorio) {
		return new AlertaServicoAplicacao(repositorio);
	}

	public static void main(String[] args) {
		run(BackendAplicacao.class, args);
	}
}
