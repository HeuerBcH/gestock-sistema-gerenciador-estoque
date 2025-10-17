package dev.gestock.sge.dominio.principal.pedido;

/**
 * Enum: Status do Pedido
 * 
 * Estados possíveis:
 * - PENDENTE: Pedido criado, aguardando processamento
 * - EM_TRANSPORTE: Pedido em transporte
 * - RECEBIDO: Pedido recebido
 * - CANCELADO: Pedido cancelado
 */
public enum PedidoStatus {
    PENDENTE,
    EM_TRANSPORTE,
    RECEBIDO,
    CANCELADO
}
