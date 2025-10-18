package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Funcionalidade: Gerenciar Produtos
 * Histórias: H8 a H10
 */
public class GerenciarProdutosFuncionalidade extends AcervoFuncionalidade {

    private Produto produto;
    private Estoque estoque;
    private Fornecedor fornecedor;
    private RuntimeException excecao;
    private final Map<String, Produto> produtos = new HashMap<>();

    // ============================================================
    // H8 – Cadastrar Produtos
    // ============================================================

    @Given("que o cliente deseja cadastrar novos produtos")
    public void cliente_deseja_cadastrar_novos_produtos() {
        fornecedor = new Fornecedor(new FornecedorId(1), "Fornecedor Alpha", 5);
        fornecedorServico.salvar(fornecedor);

        estoque = new Estoque(new EstoqueId(1), "Estoque Recife", "Rua A", 500);
        estoqueServico.salvar(estoque);
    }

    @When("ele informa código, descrição, unidade de medida e estoque vinculado")
    public void cliente_informa_dados_produto() {
        try {
            produto = new Produto(new ProdutoId(1), "P001", "Produto Teste", "Unidade", estoque.getId());
            produtoServico.salvar(produto);
            produtos.put(produto.getCodigo(), produto);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve criar o produto com código único e associá-lo ao estoque ativo")
    public void sistema_cria_produto_com_codigo_unico() {
        assertNotNull(produto);
        assertEquals("P001", produto.getCodigo());
        assertTrue(estoque.ativo());
        assertTrue(produtoServico.existe(produto.getCodigo()));

        // Regras:
        // R1H8: Cada produto tem código único por cliente
        // R2H8: Vínculo com fornecedor ocorre via cotação
        // R3H8: Produto deve estar vinculado a pelo menos um estoque ativo
    }

    // ============================================================
    // H9 – Editar Produtos
    // ============================================================

    @Given("que o cliente possui produtos cadastrados")
    public void cliente_possui_produtos_cadastrados() {
        produto = new Produto(new ProdutoId(2), "P002", "Produto Beta", "Caixa", estoque.getId());
        produtoServico.salvar(produto);
    }

    @When("ele altera especificações como descrição e embalagem")
    public void cliente_altera_especificacoes_produto() {
        produto.setDescricao("Produto Beta Atualizado");
        produto.setUnidade("Pacote");
        produtoServico.salvar(produto);
    }

    @Then("o sistema deve atualizar as informações sem afetar cotações existentes")
    public void sistema_atualiza_produto_sem_afetar_cotacoes() {
        var atualizado = produtoServico.obter(produto.getId());
        assertEquals("Produto Beta Atualizado", atualizado.getDescricao());
        assertEquals("Pacote", atualizado.getUnidade());

        // Regras:
        // R1H9: Alterações não afetam cotações existentes,
        //       mas impedem novas cotações com dados desatualizados
    }

    // ============================================================
    // H10 – Inativar Produtos
    // ============================================================

    @Given("que o cliente deseja inativar um produto")
    public void cliente_deseja_inativar_produto() {
        produto = new Produto(new ProdutoId(3), "P003", "Produto Gama", "Unidade", estoque.getId());
        produtoServico.salvar(produto);
    }

    @When("ele solicita a inativação")
    public void cliente_solicita_inativacao_produto() {
        try {
            produtoServico.inativar(produto.getId());
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve permitir apenas se o produto não tiver saldo em estoque ou pedidos em andamento")
    public void sistema_valida_inativacao_produto() {
        if (estoqueServico.possuiSaldo(produto.getId()) || pedidoServico.possuiPedidoAtivo(produto.getId())) {
            assertNotNull(excecao, "Deveria lançar exceção ao tentar inativar produto com saldo/pedido ativo");
        } else {
            assertFalse(produto.ativo());
        }

        // Regras:
        // R1H10: Produto com saldo ou pedido ativo não pode ser inativado
        // R2H10: Ao inativar, novas cotações e pedidos devem ser bloqueados
    }
}
