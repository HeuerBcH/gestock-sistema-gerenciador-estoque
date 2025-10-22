package dev.gestock.sge.dominio.principal.pedido;

public enum StatusPedido {
    CRIADO,         // recém-registrado
    ENVIADO,        // enviado ao fornecedor
    EM_TRANSPORTE,  // em transporte
    RECEBIDO,       // mercadoria conferida/entrada registrada
    CANCELADO,      // cancelado
    CONCLUIDO       // finalizado após recebimento/baixas
}
