package dev.gestock.sge.dominio.principal.pedido;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.Cotacao;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.math.BigDecimal;
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
 * - R8: Geração de movimentação automática após recebimento.
 * - R26: Cálculo do custo total do pedido.
 */
public class PedidoServico {

    private final PedidoRepositorio pedidoRepositorio;

    public PedidoServico(PedidoRepositorio pedidoRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
    }

    /**
     * Cria um novo pedido com base nas informações do cliente, fornecedor e produto.
     *
     * @param clienteId   identificador do cliente que faz o pedido
     * @param fornecedor  fornecedor escolhido
     * @param produto     produto a ser pedido
     * @param quantidade  quantidade solicitada
     * @return o novo Pedido gerado e persistido
     */
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

        // (R7) Validações simples antes de criar o pedido
        isTrue(cotacao.getPreco() > 0, "Preço da cotação inválido");
        isTrue(cotacao.getPrazoDias() > 0, "Prazo da cotação inválido");

        // Cria o pedido base
        Pedido pedido = new Pedido(clienteId, fornecedor.getId());
        BigDecimal precoUnitario = BigDecimal.valueOf(cotacao.getPreco());

        // Adiciona item ao pedido
        pedido.adicionarItem(new ItemPedido(produto.getId(), quantidade, precoUnitario));

        // (R26) Custo total = preço × quantidade
        BigDecimal valorItens = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        pedido.registrarCusto(new CustoPedido(valorItens, BigDecimal.ZERO, BigDecimal.ZERO)); // Sem frete por ora

        // Persiste o pedido (DDD: delega ao repositório)
        pedidoRepositorio.salvar(pedido);

        return pedido;
    }

    /**
     * (R14) Atualiza automaticamente o lead time do fornecedor
     * com base nas últimas entregas registradas.
     */
    public void recalibrarLeadTime(Fornecedor fornecedor, ProdutoId produtoId, int... diasEntrega) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(diasEntrega.length > 0, "Histórico de entregas é obrigatório");

        // Converte array de int para lista
        var lista = java.util.Arrays.stream(diasEntrega).boxed().toList();

        fornecedor.recalibrarLeadTime(lista);
    }
}
