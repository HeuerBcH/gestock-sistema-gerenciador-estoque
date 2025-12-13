package dev.gestock.sge;

import org.springframework.boot.SpringApplication;

public class BackendDesenvolvimentoAplicacao {
	public static void main(String[] args) {
		var aplicacao = new SpringApplication(BackendAplicacao.class);
		aplicacao.setAdditionalProfiles("desenvolvimento");
		aplicacao.run(args);
	}
}
