package dev.gestock.sge.dominio.principal.alerta;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import static org.apache.commons.lang3.Validate.*;

// Serviço de domínio para gerenciamento de alertas de estoque baixo.

public class AlertaServico {

    private final AlertaRepositorio repositorio;

    public AlertaServico(AlertaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    // Gera um alerta ativo quando produto atinge ROP (H16).
    public Alerta gerarAlerta(ProdutoId produtoId, EstoqueId estoqueId, FornecedorId fornecedorSugerido) {
        notNull(produtoId, "Produto é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        // Geração de ID única independente do tipo de repositório
        long nano = System.nanoTime();
        if (nano <= 0) nano = Math.abs(nano) + 1; // garante positivo
        AlertaId id = new AlertaId(nano);
        Alerta alerta = new Alerta(id, produtoId, estoqueId, fornecedorSugerido);
        repositorio.salvar(alerta);
        return alerta;
    }

    // Desativa um alerta após o recebimento do pedido (H17).
    public void desativarAlerta(Alerta alerta) {
        notNull(alerta, "Alerta é obrigatório");
        alerta.desativar();
        repositorio.salvar(alerta);
    }
}