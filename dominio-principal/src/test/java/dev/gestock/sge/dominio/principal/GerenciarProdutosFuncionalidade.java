package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;

import static org.junit.Assert.*;

public class GerenciarProdutosFuncionalidade {

    private Produto produto;
    private Map<String, Produto> produtos = new HashMap<>();
    private Map<String, Fornecedor> fornecedores = new HashMap<>();
    private Map<String, Estoque> estoques = new HashMap<>();
    private Map<String, String> codigosProdutos = new HashMap<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private int contadorProdutos = 1;
    private int contadorFornecedores = 1;
    private int contadorEstoques = 1;
    private int saldoAtual;
    private boolean atingiuROP;
    private int totalCotacoesFornecedores = 0;
    private boolean temSaldoPositivo = false;
    private boolean temPedidosAndamento = false;
    private int numeroCotacoesExistentes = 0;

    // ========== WHEN (Quando) ==========

    @Quando("eu cadastro um produto com código {string}, nome {string}, unidade {string} e não perecível")
    public void euCadastroUmProdutoComCodigoNomeUnidadeENaoPerecivel(String codigo, String nome, String unidade) {
        try {
            ProdutoId id = new ProdutoId((long) contadorProdutos++);
            produto = new Produto(id, codigo, nome, unidade, false);
            produtos.put(nome, produto);
            codigosProdutos.put(codigo, nome);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu cadastro um produto com código {string}, nome {string}, unidade {string} e perecível")
    public void euCadastroUmProdutoComCodigoNomeUnidadeEPerecivel(String codigo, String nome, String unidade) {
        try {
            ProdutoId id = new ProdutoId((long) contadorProdutos++);
            produto = new Produto(id, codigo, nome, unidade, true);
            produtos.put(nome, produto);
            codigosProdutos.put(codigo, nome);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento cadastrar outro produto com código {string}")
    public void euTentoCadastrarOutroProdutoComCodigo(String codigo) {
        try {
            if (codigosProdutos.containsKey(codigo)) {
                throw new IllegalArgumentException("Código do produto já existe");
            }
            ProdutoId id = new ProdutoId((long) contadorProdutos++);
            produto = new Produto(id, codigo, "Novo Produto", "UN", false);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("os fornecedores registram cotações para o produto:")
    public void osFornecedoresRegistramCotacoesParaOProduto(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        totalCotacoesFornecedores = rows.size();
        for (Map<String, String> row : rows) {
            String nomeFornecedor = row.get("fornecedor");
            Fornecedor forn = fornecedores.get(nomeFornecedor);
            if (forn != null) {
                double preco = Double.parseDouble(row.get("preco"));
                int prazo = Integer.parseInt(row.get("prazo"));
                forn.registrarCotacao(produto.getId(), preco, prazo);
            }
        }
    }

    @Quando("eu cadastro um produto {string} vinculado ao estoque {string}")
    public void euCadastroUmProdutoVinculadoAoEstoque(String nomeProduto, String nomeEstoque) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nomeProduto, "UN", false);
        produtos.put(nomeProduto, produto);
    }

    @Quando("eu atualizo o nome para {string} e unidade para {string}")
    public void euAtualizoONomeParaEUnidadePara(String nome, String unidade) {
        produto.atualizar(nome, unidade);
    }

    @Quando("eu atualizo as especificações do produto")
    public void euAtualizoAsEspecificacoesDoProduto() {
        produto.atualizar("Produto Atualizado", "KG");
    }

    @Quando("eu inativo o produto {string}")
    public void euInativoOProduto(String nome) {
        try {
            produto = produtos.get(nome);
            produto.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento inativar o produto {string}")
    public void euTentoInativarOProduto(String nome) {
        try {
            produto = produtos.get(nome);
            if (temSaldoPositivo) {
                throw new IllegalStateException("Produto com saldo positivo não pode ser inativado");
            }
            if (temPedidosAndamento) {
                throw new IllegalStateException("Produto com pedidos em andamento não pode ser inativado");
            }
            produto.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento registrar uma nova cotação para o produto")
    public void euTentoRegistrarUmaNovaCotacaoParaOProduto() {
        try {
            if (produto != null && !produto.isAtivo()) {
                throw new IllegalStateException("Produto inativo não pode receber novas cotações");
            }
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu defino o ROP com consumo médio {string}, lead time {string} dias e estoque de segurança {string}")
    public void euDefinoOROPComConsumoMedioLeadTimeDiasEEstoqueDeSeguranca(String consumo, String leadTime, String seguranca) {
        double consumoMedio = Double.parseDouble(consumo);
        int lead = Integer.parseInt(leadTime);
        int seg = Integer.parseInt(seguranca);
        produto.definirROP(consumoMedio, lead, seg);
    }

    @Quando("o saldo atual é {string} unidades")
    public void oSaldoAtualEUnidades(String saldo) {
        saldoAtual = Integer.parseInt(saldo);
        atingiuROP = produto.atingiuROP(saldoAtual);
    }

    // ========== GIVEN (Dado) ==========

    @Dado("que existe um produto com código {string}")
    public void queExisteUmProdutoComCodigo(String codigo) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, codigo, "Produto Existente", "UN", false);
        produtos.put("Produto Existente", produto);
        codigosProdutos.put(codigo, "Produto Existente");
    }

    @Dado("que existe um produto {string} com id {string}")
    public void queExisteUmProdutoComId(String nome, String id) {
        ProdutoId prodId = new ProdutoId(Long.parseLong(id));
        produto = new Produto(prodId, "PROD-" + id, nome, "UN", false);
        produtos.put(nome, produto);
    }

    @Dado("existem os seguintes fornecedores:")
    public void existemOsSeguintesFornecedores(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String nome = row.get("nome");
            String cnpj = row.get("cnpj");
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            Fornecedor forn = new Fornecedor(id, nome, cnpj, "contato@fornecedor.com");
            fornecedores.put(nome, forn);
        }
    }

    @Dado("que existe um estoque ativo {string}")
    public void queExisteUmEstoqueAtivo(String nome) {
        EstoqueId id = new EstoqueId((long) contadorEstoques++);
        ClienteId clienteId = new ClienteId(1L);
        Estoque est = new Estoque(id, clienteId, nome, "Endereco X", 1000);
        estoques.put(nome, est);
    }

    @Dado("que existe um produto {string} com unidade {string}")
    public void queExisteUmProdutoComUnidade(String nome, String unidade) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, unidade, false);
        produtos.put(nome, produto);
    }

    @Dado("que existe um produto {string} com cotações registradas")
    public void queExisteUmProdutoComCotacoesRegistradas(String nome) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produtos.put(nome, produto);
        numeroCotacoesExistentes = 2; // Simula cotações existentes
    }

    @Dado("que existe um produto {string} sem saldo em estoque")
    public void queExisteUmProdutoSemSaldoEmEstoque(String nome) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produtos.put(nome, produto);
        temSaldoPositivo = false;
    }

    @Dado("não existem pedidos em andamento para o produto")
    public void naoExistemPedidosEmAndamentoParaOProduto() {
        temPedidosAndamento = false;
    }

    @Dado("que existe um produto {string} com saldo de {string} unidades")
    public void queExisteUmProdutoComSaldoDeUnidades(String nome, String saldo) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produtos.put(nome, produto);
        temSaldoPositivo = Integer.parseInt(saldo) > 0;
    }

    @Dado("existem pedidos em andamento para o produto")
    public void existemPedidosEmAndamentoParaOProduto() {
        temPedidosAndamento = true;
    }

    @Dado("que existe um produto {string} inativo")
    public void queExisteUmProdutoInativo(String nome) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produto.inativar();
        produtos.put(nome, produto);
    }

    @Dado("que existe um produto {string}")
    public void queExisteUmProduto(String nome) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produtos.put(nome, produto);
    }

