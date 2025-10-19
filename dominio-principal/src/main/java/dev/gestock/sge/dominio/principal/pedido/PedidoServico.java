package dev.gestock.sge.dominio.principal.pedido;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.Cotacao;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio: PedidoServico
 *
 * Responsável por orquestrar a criação de pedidos,
 * aplicando as regras de negócio que envolvem múltiplos agregados:
 *
 * - R5: Seleção automática da melhor cotação (menor preço).
 * - R6: Desempate por menor prazo (lead time).
 * - R7: Validação de quantidade mínima e dados obrigatórios.
 * - R2H11: Define data prevista de entrega com base no lead time do fornecedor.
 * - R24/R25: Reserva/liberação de estoque vinculado a pedidos pendentes.
 * - R26: Cálculo do custo total do pedido.
 */
public class PedidoServico {

    private final PedidoRepositorio pedidoRepositorio;
    private final EstoqueRepositorio estoqueRepositorio;

    public PedidoServico(PedidoRepositorio pedidoRepositorio) {
        this(pedidoRepositorio, null);
    }

    public PedidoServico(PedidoRepositorio pedidoRepositorio, EstoqueRepositorio estoqueRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.estoqueRepositorio = estoqueRepositorio;
    }

    /* Cria um pedido e define data prevista de entrega a partir da cotação */
    public Pedido gerarPedido(ClienteId clienteId, Fornecedor fornecedor, Produto produto, int quantidade) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(produto, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser maior que zero");

        // (R5 e R6) Seleciona a melhor cotação para o produto
        Optional<Cotacao> cotacaoOpt = fornecedor.obterCotacaoPorProduto(produto.getId());
        if (cotacaoOpt.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma cotação encontrada para o produto: " + produto.getNome());
        }

        Cotacao cotacao = cotacaoOpt.get();
        isTrue(cotacao.getPreco() > 0, "Preço da cotação inválido");
        isTrue(cotacao.getPrazoDias() > 0, "Prazo da cotação inválido");

        // Cria o pedido base
        Pedido pedido = new Pedido(new PedidoId(1L), clienteId, fornecedor.getId());
        BigDecimal precoUnitario = BigDecimal.valueOf(cotacao.getPreco());
        pedido.adicionarItem(new ItemPedido(produto.getId(), quantidade, precoUnitario));

        // (R2H11) Data prevista = hoje + prazo da cotação
        LocalDate prevista = LocalDate.now().plusDays(cotacao.getPrazoDias());
        pedido.setDataPrevistaEntrega(prevista);

        // (R26) Custo total = preço × quantidade
        BigDecimal valorItens = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        pedido.registrarCusto(new CustoPedido(valorItens, BigDecimal.ZERO, BigDecimal.ZERO));

        pedidoRepositorio.salvar(pedido);
        return pedido;
    }

    /* Variante que associa um estoque e reserva a quantidade (R1H24, R2H24). */
    public Pedido gerarPedidoParaEstoque(ClienteId clienteId, Fornecedor fornecedor, Produto produto, int quantidade, Estoque estoque) {
        notNull(estoque, "Estoque é obrigatório");
        Pedido pedido = gerarPedido(clienteId, fornecedor, produto, quantidade);
        pedido.setEstoqueId(estoque.getId());

        // Reserva quantidade no estoque
        estoque.reservar(produto.getId(), quantidade);
        if (estoqueRepositorio != null) {
            estoqueRepositorio.salvar(estoque);
        }
        pedidoRepositorio.salvar(pedido);
        return pedido;
    }

    /* Cancela o pedido e libera a reserva associada (R1H25) */
    public void cancelarComLiberacao(Pedido pedido, Estoque estoque, ProdutoId produtoId, int quantidade) {
        notNull(pedido, "Pedido é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser > 0");

        pedido.cancelar();
        estoque.liberarReserva(produtoId, quantidade);
        if (estoqueRepositorio != null) {
            estoqueRepositorio.salvar(estoque);
        }
        pedidoRepositorio.salvar(pedido);
    }

    /* (R14) Atualiza automaticamente o lead time do fornecedor */
    public void recalibrarLeadTime(Fornecedor fornecedor, ProdutoId produtoId, int... diasEntrega) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(diasEntrega.length > 0, "Histórico de entregas é obrigatório");

        var lista = java.util.Arrays.stream(diasEntrega).boxed().toList();
        fornecedor.recalibrarLeadTime(lista);
    }
}
