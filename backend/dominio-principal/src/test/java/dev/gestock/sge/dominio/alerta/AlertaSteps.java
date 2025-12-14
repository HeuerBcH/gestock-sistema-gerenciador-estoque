package dev.gestock.sge.dominio.alerta;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;

public class AlertaSteps {
	
	private AlertaServico servico;
	private PercentualAbaixoRop percentual;
	private NivelAlerta nivel;
	
	public AlertaSteps() {
		MockitoAnnotations.openMocks(this);
		this.servico = new AlertaServico();
	}
	
	@Dado("que o percentual abaixo do ROP é {double}")
	public void que_o_percentual_abaixo_do_rop_e(Double valor) {
		percentual = new PercentualAbaixoRop(valor);
	}
	
	@Quando("eu determino o nível do alerta")
	public void eu_determino_o_nivel_do_alerta() {
		assertThat(percentual).isNotNull(); // Garante que o percentual foi inicializado
		nivel = servico.determinarNivel(percentual);
	}
	
	@Então("o nível do alerta deve ser {string}")
	public void o_nivel_do_alerta_deve_ser(String nivelEsperado) {
		assertThat(nivel).isNotNull();
		assertThat(nivel).isEqualTo(NivelAlerta.valueOf(nivelEsperado));
	}
	
	@Então("o nível do alerta deve ser CRITICO")
	public void o_nivel_do_alerta_deve_ser_critico() {
		assertThat(nivel).isNotNull();
		assertThat(nivel).isEqualTo(NivelAlerta.CRITICO);
	}
	
	@Então("o nível do alerta deve ser ALTO")
	public void o_nivel_do_alerta_deve_ser_alto() {
		assertThat(nivel).isNotNull();
		assertThat(nivel).isEqualTo(NivelAlerta.ALTO);
	}
	
	@Então("o nível do alerta deve ser MEDIO")
	public void o_nivel_do_alerta_deve_ser_medio() {
		assertThat(nivel).isNotNull();
		assertThat(nivel).isEqualTo(NivelAlerta.MEDIO);
	}
	
	@Então("não deve ser gerado nenhum alerta")
	public void nao_deve_ser_gerado_nenhum_alerta() {
		assertThat(nivel).isNull();
	}
}

