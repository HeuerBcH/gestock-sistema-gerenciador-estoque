package dev.gestock.sge.apresentacao.principal.fornecedor;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorResumo;
import dev.gestock.sge.aplicacao.dominio.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorServico;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/fornecedores")
@CrossOrigin(origins = "*")
public class FornecedorControlador {

    @Autowired
    private FornecedorServico fornecedorServico;

    @Autowired
    private FornecedorServicoAplicacao fornecedorServicoAplicacao;

    @Autowired
    private FornecedorRepositorio fornecedorRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os fornecedores (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<FornecedorResumo>> listar() {
        var fornecedores = fornecedorServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(fornecedores);
    }

    // GET: Buscar fornecedor por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Fornecedor> buscarPorId(@PathVariable("id") Long id) {
        var fornecedorId = new FornecedorId(id);
        var fornecedor = fornecedorRepositorio.buscarPorId(fornecedorId);
        
        if (fornecedor.isPresent()) {
            return ResponseEntity.ok(fornecedor.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST: Cadastrar novo fornecedor
    @RequestMapping(method = POST)
    public ResponseEntity<String> cadastrar(@RequestBody FornecedorForm.FornecedorCadastroDto dto) {
        try {
            var fornecedor = new Fornecedor(
                new FornecedorId(null), // ID será gerado pelo banco
                dto.getNome(),
                dto.getCnpj(),
                dto.getContato(),
                new LeadTime(dto.getLeadTimeDias())
            );
            
            fornecedorServico.cadastrar(fornecedor);
            return ResponseEntity.status(CREATED).body("Fornecedor cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: Atualizar fornecedor existente
    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<String> atualizar(
            @PathVariable("id") Long id,
            @RequestBody FornecedorForm.FornecedorAtualizacaoDto dto) {
        try {
            var fornecedorId = new FornecedorId(id);
            var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
            
            if (fornecedorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var fornecedor = fornecedorOpt.get();
            fornecedor.atualizarDados(dto.getNome(), dto.getContato());
            fornecedorServico.atualizar(fornecedor);
            
            return ResponseEntity.ok("Fornecedor atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Registrar cotação para produto
    @RequestMapping(method = POST, path = "/{id}/cotacao")
    public ResponseEntity<String> registrarCotacao(
            @PathVariable("id") Long id,
            @RequestBody FornecedorForm.CotacaoRegistroDto dto) {
        try {
            var fornecedorId = new FornecedorId(id);
            var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
            
            if (fornecedorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var fornecedor = fornecedorOpt.get();
            fornecedor.registrarCotacao(
                new ProdutoId(dto.getProdutoId()),
                dto.getPreco(),
                dto.getPrazoDias()
            );
            fornecedorServico.salvar(fornecedor);
            
            return ResponseEntity.ok("Cotação registrada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE: Inativar fornecedor
    @RequestMapping(method = DELETE, path = "/{id}")
    public ResponseEntity<String> inativar(@PathVariable("id") Long id) {
        try {
            var fornecedorId = new FornecedorId(id);
            var fornecedorOpt = fornecedorRepositorio.buscarPorId(fornecedorId);
            
            if (fornecedorOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var fornecedor = fornecedorOpt.get();
            fornecedorServico.inativar(fornecedor);
            
            return ResponseEntity.ok("Fornecedor inativado com sucesso");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

