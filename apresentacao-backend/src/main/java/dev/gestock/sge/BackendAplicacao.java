package dev.gestock.sge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.gestock.sge.aplicacao.auth.AutenticacaoServico;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoServico;

@SpringBootApplication(scanBasePackages = "dev.gestock.sge")
public class BackendAplicacao {
	
	@Bean
	public EstoqueServicoAplicacao estoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		return new EstoqueServicoAplicacao(repositorio);
	}
	
	@Bean
	public EstoqueServico estoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
		return new EstoqueServico(estoqueRepositorio, pedidoRepositorio);
	}
	
	@Bean
	public AutenticacaoServico autenticacaoServico(ClienteRepositorio clienteRepositorio) {
		return new AutenticacaoServico(clienteRepositorio);
	}
	
	@Bean
	public ClienteServico clienteServico(ClienteRepositorio clienteRepositorio) {
		return new ClienteServico(clienteRepositorio);
	}
	
	@Bean
	public FornecedorServicoAplicacao fornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		return new FornecedorServicoAplicacao(repositorio);
	}
	
	@Bean
	public FornecedorServico fornecedorServico(FornecedorRepositorio fornecedorRepositorio, PedidoRepositorio pedidoRepositorio) {
		return new FornecedorServico(fornecedorRepositorio, pedidoRepositorio);
	}
	
	@Bean
	public ProdutoServicoAplicacao produtoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		return new ProdutoServicoAplicacao(repositorio);
	}
	
	@Bean
	public ProdutoServico produtoServico(ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
		return new ProdutoServico(produtoRepositorio, estoqueRepositorio, pedidoRepositorio);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BackendAplicacao.class, args);
	}
}
