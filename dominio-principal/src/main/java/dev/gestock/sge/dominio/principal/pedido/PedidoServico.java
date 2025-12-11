package dev.gestock.sge.dominio.principal.pedido;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.Cotacao;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.alerta.Alerta;
import dev.gestock.sge.dominio.principal.alerta.AlertaRepositorio;
import dev.gestock.sge.dominio.principal.alerta.AlertaServico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio para gerenciamento de pedidos.
 *
 * Suporta:
 * - H11-H13: Gerenciar Pedidos (criar, cancelar, confirmar recebimento)
 * - H24-H25: Reservar Estoque Para Pedidos Pendentes
 * 
 * Validações de regras de negócio:
 * - R1H11, R2H11: Criação de pedido com cotação válida e data prevista
 * - R1H12: Cancelamento (validado no agregado)
 * - R1H13: Confirmar recebimento gera entrada automática
 * - R1H24, R2H24, R1H25, R2H25: Reserva e liberação de estoque
 */
public class PedidoServico {

    private final PedidoRepositorio pedidoRepositorio;
    private final EstoqueRepositorio estoqueRepositorio;
    private final AlertaRepositorio alertaRepositorio;

    public PedidoServico(PedidoRepositorio pedidoRepositorio) {
        this(pedidoRepositorio, null, null);
    }

    public PedidoServico(PedidoRepositorio pedidoRepositorio, EstoqueRepositorio estoqueRepositorio) {
        this(pedidoRepositorio, estoqueRepositorio, null);
    }

    public PedidoServico(PedidoRepositorio pedidoRepositorio, EstoqueRepositorio estoqueRepositorio, AlertaRepositorio alertaRepositorio) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.estoqueRepositorio = estoqueRepositorio;
        this.alertaRepositorio = alertaRepositorio;
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

        // Reserva para pedido de compra: projeta aumento do estoque físico e reserva
        estoque.reservarParaPedidoCompra(produto.getId(), quantidade);
        if (estoqueRepositorio != null) {
            estoqueRepositorio.salvar(estoque);
        }
        pedidoRepositorio.salvar(pedido);
        return pedido;
    }

    /* Cancela o pedido (H12) e libera a reserva associada (R1H25, R2H25) */
    public void cancelarComLiberacao(Pedido pedido, Estoque estoque, ProdutoId produtoId, int quantidade) {
        notNull(pedido, "Pedido é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser > 0");

        pedido.cancelar(); // R1H12 validado no agregado
        estoque.liberarReserva(produtoId, quantidade); // R1H25
        if (estoqueRepositorio != null) {
            estoqueRepositorio.salvar(estoque);
        }
        pedidoRepositorio.cancelar(pedido);
    }

    /**
     * Confirma o recebimento de um pedido (H13).
     * Valida:
     * - R1H13: Gera movimentação de entrada automaticamente no estoque
     */
    public void confirmarRecebimento(Pedido pedido, Estoque estoque, String responsavel) {
        notNull(pedido, "Pedido é obrigatório");
        notBlank(responsavel, "Responsável é obrigatório");

        // Valida o status do pedido primeiro (deve ser ENVIADO)
        pedido.registrarRecebimento();
        
        // Após validar o status, valida o estoque
        notNull(estoque, "Estoque é obrigatório");

        // R1H13: Gera movimentação de entrada para cada item do pedido
        for (ItemPedido item : pedido.getItens()) {
            estoque.registrarEntrada(
                item.getProdutoId(),
                item.getQuantidade(),
                responsavel,
                "Recebimento de pedido " + pedido.getId(),
                java.util.Map.of("pedidoId", pedido.getId().toString())
            );
            
            // R1H17: Remove alertas automaticamente se estoque ficou acima do ROP
            if (alertaRepositorio != null) {
                removerAlertasSeNecessario(estoque, item.getProdutoId());
            }
        }

        if (estoqueRepositorio != null) {
            estoqueRepositorio.salvar(estoque);
        }
        pedidoRepositorio.salvar(pedido);
    }

    /**
     * Remove alertas ativos se o estoque físico do produto ficou acima do ROP (R1H17).
     */
    private void removerAlertasSeNecessario(Estoque estoque, ProdutoId produtoId) {
        if (alertaRepositorio == null) return;
        
        // Verifica se o produto tem ROP definido
        var rop = estoque.getROP(produtoId);
        if (rop == null) return;
        
        // Verifica se o saldo físico está acima do ROP
        int saldoFisico = estoque.getSaldoFisico(produtoId);
        if (saldoFisico > rop.getValorROP()) {
            // Busca alertas ativos para este produto neste estoque
            List<Alerta> alertas = alertaRepositorio.listarPorProduto(produtoId);
            for (Alerta alerta : alertas) {
                if (alerta.isAtivo() && alerta.getEstoqueId().equals(estoque.getId())) {
                    AlertaServico alertaServico = new AlertaServico(alertaRepositorio);
                    alertaServico.desativarAlerta(alerta);
                }
            }
        }
    }

    /* Atualiza automaticamente o lead time do fornecedor (R1H6) */
    public void recalibrarLeadTime(Fornecedor fornecedor, ProdutoId produtoId, int... diasEntrega) {
        notNull(fornecedor, "Fornecedor é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(diasEntrega.length > 0, "Histórico de entregas é obrigatório");

        var lista = java.util.Arrays.stream(diasEntrega).boxed().toList();
        fornecedor.recalibrarLeadTime(lista);
    }

    /**
     * Cancela um pedido (H12).
     * Valida:
     * - R1H12: Cancelamento validado no agregado
     */
    public void cancelar(Pedido pedido) {
        notNull(pedido, "Pedido é obrigatório");
        pedido.cancelar();
        pedidoRepositorio.salvar(pedido);
    }

    /**
     * Envia um pedido ao fornecedor (transição CRIADO → ENVIADO).
     */
    public void enviar(Pedido pedido) {
        notNull(pedido, "Pedido é obrigatório");
        pedido.enviar();
        pedidoRepositorio.salvar(pedido);
    }

    /**
     * Conclui um pedido (apenas após RECEBIDO).
     */
    public void concluir(Pedido pedido) {
        notNull(pedido, "Pedido é obrigatório");
        pedido.concluir();
        pedidoRepositorio.salvar(pedido);
    }
}
