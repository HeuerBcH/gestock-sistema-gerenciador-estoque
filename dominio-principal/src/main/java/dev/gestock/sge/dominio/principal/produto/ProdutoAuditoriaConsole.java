package dev.gestock.sge.dominio.principal.produto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementação concreta de ProdutoAuditoria que registra eventos no console.
 * 
 * Responsabilidade:
 * - Registra eventos de auditoria no console com timestamp
 * - Formata mensagens de forma legível para debugging e desenvolvimento
 * 
 * Extensibilidade:
 * - Pode ser substituída por implementações que gravam em banco de dados, arquivo ou serviço externo
 * - Útil para desenvolvimento e testes
 */
public class ProdutoAuditoriaConsole implements ProdutoAuditoria {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public void registrarSalvar(Produto produto) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA PRODUTO] %s | SALVAR | ProdutoId=%s | Codigo=%s | Nome=%s%n",
                timestamp,
                produto.getId(),
                produto.getCodigo(),
                produto.getNome());
    }
    
    @Override
    public void registrarLeitura(ProdutoId id) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA PRODUTO] %s | LEITURA | ProdutoId=%s%n",
                timestamp,
                id);
    }
    
    @Override
    public void registrarBuscaPorCodigo(CodigoProduto codigo) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA PRODUTO] %s | BUSCA_POR_CODIGO | Codigo=%s%n",
                timestamp,
                codigo.getValor());
    }
    
    @Override
    public void registrarInativacao(Produto produto) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        System.out.printf("[AUDITORIA PRODUTO] %s | INATIVACAO | ProdutoId=%s | Codigo=%s | Nome=%s%n",
                timestamp,
                produto.getId(),
                produto.getCodigo(),
                produto.getNome());
    }
}

