package dev.gestock.sge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;

@SpringBootApplication
public class BackendAplicacao {
	
	@Bean
	public EstoqueServicoAplicacao estoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		return new EstoqueServicoAplicacao(repositorio);
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BackendAplicacao.class, args);
	}
}
