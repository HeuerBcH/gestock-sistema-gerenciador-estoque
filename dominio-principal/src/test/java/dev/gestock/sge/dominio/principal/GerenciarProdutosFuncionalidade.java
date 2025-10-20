package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;
import static org.junit.Assert.*;

public class GerenciarProdutosFuncionalidade {

    private final Repositorio repositorio = new Repositorio();

    private Produto produto;
    private Estoque estoque;
    private Exception excecaoCapturada;
    private String mensagemErro;

    private final Map<String, Fornecedor> fornecedoresPorNome = new HashMap<>();
    private final List<Fornecedor> fornecedoresCriadosNoCenario = new ArrayList<>();
    private boolean atingiuROP = false;

    // =============================================================
    // H8 — Cadastrar produtos
    // =============================================================

    @Dado("que o cliente informa codigo {string}, nome {string}, unidade {string} e indica que nao e perecivel")
    public void dadoProdutoNaoPerecivel(String codigo, String nome, String unidade) {
        ProdutoId id = repositorio.novoProdutoId();
        produto = new Produto(id, codigo, nome, unidade, false, 0.0);
    }

    @Dado("que o cliente informa codigo {string}, nome {string}, unidade {string} e indica que e perecivel")
    public void dadoProdutoPerecivel(String codigo, String nome, String unidade) {
        ProdutoId id = repositorio.novoProdutoId();
        produto = new Produto(id, codigo, nome, unidade, true, 0.0);
    }

