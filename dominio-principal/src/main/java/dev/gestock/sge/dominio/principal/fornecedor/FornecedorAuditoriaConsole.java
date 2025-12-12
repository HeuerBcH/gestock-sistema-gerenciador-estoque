package dev.gestock.sge.dominio.principal.fornecedor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementação concreta de FornecedorAuditoria que registra eventos no console.
 * 
 * Responsabilidade:
 * - Registra eventos de auditoria no console com timestamp
 * - Formata mensagens de forma legível para debugging e desenvolvimento
 * 
 * Extensibilidade:
 * - Pode ser substituída por implementações que gravam em banco de dados, arquivo ou serviço externo
 * - Útil para desenvolvimento e testes
 */
public class FornecedorAuditoriaConsole implements FornecedorAuditoria {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void registrarSalvar(Fornecedor fornecedor) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA FORNECEDOR] %s | SALVAR | FornecedorId=%s | Nome=%s | CNPJ=%s%n",
                timestamp,
                fornecedor.getId(),
                fornecedor.getNome(),
                fornecedor.getCnpj());
    }
    
    @Override
    public void registrarLeitura(FornecedorId id) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA FORNECEDOR] %s | LEITURA | FornecedorId=%s%n",
                timestamp,
                id);
    }
    
    @Override
    public void registrarBuscaPorCnpj(String cnpj) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA FORNECEDOR] %s | BUSCA_POR_CNPJ | CNPJ=%s%n",
                timestamp,
                cnpj);
    }
}

