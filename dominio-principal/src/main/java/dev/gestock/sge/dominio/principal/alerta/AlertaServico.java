package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueObserver;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio para gerenciamento de alertas de estoque baixo.
 * 
 * Suporta:
 * - H16-H17: Emitir Alertas de Estoque Baixo
 * - Validações de regras de negócio R1H16, R2H16, R1H17
 * 
 * Padrão Observer:
 * - Implementa EstoqueObserver para reagir a atualizações de estoque
 * - Remove alertas automaticamente quando estoque fica acima do ROP (R1H17)
 */
public class AlertaServico implements EstoqueObserver {

    private final AlertaRepositorio repositorio;
    private final EstoqueRepositorio estoqueRepositorio;

    public AlertaServico(AlertaRepositorio repositorio) {
        this(repositorio, null);
    }
    
    public AlertaServico(AlertaRepositorio repositorio, EstoqueRepositorio estoqueRepositorio) {
        this.repositorio = repositorio;
        this.estoqueRepositorio = estoqueRepositorio;
    }
    
    @Override
    public void aoAtualizarEstoque(EstoqueId estoqueId) {
        // R1H17: Remove alertas automaticamente se estoque físico ficou acima do ROP
        if (estoqueRepositorio == null) return;
        
        Optional<Estoque> estoqueOpt = estoqueRepositorio.buscarPorId(estoqueId);
        if (estoqueOpt.isEmpty()) return;
        
        Estoque estoque = estoqueOpt.get();
        List<Alerta> alertas = repositorio.listarPorEstoque(estoqueId);
        
        for (Alerta alerta : alertas) {
            if (!alerta.isAtivo()) continue; // Pula alertas já desativados
            
            // Verifica se o produto tem ROP definido
            var rop = estoque.getROP(alerta.getProdutoId());
            if (rop == null) continue;
            
            // Verifica se o saldo físico está acima do ROP
            int saldoFisico = estoque.getSaldoFisico(alerta.getProdutoId());
            if (saldoFisico > rop.getValorROP()) {
                desativarAlerta(alerta);
            }
        }
    }

    /**
     * Gera um alerta ativo quando produto atinge ROP (H16).
     * Valida:
     * - R1H16: Alerta gerado automaticamente ao atingir ROP
     * - R2H16: Indica produto, estoque e fornecedor sugerido
     */
    public Alerta gerarAlerta(ProdutoId produtoId, EstoqueId estoqueId, FornecedorId fornecedorSugerido) {
        notNull(produtoId, "Produto é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        // ID deve ser gerado pela camada de persistência
        AlertaId novoId = repositorio.novoAlertaId();
        Alerta alerta = new Alerta(novoId, produtoId, estoqueId, fornecedorSugerido);
        repositorio.salvar(alerta);
        return alerta;
    }

    /**
     * Desativa um alerta após o recebimento do pedido (H17).
     * Valida:
     * - R1H17: Alerta removido automaticamente após recebimento
     */
    public void desativarAlerta(Alerta alerta) {
        notNull(alerta, "Alerta é obrigatório");
        alerta.desativar();
        repositorio.salvar(alerta);
    }
}
