package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

// Repositório do agregado Alerta.
public interface AlertaRepositorio {
    
    // Persiste ou atualiza um alerta (H16 - R1H16)
    void salvar(Alerta alerta);
    
    // Busca um alerta por seu ID
    Optional<Alerta> obter(AlertaId id);
    
    // Lista todos os alertas ativos (H17)
    List<Alerta> listarAtivos();
    
    // Lista alertas de um produto específico
    List<Alerta> listarPorProduto(ProdutoId produtoId);
    
    // Lista alertas de um estoque específico
    List<Alerta> listarPorEstoque(EstoqueId estoqueId);
}
