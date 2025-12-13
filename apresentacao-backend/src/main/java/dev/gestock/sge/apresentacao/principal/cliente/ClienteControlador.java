package dev.gestock.sge.apresentacao.principal.cliente;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.gestock.sge.aplicacao.dominio.cliente.ClienteResumo;
import dev.gestock.sge.aplicacao.dominio.cliente.ClienteServicoAplicacao;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.principal.cliente.ClienteServico;
import dev.gestock.sge.apresentacao.BackendMapeador;

@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*")
public class ClienteControlador {

    @Autowired
    private ClienteServico clienteServico;

    @Autowired
    private ClienteServicoAplicacao clienteServicoAplicacao;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private BackendMapeador mapeador;

    // GET: Listar todos os clientes (resumos)
    @RequestMapping(method = GET)
    public ResponseEntity<List<ClienteResumo>> listar() {
        var clientes = clienteServicoAplicacao.pesquisarResumos();
        return ResponseEntity.ok(clientes);
    }

    // GET: Buscar cliente por ID
    @RequestMapping(method = GET, path = "/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable("id") Long id) {
        var clienteId = new ClienteId(id);
        var cliente = clienteRepositorio.buscarPorId(clienteId);
        
        if (cliente.isPresent()) {
            return ResponseEntity.ok(cliente.get());
        }
        return ResponseEntity.notFound().build();
    }

    // POST: Cadastrar novo cliente
    @RequestMapping(method = POST)
    public ResponseEntity<String> cadastrar(@RequestBody ClienteForm.ClienteCadastroDto dto) {
        try {
            var cliente = new Cliente(
                new ClienteId(null), // ID será gerado pelo banco
                dto.getNome(),
                dto.getDocumento(),
                dto.getEmail()
            );
            
            clienteServico.registrarCliente(cliente);
            return ResponseEntity.status(CREATED).body("Cliente cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
