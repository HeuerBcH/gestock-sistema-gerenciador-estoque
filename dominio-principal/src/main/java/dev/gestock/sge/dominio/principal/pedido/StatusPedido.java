package dev.gestock.sge.dominio.principal.pedido;

/** Estados válidos do pedido (controlam transições de negócio). */
public enum StatusPedido {
    CRIADO,      // recém-registrado
    ENVIADO,     // enviado ao fornecedor
    RECEBIDO,    // mercadoria conferida/entrada registrada (R8)
    CANCELADO,   // cancelado
    CONCLUIDO    // finalizado após recebimento/baixas
}
