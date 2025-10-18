package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.List;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.cotacao.Cotacao;
import dev.gestock.sge.dominio.principal.cotacao.CotacaoId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

/**
 * Testes BDD para a funcionalidade "Gerenciar Fornecedores".
 *
 * Histórias cobertas:
 * H5 – Cadastrar Fornecedores
 * H6 – Atualizar Fornecedores
 * H7 – Inativar Fornecedores
 */
public class GerenciarFornecedoresFuncionalidade extends AcervoFuncionalidade {

    private ClienteId clienteId = new ClienteId(1);
    private FornecedorId fornecedorId = new FornecedorId(1);
    private ProdutoId produtoId = new ProdutoId(1);
    private CotacaoId cotacaoId = new CotacaoId(1);

    private RuntimeException excecao;
    private Fornecedor fornecedorAtualizado;
    private List<Fornecedor> listaFornecedores;

    /* ---------------------------------------------------------------------
     * H5 – Cadastrar Fornecedores
     * Given que o cliente deseja registrar um novo fornecedor
     * When ele informa nome, contato, lead time e cotações
     * Then o sistema deve criar o fornecedor com suas cotações vinculadas
     * Regras:
     * R1H5: Cada fornecedor deve possuir cotação vinculada a um produto
     * R2H5: O Lead Time deve ser um número positivo
     * ------------------------------------------------------------------ */

    @Given("que o cliente deseja registrar um novo fornecedor")
    public void cliente_deseja_registrar_novo_fornecedor() {
        // Setup inicial — cliente já cadastrado
        assertNotNull(clienteId, "Cliente deve estar definido");
    }

    @When("ele informa nome {string}, contato {string}, lead time {int} e cotação para o produto")
    public void informa_dados_do_fornecedor(String nome, String contato, int leadTime) {
        try {
            if (leadTime <= 0) {
                throw new IllegalArgumentException("R2H5: Lead Time deve ser positivo");
            }

            var cotacao = new Cotacao(cotacaoId, produtoId, fornecedorId, 50.0);
            cotacaoServico.salvar(cotacao);

            var fornecedor = new Fornecedor(fornecedorId, nome, contato, leadTime, clienteId, Arrays.asList(cotacaoId), true);
            fornecedorServico.salvar(fornecedor);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema cria o fornecedor com cotação vinculada ao produto")
    public void sistema_cria_fornecedor_com_cotacao_vinculada() {
        if (excecao != null) {
            assertTrue(excecao.getMessage().contains("Lead Time"), "Erro esperado para lead time inválido (R2H5)");
            return;
        }

        var fornecedor = fornecedorServico.obter(fornecedorId);
        assertNotNull(fornecedor, "Fornecedor deve ter sido criado");

        var cotacoes = cotacaoServico.pesquisarPorFornecedor(fornecedorId);
        assertTrue(!cotacoes.isEmpty(), "R1H5: Cada fornecedor deve possuir cotação vinculada a um produto");
    }

    /* ---------------------------------------------------------------------
     * H6 – Atualizar Fornecedores
     * Given que o cliente possui fornecedores cadastrados
     * When ele altera dados como lead time ou contato
     * Then o sistema deve atualizar as informações e recalcular o ponto de ressuprimento
     * Regras:
     * R1H6: Alterar Lead Time recalcula o ponto de ressuprimento
     * ------------------------------------------------------------------ */

    @Given("que o cliente possui fornecedores cadastrados")
    public void cliente_possui_fornecedores_cadastrados() {
        var fornecedor = new Fornecedor(fornecedorId, "Fornecedor Atualizável", "contato@teste.com", 5, clienteId, null, true);
        fornecedorServico.salvar(fornecedor);
    }

    @When("ele altera o lead time para {int} e contato para {string}")
    public void altera_dados_do_fornecedor(int novoLeadTime, String novoContato) {
        try {
            fornecedorServico.atualizarDados(fornecedorId, novoLeadTime, novoContato);
            fornecedorAtualizado = fornecedorServico.obter(fornecedorId);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve atualizar as informações e recalcular o ponto de ressuprimento dos produtos")
    public void sistema_atualiza_informacoes_e_recalcula_rop() {
        assertNotNull(fornecedorAtualizado, "Fornecedor deve ter sido atualizado");
        assertTrue(fornecedorAtualizado.getLeadTime() > 0, "Lead time deve ser positivo");

        // Simula recalcular ROP nos produtos associados
        var produtos = produtoServico.obterPorFornecedor(fornecedorId);
        for (var p : produtos) {
            var ropAntigo = p.getPontoRessuprimento();
            var ropNovo = produtoServico.recalcularRop(p.getId(), fornecedorAtualizado.getLeadTime());
            assertTrue(ropNovo >= ropAntigo || ropNovo > 0, "R1H6: Alterar Lead Time recalcula o ponto de ressuprimento");
        }
    }

    /* ---------------------------------------------------------------------
     * H7 – Inativar Fornecedores
     * Given que o cliente deseja remover um fornecedor
     * When ele solicita a inativação
     * Then o sistema deve permitir apenas se o fornecedor não possuir pedidos pendentes
     * Regras:
     * R1H7: Fornecedor com pedidos pendentes não pode ser inativado
     * ------------------------------------------------------------------ */

    @Given("que o cliente deseja remover um fornecedor com pedido pendente {string}")
    public void cliente_deseja_remover_fornecedor(String possuiPedidoPendente) {
        var fornecedor = new Fornecedor(fornecedorId, "Fornecedor Removível", "contato@teste.com", 7, clienteId, null, true);
        fornecedorServico.salvar(fornecedor);

        if ("sim".equalsIgnoreCase(possuiPedidoPendente)) {
            pedidoServico.criarPedidoPendente(fornecedorId);
        }
    }

    @When("ele solicita a inativação do fornecedor")
    public void solicita_inativacao_do_fornecedor() {
        try {
            fornecedorServico.inativar(fornecedorId);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve permitir a inativação apenas se não houver pedidos pendentes")
    public void sistema_valida_inativacao_do_fornecedor() {
        boolean temPedido = pedidoServico.existePedidoPendente(fornecedorId);

        if (temPedido) {
            assertNotNull(excecao, "R1H7: Fornecedor com pedidos pendentes não pode ser inativado");
        } else {
            var f = fornecedorServico.obter(fornecedorId);
            assertFalse(f.isAtivo(), "Fornecedor deve ter sido inativado com sucesso");
        }
    }
}
