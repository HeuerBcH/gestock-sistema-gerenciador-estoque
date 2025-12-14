package dev.gestock.sge.apresentacao.config;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import dev.gestock.sge.dominio.comum.RegraVioladaException;

/**
 * Handler global para exceções de regras de negócio.
 * Retorna erros no formato: { "erro": "CODIGO_REGRA: Mensagem" }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RegraVioladaException.class)
    public ResponseEntity<Map<String, String>> handleRegraViolada(RegraVioladaException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
    }
}

