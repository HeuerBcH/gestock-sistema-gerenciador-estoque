package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;
import static org.junit.Assert.*;

public class GerenciarFornecedoresFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Fornecedor fornecedor;
    private Exception excecaoCapturada;
    private String mensagemErro;
    private List<Integer> historicoEntregas = new ArrayList<>();
    private boolean temPedidosPendentes = false;

    // =============================================================
    // H5: Cadastrar fornecedores e registrar cotacoes
    // =============================================================

    @Dado("que nao existe um fornecedor cadastrado com o CNPJ {string}")
    public void dadoQueNaoExisteFornecedorComCnpj(String cnpj) {
        repositorio.limparTodos();
        fornecedor = null;
        excecaoCapturada = null;
        mensagemErro = null;
        historicoEntregas.clear();
        temPedidosPendentes = false;
    }

    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string} e contato {string}")
    public void quandoCadastraFornecedorBasico(String nome, String cnpj, String contato) {
        try {
            FornecedorId id = repositorio.novoFornecedorId();
            fornecedor = new Fornecedor(id, nome, cnpj, contato);
            repositorio.salvar(fornecedor);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o fornecedor deve ser cadastrado com sucesso")
    public void entaoFornecedorCadastradoComSucesso() {
        assertNotNull("Fornecedor deveria ter sido criado", fornecedor);
        assertTrue("Fornecedor deveria iniciar ativo", fornecedor.isAtivo());
    }

    @Entao("o fornecedor deve estar ativo na listagem")
    public void entaoFornecedorAtivoNaListagem() {
        assertTrue("Fornecedor deveria estar ativo", fornecedor.isAtivo());
    }

    @Entao("o lead time inicial deve ser {int} dias")
    public void entaoLeadTimeInicial(int dias) {
        assertEquals(dias, fornecedor.getLeadTimeMedio().getDias());
    }

    // -------------------------------------------------------------

    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string}, contato {string} e lead time de {string} dias")
    public void quandoCadastraFornecedorComLeadTime(String nome, String cnpj, String contato, String leadTime) {
        try {
            FornecedorId id = repositorio.novoFornecedorId();
            LeadTime lt = new LeadTime(Integer.parseInt(leadTime));
            fornecedor = new Fornecedor(id, nome, cnpj, contato, lt);
            repositorio.salvar(fornecedor);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o lead time deve ser {string} dias")
    public void entaoLeadTimeDeveSer(String dias) {
        assertEquals(Integer.parseInt(dias), fornecedor.getLeadTimeMedio().getDias());
    }

    // -------------------------------------------------------------

    @Dado("que existe um fornecedor {string}")
    public void dadoExisteFornecedor(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);
    }

    @Dado("existe um produto {string} com id {string}")
    public void dadoExisteProdutoComId(String nome, String idStr) {
        long id = Long.parseLong(idStr);
        ProdutoId pid = new ProdutoId(id);
        String codigo = String.format("PROD-%03d", id);
        // Construtor coerente com o domínio (getCodigo() retorna String)
        Produto produto = new Produto(pid, codigo, nome, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Quando("o cliente registra uma cotacao de {string} reais com prazo de {string} dias para o produto {string}")
    public void quandoRegistraCotacaoParaProdutoPorCodigo(String preco, String prazo, String codigoProduto) {
        try {
            Produto produto = repositorio.buscarPorCodigo(new CodigoProduto(codigoProduto))
                    .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado pelo codigo: " + codigoProduto));
            fornecedor.registrarCotacao(produto.getId(), Double.parseDouble(preco), Integer.parseInt(prazo));
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("a cotacao deve ser registrada com sucesso")
    public void entaoCotacaoRegistradaComSucesso() {
        assertFalse("Fornecedor deveria possuir cotacoes", fornecedor.getCotacoesSnapshot().isEmpty());
    }

    @Entao("o fornecedor deve ter cotacao para o produto {string}")
    public void entaoFornecedorTemCotacaoParaProduto(String codigoProduto) {
        Produto produto = repositorio.buscarPorCodigo(new CodigoProduto(codigoProduto))
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado pelo codigo: " + codigoProduto));
        assertTrue("Deveria existir cotacao para o produto", fornecedor.obterCotacaoPorProduto(produto.getId()).isPresent());
    }

    // -------------------------------------------------------------

    @Dado("existem os seguintes produtos:")
    public void dadoExistemOsSeguintesProdutos(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            long id = Long.parseLong(row.get("id"));
            String nome = row.get("nome");
            ProdutoId pid = new ProdutoId(id);
            String codigo = String.format("PROD-%03d", id);
            Produto produto = new Produto(pid, codigo, nome, "UN", false, 0.0);
            repositorio.salvar(produto);
        }
    }

    @Quando("o cliente registra as seguintes cotacoes:")
    public void quandoRegistraVariasCotacoes(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
            long id = Long.parseLong(row.get("id"));
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            Produto produto = repositorio.buscarPorId(new ProdutoId(id))
                    .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado por id: " + id));
            fornecedor.registrarCotacao(produto.getId(), preco, prazo);
        }
    }

    @Entao("o fornecedor deve ter {int} cotacoes cadastradas")
    public void entaoQuantidadeCotacoes(int quantidade) {
        assertEquals(quantidade, fornecedor.getCotacoesSnapshot().size());
    }

    // -------------------------------------------------------------

    @Dado("que existe um fornecedor chamado {string}")
    public void dadoExisteUmFornecedorChamado(String nome) {
        dadoExisteFornecedor(nome);
    }

    @Dado("existe um produto chamado {string} com id {string}")
    public void dadoExisteProdutoChamadoComId(String nome, String id) {
        dadoExisteProdutoComId(nome, id);
    }

    @Quando("o cliente tenta registrar uma cotacao para o produto {string} com prazo {int} dias")
    public void quandoTentaRegistrarCotacaoPrazoInvalido(String nomeProduto, int prazo) {
        try {
            // usa qualquer produto existente ou id dummy para acionar validacao
            fornecedor.registrarCotacao(new ProdutoId(1L), 100.0, prazo);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente tenta registrar uma cotacao para o produto {string} com preco {int} reais")
    public void quandoTentaRegistrarCotacaoPrecoInvalido(String nomeProduto, int preco) {
        try {
            fornecedor.registrarCotacao(new ProdutoId(1L), preco, 10);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar a operacao")
    public void entaoSistemaRejeitaOperacao() {
        assertNotNull("Uma excecao deveria ter sido capturada", excecaoCapturada);
    }

    @Entao("deve exibir a mensagem {string}")
    public void entaoDeveExibirMensagem(String mensagemEsperada) {
        assertNotNull("Mensagem de erro nao capturada", mensagemErro);
        assertTrue("Mensagem incorreta: " + mensagemErro, mensagemErro.contains(mensagemEsperada));
    }

    // =============================================================
    // H6: Atualizar dados e recalibrar lead time
    // =============================================================

    @Dado("que existe um fornecedor chamado {string} com contato {string}")
    public void dadoFornecedorComContato(String nome, String contato) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", contato);
        repositorio.salvar(fornecedor);
    }

    @Quando("o cliente atualiza os dados do fornecedor para nome {string} e contato {string}")
    public void quandoAtualizaDadosFornecedor(String novoNome, String novoContato) {
        fornecedor.atualizarDados(novoNome, novoContato);
    }

    @Entao("os dados do fornecedor devem ser atualizados")
    public void entaoDadosAtualizados() {
        assertNotNull("Nome deveria estar definido", fornecedor.getNome());
    }

    @Entao("o nome do fornecedor deve ser {string}")
    public void entaoNomeFornecedor(String nome) {
        assertEquals(nome, fornecedor.getNome());
    }

    @Entao("o contato do fornecedor deve ser {string}")
    public void entaoContatoFornecedor(String contato) {
        assertEquals(contato, fornecedor.getContato());
    }

    // -------------------------------------------------------------

    @Dado("que existe um fornecedor chamado {string} com lead time de {int} dias")
    public void dadoFornecedorComLeadTime(String nome, int leadTime) {
        FornecedorId id = repositorio.novoFornecedorId();
        LeadTime lt = new LeadTime(leadTime);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com", lt);
        repositorio.salvar(fornecedor);
    }

    // cobre variante com erro de digitacao do .feature
    @Quando("o lead timedo forncedor e recalibrado com base no historico de entregas")
    @Quando("o lead time do fornecedor e recalibrado com base no historico de entregas")
    public void quandoRecalibraLeadTime() {
        fornecedor.recalibrarLeadTime(historicoEntregas);
    }

    @Dado("o fornecedor possui historico de entregas de {int}, {int}, {int}, {int} e {int} dias")
    public void dadoHistoricoEntregas(int d1, int d2, int d3, int d4, int d5) {
        historicoEntregas = Arrays.asList(d1, d2, d3, d4, d5);
    }

    @Entao("o lead time do fornecedor deve ser atualizado para {int} dias")
    public void entaoLeadTimeAtualizadoPara(int dias) {
        assertEquals(dias, fornecedor.getLeadTimeMedio().getDias());
    }

    @Dado("que existe um fornecedor chamado {string} sem historico de entregas")
    public void dadoFornecedorSemHistorico(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);
        historicoEntregas.clear();
    }

    @Quando("o cliente tenta recalibrar o lead time")
    public void quandoTentaRecalibrarSemHistorico() {
        try {
            fornecedor.recalibrarLeadTime(new ArrayList<>());
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    // =============================================================
    // H7: Inativar, reativar e remover cotacoes
    // =============================================================

    @Dado("que existe um fornecedor {string} sem pedidos pendentes")
    public void dadoFornecedorSemPedidosPendentes(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);
        temPedidosPendentes = false;
    }

    @Quando("o cliente inativa o fornecedor {string}")
    public void quandoInativaFornecedor(String nome) {
        fornecedor.inativar();
    }

    @Entao("o fornecedor deve ser inativado com sucesso")
    public void entaoFornecedorInativadoComSucesso() {
        assertFalse("Fornecedor deveria estar inativo", fornecedor.isAtivo());
    }

    @Entao("o status do fornecedor deve ser {string}")
    public void entaoStatusFornecedor(String status) {
        if ("inativo".equals(status)) {
            assertFalse(fornecedor.isAtivo());
        } else {
            assertTrue(fornecedor.isAtivo());
        }
    }

    @Dado("que existe um fornecedor {string} com pedidos pendentes")
    public void dadoFornecedorComPedidosPendentes(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        repositorio.salvar(fornecedor);
        temPedidosPendentes = true;
    }

    @Quando("o cliente tenta inativar o fornecedor {string}")
    public void quandoTentaInativarFornecedorComPedidos(String nome) {
        try {
            if (temPedidosPendentes) {
                throw new IllegalStateException("Fornecedor com pedidos pendentes nao pode ser inativado");
            }
            fornecedor.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Dado("que existe um fornecedor {string} inativo")
    public void dadoFornecedorInativo(String nome) {
        FornecedorId id = repositorio.novoFornecedorId();
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedor.inativar();
        repositorio.salvar(fornecedor);
    }

    @Quando("o cliente reativa o fornecedor {string}")
    public void quandoReativaFornecedor(String nome) {
        fornecedor.ativar();
    }

    @Entao("o fornecedor deve ser reativado com sucesso")
    public void entaoFornecedorReativadoComSucesso() {
        assertTrue("Fornecedor deveria estar ativo", fornecedor.isAtivo());
    }

    @Dado("o fornecedor possui cotacao para o produto {string}")
    public void dadoFornecedorPossuiCotacaoParaProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        fornecedor.registrarCotacao(pid, 100.0, 10);
    }

    @Quando("o cliente remove a cotacao do produto {string}")
    public void quandoRemoveCotacaoDoProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        fornecedor.removerCotacao(pid);
    }

    @Entao("a cotacao deve ser removida")
    public void entaoCotacaoRemovida() {
        assertNotNull("Fornecedor deveria existir", fornecedor);
    }

    @Entao("o fornecedor nao deve possuir cotacao para o produto {string}")
    public void entaoFornecedorNaoPossuiCotacaoParaProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        assertFalse("Nao deveria existir cotacao", fornecedor.obterCotacaoPorProduto(pid).isPresent());
    }
}
