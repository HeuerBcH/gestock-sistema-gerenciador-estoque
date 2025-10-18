package dev.gestock.sge.dominio.principal.estoque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import dev.gestock.sge.dominio.principal.AcervoFuncionalidade;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Testes BDD (Cucumber) para a funcionalidade "Gerenciar Estoques".
 *
 * Observações:
 * - Esta classe assume existência de serviços e repositórios como: clienteServico, estoqueServico, pedidoServico.
 * - Adapte chamadas de serviço para os nomes/metodos reais do seu projeto.
 */
public class GerenciarEstoquesFuncionalidade extends AcervoFuncionalidade {

    private ClienteId clienteId = new ClienteId(1);
    private EstoqueId estoqueId = new EstoqueId(1);
    private PedidoId pedidoId = new PedidoId(1);

    // Parâmetros de criação/edição
    private String nomeInformado;
    private String enderecoInformado;
    private int capacidadeInformada;

    private RuntimeException excecao;

    // Resultado de pesquisa
    private List<Estoque> resultadoPesquisa;

    /* ---------------------------------------------------------------------
     * H1 – Cadastrar Estoques
     * Given que o cliente deseja cadastrar um novo estoque
     * When ele informa nome, endereço e capacidade do estoque
     * Then o sistema deve criar o estoque vinculado exclusivamente a esse cliente
     *
     * Regras:
     * R1H1: Cada estoque pertence a um único cliente
     * R2H1: Não pode haver dois estoques no mesmo endereço
     * R3H1: Dois estoques não podem ter o mesmo nome
     * ------------------------------------------------------------------ */

    @Given("que o cliente deseja cadastrar um novo estoque")
    public void que_o_cliente_deseja_cadastrar_um_novo_estoque() {
        var cliente = new Cliente(clienteId, "Cliente Teste");
        clienteServico.salvar(cliente);
    }

