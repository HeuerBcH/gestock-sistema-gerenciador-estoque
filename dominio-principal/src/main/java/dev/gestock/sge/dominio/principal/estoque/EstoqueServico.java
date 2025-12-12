package dev.gestock.sge.dominio.principal.estoque;

import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public class EstoqueServico {

    private final EstoqueRepositorio estoqueRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final AtualizacaoEstoqueTemplate atualizacaoTemplate;

    public EstoqueServico(EstoqueRepositorio estoqueRepositorio) {
        this(estoqueRepositorio, null, new AtualizacaoEstoquePadrao(estoqueRepositorio));
    }

    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
        this(estoqueRepositorio, pedidoRepositorio, new AtualizacaoEstoquePadrao(estoqueRepositorio, pedidoRepositorio));
    }

    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio, AtualizacaoEstoqueTemplate atualizacaoTemplate) {
        notNull(estoqueRepositorio, "EstoqueRepositorio é obrigatório");
        notNull(atualizacaoTemplate, "AtualizacaoEstoqueTemplate é obrigatório");
        this.estoqueRepositorio = estoqueRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
        this.atualizacaoTemplate = atualizacaoTemplate;
    }

    public void cadastrar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        
        if (estoqueRepositorio.existePorEndereco(estoque.getEndereco(), estoque.getClienteId())) {
            throw new IllegalArgumentException("Já existe um estoque cadastrado neste endereço (R2H1)");
        }
        
        if (estoqueRepositorio.existePorNome(estoque.getNome(), estoque.getClienteId())) {
            throw new IllegalArgumentException("Já existe um estoque com este nome (R3H1)");
        }
        
        estoqueRepositorio.salvar(estoque);
    }

    public void inativar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
 
        if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendentePorEstoqueId(estoque.getId())) {
            throw new IllegalStateException("Estoque com pedido em andamento nao pode ser inativado (R2H2)");
        }
        
        estoque.inativar();
        estoqueRepositorio.salvar(estoque);
    }

    public void remover(EstoqueId estoqueId) {
        notNull(estoqueId, "ID do estoque é obrigatório");
        
        var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado: " + estoqueId);
        }
        
        // TODO: Adicionar validação de pedidos pendentes (R2H2)
        // var estoque = estoqueOpt.get();
        // if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendentePorEstoqueId(estoqueId)) {
        //     throw new IllegalStateException("Estoque com pedido em andamento não pode ser removido (R2H2)");
        // }
        
        // TODO: Adicionar validação de produtos em estoque (R1H2)
        // if (estoque.getSaldosSnapshot().values().stream().anyMatch(s -> s.fisico() > 0)) {
        //     throw new IllegalStateException("Estoque com produtos não pode ser removido (R1H2)");
        // }
        
        estoqueRepositorio.remover(estoqueId);
    }

    public void atualizar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        atualizacaoTemplate.atualizar(estoque.getId());
    }

    public void atualizar(EstoqueId estoqueId, String nome, String endereco, Integer capacidade) {
        notNull(estoqueId, "ID do estoque é obrigatório");
        
        var estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
        if (estoqueOpt.isEmpty()) {
            throw new IllegalArgumentException("Estoque não encontrado: " + estoqueId);
        }
        
        var estoque = estoqueOpt.get();
        
        if (nome != null && !nome.isBlank()) {
            estoque.renomear(nome);
        }
        if (endereco != null && !endereco.isBlank()) {
            estoque.alterarEndereco(endereco);
        }
        if (capacidade != null && capacidade > 0) {
            estoque.alterarCapacidade(capacidade);
        }
        
        atualizacaoTemplate.atualizar(estoque);
    }

    public Estoque buscarPorId(EstoqueId estoqueId) {
        notNull(estoqueId, "ID do estoque é obrigatório");
        return estoqueRepositorio.buscarPorId(estoqueId)
                .orElseThrow(() -> new IllegalArgumentException("Estoque não encontrado: " + estoqueId));
    }

    public List<Estoque> pesquisarPorCliente(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        List<Estoque> estoques = estoqueRepositorio.buscarEstoquesPorClienteId(clienteId);
        
        if (estoques.isEmpty()) {
            throw new IllegalStateException("Nenhum estoque cadastrado para este cliente (R1H4)");
        }
        
        return estoques;
    }

    public void transferir(Estoque origem,
                           Estoque destino,
                           ProdutoId produtoId,
                           int quantidade,
                           String responsavel,
                           String motivo) {

        notNull(origem, "Estoque de origem é obrigatório");
        notNull(destino, "Estoque de destino é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");

        if (!origem.getClienteId().equals(destino.getClienteId())) {
            throw new IllegalArgumentException("Transferência só pode ocorrer entre estoques do mesmo cliente (R1H22)");
        }

        origem.registrarSaida(produtoId, quantidade, responsavel, motivo);

        destino.registrarEntrada(produtoId, quantidade, responsavel, "Transferência de estoque", Map.of(
                "transferencia", "true",
                "origem", origem.getId().toString()
        ));

        estoqueRepositorio.salvar(origem);
        estoqueRepositorio.salvar(destino);
    }
}