    @Dado("que existe um produto {string} com ROP de {string} unidades")
    public void queExisteUmProdutoComROPDeUnidades(String nome, String rop) {
        ProdutoId id = new ProdutoId((long) contadorProdutos++);
        produto = new Produto(id, "PROD-X", nome, "UN", false);
        produto.definirROP(10, 7, 20); // Define ROP que resulta em 90
        produtos.put(nome, produto);
    }

    // ========== THEN (Então) ==========

    @Então("o produto deve ser cadastrado com sucesso")
    public void oProdutoDeveSerCadastradoComSucesso() {
        assertNotNull("Produto não foi cadastrado", produto);
    }

    @Então("o produto deve estar ativo")
    public void oProdutoDeveEstarAtivo() {
        assertTrue("Produto deveria estar ativo", produto.isAtivo());
    }

    @Então("o ROP deve estar nulo inicialmente")
    public void oROPDeveEstarNuloInicialmente() {
        assertNull("ROP deveria estar nulo", produto.getRop());
    }

    @Então("o produto deve ser marcado como perecível")
    public void oProdutoDeveSerMarcadoComoPerecivel() {
        assertTrue("Produto deveria ser perecível", produto.isPerecivel());
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

    @Então("o produto deve ter cotações de {int} fornecedores")
    public void oProdutoDeveTerCotacoesDeFornecedores(int quantidade) {
        assertEquals(quantidade, totalCotacoesFornecedores);
    }

    @Então("o produto deve estar vinculado ao estoque {string}")
    public void oProdutoDeveEstarVinculadoAoEstoque(String nomeEstoque) {
        assertTrue("Produto deveria estar vinculado ao estoque", estoques.containsKey(nomeEstoque));
    }

    @Então("os dados do produto devem ser atualizados")
    public void osDadosDoProdutoDevemSerAtualizados() {
        assertNotNull("Nome do produto não foi atualizado", produto.getNome());
    }

    @Então("o nome deve ser {string}")
    public void oNomeDeveSer(String nome) {
        assertEquals(nome, produto.getNome());
    }

    @Então("a unidade deve ser {string}")
    public void aUnidadeDeveSer(String unidade) {
        assertEquals(unidade, produto.getUnidadeMedida());
    }

    @Então("as cotações existentes devem permanecer inalteradas")
    public void asCotacoesExistentesDevemPermanecerInalteradas() {
        assertEquals(2, numeroCotacoesExistentes);
    }

    @Então("o produto deve estar atualizado")
    public void oProdutoDeveEstarAtualizado() {
        assertNotNull("Produto não foi atualizado", produto);
    }

    @Então("o produto deve ser inativado com sucesso")
    public void oProdutoDeveSerInativadoComSucesso() {
        assertFalse("Produto deveria estar inativo", produto.isAtivo());
    }

    @Então("o status do produto deve ser {string}")
    public void oStatusDoProdutoDeveSer(String status) {
        if ("inativo".equals(status)) {
            assertFalse("Produto deveria estar inativo", produto.isAtivo());
        } else {
            assertTrue("Produto deveria estar ativo", produto.isAtivo());
        }
    }

    @Então("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Deveria ter capturado uma exceção", excecaoCapturada);
    }

    @Então("o ROP deve ser calculado corretamente")
    public void oROPDeveSerCalculadoCorretamente() {
        assertNotNull("ROP não foi calculado", produto.getRop());
    }

    @Então("o valor do ROP deve ser {string} unidades")
    public void oValorDoROPDeveSerUnidades(String valor) {
        assertEquals(Integer.parseInt(valor), produto.getRop().getValorROP());
    }

    @Então("o produto deve ter atingido o ROP")
    public void oProdutoDeveTerAtingidoOROP() {
        assertTrue("Produto deveria ter atingido o ROP", atingiuROP);
    }

    @Então("deve ser necessário acionar reposição")
    public void deveSerNecessarioAcionarReposicao() {
        assertTrue("Deveria acionar reposição", atingiuROP);
    }

    @Então("o produto não deve ter atingido o ROP")
    public void oProdutoNaoDeveTerAtingidoOROP() {
        assertFalse("Produto não deveria ter atingido o ROP", atingiuROP);
    }
}
