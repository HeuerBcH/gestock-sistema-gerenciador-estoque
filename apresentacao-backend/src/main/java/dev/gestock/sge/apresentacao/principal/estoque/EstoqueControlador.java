package dev.gestock.sge.apresentacao.principal.estoque;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueResumo;
import dev.gestock.sge.aplicacao.dominio.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueServico;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/estoques")
@CrossOrigin(origins = "*")
public class EstoqueControlador {

    @Autowired
    private EstoqueServico estoqueServico;

    @Autowired
    private EstoqueServicoAplicacao estoqueServicoAplicacao;

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os estoques (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<EstoqueResumo>> listar() {
        var estoques = estoqueServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(estoques);
    }

    // GET: Buscar estoque por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable("id") Long id) {
        var estoqueId = new EstoqueId(id);
        var estoque = estoqueRepositorio.buscarPorId(estoqueId);
        
        if (estoque.isPresent()) {
            return ResponseEntity.ok(estoque.get());
        }
        return ResponseEntity.notFound().build();
    }

    // GET: Pesquisar estoques por cliente
    @RequestMapping(method = GET, path = "/cliente/{clienteId}")
    public ResponseEntity<List<Estoque>> pesquisarPorCliente(@PathVariable("clienteId") Long clienteId) {
        try {
            var estoques = estoqueServico.pesquisarPorCliente(new ClienteId(clienteId));
            return ResponseEntity.ok(estoques);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(NOT_FOUND).build();
        }
    }

    // POST: Cadastrar novo estoque
    @RequestMapping(method = POST)
    public ResponseEntity<String> cadastrar(@RequestBody EstoqueFormulario.EstoqueCadastroDto dto) {
        try {
            var estoque = new Estoque(
                new EstoqueId(null), // ID será gerado pelo banco
                new ClienteId(dto.getClienteId()),
                dto.getNome(),
                dto.getEndereco(),
                dto.getCapacidade()
            );
            
            estoqueServico.cadastrar(estoque);
            return ResponseEntity.status(CREATED).body("Estoque cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: Atualizar capacidade do estoque
    @RequestMapping(method = PUT, path = "/{id}/capacidade")
    public ResponseEntity<String> atualizarCapacidade(
            @PathVariable("id") Long id,
            @RequestBody EstoqueFormulario.CapacidadeAtualizacaoDto dto) {
        try {
            var estoqueId = new EstoqueId(id);
            var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
            
            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var estoque = estoqueOpt.get();
            estoque.alterarCapacidade(dto.getNovaCapacidade());
            estoqueServico.atualizar(estoque);
            
            return ResponseEntity.ok("Capacidade atualizada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Registrar entrada de produtos
    @RequestMapping(method = POST, path = "/{id}/entrada")
    public ResponseEntity<String> registrarEntrada(
            @PathVariable("id") Long id,
            @RequestBody EstoqueFormulario.EntradaDto dto) {
        try {
            var estoqueId = new EstoqueId(id);
            var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
            
            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var estoque = estoqueOpt.get();
            estoque.registrarEntrada(
                new ProdutoId(dto.getProdutoId()),
                dto.getQuantidade(),
                dto.getResponsavel(),
                dto.getMotivo(),
                dto.getMetadados()
            );
            estoqueServico.atualizar(estoque);
            
            return ResponseEntity.ok("Entrada registrada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Registrar saída de produtos
    @RequestMapping(method = POST, path = "/{id}/saida")
    public ResponseEntity<String> registrarSaida(
            @PathVariable("id") Long id,
            @RequestBody EstoqueFormulario.SaidaDto dto) {
        try {
            var estoqueId = new EstoqueId(id);
            var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
            
            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var estoque = estoqueOpt.get();
            estoque.registrarSaida(
                new ProdutoId(dto.getProdutoId()),
                dto.getQuantidade(),
                dto.getResponsavel(),
                dto.getMotivo()
            );
            estoqueServico.atualizar(estoque);
            
            return ResponseEntity.ok("Saída registrada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Transferir produtos entre estoques
    @RequestMapping(method = POST, path = "/transferencia")
    public ResponseEntity<String> transferir(@RequestBody EstoqueFormulario.TransferenciaDto dto) {
        try {
            var origemOpt = estoqueRepositorio.buscarPorId(new EstoqueId(dto.getEstoqueOrigemId()));
            var destinoOpt = estoqueRepositorio.buscarPorId(new EstoqueId(dto.getEstoqueDestinoId()));
            
            if (origemOpt.isEmpty() || destinoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            estoqueServico.transferir(
                origemOpt.get(),
                destinoOpt.get(),
                new ProdutoId(dto.getProdutoId()),
                dto.getQuantidade(),
                dto.getResponsavel(),
                dto.getMotivo()
            );
            
            return ResponseEntity.ok("Transferência realizada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Definir ROP para produto no estoque
    @RequestMapping(method = POST, path = "/{id}/rop")
    public ResponseEntity<String> definirROP(
            @PathVariable("id") Long id,
            @RequestBody EstoqueFormulario.RopDefinicaoDto dto) {
        try {
            var estoqueId = new EstoqueId(id);
            var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
            
            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var estoque = estoqueOpt.get();
            estoque.definirROP(
                new ProdutoId(dto.getProdutoId()),
                dto.getConsumoMedio(),
                dto.getLeadTimeDias(),
                dto.getEstoqueSeguranca()
            );
            estoqueServico.atualizar(estoque);
            
            return ResponseEntity.ok("ROP definido com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE: Inativar estoque
    @RequestMapping(method = DELETE, path = "/{id}")
    public ResponseEntity<String> inativar(@PathVariable("id") Long id) {
        try {
            var estoqueId = new EstoqueId(id);
            var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
            
            if (estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var estoque = estoqueOpt.get();
            estoqueServico.inativar(estoque);
            
            return ResponseEntity.ok("Estoque inativado com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
