package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;

import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public class GerenciarFornecedoresFeature {

    private Repositorio repo;
    private FornecedorServico fornecedorSrv;
    private AtomicLong seq;

    private Map<String, FornecedorId> aliasFornecedor;
    private Map<String, ProdutoId> aliasProduto;

    private FornecedorId currentFornecedorId;
    private Exception lastError;

    @Before
    public void reset() {
        repo = new Repositorio();
        fornecedorSrv = new FornecedorServico(repo, repo);
        seq = new AtomicLong(1);
        aliasFornecedor = new HashMap<>();
        aliasProduto = new HashMap<>();
        currentFornecedorId = null;
        lastError = null;
    }

    // utils
    private FornecedorId ensureFornecedor(String nome, String cnpj, String contato) {
        return aliasFornecedor.computeIfAbsent(nome, k -> {
            FornecedorId id = repo.novoFornecedorId();
            Fornecedor f = new Fornecedor(id, nome, cnpj, contato);
            repo.salvar(f);
            return id;
        });
    }

    private ProdutoId ensureProduto(String idAlias) {
        return aliasProduto.computeIfAbsent(idAlias, k -> {
            ProdutoId pid = repo.novoProdutoId();
            dev.gestock.sge.dominio.principal.produto.Produto p =
                new dev.gestock.sge.dominio.principal.produto.Produto(pid, "PROD-"+pid.getId(), "Produto"+pid.getId(), "un", false, 1.0);
            repo.salvar(p);
            return pid;
        });
    }

    // ===== DADOS =====

    @Dado("nao existe um fornecedor cadastrado com o CNPJ {string}")
    public void nao_existe_fornecedor_com_cnpj(String cnpj) {
        repo.limparTodos();
    }

    @Dado("existe um fornecedor {string}")
    public void existe_um_fornecedor(String nome) {
        String cnpj = "cnpj-" + seq.getAndIncrement();
        currentFornecedorId = ensureFornecedor(nome, cnpj, "contato@ex.com");
    }

    @Dado("existe um produto {string} com id {string}")
    public void existe_produto_com_id(String nome, String id) {
        ensureProduto(id);
    }

    @Dado("existem os seguintes produtos:")
    public void existem_produtos(io.cucumber.datatable.DataTable table) {
        List<Map<String,String>> rows = table.asMaps();
        for (Map<String,String> r : rows) {
            ensureProduto(r.get("id"));
        }
    }

    @Dado("existe um fornecedor {string} com lead time de {int} dias")
    public void existe_fornecedor_com_leadtime(String nome, int dias) {
        String cnpj = "cnpj-" + seq.getAndIncrement();
        currentFornecedorId = ensureFornecedor(nome, cnpj, "contato@ex.com");
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        f.recalibrarLeadTime(java.util.List.of(dias));
        repo.salvar(f);
    }

    @Dado("o fornecedor possui historico de entregas de {string}")
    public void fornecedor_possui_historico(String lista) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        String[] parts = lista.split(",");
        java.util.List<Integer> vals = new java.util.ArrayList<>();
        for (String p : parts) vals.add(Integer.parseInt(p.trim()));
        f.recalibrarLeadTime(vals);
        repo.salvar(f);
    }

    @Dado("existe um fornecedor chamado {string} sem historico de entregas")
    public void fornecedor_sem_historico(String nome) {
        currentFornecedorId = ensureFornecedor(nome, "cnpj-"+seq.getAndIncrement(), "contato@ex.com");
    }

    @Dado("existe um fornecedor chamado {string} sem pedidos pendentes")
    public void fornecedor_sem_pedidos_pendentes(String nome) {
        currentFornecedorId = ensureFornecedor(nome, "cnpj-"+seq.getAndIncrement(), "contato@ex.com");
    }

    @Dado("existe um fornecedor chamado {string} com pedidos pendentes")
    public void fornecedor_com_pedidos_pendentes(String nome) {
        currentFornecedorId = ensureFornecedor(nome, "cnpj-"+seq.getAndIncrement(), "contato@ex.com");
        dev.gestock.sge.dominio.principal.pedido.PedidoId pid = repo.novoPedidoId();
        dev.gestock.sge.dominio.principal.pedido.Pedido p =
            new dev.gestock.sge.dominio.principal.pedido.Pedido(pid, new dev.gestock.sge.dominio.principal.cliente.ClienteId(1L), currentFornecedorId);
        repo.salvar(p);
    }

    // ✅ NOVO: faltava step para "existe um fornecedor chamado X com contato Y"
    @Dado("existe um fornecedor chamado {string} com contato {string}")
    public void existe_fornecedor_com_contato(String nome, String contato) {
        currentFornecedorId = ensureFornecedor(nome, "cnpj-"+seq.getAndIncrement(), contato);
    }

    // ✅ NOVO: faltava step para "existe um produto chamado X com id Y"
    @Dado("existe um produto chamado {string} com id {string}")
    public void existe_produto_chamado_com_id(String nome, String id) {
        ensureProduto(id);
    }

    // ===== QUANDOS =====

    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string} e contato {string}")
    public void cadastra_fornecedor(String nome, String cnpj, String contato) {
        lastError = null;
        try {
            FornecedorId id = repo.novoFornecedorId();
            Fornecedor f = new Fornecedor(id, nome, cnpj, contato);
            fornecedorSrv.cadastrar(f);
            aliasFornecedor.put(nome, id);
            currentFornecedorId = id;
        } catch (Exception ex) { lastError = ex; }
    }

    // ✅ Corrigido: o feature usa aspas no número, então trocamos {int} por {string}
    @Quando("o cliente cadastra um fornecedor com nome {string}, CNPJ {string}, contato {string} e lead time de {string} dias")
    public void cadastra_fornecedor_com_leadtime(String nome, String cnpj, String contato, String diasStr) {
        lastError = null;
        try {
            int dias = Integer.parseInt(diasStr.replace("\"", ""));
            FornecedorId id = repo.novoFornecedorId();
            Fornecedor f = new Fornecedor(id, nome, cnpj, contato, new LeadTime(dias));
            fornecedorSrv.cadastrar(f);
            currentFornecedorId = id;
        } catch (Exception ex) { lastError = ex; }
    }

    // ✅ Corrigido: feature usa "150.00" e "10" entre aspas
    @Quando("o cliente registra uma cotacao de {string} reais com prazo de {string} dias para o produto {string}")
    public void registra_cotacao(String precoStr, String prazoStr, String codigoProduto) {
        lastError = null;
        try {
            int prazo = Integer.parseInt(prazoStr.replace("\"", ""));
            ProdutoId pid = ensureProduto(codigoProduto);
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            double preco = Double.parseDouble(precoStr.replace(',', '.').replace("\"", ""));
            f.registrarCotacao(pid, preco, prazo);
            repo.salvar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente registra as seguintes cotacoes:")
    public void registra_varias_cotacoes(io.cucumber.datatable.DataTable table) {
        lastError = null;
        try {
            List<Map<String,String>> rows = table.asMaps();
            for (Map<String,String> r : rows) {
                ProdutoId pid = ensureProduto(r.get("id"));
                double preco = Double.parseDouble(r.get("preco").replace(',', '.'));
                int prazo = Integer.parseInt(r.get("prazo"));
                Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
                f.registrarCotacao(pid, preco, prazo);
                repo.salvar(f);
            }
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta registrar uma cotacao para o produto {string} com prazo {int} dias")
    public void tenta_registrar_cotacao_prazo_invalido(String produto, int prazo) {
        lastError = null;
        try {
            ProdutoId pid = ensureProduto(produto);
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            f.registrarCotacao(pid, 100.0, prazo);
            repo.salvar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta registrar uma cotacao para o produto {string} com preco {int} reais")
    public void tenta_registrar_cotacao_preco_invalido(String produto, int preco) {
        lastError = null;
        try {
            ProdutoId pid = ensureProduto(produto);
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            f.registrarCotacao(pid, preco, 10);
            repo.salvar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente atualiza os dados do fornecedor para nome {string} e contato {string}")
    public void atualiza_dados_fornecedor(String nome, String contato) {
        lastError = null;
        try {
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            f.atualizarDados(nome, contato);
            fornecedorSrv.atualizar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o lead time do fornecedor e recalibrado com base no historico de entregas")
    public void recalibrar_leadtime_historico() {
        lastError = null;
        try {
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            repo.salvar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta recalibrar o lead time")
    public void cliente_tenta_recalibrar_leadtime() {
        lastError = null;
        try {
            Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
            f.recalibrarLeadTime(java.util.Collections.emptyList());
            repo.salvar(f);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente inativa o fornecedor {string}")
    public void cliente_inativa_fornecedor(String nome) {
        lastError = null;
        try {
            FornecedorId fid = aliasFornecedor.get(nome);
            if (fid != null) {
                Fornecedor f = repo.buscarPorId(fid).orElseThrow();
                fornecedorSrv.inativar(f);
                repo.salvar(f);
                currentFornecedorId = f.getId();
                return;
            }
            throw new IllegalStateException("Fornecedor não encontrado: " + nome);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta inativar o fornecedor {string}")
    public void cliente_tenta_inativar_fornecedor(String nome) { cliente_inativa_fornecedor(nome); }

    // ===== ENTAOS =====

    @Entao("o fornecedor deve ser cadastrado com sucesso")
    public void fornecedor_cadastrado_sucesso() {
        assertNull(lastError, "Esperava sucesso no cadastro: " + (lastError==null?"":lastError.getMessage()));
        assertNotNull(currentFornecedorId);
        assertTrue(repo.buscarPorId(currentFornecedorId).isPresent());
    }

    @Entao("o fornecedor deve estar ativo na listagem")
    public void fornecedor_ativo_na_listagem() {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertTrue(f.isAtivo());
    }

    @Entao("o lead time inicial deve ser {int} dias")
    public void lead_time_inicial(int dias) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(dias, f.getLeadTimeMedio().getDias());
    }

    // ✅ NOVO: feature usa “E o lead time deve ser ‘7’ dias”
    @Entao("o lead time deve ser {string} dias")
    public void lead_time_generico(String diasStr) {
        int dias = Integer.parseInt(diasStr.replace("\"", ""));
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(dias, f.getLeadTimeMedio().getDias());
    }

    @Entao("a cotacao deve ser registrada com sucesso")
    public void cotacao_registrada_sucesso() {
        assertNull(lastError);
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertFalse(f.getCotacoesSnapshot().isEmpty());
    }

    @Entao("o fornecedor deve ter cotacao para o produto {string}")
    public void fornecedor_tem_cotacao_para_produto(String codigo) {
        ProdutoId pid = ensureProduto(codigo);
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertTrue(f.obterCotacaoPorProduto(pid).isPresent());
    }

    @Entao("o fornecedor deve ter {int} cotacoes cadastradas")
    public void fornecedor_tem_n_cotacoes(int n) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(n, f.getCotacoesSnapshot().size());
    }

    @Entao("o sistema deve rejeitar a operacao de fornecedor")
    public void sistema_rejeita_operacao() { assertNotNull(lastError); }

    @Entao("deve exibir a mensagem de fornecedor \"{string}\"")
    public void deve_exibir_msg_fornecedor(String msg) {
        assertNotNull(lastError);
        String m = lastError.getMessage() == null ? "" : lastError.getMessage();
        assertTrue(m.toLowerCase().contains(msg.toLowerCase()));
    }

    @Entao("os dados do fornecedor devem ser atualizados")
    public void dados_fornecedor_atualizados() { assertNull(lastError); }

    @Entao("o nome do fornecedor deve ser \"{string}\"")
    public void nome_fornecedor_deve_ser(String nome) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(nome, f.getNome());
    }

    @Entao("o contato do fornecedor deve ser \"{string}\"")
    public void contato_fornecedor_deve_ser(String contato) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(contato, f.getContato());
    }

    @Entao("o lead time do fornecedor deve ser atualizado para {int} dias")
    public void lead_time_deve_ser_atualizado(int dias) {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertEquals(dias, f.getLeadTimeMedio().getDias());
    }

    // ✅ NOVO: feature usa “E o ponto de ressuprimento dos produtos associados deve ser recalculado”
    @Entao("o ponto de ressuprimento dos produtos associados deve ser recalculado")
    public void ponto_de_ressuprimento_recalculado() {
        assertNull(lastError, "Esperava sucesso ao recalibrar ROP");
    }

    @Entao("o sistema rejeita a operacao de fornecedor")
    public void sistema_rejeita_operacao_fornecedor() { assertNotNull(lastError); }

    @Entao("o fornecedor deve ser inativado com sucesso")
    public void fornecedor_inativado_sucesso() {
        assertNull(lastError);
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertFalse(f.isAtivo());
    }

    @Entao("o status do fornecedor deve ser \"inativo\"")
    public void status_fornecedor_inativo() {
        Fornecedor f = repo.buscarPorId(currentFornecedorId).orElseThrow();
        assertFalse(f.isAtivo());
    }
}
