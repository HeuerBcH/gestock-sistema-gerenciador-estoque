package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;

import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

public class GerenciarEstoqueFeature {

    private Repositorio repo;
    private EstoqueServico estoqueSrv;

    private AtomicLong seq;

    // aliases
    private Map<String, EstoqueId> aliasEstoque;
    private Map<String, ProdutoId> aliasProduto;

    // estado corrente
    private ClienteId currentClienteId;
    private EstoqueId currentEstoqueId;
    private Exception lastError;

    @Before
    public void reset() {
        repo = new Repositorio();
        estoqueSrv = new EstoqueServico(repo, repo);
        seq = new AtomicLong(1);
        aliasEstoque = new HashMap<>();
        aliasProduto = new HashMap<>();
        currentClienteId = null;
        currentEstoqueId = null;
        lastError = null;
    }

    // utils
    private ClienteId clienteByString(String s) {
        if (s == null) return new ClienteId(1L);
        try {
            long v = Long.parseLong(s);
            return new ClienteId(v);
        } catch (Exception e) {
            return new ClienteId(1L);
        }
    }

    private EstoqueId ensureEstoque(String nome, String endereco, int capacidade) {
        String key = nome + "|" + endereco;
        return aliasEstoque.computeIfAbsent(key, k -> {
            EstoqueId id = repo.novoEstoqueId();
            ClienteId cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            Estoque e = new Estoque(id, cid, nome, endereco, capacidade);
            repo.salvar(e);
            return id;
        });
    }

    private ProdutoId ensureProduto(String idStr) {
        return aliasProduto.computeIfAbsent(idStr, k -> {
            ProdutoId pid = repo.novoProdutoId();
            dev.gestock.sge.dominio.principal.produto.Produto p =
                new dev.gestock.sge.dominio.principal.produto.Produto(pid, "PROD-"+pid.getId(), "Produto " + pid.getId(), "un", false, 1.0);
            repo.salvar(p);
            return pid;
        });
    }

    // ===== DADOS =====

    @Dado("que existe um cliente com id {string}")
    public void existe_cliente_com_id(String id) {
        currentClienteId = clienteByString(id);
        dev.gestock.sge.dominio.principal.cliente.Cliente c =
            new dev.gestock.sge.dominio.principal.cliente.Cliente(repo.novoClienteId(), "Cliente "+currentClienteId.getId(), "doc", "email@ex.com");
        repo.salvar(c);
    }

    @Dado("ja existe um estoque chamado {string} no endereco {string}")
    public void ja_existe_estoque_com_nome_endereco(String nome, String endereco) {
        ensureEstoque(nome, endereco, 1000);
    }

    // ✅ NOVO: faltava step simples sem endereco
    @Dado("ja existe um estoque chamado {string}")
    public void ja_existe_estoque_simples(String nome) {
        ensureEstoque(nome, "Endereco padrao " + seq.getAndIncrement(), 1000);
    }

    @Dado("ja existe um estoque no endereco {string}")
    public void ja_existe_estoque_no_endereco(String endereco) {
        ensureEstoque("Existente", endereco, 1000);
    }

    @Dado("existe um estoque chamado {string} sem produtos")
    public void existe_estoque_sem_produtos(String nome) {
        currentEstoqueId = ensureEstoque(nome, "Rua X, " + seq.getAndIncrement(), 500);
    }

    @Dado("existe um estoque chamado {string} com produtos")
    public void existe_estoque_com_produtos(String nome) {
        currentEstoqueId = ensureEstoque(nome, "End " + seq.getAndIncrement(), 500);
        Estoque e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        ProdutoId pid = ensureProduto("01");
        e.registrarEntrada(pid, 10, "Teste", "Entrada inicial", null);
        repo.salvar(e);
    }

    @Dado("o produto {string} tem saldo fisico de {int} unidades")
    public void produto_tem_saldo_fisico(String produtoAlias, int qtd) {
        ProdutoId pid = ensureProduto(produtoAlias);
        Estoque e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        e.registrarEntrada(pid, qtd, "Teste", "setup", null);
        repo.salvar(e);
    }

    @Dado("nao existem estoques cadastrados")
    public void nao_existem_estoques_cadastrados() {
        repo.limparTodos();
    }

    @Dado("existem estoques cadastrados")
    public void existem_estoques_cadastrados() {
        ensureEstoque("Estoque Central", "Rua A, 123", 1000);
        ensureEstoque("Outro", "Rua B, 200", 500);
    }

