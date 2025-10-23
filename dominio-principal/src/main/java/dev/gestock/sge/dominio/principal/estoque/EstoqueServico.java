package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import java.util.List;
import java.util.Map;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

// Serviço de domínio para operações de gerenciamento de estoques.
public class EstoqueServico {

    private final EstoqueRepositorio estoqueRepositorio;
    private final PedidoRepositorio pedidoRepositorio;

    public EstoqueServico(EstoqueRepositorio estoqueRepositorio) {
        this(estoqueRepositorio, null);
    }

    public EstoqueServico(EstoqueRepositorio estoqueRepositorio, PedidoRepositorio pedidoRepositorio) {
        this.estoqueRepositorio = estoqueRepositorio;
        this.pedidoRepositorio = pedidoRepositorio;
    }

    // Cadastra um novo estoque (H1).
    public void cadastrar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        
        // R2H1: Validar endereço único
        if (estoqueRepositorio.existePorEndereco(estoque.getEndereco(), estoque.getClienteId())) {
            throw new IllegalArgumentException("Já existe um estoque cadastrado neste endereço (R2H1)");
        }
        
        // R3H1: Validar nome único
        if (estoqueRepositorio.existePorNome(estoque.getNome(), estoque.getClienteId())) {
            throw new IllegalArgumentException("Já existe um estoque com este nome (R3H1)");
        }
        
        estoqueRepositorio.salvar(estoque);
    }

    // Inativa um estoque (H2).
    public void inativar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
 
        if (pedidoRepositorio != null && pedidoRepositorio.existePedidoPendentePorEstoqueId(estoque.getId())) {
            throw new IllegalStateException("Estoque com pedido em andamento nao pode ser inativado (R2H2)");
        }
        
        estoque.inativar();
        estoqueRepositorio.salvar(estoque);
    }

    // Atualiza parâmetros de um estoque (H3).
    public void atualizar(Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        estoqueRepositorio.salvar(estoque);
    }

    // Pesquisa estoques de um cliente (H4).
    public List<Estoque> pesquisarPorCliente(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        List<Estoque> estoques = estoqueRepositorio.buscarEstoquesPorClienteId(clienteId);
        
        // R1H4: Validar se há estoques cadastrados
        if (estoques.isEmpty()) {
            throw new IllegalStateException("Nenhum estoque cadastrado para este cliente (R1H4)");
        }
        
        return estoques;
    }

    //Transfere produtos entre estoques (H22-H23).
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

        // R1H22: Validar que estoques pertencem ao mesmo cliente
        if (!origem.getClienteId().equals(destino.getClienteId())) {
            throw new IllegalArgumentException("Transferência só pode ocorrer entre estoques do mesmo cliente (R1H22)");
        }

        // R2H22: saída na origem (valida saldo disponível automaticamente)
        origem.registrarSaida(produtoId, quantidade, responsavel, motivo);

        // R3H22: entrada no destino (metadados de transferência)
        destino.registrarEntrada(produtoId, quantidade, responsavel, "Transferência de estoque", Map.of(
                "transferencia", "true",
                "origem", origem.getId().toString()
        ));

        // Persistir ambos os agregados
        estoqueRepositorio.salvar(origem);
        estoqueRepositorio.salvar(destino);
    }
}


