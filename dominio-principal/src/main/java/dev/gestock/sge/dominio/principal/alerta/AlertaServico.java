package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import static org.apache.commons.lang3.Validate.*;

/**
 * Serviço de domínio para gerenciamento de alertas de estoque baixo.
 * 
 * Suporta:
 * - H16-H17: Emitir Alertas de Estoque Baixo
 * - Validações de regras de negócio R1H16, R2H16, R1H17
 */
public class AlertaServico {

    private final AlertaRepositorio repositorio;

    public AlertaServico(AlertaRepositorio repositorio) {
        this.repositorio = repositorio;
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
        // NOTA: ID será gerado pela camada de persistência
        Alerta alerta = new Alerta(new AlertaId(1L), produtoId, estoqueId, fornecedorSugerido);
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