    // ✅ NOVO: faltava suporte a "existe um pedido pendente alocado ao estoque"
    @Dado("existe um pedido pendente alocado ao estoque")
    public void existe_pedido_pendente_estoque() {
        Estoque e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        dev.gestock.sge.dominio.principal.pedido.PedidoId pid = repo.novoPedidoId();
        dev.gestock.sge.dominio.principal.pedido.Pedido p =
            new dev.gestock.sge.dominio.principal.pedido.Pedido(pid, e.getClienteId(), null);
        // supondo status padrão CRIADO como pendente
        repo.salvar(p);
    }

    // ✅ NOVO: faltava suporte a "existe um estoque chamado X com capacidade N"
    @Dado("existe um estoque chamado {string} com capacidade {int}")
    public void existe_estoque_com_capacidade(String nome, int cap) {
        currentEstoqueId = ensureEstoque(nome, "Rua Y, " + seq.getAndIncrement(), cap);
    }

    // ✅ NOVO: faltava suporte a "o estoque esta com X unidades armazenadas"
    @Dado("o estoque esta com {int} unidades armazenadas")
    public void estoque_com_unidades_armazenadas(int qtd) {
        Estoque e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        ProdutoId pid = ensureProduto("01");
        e.registrarEntrada(pid, qtd, "Teste", "ocupando capacidade", null);
        repo.salvar(e);
    }

    // ===== QUANDOS =====

