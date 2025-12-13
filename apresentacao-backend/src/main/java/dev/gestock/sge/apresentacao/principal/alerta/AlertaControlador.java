package dev.gestock.sge.apresentacao.principal.alerta;

import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.alerta.AlertaResumo;
import dev.gestock.sge.aplicacao.dominio.alerta.AlertaServicoAplicacao;
import dev.gestock.sge.dominio.principal.alerta.Alerta;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.alerta.AlertaRepositorio;
import dev.gestock.sge.dominio.principal.alerta.AlertaServico;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaControlador {

    @Autowired
    private AlertaServico alertaServico;

    @Autowired
    private AlertaServicoAplicacao alertaServicoAplicacao;

    @Autowired
    private AlertaRepositorio alertaRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os alertas (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<AlertaResumo>> listar() {
        var alertas = alertaServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(alertas);
    }

    // GET: Buscar alerta por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Alerta> buscarPorId(@PathVariable("id") Long id) {
        var alertaId = new AlertaId(id);
        var alerta = alertaRepositorio.obter(alertaId);
        
        if (alerta.isPresent()) {
            return ResponseEntity.ok(alerta.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET: Listar alertas por estoque
    @RequestMapping(method = GET, path = "/estoque/{estoqueId}")
    public ResponseEntity<List<Alerta>> listarPorEstoque(@PathVariable("estoqueId") Long estoqueId) {
        var alertas = alertaRepositorio.listarPorEstoque(new EstoqueId(estoqueId));
        return ResponseEntity.ok(alertas);
    }

    // GET: Listar alertas por produto
    @RequestMapping(method = GET, path = "/produto/{produtoId}")
    public ResponseEntity<List<Alerta>> listarPorProduto(@PathVariable("produtoId") Long produtoId) {
        var alertas = alertaRepositorio.listarPorProduto(new ProdutoId(produtoId));
        return ResponseEntity.ok(alertas);
    }

    // GET: Listar apenas alertas ativos
    @RequestMapping(method = GET, path = "/ativos")
    public ResponseEntity<List<AlertaResumo>> listarAtivos() {
        var alertas = alertaServicoAplicacao.pesquisarResumos();
        // Aqui você poderia filtrar apenas os ativos se o repositório tiver esse método
        return ResponseEntity.ok(alertas);
    }

    // POST: Desativar alerta manualmente
    @RequestMapping(method = POST, path = "/{id}/desativar")
    public ResponseEntity<String> desativar(@PathVariable("id") Long id) {
        try {
            var alertaId = new AlertaId(id);
            var alertaOpt = alertaRepositorio.obter(alertaId);
            
            if (alertaOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var alerta = alertaOpt.get();
            alertaServico.desativarAlerta(alerta);
            
            return ResponseEntity.ok("Alerta desativado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