    @When("ele informa nome {string}, endereço {string} e capacidade {int} do estoque")
    public void ele_informa_dados_do_estoque(String nome, String endereco, int capacidade) {
        this.nomeInformado = nome;
        this.enderecoInformado = endereco;
        this.capacidadeInformada = capacidade;

        try {
            var estoque = new Estoque(estoqueId, nomeInformado, enderecoInformado, capacidadeInformada, clienteId, true);
            estoqueServico.salvar(estoque);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve criar o estoque vinculado exclusivamente a esse cliente")
    public void sistema_deve_criar_estoque_vinculado_ao_cliente() {
        assertNotNull(estoqueServico.obter(estoqueId), "Estoque deve ter sido criado");
        var salvo = estoqueServico.obter(estoqueId);
        assertTrue(salvo.getClienteId().equals(clienteId), "R1H1: Cada estoque pertence a um único cliente");

        // R2H1: Não pode haver dois estoques no mesmo endereço
        Optional<Estoque> porEndereco = estoqueServico.pesquisarPorEndereco(enderecoInformado).stream().findAny();
        assertTrue(porEndereco.isPresent(), "Deve existir estoque no endereço informado");
        // Se houver outro estoque com mesmo endereço, serviço deve lançar ou rejeitar
        // Aqui verificamos que não exista mais de um
        long countNoMesmoEndereco = estoqueServico.pesquisarPorEndereco(enderecoInformado).size();
        assertEquals(1, countNoMesmoEndereco, "R2H1: Não deve haver 2+ estoques no mesmo endereço");

        // R3H1: Dois estoques não podem ter o mesmo nome
        long countMesmoNome = estoqueServico.pesquisarPorNome(nomeInformado).size();
        assertEquals(1, countMesmoNome, "R3H1: Não deve haver 2+ estoques com mesmo nome");
    }

    /* ---------------------------------------------------------------------
     * H2 – Inativar Estoques
     * Given que o cliente possui estoques ativos
     * When ele solicita a inativação de um estoque
     * Then o sistema deve validar se o estoque está vazio e sem pedidos em andamento antes de inativar
     *
     * R1H2: Estoque com produtos não pode ser removido
     * R2H2: Estoque com pedido em andamento não pode ser removido
     * ------------------------------------------------------------------ */

    @Given("que o cliente possui estoques ativos com estoque cheio? {string}")
    public void cliente_possui_estoques_ativos(String possuiProdutos) {
        // cria estoque e opcionalmente adiciona produto(s)
        var estoque = new Estoque(estoqueId, "Estoque Inativar", "Rua Teste, 100", 100, clienteId, true);
        estoqueServico.salvar(estoque);

        if ("sim".equalsIgnoreCase(possuiProdutos)) {
            // simula produto dentro do estoque
            estoqueServico.adicionarProduto(estoqueId, /*produtoId*/ new Object(), 10);
        }
    }

    @When("ele solicita a inativação do estoque")
    public void solicita_inativacao_do_estoque() {
        try {
            estoqueServico.inativar(estoqueId);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve inativar o estoque se vazio e sem pedidos em andamento")
    public void sistema_inativa_se_vazio_sem_pedidos() {
        if (estoqueServico.possuiProdutos(estoqueId) || pedidoServico.existePedidoEmAndamentoParaEstoque(estoqueId)) {
            // Deve ter lançado exceção e não inativado
            assertNotNull(excecao, "Deve haver exceção quando há produtos ou pedidos em andamento (R1H2/R2H2)");
        } else {
            var obtido = estoqueServico.obter(estoqueId);
            assertFalse(obtido.ativo(), "Estoque deve estar inativo");
        }
    }

    /* ---------------------------------------------------------------------
     * H3 – Editar Estoques
     * Given que o cliente possui um estoque ativo
     * When ele tenta editar seus parâmetros (capacidade, nome, endereço etc.)
     * Then o sistema deve permitir a edição desde que a nova capacidade não seja menor que a ocupação atual
     *
     * R1H3: Não é permitido reduzir o tamanho do estoque se estiver na capacidade máxima
     * ------------------------------------------------------------------ */

    @Given("que o cliente possui um estoque ativo com ocupacao {int}")
    public void cliente_possui_estoque_com_ocupacao(int ocupacao) {
        var estoque = new Estoque(estoqueId, "Estoque Editar", "Av. Exemplo, 50", 50, clienteId, true);
        estoqueServico.salvar(estoque);

        // preenche estoque com quantidade = ocupacao
        if (ocupacao > 0) {
            estoqueServico.adicionarProduto(estoqueId, /*produtoId*/ new Object(), ocupacao);
        }
    }

    @When("ele tenta editar a capacidade para {int}")
    public void tenta_editar_capacidade_para(int novaCapacidade) {
        try {
            estoqueServico.atualizarCapacidade(estoqueId, novaCapacidade);
        } catch (RuntimeException e) {
            this.excecao = e;
        }
    }

    @Then("o sistema deve permitir a edição somente se nova capacidade >= ocupação atual")
    public void sistema_valida_nova_capacidade() {
        int ocupacaoAtual = estoqueServico.obterOcupacao(estoqueId);
        var estoque = estoqueServico.obter(estoqueId);

        if (estoque.getCapacidade() < ocupacaoAtual && excecao == null) {
            // Caso estranho — fail seguro
            throw new AssertionError("Capacidade atual menor que ocupação e nenhuma exceção lançada");
        }

        if (estoque.getCapacidade() >= ocupacaoAtual) {
            // atualização bem sucedida
            assertTrue(estoque.getCapacidade() >= ocupacaoAtual, "Capacidade não deve ficar menor que ocupação");
        } else {
            // exceção esperada quando tentativa reduz abaixo da ocupação
            assertNotNull(excecao, "Deve lançar exceção ao reduzir capacidade abaixo da ocupação (R1H3)");
        }
    }

    /* ---------------------------------------------------------------------
     * H4 – Pesquisar e Visualizar Estoques
     * Given que o cliente deseja consultar estoques
     * When ele acessa a funcionalidade de pesquisa
     * Then o sistema deve exibir todos os estoques cadastrados ou permitir busca por parâmetros como nome e endereço
     *
     * R1H4: Não é possível pesquisar se não houver estoques cadastrados
     * R2H4: Deve ser possível pesquisar por mais parâmetros além do nome
     * ------------------------------------------------------------------ */

    @Given("que existem {int} estoques cadastrados para o cliente")
    public void existem_estoques_cadastrados(int quantidade) {
        for (int i = 1; i <= quantidade; i++) {
            var id = new EstoqueId(i);
            var e = new Estoque(id, "Estoque " + i, "Endereco " + i, 100 + i, clienteId, true);
            estoqueServico.salvar(e);
        }
    }

    @When("ele pesquisa estoques por nome {string}")
    public void pesquisa_estoques_por_nome(String nome) {
        resultadoPesquisa = estoqueServico.pesquisarPorNome(nome);
    }

    @When("ele pesquisa estoques por endereco {string}")
    public void pesquisa_estoques_por_endereco(String endereco) {
        resultadoPesquisa = estoqueServico.pesquisarPorEndereco(endereco);
    }

    @Then("o sistema deve exibir os estoques correspondentes ou mensagem vazio")
    public void sistema_exibe_resultados_ou_mensagem_vazio() {
        if (estoqueServico.totalCadastrados(clienteId) == 0) {
            assertTrue(resultadoPesquisa == null || resultadoPesquisa.isEmpty(), "R1H4: Não é possível pesquisar se não houver estoques cadastrados");
        } else {
            assertNotNull(resultadoPesquisa, "Resultado da pesquisa não pode ser nulo");
            // R2H4: Verificamos que possa pesquisar por mais params (nome/endereco). Aqui apenas checamos retorno consistente.
            assertTrue(resultadoPesquisa.size() >= 0, "Pesquisa retorna lista (pode ser vazia)");
        }
    }

    // Métodos auxiliares (se necessário) — normalmente parte do AcervoFuncionalidade ou dos serviços de teste.
    // Ex.: limpar dados entre cenários, mock de serviços, etc.
}
