package dev.gestock.sge.apresentacao.reserva;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.reserva.ReservaResumo;
import dev.gestock.sge.aplicacao.reserva.ReservaServicoAplicacao;
import dev.gestock.sge.aplicacao.reserva.ReservaTotais;

@RestController
@RequestMapping("backend/reserva")
class ReservaControlador {

	private @Autowired ReservaServicoAplicacao reservaServicoAplicacao;

	@RequestMapping(method = GET, path = "pesquisa")
	List<ReservaResumo> pesquisar(@RequestParam(required = false, defaultValue = "") String busca) {
		return reservaServicoAplicacao.pesquisarResumos(busca);
	}

	@RequestMapping(method = GET, path = "totais")
	ReservaTotais obterTotais() {
		return reservaServicoAplicacao.obterTotais();
	}
}

