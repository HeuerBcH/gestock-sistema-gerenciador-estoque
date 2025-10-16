package dev.gestock.sge.dominio.principal.custo;

/**
 * Enum: Tipos de custo possíveis em um pedido.
 */
public enum TipoCusto {
    PRODUTO,      // preço de compra dos itens
    FRETE,        // custo de transporte
    LOGISTICO,    // custos administrativos e de armazenagem
    OUTRO         // outros custos eventuais
}
