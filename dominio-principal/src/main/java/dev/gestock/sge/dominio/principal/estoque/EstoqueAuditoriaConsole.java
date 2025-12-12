package dev.gestock.sge.dominio.principal.estoque;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;

/**
 * Implementação concreta de EstoqueAuditoria que registra eventos no console.
 * 
 * Responsabilidade:
 * - Registra eventos de auditoria no console com timestamp
 * - Formata mensagens de forma legível para debugging e desenvolvimento
 * 
 * Extensibilidade:
 * - Pode ser substituída por implementações que gravam em banco de dados, arquivo ou serviço externo
 * - Útil para desenvolvimento e testes
 */
public class EstoqueAuditoriaConsole implements EstoqueAuditoria {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void registrarSalvar(Estoque estoque) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA ESTOQUE] %s | SALVAR | EstoqueId=%s | Nome=%s | ClienteId=%s%n",
                timestamp,
                estoque.getId(),
                estoque.getNome(),
                estoque.getClienteId());
    }
    
    @Override
    public void registrarLeitura(EstoqueId id) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA ESTOQUE] %s | LEITURA | EstoqueId=%s%n",
                timestamp,
                id);
    }
    
    @Override
    public void registrarBuscaPorCliente(ClienteId clienteId) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA ESTOQUE] %s | BUSCA_POR_CLIENTE | ClienteId=%s%n",
                timestamp,
                clienteId);
    }
    
    @Override
    public void registrarRemocao(EstoqueId id) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA ESTOQUE] %s | REMOCAO | EstoqueId=%s%n",
                timestamp,
                id);
    }
}

