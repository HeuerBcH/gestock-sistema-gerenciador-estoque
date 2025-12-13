package dev.gestock.sge.apresentacao.principal.pedido;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.pedido.PedidoResumo;
import dev.gestock.sge.aplicacao.dominio.pedido.PedidoServicoAplicacao;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.pedido.PedidoServico;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
public class PedidoControlador {

    @Autowired
    private PedidoServico pedidoServico;

    @Autowired
    private PedidoServicoAplicacao pedidoServicoAplicacao;

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private FornecedorRepositorio fornecedorRepositorio;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Autowired
    private EstoqueRepositorio estoqueRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os pedidos (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<PedidoResumo>> listar() {
        var pedidos = pedidoServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(pedidos);
    }

    // GET: Buscar pedido por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable("id") Long id) {
        var pedidoId = new PedidoId(id);
        var pedido = pedidoRepositorio.buscarPorId(pedidoId);
        
        if (pedido.isPresent()) {
            return ResponseEntity.ok(pedido.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST: Criar novo pedido
    @RequestMapping(method = POST)
    public ResponseEntity<String> criar(@RequestBody PedidoForm.PedidoCriacaoDto dto) {
        try {
            var fornecedorOpt = fornecedorRepositorio.buscarPorId(new FornecedorId(dto.getFornecedorId()));
            var produtoOpt = produtoRepositorio.buscarPorId(new ProdutoId(dto.getProdutoId()));
            
            if (fornecedorOpt.isEmpty() || produtoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Fornecedor ou Produto não encontrado");
            }
            
            Pedido pedido;
            
            // Se tem estoque, cria pedido com reserva
            if (dto.getEstoqueId() != null) {
                var estoqueOpt = estoqueRepositorio.buscarPorId(new EstoqueId(dto.getEstoqueId()));
                if (estoqueOpt.isEmpty()) {
                    return ResponseEntity.badRequest().body("Estoque não encontrado");
                }
                
                pedido = pedidoServico.gerarPedidoParaEstoque(
                    new ClienteId(dto.getClienteId()),
                    fornecedorOpt.get(),
                    produtoOpt.get(),
                    dto.getQuantidade(),
                    estoqueOpt.get()
                );
            } else {
                // Cria pedido simples sem estoque
                pedido = pedidoServico.gerarPedido(
                    new ClienteId(dto.getClienteId()),
                    fornecedorOpt.get(),
                    produtoOpt.get(),
                    dto.getQuantidade()
                );
            }
            
            return ResponseEntity.status(CREATED).body("Pedido criado com sucesso: " + pedido.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Enviar pedido ao fornecedor
    @RequestMapping(method = POST, path = "/{id}/enviar")
    public ResponseEntity<String> enviar(@PathVariable("id") Long id) {
        try {
            var pedidoId = new PedidoId(id);
            var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var pedido = pedidoOpt.get();
            pedidoServico.enviar(pedido);
            
            return ResponseEntity.ok("Pedido enviado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Confirmar recebimento do pedido
    @RequestMapping(method = POST, path = "/{id}/receber")
    public ResponseEntity<String> confirmarRecebimento(
            @PathVariable("id") Long id,
            @RequestBody PedidoForm.RecebimentoDto dto) {
        try {
            var pedidoId = new PedidoId(id);
            var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
            var estoqueOpt = estoqueRepositorio.buscarPorId(new EstoqueId(dto.getEstoqueId()));
            
            if (pedidoOpt.isEmpty() || estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            pedidoServico.confirmarRecebimento(
                pedidoOpt.get(),
                estoqueOpt.get(),
                dto.getResponsavel()
            );
            
            return ResponseEntity.ok("Recebimento confirmado com sucesso");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Cancelar pedido
    @RequestMapping(method = POST, path = "/{id}/cancelar")
    public ResponseEntity<String> cancelar(@PathVariable("id") Long id) {
        try {
            var pedidoId = new PedidoId(id);
            var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var pedido = pedidoOpt.get();
            pedidoServico.cancelar(pedido);
            
            return ResponseEntity.ok("Pedido cancelado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Cancelar pedido com liberação de reserva
    @RequestMapping(method = POST, path = "/{id}/cancelar-com-liberacao")
    public ResponseEntity<String> cancelarComLiberacao(
            @PathVariable("id") Long id,
            @RequestBody PedidoForm.CancelamentoDto dto) {
        try {
            var pedidoId = new PedidoId(id);
            var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
            var estoqueOpt = estoqueRepositorio.buscarPorId(new EstoqueId(dto.getEstoqueId()));
            
            if (pedidoOpt.isEmpty() || estoqueOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            pedidoServico.cancelarComLiberacao(
                pedidoOpt.get(),
                estoqueOpt.get(),
                new ProdutoId(dto.getProdutoId()),
                dto.getQuantidade()
            );
            
            return ResponseEntity.ok("Pedido cancelado e reserva liberada com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // POST: Concluir pedido
    @RequestMapping(method = POST, path = "/{id}/concluir")
    public ResponseEntity<String> concluir(@PathVariable("id") Long id) {
        try {
            var pedidoId = new PedidoId(id);
            var pedidoOpt = pedidoRepositorio.buscarPorId(pedidoId);
            
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var pedido = pedidoOpt.get();
            pedidoServico.concluir(pedido);
            
            return ResponseEntity.ok("Pedido concluído com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

