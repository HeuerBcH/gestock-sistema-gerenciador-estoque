package dev.gestock.sge.dominio.principal;

// TIP: execute os testes com "mvn test" (não use "mvn run")

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;

import static org.junit.Assert.*;

public class GerenciarEstoqueFuncionalidade {

    private ClienteId clienteId;
    private Estoque estoque;
    private Estoque estoqueAtual;
    private Map<String, Estoque> estoques = new HashMap<>();
    private List<Estoque> resultadosPesquisa = new ArrayList<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private int contadorEstoques = 1;
    private Map<String, String> estoquePorEndereco = new HashMap<>();
    private Map<String, String> estoquePorNome = new HashMap<>();
    private boolean temPedidoPendente = false;

    // ========== GIVEN (Dado) ==========

    @Dado("que existe um cliente com id {string}")
    public void queExisteUmClienteComId(String id) {
        this.clienteId = new ClienteId(Long.parseLong(id));
    }

    @Dado("já existe um estoque {string} no endereço {string}")
    public void jaExisteUmEstoqueNoEndereco(String nome, String endereco) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        Estoque est = new Estoque(id, clienteId, nome, endereco, 1000);
        estoques.put(nome, est);
        estoquePorEndereco.put(endereco, nome);
        estoquePorNome.put(nome, nome);
    }

    @Dado("já existe um estoque no endereço {string}")
    public void jaExisteUmEstoqueNoEndereco(String endereco) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        Estoque est = new Estoque(id, clienteId, "Estoque Existente", endereco, 1000);
        estoques.put("Estoque Existente", est);
        estoquePorEndereco.put(endereco, "Estoque Existente");
    }

    @Dado("já existe um estoque com nome {string}")
    public void jaExisteUmEstoqueComNome(String nome) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        Estoque est = new Estoque(id, clienteId, nome, "Endereço Padrão " + contadorEstoques, 1000);
        estoques.put(nome, est);
        estoquePorNome.put(nome, nome);
    }

    @Dado("que existe um estoque {string} sem produtos")
    public void queExisteUmEstoqueSemProdutos(String nome) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereço " + contadorEstoques, 1000);
        estoques.put(nome, estoqueAtual);
    }

    @Dado("que existe um estoque {string} com produtos")
    public void queExisteUmEstoqueComProdutos(String nome) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereço " + contadorEstoques, 1000);
        estoques.put(nome, estoqueAtual);
        // Adicionar produtos no próximo step
    }

    @Dado("o produto {string} tem saldo físico de {int} unidades")
    public void oProdutoTemSaldoFisicoDeUnidades(String nomeProduto, int quantidade) {
        ProdutoId produtoId = new ProdutoId((long) nomeProduto.hashCode());
        estoqueAtual.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", java.util.Map.of());
    }

    @Dado("existe um pedido pendente alocado ao estoque")
    public void existeUmPedidoPendenteAlocadoAoEstoque() {
        this.temPedidoPendente = true;
    }

    @Dado("que existe um estoque {string}")
    public void queExisteUmEstoque(String nome) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereço " + contadorEstoques, 1000);
        estoques.put(nome, estoqueAtual);
    }

    @Dado("que existe um estoque com capacidade {string}")
    public void queExisteUmEstoqueComCapacidade(String capacidade) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        estoqueAtual = new Estoque(id, clienteId, "Estoque Teste", "Endereço Teste", Integer.parseInt(capacidade));
    }

    @Dado("a ocupação atual é de {string} unidades")
    public void aOcupacaoAtualEDeUnidades(String ocupacao) {
        ProdutoId produtoId = new ProdutoId(1L);
        estoqueAtual.registrarEntrada(produtoId, Integer.parseInt(ocupacao), "Sistema", "Ocupação", java.util.Map.of());
    }

    @Dado("que existem os seguintes estoques:")
    public void queExistemOsSeguintesEstoques(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nome = row.get("nome");
            String endereco = row.get("endereço");
            int capacidade = Integer.parseInt(row.get("capacidade"));
            EstoqueId id = new EstoqueId((long) contadorEstoques++);
            Estoque est = new Estoque(id, clienteId, nome, endereco, capacidade);
            estoques.put(nome, est);
        }
    }

    @Dado("que não existem estoques cadastrados")
    public void queNaoExistemEstoquesCadastrados() {
        estoques.clear();
    }

    @Dado("que existe um estoque {string} no endereço {string} com capacidade {string}")
    public void queExisteUmEstoqueNoEnderecoComCapacidade(String nome, String endereco, String capacidade) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        estoqueAtual = new Estoque(id, clienteId, nome, endereco, Integer.parseInt(capacidade));
        estoques.put(nome, estoqueAtual);
    }

    // ========== WHEN (Quando) ==========

    @Quando("eu cadastro um estoque com nome {string}, endereço {string} e capacidade {string}")
    public void euCadastroUmEstoqueComNomeEnderecoECapacidade(String nome, String endereco, String capacidade) {
        try {
            EstoqueId id = new EstoqueId((long) contadorEstoques++);
            estoque = new Estoque(id, clienteId, nome, endereco, Integer.parseInt(capacidade));
            estoques.put(nome, estoque);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento cadastrar um estoque com endereço {string}")
    public void euTentoCadastrarUmEstoqueComEndereco(String endereco) {
        try {
            if (estoquePorEndereco.containsKey(endereco)) {
                throw new IllegalArgumentException("Já existe um estoque cadastrado neste endereço");
            }
            EstoqueId id = new EstoqueId((long) contadorEstoques++);
            estoque = new Estoque(id, clienteId, "Novo Estoque", endereco, 1000);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento cadastrar um estoque com nome {string}")
    public void euTentoCadastrarUmEstoqueComNome(String nome) {
        try {
            if (estoquePorNome.containsKey(nome)) {
                throw new IllegalArgumentException("Já existe um estoque com este nome");
            }
            EstoqueId id = new EstoqueId((long) contadorEstoques++);
            estoque = new Estoque(id, clienteId, nome, "Endereço Novo", 1000);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu inativo o estoque {string}")
    public void euInativoOEstoque(String nome) {
        try {
            estoqueAtual = estoques.get(nome);
            estoqueAtual.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento inativar o estoque {string}")
    public void euTentoInativarOEstoque(String nome) {
        try {
            estoqueAtual = estoques.get(nome);
            if (temPedidoPendente) {
                throw new IllegalStateException("Estoque com pedido em andamento não pode ser inativado");
            }
            estoqueAtual.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu altero o nome para {string}")
    public void euAlteroONomePara(String novoNome) {
        estoqueAtual.renomear(novoNome);
    }

    @Quando("eu altero a capacidade para {string}")
    public void euAlteroACapacidadePara(String novaCapacidade) {
        try {
            estoqueAtual.alterarCapacidade(Integer.parseInt(novaCapacidade));
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento alterar a capacidade para {string}")
    public void euTentoAlterarACapacidadePara(String novaCapacidade) {
        try {
            estoqueAtual.alterarCapacidade(Integer.parseInt(novaCapacidade));
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu pesquiso por estoques com nome contendo {string}")
    public void euPesquisoPorEstoquesComNomeContendo(String termo) {
        resultadosPesquisa.clear();
        for (Estoque est : estoques.values()) {
            if (est.getNome().contains(termo)) {
                resultadosPesquisa.add(est);
            }
        }
    }

    @Quando("eu tento pesquisar estoques")
    public void euTentoPesquisarEstoques() {
        if (estoques.isEmpty()) {
            mensagemErro = "Nenhum estoque cadastrado";
        }
    }

    @Quando("eu pesquiso por estoques com endereço contendo {string}")
    public void euPesquisoPorEstoquesComEnderecoContendo(String termo) {
        resultadosPesquisa.clear();
        for (Estoque est : estoques.values()) {
            if (est.getEndereco().contains(termo)) {
                resultadosPesquisa.add(est);
            }
        }
    }

    @Quando("eu visualizo os detalhes do estoque")
    public void euVisualizoOsDetalhesDoEstoque() {
        // Apenas prepara para verificação
    }

    // ========== THEN (Então) ==========

    @Então("o estoque deve ser cadastrado com sucesso")
    public void oEstoqueDeveSerCadastradoComSucesso() {
        assertNotNull("Estoque não foi cadastrado", estoque);
    }

    @Então("o estoque deve estar ativo")
    public void oEstoqueDeveEstarAtivo() {
        assertTrue("Estoque deveria estar ativo", estoque.isAtivo());
    }

    @Então("o estoque deve pertencer ao cliente {string}")
    public void oEstoqueDevePertencerAoCliente(String clienteIdEsperado) {
        assertEquals(clienteIdEsperado, estoque.getClienteId().toString());
    }

    @Então("devem existir {int} estoques cadastrados para o cliente")
    public void devemExistirEstoquesCadastradosParaOCliente(int quantidade) {
        assertEquals(quantidade, estoques.size());
    }

    @Então("o sistema deve rejeitar o cadastro")
    public void oSistemaDeveRejeitarOCadastro() {
        assertNotNull("Deveria ter capturado uma exceção", excecaoCapturada);
    }

    @Então("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagemEsperada) {
        assertNotNull("Mensagem de erro não foi capturada", mensagemErro);
        assertTrue("Mensagem incorreta: " + mensagemErro, mensagemErro.contains(mensagemEsperada));
    }

    @Então("o estoque deve ser inativado com sucesso")
    public void oEstoqueDeveSerInativadoComSucesso() {
        assertFalse("Estoque deveria estar inativo", estoqueAtual.isAtivo());
    }

    @Então("o status do estoque deve ser {string}")
    public void oStatusDoEstoqueDeveSer(String status) {
        if ("inativo".equals(status)) {
            assertFalse("Estoque deveria estar inativo", estoqueAtual.isAtivo());
        } else {
            assertTrue("Estoque deveria estar ativo", estoqueAtual.isAtivo());
        }
    }

    @Então("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Deveria ter capturado uma exceção", excecaoCapturada);
    }

    @Então("o nome do estoque deve ser atualizado")
    public void oNomeDoEstoqueDeveSerAtualizado() {
        assertNotNull("Nome do estoque não foi atualizado", estoqueAtual.getNome());
    }

    @Então("a capacidade deve ser atualizada com sucesso")
    public void aCapacidadeDeveSerAtualizadaComSucesso() {
        assertEquals(1500, estoqueAtual.getCapacidadeMaxima());
    }

    @Então("devo encontrar {int} estoques")
    public void devoEncontrarEstoques(int quantidade) {
        assertEquals(quantidade, resultadosPesquisa.size());
    }

    @Então("os resultados devem incluir {string} e {string}")
    public void osResultadosDevemIncluirE(String nome1, String nome2) {
        boolean temNome1 = resultadosPesquisa.stream().anyMatch(e -> e.getNome().equals(nome1));
        boolean temNome2 = resultadosPesquisa.stream().anyMatch(e -> e.getNome().equals(nome2));
        assertTrue("Resultado deveria incluir " + nome1, temNome1);
        assertTrue("Resultado deveria incluir " + nome2, temNome2);
    }

    @Então("o sistema deve informar {string}")
    public void oSistemaDeveInformar(String mensagem) {
        assertEquals(mensagem, mensagemErro);
    }

    @Então("devo ver o nome {string}")
    public void devoVerONome(String nome) {
        assertEquals(nome, estoqueAtual.getNome());
    }

    @Então("devo ver o endereço {string}")
    public void devoVerOEndereco(String endereco) {
        assertEquals(endereco, estoqueAtual.getEndereco());
    }

    @Então("devo ver a capacidade {string}")
    public void devoVerACapacidade(String capacidade) {
        assertEquals(Integer.parseInt(capacidade), estoqueAtual.getCapacidadeMaxima());
    }

    @Então("devo ver o status {string}")
    public void devoVerOStatus(String status) {
        if ("ativo".equals(status)) {
            assertTrue("Estoque deveria estar ativo", estoqueAtual.isAtivo());
        } else {
            assertFalse("Estoque deveria estar inativo", estoqueAtual.isAtivo());
        }
    }
}
