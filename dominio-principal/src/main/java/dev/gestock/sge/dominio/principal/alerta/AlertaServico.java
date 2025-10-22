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

    public Alerta gerarAlerta(ProdutoId produtoId, EstoqueId estoqueId, FornecedorId fornecedorSugerido) {
        notNull(produtoId, "Produto é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        Alerta alerta = new Alerta(new AlertaId(1L), produtoId, estoqueId, fornecedorSugerido);
        repositorio.salvar(alerta);
        return alerta;
    }

    public void desativarAlerta(Alerta alerta) {
        notNull(alerta, "Alerta é obrigatório");
        alerta.desativar();
        repositorio.salvar(alerta);
    }
}
