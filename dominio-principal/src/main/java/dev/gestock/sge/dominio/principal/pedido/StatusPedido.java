package dev.gestock.sge.dominio.principal.pedido;

/** Estados válidos do pedido (controlam transições de negócio). */
public enum StatusPedido {
    CRIADO,         // recém-registrado
    ENVIADO,        // enviado ao fornecedor
    EM_TRANSPORTE,  // em transporte (R1H12)
    RECEBIDO,       // mercadoria conferida/entrada registrada (R1H13)
    CANCELADO,      // cancelado
    CONCLUIDO       // finalizado após recebimento/baixas
}
