package dev.gestock.sge.apresentacao.principal.produto;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.produto.ProdutoResumo;
import dev.gestock.sge.aplicacao.dominio.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoServico;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoControlador {

    @Autowired
    private ProdutoServico produtoServico;

    @Autowired
    private ProdutoServicoAplicacao produtoServicoAplicacao;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os produtos (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<ProdutoResumo>> listar() {
        var produtos = produtoServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(produtos);
    }

    // GET: Buscar produto por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable("id") Long id) {
        var produtoId = new ProdutoId(id);
        var produto = produtoRepositorio.buscarPorId(produtoId);
        
        if (produto.isPresent()) {
            return ResponseEntity.ok(produto.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST: Cadastrar novo produto
    @RequestMapping(method = POST)
    public ResponseEntity<String> cadastrar(@RequestBody ProdutoForm.ProdutoCadastroDto dto) {
        try {
            var produto = new Produto(
                new ProdutoId(null), // ID será gerado pelo banco
                dto.getCodigo(),
                dto.getNome(),
                dto.getUnidadePeso(),
                dto.isPerecivel(),
                dto.getPeso()
            );
            
            produtoServico.cadastrar(produto);
            return ResponseEntity.status(CREATED).body("Produto cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT: Atualizar produto existente
    @RequestMapping(method = PUT, path = "/{id}")
    public ResponseEntity<String> atualizar(
            @PathVariable("id") Long id,
            @RequestBody ProdutoForm.ProdutoAtualizacaoDto dto) {
        try {
            var produtoId = new ProdutoId(id);
            var produtoOpt = produtoRepositorio.buscarPorId(produtoId);
            
            if (produtoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var produto = produtoOpt.get();
            produto.atualizar(dto.getNome(), dto.getUnidadePeso(), dto.getPeso());
            produtoServico.atualizar(produto);
            
            return ResponseEntity.ok("Produto atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE: Inativar produto (soft delete)
    @RequestMapping(method = DELETE, path = "/{id}")
    public ResponseEntity<String> inativar(@PathVariable("id") Long id) {
        try {
            var produtoId = new ProdutoId(id);
            var produtoOpt = produtoRepositorio.buscarPorId(produtoId);
            
            if (produtoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var produto = produtoOpt.get();
            produtoServico.inativar(produto);
            
            return ResponseEntity.ok("Produto inativado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

