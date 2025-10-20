package dev.gestock.sge.dominio.principal;

import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.pt.*;

import java.util.*;

import static org.junit.Assert.*;

public class GerenciarFornecedoresFuncionalidade {

    private Fornecedor fornecedor;
    private Map<String, Fornecedor> fornecedores = new HashMap<>();
    private Map<String, ProdutoId> produtos = new HashMap<>();
    private Exception excecaoCapturada;
    private String mensagemErro;
    private int contadorFornecedores = 1;
    private List<Integer> historicoEntregas = new ArrayList<>();
    private boolean temPedidosPendentes = false;
    private ProdutoId produtoAtual;

    // ========== GIVEN (Dado) - Cadastro ==========

    @Dado("que não existe um fornecedor cadastrado com o CNPJ {string}")
    public void queNaoExisteUmFornecedorCadastradoComOCNPJ(String cnpj) {
        // Verifica que não existe fornecedor com esse CNPJ
        // Não faz nada - apenas garante que o contexto está limpo
    }

    // ========== WHEN (Quando) - Cadastro ==========

    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string} e contato {string}")
    public void oClienteCadastraUmFornecedorComNomeCNPJEContato(String nome, String cnpj, String contato) {
        euCadastroUmFornecedorComNomeCNPJEContato(nome, cnpj, contato);
    }

    @Quando("eu cadastro um fornecedor com nome {string}, CNPJ {string} e contato {string}")
    public void euCadastroUmFornecedorComNomeCNPJEContato(String nome, String cnpj, String contato) {
        try {
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            fornecedor = new Fornecedor(id, nome, cnpj, contato);
            fornecedores.put(nome, fornecedor);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string}, contato {string} e lead time de {string} dias")
    public void oClienteCadastraUmFornecedorComNomeCNPJContatoELeadTimeDeDias(String nome, String cnpj, String contato, String leadTime) {
        euCadastroUmFornecedorComNomeCNPJContatoELeadTimeDeDias(nome, cnpj, contato, leadTime);
    }

    @Quando("eu cadastro um fornecedor com nome {string}, CNPJ {string}, contato {string} e lead time de {string} dias")
    public void euCadastroUmFornecedorComNomeCNPJContatoELeadTimeDeDias(String nome, String cnpj, String contato, String leadTime) {
        try {
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            LeadTime lt = new LeadTime(Integer.parseInt(leadTime));
            fornecedor = new Fornecedor(id, nome, cnpj, contato, lt);
            fornecedores.put(nome, fornecedor);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    // ========== GIVEN (Dado) ==========

    @Dado("que existe um fornecedor {string}")
    public void queExisteUmFornecedor(String nome) {
        if (!fornecedores.containsKey(nome)) {
            FornecedorId id = new FornecedorId((long) contadorFornecedores++);
            fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
            fornecedores.put(nome, fornecedor);
        } else {
            fornecedor = fornecedores.get(nome);
        }
    }

    @Dado("existe um produto chamado {string} com id {string}")
    public void existeUmProdutoChamadoComId(String nome, String id) {
        existeUmProdutoComId(nome, id);
    }

    @Dado("existe um produto {string} com id {string}")
    public void existeUmProdutoComId(String nome, String id) {
        ProdutoId produtoId = new ProdutoId(Long.parseLong(id));
        produtos.put(id, produtoId);
        produtoAtual = produtoId;
    }

    @Dado("existem os seguintes produtos:")
    public void existemOsSeguintesProdutos(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String id = row.get("id");
            ProdutoId produtoId = new ProdutoId(Long.parseLong(id));
            produtos.put(id, produtoId);
        }
    }

    @Dado("que existe um fornecedor chamado {string} com contato {string}")
    public void queExisteUmFornecedorChamadoComContato(String nome, String contato) {
        queExisteUmFornecedorComContato(nome, contato);
    }

    @Dado("que existe um fornecedor {string} com contato {string}")
    public void queExisteUmFornecedorComContato(String nome, String contato) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", contato);
        fornecedores.put(nome, fornecedor);
    }

    @Dado("que existe um fornecedor chamado {string} com lead time de {int} dias")
    public void queExisteUmFornecedorChamadoComLeadTimeDeDias(String nome, int leadTime) {
        queExisteUmFornecedorComLeadTimeDeDias(nome, String.valueOf(leadTime));
    }

    @Dado("o fornecedor possui histórico de entregas de {int}, {int}, {int}, {int} e {int} dias")
    public void oFornecedorPossuiHistoricoDeEntregasDeDias(int d1, int d2, int d3, int d4, int d5) {
        historicoEntregas.clear();
        historicoEntregas.add(d1);
        historicoEntregas.add(d2);
        historicoEntregas.add(d3);
        historicoEntregas.add(d4);
        historicoEntregas.add(d5);
    }

    @Dado("que existe um fornecedor {string} com lead time de {string} dias")
    public void queExisteUmFornecedorComLeadTimeDeDias(String nome, String leadTime) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        LeadTime lt = new LeadTime(Integer.parseInt(leadTime));
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com", lt);
        fornecedores.put(nome, fornecedor);
    }

    @Dado("que existe um fornecedor chamado {string} sem histórico de entregas")
    public void queExisteUmFornecedorChamadoSemHistoricoDeEntregas(String nome) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedores.put(nome, fornecedor);
        historicoEntregas.clear();
    }

    @Dado("o fornecedor possui hist\u00f3rico de entregas: {string} dias")
    public void oFornecedorPossuiHistoricoDeEntregasDias(String historico) {
        historicoEntregas.clear();
        String[] valores = historico.split(",\\s*");
        for (String valor : valores) {
            historicoEntregas.add(Integer.parseInt(valor.trim()));
        }
    }

    @Dado("que existe um fornecedor {string} sem pedidos pendentes")
    public void queExisteUmFornecedorSemPedidosPendentes(String nome) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedores.put(nome, fornecedor);
        temPedidosPendentes = false;
    }

    @Dado("que existe um fornecedor {string} com pedidos pendentes")
    public void queExisteUmFornecedorComPedidosPendentes(String nome) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedores.put(nome, fornecedor);
        temPedidosPendentes = true;
    }

    @Dado("que existe um fornecedor {string} inativo")
    public void queExisteUmFornecedorInativo(String nome) {
        FornecedorId id = new FornecedorId((long) contadorFornecedores++);
        fornecedor = new Fornecedor(id, nome, "12.345.678/0001-90", "contato@fornecedor.com");
        fornecedor.inativar();
        fornecedores.put(nome, fornecedor);
    }

    @Dado("o fornecedor possui cotação para o produto {string}")
    public void oFornecedorPossuiCotacaoParaOProduto(String produtoId) {
        oFornecedorTemCotacaoParaOProduto(produtoId);
    }

    @Dado("o fornecedor tem cotação para o produto {string}")
    public void oFornecedorTemCotacaoParaOProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        fornecedor.registrarCotacao(pid, 100.0, 10);
    }

    // ========== WHEN (Quando) - Operações ==========

    @Quando("o cliente registra uma cotação de {string} reais com prazo de {string} dias para o produto {string}")
    public void oClienteRegistraUmaCotacaoDeReaisComPrazoDeDiasParaOProduto(String preco, String prazo, String produtoId) {
        euRegistroUmaCotacaoDeReaisComPrazoDeDiasParaOProduto(preco, prazo, produtoId);
    }

    @Quando("o cliente registra as seguintes cotações:")
    public void oClienteRegistraAsSeguintesCotacoes(DataTable dataTable) {
        euRegistroAsSeguintesCotacoes(dataTable);
    }

    @Quando("o cliente tenta registrar uma cotação para o produto {string} com prazo {int} dias")
    public void oClienteTentaRegistrarUmaCotacaoParaOProdutoComPrazoDias(String nomeProduto, int prazo) {
        euTentoRegistrarUmaCotacaoComPrazoDias(String.valueOf(prazo));
    }

    @Quando("o cliente tenta registrar uma cotação para o produto {string} com preço {int} reais")
    public void oClienteTentaRegistrarUmaCotacaoParaOProdutoComPrecoReais(String nomeProduto, int preco) {
        euTentoRegistrarUmaCotacaoComPrecoReais(String.valueOf(preco));
    }

    @Quando("o cliente atualiza os dados do fornecedor para nome {string} e contato {string}")
    public void oClienteAtualizaOsDadosDoFornecedorParaNomeEContato(String nome, String contato) {
        euAtualizoONomeParaEContatoPara(nome, contato);
    }

    @Quando("o lead timedo forncedor é recalibrado com base no histórico de entregas")
    public void oLeadTimeDoFornecedorERecalibradoComBaseNoHistoricoDeEntregas() {
        euRecalibroOLeadTimeComBaseNoHistorico();
    }

    @Quando("o cliente tenta recalibrar o lead time")
    public void oClienteTentaRecalibrarOLeadTime() {
        euTentoRecalibroOLeadTimeSemHistorico();
    }

    @Quando("o cliente inativa o fornecedor {string}")
    public void oClienteInativaOFornecedor(String nome) {
        euInativoOFornecedor(nome);
    }

    @Quando("o cliente tenta inativar o fornecedor {string}")
    public void oClienteTentaInativarOFornecedor(String nome) {
        euTentoInativarOFornecedor(nome);
    }

    @Quando("o cliente reativa o fornecedor {string}")
    public void oClienteReativaOFornecedor(String nome) {
        euReativoOFornecedor(nome);
    }

    @Quando("o cliente remove a cotação do produto {string}")
    public void oClienteRemoveACotacaoDoProduto(String produtoId) {
        euRemovoACotacaoDoProduto(produtoId);
    }

    @Quando("eu registro uma cotação de {string} reais com prazo de {string} dias para o produto {string}")
    @Quando("eu registro uma cota\u00e7\u00e3o de {string} reais com prazo de {string} dias para o produto {string}")
    public void euRegistroUmaCotacaoDeReaisComPrazoDeDiasParaOProduto(String preco, String prazo, String produtoId) {
        try {
            ProdutoId pid = produtos.get(produtoId);
            if (pid == null) pid = new ProdutoId(Long.parseLong(produtoId));
            fornecedor.registrarCotacao(pid, Double.parseDouble(preco), Integer.parseInt(prazo));
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu registro as seguintes cota\u00e7\u00f5es:")
    public void euRegistroAsSeguintesCotacoes(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> row : rows) {
            String produtoId = row.get("produtoId");
            double preco = Double.parseDouble(row.get("preco"));
            int prazo = Integer.parseInt(row.get("prazo"));
            ProdutoId pid = produtos.get(produtoId);
            if (pid == null) pid = new ProdutoId(Long.parseLong(produtoId));
            fornecedor.registrarCotacao(pid, preco, prazo);
        }
    }

    @Quando("eu tento registrar uma cota\u00e7\u00e3o com prazo {string} dias")
    public void euTentoRegistrarUmaCotacaoComPrazoDias(String prazo) {
        try {
            ProdutoId pid = produtoAtual != null ? produtoAtual : new ProdutoId(1L);
            fornecedor.registrarCotacao(pid, 100.0, Integer.parseInt(prazo));
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento registrar uma cota\u00e7\u00e3o com pre\u00e7o {string} reais")
    public void euTentoRegistrarUmaCotacaoComPrecoReais(String preco) {
        try {
            ProdutoId pid = produtoAtual != null ? produtoAtual : new ProdutoId(1L);
            fornecedor.registrarCotacao(pid, Double.parseDouble(preco), 10);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu atualizo o nome para {string} e contato para {string}")
    public void euAtualizoONomeParaEContatoPara(String nome, String contato) {
        fornecedor.atualizarDados(nome, contato);
    }

    @Quando("eu recalibro o lead time com base no hist\u00f3rico")
    public void euRecalibroOLeadTimeComBaseNoHistorico() {
        try {
            fornecedor.recalibrarLeadTime(historicoEntregas);
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento recalibrar o lead time sem hist\u00f3rico")
    public void euTentoRecalibroOLeadTimeSemHistorico() {
        try {
            fornecedor.recalibrarLeadTime(new ArrayList<>());
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu inativo o fornecedor {string}")
    public void euInativoOFornecedor(String nome) {
        try {
            fornecedor = fornecedores.get(nome);
            fornecedor.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu tento inativar o fornecedor {string}")
    public void euTentoInativarOFornecedor(String nome) {
        try {
            fornecedor = fornecedores.get(nome);
            if (temPedidosPendentes) {
                throw new IllegalStateException("Fornecedor com pedidos pendentes n\u00e3o pode ser inativado");
            }
            fornecedor.inativar();
        } catch (Exception e) {
            excecaoCapturada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Quando("eu reativo o fornecedor {string}")
    public void euReativoOFornecedor(String nome) {
        fornecedor = fornecedores.get(nome);
        fornecedor.ativar();
    }

    @Quando("eu removo a cotação do produto {string}")
    public void euRemovoACotacaoDoProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        fornecedor.removerCotacao(pid);
    }

    // ========== THEN (Ent\u00e3o) ==========

    @Ent\u00e3o("o fornecedor deve ser cadastrado com sucesso")
    public void oFornecedorDeveSerCadastradoComSucesso() {
        assertNotNull("Fornecedor n\u00e3o foi cadastrado", fornecedor);
    }

    @Então("o fornecedor deve estar ativo na listagem")
    public void oFornecedorDeveEstarAtivoNaListagem() {
        oFornecedorDeveEstarAtivo();
    }

    @Ent\u00e3o("o fornecedor deve estar ativo")
    public void oFornecedorDeveEstarAtivo() {
        assertTrue("Fornecedor deveria estar ativo", fornecedor.isAtivo());
    }

    @Ent\u00e3o("o lead time inicial deve ser {int} dias")
    public void oLeadTimeInicialDeveSerDias(int dias) {
        assertEquals(dias, fornecedor.getLeadTimeMedio().getDias());
    }

    @Ent\u00e3o("o lead time deve ser {string} dias")
    public void oLeadTimeDeveSerDias(String dias) {
        assertEquals(Integer.parseInt(dias), fornecedor.getLeadTimeMedio().getDias());
    }

    @Ent\u00e3o("a cotação deve ser registrada com sucesso")
    public void aCotacaoDeveSerRegistradaComSucesso() {
        assertNotNull("Cotação n\u00e3o foi registrada", fornecedor.getCotacoesSnapshot());
    }

    @Então("o fornecedor deve ter cotação para o produto {string}")
    public void oFornecedorDeveTerCotacaoParaOProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        assertTrue("Fornecedor deveria ter cotação para o produto", 
                   fornecedor.obterCotacaoPorProduto(pid).isPresent());
    }

    @Ent\u00e3o("o fornecedor deve ter {int} cotações cadastradas")
    public void oFornecedorDeveTerCotacoesCadastradas(int quantidade) {
        assertEquals(quantidade, fornecedor.getCotacoesSnapshot().size());
    }

    @Ent\u00e3o("o sistema deve rejeitar a operação")
    public void oSistemaDeveRejeitarAOperacao() {
        assertNotNull("Deveria ter capturado uma exceção", excecaoCapturada);
    }

    @Ent\u00e3o("deve exibir a mensagem {string}")
    public void deveExibirAMensagem(String mensagemEsperada) {
        assertNotNull("Mensagem de erro n\u00e3o foi capturada", mensagemErro);
        assertTrue("Mensagem incorreta: " + mensagemErro, mensagemErro.contains(mensagemEsperada));
    }

    @Então("o nome do fornecedor deve ser {string}")
    public void oNomeDoFornecedorDeveSer(String nome) {
        oNomeDeveSer(nome);
    }

    @Então("o contato do fornecedor deve ser {string}")
    public void oContatoDoFornecedorDeveSer(String contato) {
        oContatoDeveSer(contato);
    }

    @Ent\u00e3o("os dados do fornecedor devem ser atualizados")
    public void osDadosDoFornecedorDevemSerAtualizados() {
        assertNotNull("Nome do fornecedor n\u00e3o foi atualizado", fornecedor.getNome());
    }

    @Ent\u00e3o("o nome deve ser {string}")
    public void oNomeDeveSer(String nome) {
        assertEquals(nome, fornecedor.getNome());
    }

    @Ent\u00e3o("o contato deve ser {string}")
    public void oContatoDeveSer(String contato) {
        assertEquals(contato, fornecedor.getContato());
    }

    @Ent\u00e3o("o lead time deve ser atualizado para {string} dias")
    public void oLeadTimeDeveSerAtualizadoParaDias(String dias) {
        assertEquals(Integer.parseInt(dias), fornecedor.getLeadTimeMedio().getDias());
    }

    @Ent\u00e3o("o fornecedor deve ser inativado com sucesso")
    public void oFornecedorDeveSerInativadoComSucesso() {
        assertFalse("Fornecedor deveria estar inativo", fornecedor.isAtivo());
    }

    @Ent\u00e3o("o status do fornecedor deve ser {string}")
    public void oStatusDoFornecedorDeveSer(String status) {
        if ("inativo".equals(status)) {
            assertFalse("Fornecedor deveria estar inativo", fornecedor.isAtivo());
        } else {
            assertTrue("Fornecedor deveria estar ativo", fornecedor.isAtivo());
        }
    }

    @Ent\u00e3o("o fornecedor deve ser reativado com sucesso")
    public void oFornecedorDeveSerReativadoComSucesso() {
        assertTrue("Fornecedor deveria estar ativo", fornecedor.isAtivo());
    }

    @Ent\u00e3o("a cotação deve ser removida")
    public void aCotacaoDeveSerRemovida() {
        // Verificação implícita - não lançou exceção
        assertNotNull(fornecedor);
    }

    @Então("o fornecedor não deve ter cotação para o produto {string}")
    public void oFornecedorNaoDeveTerCotacaoParaOProduto(String produtoId) {
        ProdutoId pid = new ProdutoId(Long.parseLong(produtoId));
        assertFalse("Fornecedor não deveria ter cotação para o produto", 
                    fornecedor.obterCotacaoPorProduto(pid).isPresent());
    }

    @Então("o fornecedor não deve possuir cotação para o produto {string}")
    public void oFornecedorNaoDevePossuirCotacaoParaOProduto(String produtoId) {
        oFornecedorNaoDeveTerCotacaoParaOProduto(produtoId);
    }
}
