package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import java.util.Map;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

/**
 * Serviço de domínio para operações que envolvem mais de um Estoque
 * ou orquestram múltiplas chamadas atômicas.
 *
 * Exemplos: transferência (R17/R18).
 */
public class EstoqueServico {

    private final EstoqueRepositorio repositorio;

    public EstoqueServico(EstoqueRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /* Transfere produtos entre estoques (R17/R18):
       Gera SAIDA no estoque de origem e ENTRADA no destino.
       Opera de forma atômica no nível de aplicação (transação deve envolver este método). */
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

        // 1) saída na origem (valida saldo disponível)
        origem.registrarSaida(produtoId, quantidade, responsavel, motivo);

        // 2) entrada no destino (metadados opcionais)
        destino.registrarEntrada(produtoId, quantidade, responsavel, "Transferência de estoque", Map.of(
                "transferencia", "true",
                "origem", origem.getId().toString()
        ));

        // 3) persistir ambos os agregados
        repositorio.salvar(origem);
        repositorio.salvar(destino);
    }
}