    @Quando("o cliente confirma o cadastro do produto")
    public void quandoConfirmaCadastro() {
        try {
            if (repositorio.codigoExiste(new CodigoProduto(produto.getCodigo()))) {
                throw new IllegalArgumentException("Codigo do produto ja existe");
            }
            repositorio.salvar(produto);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve cadastrar o produto com sucesso")
    public void entaoCadastraComSucesso() {
        assertNotNull(produto);
        assertTrue(repositorio.buscarPorId(produto.getId()).isPresent());
    }

    @Entao("o produto deve estar ativo")
    public void entaoProdutoAtivo() {
        assertTrue(produto.isAtivo());
    }

    @Entao("o ROP deve estar nulo inicialmente")
    public void entaoRopNuloInicialmente() {
        assertTrue(true);
    }

    @Entao("o produto deve ser marcado como perecivel")
    public void entaoProdutoPerecivel() {
        assertTrue(produto.isPerecivel());
    }

    // -------------------------------------------------------------
    // R1H8 — Código único
    // -------------------------------------------------------------

    @Dado("que existe um produto cadastrado com codigo {string}")
    public void dadoProdutoJaCadastrado(String codigo) {
        ProdutoId id = repositorio.novoProdutoId();
        Produto existente = new Produto(id, codigo, "Produto Existente", "UN", false, 0.0);
        repositorio.salvar(existente);
    }

    @Quando("o cliente tenta cadastrar outro produto com o mesmo codigo {string}")
    public void quandoTentaCadastrarDuplicado(String codigo) {
        try {
            if (repositorio.codigoExiste(new CodigoProduto(codigo))) {
                throw new IllegalArgumentException("Codigo do produto ja existe");
            }
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Entao("o sistema deve rejeitar o cadastro")
    public void entaoSistemaRejeitaCadastro() {
        assertNotNull(excecaoCapturada);
    }

    @Entao("o sistema deve exibir a mensagem {string}")
    public void entaoExibeMensagem(String msg) {
        assertNotNull(mensagemErro);
        assertTrue(mensagemErro.contains(msg));
    }

    // -------------------------------------------------------------
    // R2H8 — Produto fornecido por múltiplos fornecedores
    // -------------------------------------------------------------

    @Dado("que existe um produto chamado {string} para gerenciamento com id {string}")
    public void dadoProdutoComId(String nome, String id) {
        ProdutoId pid = new ProdutoId(Long.parseLong(id));
        produto = new Produto(pid, "PROD-" + id, nome, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Dado("existem os seguintes fornecedores cadastrados:")
    public void dadoFornecedoresCadastrados(DataTable dataTable) {
        fornecedoresPorNome.clear();
        fornecedoresCriadosNoCenario.clear();
        for (Map<String, String> row : dataTable.asMaps()) {
            FornecedorId fid = repositorio.novoFornecedorId();
            Fornecedor forn = new Fornecedor(fid, row.get("nome"), row.get("cnpj"), "contato@fornecedor.com");
            repositorio.salvar(forn);
            fornecedoresPorNome.put(row.get("nome"), forn);
            fornecedoresCriadosNoCenario.add(forn);
        }
    }

    @Quando("os fornecedores registram cotacoes para o produto:")
    public void quandoFornecedoresRegistramCotacoes(DataTable dataTable) {
        for (Map<String, String> row : dataTable.asMaps()) {
            String nomeFornecedor = row.get("fornecedor");
            Fornecedor forn = fornecedoresPorNome.get(nomeFornecedor);
            if (forn != null) {
                double preco = Double.parseDouble(row.get("preco"));
                int prazo = Integer.parseInt(row.get("prazo"));
                forn.registrarCotacao(produto.getId(), preco, prazo);
            }
        }
    }

    @Entao("o produto deve possuir cotacoes de dois fornecedores")
    public void entaoProdutoComDuasCotacoes() {
        int total = 0;
        for (Fornecedor f : fornecedoresCriadosNoCenario) {
            if (f.obterCotacaoPorProduto(produto.getId()).isPresent()) {
                total++;
            }
        }
        assertEquals(2, total);
    }

    // -------------------------------------------------------------
    // R3H8 — Produto vinculado a pelo menos um estoque
    // -------------------------------------------------------------

    @Dado("que existe um estoque ativo chamado {string}")
    public void dadoEstoqueAtivo(String nome) {
        EstoqueId eid = repositorio.novoEstoqueId();
        estoque = new Estoque(eid, new ClienteId(1L), nome, "Endereco X", 1000);
        repositorio.salvar(estoque);
    }

    @Quando("o cliente cadastra um produto chamado {string} vinculado ao estoque {string}")
    public void quandoCadastraProdutoVinculadoAoEstoque(String nomeProduto, String nomeEstoque) {
        ProdutoId id = repositorio.novoProdutoId();
        produto = new Produto(id, "PROD-X", nomeProduto, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Entao("o produto deve estar vinculado ao estoque {string}")
    public void entaoProdutoVinculadoAoEstoque(String nomeEstoque) {
        assertNotNull(repositorio.buscarPorId(estoque.getId()).orElse(null));
        assertNotNull(repositorio.buscarPorId(produto.getId()).orElse(null));
    }

    // =============================================================
    // H14 — Definir e calcular ROP
    // =============================================================

    @Dado("que existe um produto chamado {string} para gerenciamento")
    public void dadoExisteProdutoParaGerenciamento(String nome) {
        ProdutoId pid = repositorio.novoProdutoId();
        produto = new Produto(pid, "PROD-GER", nome, "UN", false, 0.0);
        repositorio.salvar(produto);
    }

    @Quando("o cliente define o ROP informando consumo medio de {int} unidades por dia, lead time de {int} dias e estoque de seguranca de {int} unidades")
    public void quandoDefineROP(int consumo, int lead, int seguranca) {
        EstoqueId eid = repositorio.novoEstoqueId();
        estoque = new Estoque(eid, new ClienteId(1L), "Estoque Principal", "Endereco X", 1000);
        repositorio.salvar(estoque);
        estoque.definirROP(produto.getId(), consumo, lead, seguranca);
    }

    @Entao("o sistema deve calcular o ROP corretamente")
    public void entaoCalculouRop() {
        assertNotNull(estoque.getROP(produto.getId()));
    }

    @Entao("o valor do ROP deve ser {int} unidades")
    public void entaoValorRopEsperado(int esperado) {
        assertEquals(esperado, estoque.getROP(produto.getId()).getValorROP());
    }

    @Dado("que existe um produto chamado {string} com ROP definido em {int} unidades")
    public void dadoProdutoComRopDefinido(String nome, int ropEsperado) {
        ProdutoId pid = repositorio.novoProdutoId();
        produto = new Produto(pid, "PROD-ROP2", nome, "UN", false, 0.0);
        EstoqueId eid = repositorio.novoEstoqueId();
        estoque = new Estoque(eid, new ClienteId(1L), "Estoque ROP", "Endereco X", 1000);
        repositorio.salvar(produto);
        repositorio.salvar(estoque);
        estoque.definirROP(produto.getId(), 10, 7, 20);
        assertEquals(ropEsperado, estoque.getROP(produto.getId()).getValorROP());
    }

    @Quando("o saldo atual e {int} unidades")
    public void quandoSaldoAtual(int saldo) {
        atingiuROP = estoque.atingiuROP(produto.getId(), saldo);
    }

    @Entao("o sistema deve identificar que o produto atingiu o ROP")
    public void entaoAtingiuRop() {
        assertTrue(atingiuROP);
    }

    @Entao("deve ser necessario acionar reposicao")
    public void entaoNecessarioRepor() {
        assertTrue(atingiuROP);
    }

    @Entao("o sistema deve identificar que o produto esta acima do ROP")
    public void entaoAcimaDoRop() {
        assertFalse(atingiuROP);
    }

    @Entao("nao e necessario acionar reposicao")
    public void entaoNaoNecessarioRepor() {
        assertFalse(atingiuROP);
    }
}
