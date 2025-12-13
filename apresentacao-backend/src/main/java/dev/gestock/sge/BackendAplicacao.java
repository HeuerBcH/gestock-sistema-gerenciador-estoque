package dev.gestock.sge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.gestock.sge.aplicacao.auth.AutenticacaoServico;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;

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
	
	public static void main(String[] args) {
		SpringApplication.run(BackendAplicacao.class, args);
	}
}