    @Quando("o cliente cadastra um estoque com nome {string}, endereco {string} e capacidade {int}")
    public void cliente_cadastra_estoque(String nome, String endereco, int capacidade) {
        lastError = null;
        try {
            ClienteId cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            EstoqueId id = repo.novoEstoqueId();
            Estoque e = new Estoque(id, cid, nome, endereco, capacidade);
            estoqueSrv.cadastrar(e);
            currentEstoqueId = id;
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta cadastrar um estoque com endereco {string}")
    public void tenta_cadastrar_endereco_duplicado(String endereco) {
        lastError = null;
        try {
            ClienteId cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            EstoqueId id = repo.novoEstoqueId();
            Estoque e = new Estoque(id, cid, "Novo", endereco, 100);
            estoqueSrv.cadastrar(e);
            currentEstoqueId = id;
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta cadastrar um estoque com nome {string}")
    public void tenta_cadastrar_nome_duplicado(String nome) {
        lastError = null;
        try {
            ClienteId cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            EstoqueId id = repo.novoEstoqueId();
            Estoque e = new Estoque(id, cid, nome, "Rua Y, 999", 100);
            estoqueSrv.cadastrar(e);
            currentEstoqueId = id;
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente inativa o estoque {string}")
    public void cliente_inativa_estoque(String nome) {
        lastError = null;
        try {
            for (Estoque e : repo.buscarEstoquesPorClienteId((currentClienteId != null) ? currentClienteId : new ClienteId(1L))) {
                if (e.getNome().equalsIgnoreCase(nome)) {
                    estoqueSrv.inativar(e);
                    repo.salvar(e);
                    currentEstoqueId = e.getId();
                    return;
                }
            }
            throw new IllegalStateException("Estoque não encontrado: " + nome);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente tenta inativar o estoque {string}")
    public void cliente_tenta_inativar_estoque(String nome) { cliente_inativa_estoque(nome); }

    @Quando("o cliente tenta alterar a capacidade do estoque para {int}")
    public void tenta_alterar_capacidade(int novaCap) {
        lastError = null;
        try {
            Estoque e = repo.buscarPorId(currentEstoqueId).orElseThrow();
            e.alterarCapacidade(novaCap);
            repo.salvar(e);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente realiza uma pesquisa de estoques")
    public void cliente_pesquisa_estoques() {
        lastError = null;
        try {
            var cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            estoqueSrv.pesquisarPorCliente(cid);
        } catch (Exception ex) { lastError = ex; }
    }

    @Quando("o cliente pesquisa pelo nome {string} e endereco {string}")
    public void cliente_pesquisa_nome_endereco(String nome, String endereco) {
        lastError = null;
        try {
            var cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
            var list = repo.buscarEstoquesPorClienteId(cid);
            boolean found = list.stream().anyMatch(e -> e.getNome().equalsIgnoreCase(nome) && e.getEndereco().equalsIgnoreCase(endereco));
            if (!found) throw new IllegalStateException("Nenhum estoque encontrado");
        } catch (Exception ex) { lastError = ex; }
    }

    // ===== ENTAOS =====

    @Entao("o estoque deve ser cadastrado com sucesso")
    public void estoque_cadastrado_com_sucesso() {
        assertNull(lastError, "Esperava sucesso no cadastro: " + (lastError == null ? "" : lastError.getMessage()));
        assertNotNull(currentEstoqueId, "Id do estoque não foi definido");
        assertTrue(repo.buscarPorId(currentEstoqueId).isPresent(), "Estoque não encontrado no repositório");
    }

    @Entao("o estoque deve estar ativo")
    public void estoque_deve_estar_ativo() {
        var e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        assertTrue(e.isAtivo(), "Esperava estoque ativo");
    }

    @Entao("o estoque deve estar visivel na listagem de estoques")
    public void estoque_visivel_listagem() {
        var cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
        var list = repo.buscarEstoquesPorClienteId(cid);
        assertTrue(list.stream().anyMatch(e -> e.getId().equals(currentEstoqueId)), "Estoque não aparece na listagem");
    }

    @Entao("o estoque deve pertencer ao cliente com id {string}")
    public void estoque_pertence_cliente(String id) {
        var cid = clienteByString(id);
        var e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        assertEquals(cid, e.getClienteId(), "Estoque não pertence ao cliente esperado");
    }

    @Entao("devem existir {int} estoques cadastrados para o cliente")
    public void devem_existir_n_estoques(int n) {
        var cid = (currentClienteId != null) ? currentClienteId : new ClienteId(1L);
        var list = repo.buscarEstoquesPorClienteId(cid);
        assertEquals(n, list.size(), "Quantidade de estoques divergente");
    }

    @Entao("o sistema deve rejeitar o cadastro de estoque")
    public void sistema_rejeita_cadastro() { assertNotNull(lastError, "Esperava erro no cadastro"); }

    @Entao("deve exibir a mensagem de estoque \"{string}\"")
    public void deve_exibir_mensagem_estoque(String msg) {
        assertNotNull(lastError, "Esperava erro para verificar mensagem");
        String m = lastError.getMessage() == null ? "" : lastError.getMessage();
        assertTrue(m.toLowerCase().contains(msg.toLowerCase()), "Mensagem esperada: " + msg + " - obtida: " + m);
    }

    @Entao("o estoque deve ser inativado com sucesso")
    public void estoque_inativado_sucesso() {
        assertNull(lastError, "Esperava sucesso na inativação: " + (lastError==null?"":lastError.getMessage()));
        var e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        assertFalse(e.isAtivo(), "Estoque deveria estar inativo");
    }

    @Entao("o status do estoque deve ser \"inativo\"")
    public void status_deve_ser_inativo() {
        var e = repo.buscarPorId(currentEstoqueId).orElseThrow();
        assertFalse(e.isAtivo(), "Status esperado: inativo");
    }

    @Entao("o sistema deve impedir a operacao")
    public void sistema_impede_operacao() { assertNotNull(lastError, "Esperava erro impedindo a operação"); }

    @Entao("deve exibir a mensagem de estoque \"Estoque com produtos nao pode ser inativado\"")
    public void msg_estoque_com_produtos() { deve_exibir_mensagem_estoque("Estoque com produtos nao pode ser inativado"); }

    @Entao("o sistema deve impedir a alteracao")
    public void sistema_impede_alteracao() { assertNotNull(lastError, "Esperava erro na alteração"); }

    @Entao("deve exibir a mensagem de estoque \"Nao e possivel reduzir capacidade de estoque cheio\"")
    public void msg_reduzir_capacidade_estoque_cheio() { deve_exibir_mensagem_estoque("Nao e possivel reduzir capacidade de estoque cheio"); }

    @Entao("o sistema deve exibir a mensagem \"Nenhum estoque cadastrado\"")
    public void msg_nenhum_estoque_cadastrado() { deve_exibir_mensagem_estoque("Nenhum estoque cadastrado"); }

    // ✅ NOVO: faltava para "o sistema deve exibir o estoque correspondente"
    @Entao("o sistema deve exibir o estoque correspondente")
    public void sistema_exibe_estoque_correspondente() {
        assertNull(lastError, "Esperava sucesso na pesquisa");
    }
}
