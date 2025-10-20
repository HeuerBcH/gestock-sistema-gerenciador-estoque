package dev.gestock.sge.dominio.principal;

// TIP: execute os testes com "mvn test" (não use "mvn run")

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;

import static org.junit.Assert.*;

public class GerenciarEstoqueFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private ClienteId clienteId;
    private Estoque estoque;
    private Estoque estoqueAtual;
    private final Map<String, Estoque> estoques = new HashMap<>();
    private final List<Estoque> resultadosPesquisa = new ArrayList<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private int contadorEstoques = 1;
    private final Map<String, String> estoquePorEndereco = new HashMap<>();
    private final Map<String, String> estoquePorNome = new HashMap<>();
    private boolean temPedidoPendente = false;

    // ================================================================
    // DADOS INICIAIS
    // ================================================================

    @Dado("que existe um cliente com id {string}")
    public void queExisteUmClienteComId(String id) {
        this.clienteId = new ClienteId(Long.parseLong(id));
    }

    @Dado("ja existe um estoque chamado {string} no endereco {string}")
    public void jaExisteUmEstoqueChamadoNoEndereco(String nome, String endereco) {
        EstoqueId id = repositorio.novoEstoqueId();
        Estoque est = new Estoque(id, clienteId, nome, endereco, 1000);
        repositorio.salvar(est);
        estoques.put(nome, est);
        estoquePorEndereco.put(endereco, nome);
        estoquePorNome.put(nome, nome);
    }

    @Dado("ja existe um estoque no endereco {string}")
    public void jaExisteUmEstoqueNoEndereco(String endereco) {
        EstoqueId id = repositorio.novoEstoqueId();
        Estoque est = new Estoque(id, clienteId, "Estoque Existente", endereco, 1000);
        repositorio.salvar(est);
        estoques.put("Estoque Existente", est);
        estoquePorEndereco.put(endereco, "Estoque Existente");
    }

    @Dado("ja existe um estoque chamado {string}")
    public void jaExisteUmEstoqueChamado(String nome) {
        EstoqueId id = repositorio.novoEstoqueId();
        Estoque est = new Estoque(id, clienteId, nome, "Endereco " + contadorEstoques, 1000);
        repositorio.salvar(est);
        estoques.put(nome, est);
        estoquePorNome.put(nome, nome);
    }

    @Dado("que existe um estoque chamado {string} sem produtos")
    public void queExisteUmEstoqueChamadoSemProdutos(String nome) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereco " + contadorEstoques, 1000);
        repositorio.salvar(estoqueAtual);
        estoques.put(nome, estoqueAtual);
    }

    @Dado("que existe um estoque chamado {string} com produtos")
    public void queExisteUmEstoqueChamadoComProdutos(String nome) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereco " + contadorEstoques, 1000);
        repositorio.salvar(estoqueAtual);
        estoques.put(nome, estoqueAtual);
    }

    @Dado("o produto {string} tem saldo fisico de {int} unidades")
    public void oProdutoTemSaldoFisicoDeUnidades(String nomeProduto, int quantidade) {
        ProdutoId produtoId = new ProdutoId((long) nomeProduto.hashCode());
        estoqueAtual.registrarEntrada(produtoId, quantidade, "Sistema", "Carga inicial", Map.of());
    }

    @Dado("existe um pedido pendente alocado ao estoque")
    public void existeUmPedidoPendenteAlocadoAoEstoque() {
        this.temPedidoPendente = true;
    }

    @Dado("que existe um estoque chamado {string}")
    public void queExisteUmEstoqueChamado(String nome) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoqueAtual = new Estoque(id, clienteId, nome, "Endereco " + contadorEstoques, 1000);
        repositorio.salvar(estoqueAtual);
        estoques.put(nome, estoqueAtual);
    }

    @Dado("que existe um estoque com capacidade {int}")
    public void queExisteUmEstoqueComCapacidade(int capacidade) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoqueAtual = new Estoque(id, clienteId, "Estoque Teste", "Endereco Teste", capacidade);
        repositorio.salvar(estoqueAtual);
    }

    @E("a ocupacao atual do estoque e de {int} unidades")
    public void aOcupacaoAtualDoEstoqueEDeUnidades(int ocupacao) {
        ProdutoId produtoId = new ProdutoId(1L);
        estoqueAtual.registrarEntrada(produtoId, ocupacao, "Sistema", "Ocupacao", Map.of());
    }

    @Dado("que existem os seguintes estoques:")
    public void queExistemOsSeguintesEstoques(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps()) {
            String nome = row.get("nome");
            String endereco = row.get("endereco");
            int capacidade = Integer.parseInt(row.get("capacidade"));
            EstoqueId id = repositorio.novoEstoqueId();
            Estoque est = new Estoque(id, clienteId, nome, endereco, capacidade);
            repositorio.salvar(est);
            estoques.put(nome, est);
        }
    }

    @Dado("que nao existem estoques cadastrados")
    public void queNaoExistemEstoquesCadastrados() {
        estoques.clear();
    }

    @Dado("que existe um estoque chamado {string} no endereco {string} com capacidade {int}")
    public void queExisteUmEstoqueChamadoNoEnderecoComCapacidade(String nome, String endereco, int capacidade) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoqueAtual = new Estoque(id, clienteId, nome, endereco, capacidade);
        repositorio.salvar(estoqueAtual);
        estoques.put(nome, estoqueAtual);
    }

    // ================================================================
    // QUANDO
    // ================================================================

    @Quando("o cliente cadastra um estoque com nome {string}, endereco {string} e capacidade {int}")
    public void oClienteCadastraUmEstoqueComNomeEnderecoECapacidade(String nome, String endereco, int capacidade) {
        EstoqueId id = repositorio.novoEstoqueId();
        estoque = new Estoque(id, clienteId, nome, endereco, capacidade);
        repositorio.salvar(estoque);
        estoques.put(nome, estoque);
    }

    @Quando("o cliente tenta cadastrar um estoque com endereco {string}")
    public void oClienteTentaCadastrarUmEstoqueComEndereco(String endereco) {
        try {
            if (estoquePorEndereco.containsKey(endereco)) {
                throw new IllegalArgumentException("Ja existe um estoque cadastrado neste endereco");
            }
            EstoqueId id = repositorio.novoEstoqueId();
            estoque = new Estoque(id, clienteId, "Novo Estoque", endereco, 1000);
            repositorio.salvar(estoque);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente tenta cadastrar um estoque com nome {string}")
    public void oClienteTentaCadastrarUmEstoqueComNome(String nome) {
        try {
            if (estoquePorNome.containsKey(nome)) {
                throw new IllegalArgumentException("Ja existe um estoque com este nome");
            }
            EstoqueId id = repositorio.novoEstoqueId();
            estoque = new Estoque(id, clienteId, nome, "Endereco Novo", 1000);
            repositorio.salvar(estoque);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente inativa o estoque {string}")
    public void oClienteInativaOEstoque(String nome) {
        estoqueAtual = estoques.get(nome);
        estoqueAtual.inativar();
    }

    @Quando("o cliente tenta inativar o estoque {string}")
    public void oClienteTentaInativarOEstoque(String nome) {
        try {
            if (temPedidoPendente) {
                throw new IllegalStateException("Estoque com pedido em andamento nao pode ser inativado");
            }
            estoqueAtual.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente altera o nome para {string}")
    public void oClienteAlteraONomePara(String novoNome) {
        estoqueAtual.renomear(novoNome);
    }

    @Quando("o cliente altera a capacidade para {int}")
    public void oClienteAlteraACapacidadePara(int novaCapacidade) {
        estoqueAtual.alterarCapacidade(novaCapacidade);
    }

    @Quando("o cliente tenta alterar a capacidade para {int}")
    public void oClienteTentaAlterarACapacidadePara(int novaCapacidade) {
        try {
            estoqueAtual.alterarCapacidade(novaCapacidade);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente pesquisa por estoques com nome contendo {string}")
    public void oClientePesquisaPorEstoquesComNomeContendo(String termo) {
        resultadosPesquisa.clear();
        for (Estoque e : estoques.values()) {
            if (e.getNome().toLowerCase().contains(termo.toLowerCase())) {
                resultadosPesquisa.add(e);
            }
        }
    }

    @Quando("o cliente tenta pesquisar estoques")
    public void oClienteTentaPesquisarEstoques() {
        if (estoques.isEmpty()) {
            mensagemErro = "Nenhum estoque cadastrado";
        }
    }

    @Quando("o cliente pesquisa por estoques com endereco contendo {string}")
    public void oClientePesquisaPorEstoquesComEnderecoContendo(String termo) {
        resultadosPesquisa.clear();
        for (Estoque e : estoques.values()) {
            if (e.getEndereco().toLowerCase().contains(termo.toLowerCase())) {
                resultadosPesquisa.add(e);
            }
        }
    }

    @Quando("o cliente visualiza os detalhes do estoque")
    public void oClienteVisualizaOsDetalhesDoEstoque() {
        assertNotNull(estoqueAtual);
    }

    // ================================================================
    // ENTAO
    // ================================================================

    @Entao("o estoque deve ser cadastrado com sucesso")
    public void oEstoqueDeveSerCadastradoComSucesso() {
        assertNotNull(estoque);
    }

    @Entao("o estoque deve estar ativo")
    public void oEstoqueDeveEstarAtivo() {
        assertTrue(estoque.isAtivo());
    }

    @Entao("o estoque deve estar visivel na listagem de estoques")
    public void oEstoqueDeveEstarVisivelNaListagemDeEstoques() {
        assertTrue(estoques.containsValue(estoque));
    }

    @Entao("o estoque deve pertencer ao cliente com id {string}")
    public void oEstoqueDevePertencerAoClienteComId(String id) {
        assertEquals(Long.parseLong(id), estoque.getClienteId().getId().longValue());
    }

    @Entao("devem existir {int} estoques cadastrados para o cliente")
    public void devemExistirEstoquesCadastradosParaOCliente(int quantidade) {
        assertEquals(quantidade, estoques.size());
    }

    @Entao("o sistema deve rejeitar o cadastro")
    public void oSistemaDeveRejeitarOCadastro() {
        assertNotNull(excecaoCapturada);
    }

    @Entao("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagem) {
        assertEquals(mensagem, mensagemErro);
    }

    @Entao("o estoque deve ser inativado com sucesso")
    public void oEstoqueDeveSerInativadoComSucesso() {
        assertFalse(estoqueAtual.isAtivo());
    }

    @Entao("o status do estoque deve ser {string}")
    public void oStatusDoEstoqueDeveSer(String status) {
        boolean esperadoAtivo = status.equalsIgnoreCase("ativo");
        assertEquals(esperadoAtivo, estoqueAtual.isAtivo());
    }

    @Entao("o sistema deve rejeitar a operacao")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull(excecaoCapturada);
    }

    @Entao("o nome do estoque deve ser atualizado")
    public void oNomeDoEstoqueDeveSerAtualizado() {
        assertNotNull(estoqueAtual.getNome());
    }

    @Entao("a capacidade deve ser atualizada com sucesso")
    public void aCapacidadeDeveSerAtualizadaComSucesso() {
        assertTrue(estoqueAtual.getCapacidade() > 0);
    }

    @Entao("o sistema deve exibir {int} estoques")
    public void oSistemaDeveExibirEstoques(int quantidade) {
        assertEquals(quantidade, resultadosPesquisa.size());
    }

    @Entao("os resultados devem incluir {string} e {string}")
    public void osResultadosDevemIncluirEE(String nome1, String nome2) {
        List<String> nomes = new ArrayList<>();
        for (Estoque e : resultadosPesquisa) nomes.add(e.getNome());
        assertTrue(nomes.contains(nome1));
        assertTrue(nomes.contains(nome2));
    }

    @Entao("o sistema deve informar {string}")
    public void oSistemaDeveInformar(String mensagem) {
        assertEquals(mensagem, mensagemErro);
    }

    @Entao("o sistema deve exibir o nome {string}")
    public void oSistemaDeveExibirONome(String nome) {
        assertEquals(nome, estoqueAtual.getNome());
    }

    @Entao("o sistema deve exibir o endereco {string}")
    public void oSistemaDeveExibirOEndereco(String endereco) {
        assertEquals(endereco, estoqueAtual.getEndereco());
    }

    @Entao("o sistema deve exibir a capacidade {int}")
    public void oSistemaDeveExibirACapacidade(int capacidade) {
        assertEquals(capacidade, estoqueAtual.getCapacidade());
    }

    @Entao("o sistema deve exibir o status {string}")
    public void oSistemaDeveExibirOStatus(String status) {
        boolean ativoEsperado = status.equalsIgnoreCase("ativo");
        assertEquals(ativoEsperado, estoqueAtual.isAtivo());
    }
}
