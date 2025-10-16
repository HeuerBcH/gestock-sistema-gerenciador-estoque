package dev.gestock.sge.dominio.principal.estoque;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
/**
 * Entidade de auditoria de movimentos no Estoque.
 * Mantém trilha completa: tipo, produto, quantidade, data/hora, responsável, motivo, metadados.
 */
public class Movimentacao {

    private final UUID id;                   // identidade do registro de movimento
    private final TipoMovimentacao tipo;     // ENTRADA, SAIDA ou AJUSTE
    private final ProdutoId produtoId;       // referência ao agregado Produto
    private final int quantidade;            // quantidade movimentada (pode ser negativa no log de ajuste)
    private final LocalDateTime dataHora;    // timestamp do evento
    private final String responsavel;        // quem realizou
    private final String motivo;             // por quê (obrigatório em ajuste)
    private final Map<String, String> meta;  // metadados (ex.: lote, validade, flags)

    public Movimentacao(TipoMovimentacao tipo,
                        ProdutoId produtoId,
                        int quantidade,
                        LocalDateTime dataHora,
                        String responsavel,
                        String motivo,
                        Map<String, String> meta) {
        this.id = UUID.randomUUID();
        this.tipo = tipo;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
        this.dataHora = dataHora;
        this.responsavel = responsavel;
        this.motivo = motivo;
        this.meta = meta;
    }

    // getters… (adicione conforme a necessidade de leitura)
}

