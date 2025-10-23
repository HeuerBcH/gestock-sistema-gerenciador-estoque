package dev.gestock.sge.dominio.principal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import io.cucumber.java.Before;
import io.cucumber.java.pt.*;
import dev.gestock.sge.dominio.principal.produto.*;
import dev.gestock.sge.dominio.principal.fornecedor.*;
import dev.gestock.sge.dominio.principal.alerta.*;
import dev.gestock.sge.dominio.principal.estoque.*;
import dev.gestock.sge.dominio.principal.cliente.*;

import dev.gestock.sge.infraestrutura.persistencia.memoria.Repositorio;

public class EmitirAlertasEstoqueBaixoFeature {

    // ===== Estado por cenário =====
    private Map<String, Produto> produtos;
    private Map<String, Alerta> alertas;
    private Map<String, Fornecedor> fornecedores;
    private List<Alerta> alertasAtivos;
    private Integer quantidadeRecebida;
    private Produto produtoAtual;
    private Alerta alertaAtual;
    private Fornecedor fornecedorAtual;
    private FornecedorServico fornecedorServico;
    private Fornecedor melhorFornecedorSelecionado;
    private Estoque estoqueAtual;
    private int saldoAtual;
    private int ropAtual;
    private Exception lastError;

    private AtomicInteger seq;

    private Repositorio repo;
    private AlertaServico alertaServico;

    @Before
    public void reset() {
        produtos = new HashMap<>();
        alertas = new HashMap<>();
        fornecedores = new HashMap<>();
        alertasAtivos = new ArrayList<>();
        quantidadeRecebida = null;
        produtoAtual = null;
        alertaAtual = null;
        fornecedorAtual = null;
        estoqueAtual = null;
        saldoAtual = 0;
        ropAtual = 0;
        lastError = null;
        seq = new AtomicInteger(1);
        repo = new Repositorio();
        repo.limparTodos();
        alertaServico = new AlertaServico(repo);
        fornecedorServico = new FornecedorServico(repo);
        melhorFornecedorSelecionado = null;
    }

    // ===== Givens =====

    @Dado("que existe um produto com ROP de {int} unidades")
    public void existe_produto_com_rop(int rop) {
        String nome = "Produto" + seq.getAndIncrement();
        ropAtual = rop;
        ProdutoId pid = new ProdutoId((long) seq.getAndIncrement());
        produtoAtual = new Produto(pid, "COD" + pid.getId(), nome, "kg", false, 1.0);
        produtos.put(nome, produtoAtual);
        // Cria um estoque básico associado a um cliente para suportar alertas
        ClienteId clienteId = new ClienteId((long) seq.getAndIncrement());
        EstoqueId eid = new EstoqueId((long) seq.getAndIncrement());
        estoqueAtual = new Estoque(eid, clienteId, "Estoque Principal", "Endereco", 1000);
        // Opcional: define ROP no estoque para o produto
        estoqueAtual.definirROP(produtoAtual.getId(), rop, 1, 0);
        // Persistir entidades necessárias no repositório
        repo.salvar(produtoAtual);
        repo.salvar(estoqueAtual);
    }

    @E("o saldo atual do produto e {int} unidades")
    public void saldo_atual_produto(int saldo) {
        saldoAtual = saldo;
    }

    @Dado("que existe um alerta gerado para um produto")
    public void existe_alerta_gerado_para_produto() {
        existe_produto_com_rop(100);
        saldo_atual_produto(90);
        FornecedorId fid = new FornecedorId((long) seq.getAndIncrement());
        fornecedorAtual = new Fornecedor(fid, "Fornecedor1", "00000000000191", "contato@forn.com");
        fornecedores.put(fornecedorAtual.getNome(), fornecedorAtual);
        repo.salvar(fornecedorAtual);
        // Gerar alerta via serviço de domínio (SUT)
        alertaAtual = alertaServico.gerarAlerta(produtoAtual.getId(), estoqueAtual.getId(), fornecedorAtual.getId());
        alertas.put(produtoAtual.getNome(), alertaAtual);
    }

    @E("o fornecedor sugerido possui cotacao valida e ativa")
    public void fornecedor_sugerido_cotacao_valida_ativa() {
        // Monta múltiplos fornecedores com cotações para o mesmo produto
        List<Fornecedor> candidatos = new ArrayList<>();

        Fornecedor f1 = new Fornecedor(new FornecedorId((long) seq.getAndIncrement()), "Fornecedor1", "00000000000191", "contato@f1.com");
        f1.registrarCotacao(produtoAtual.getId(), 12.50, 7); // preço menor, prazo 7
        repo.salvar(f1);
        candidatos.add(f1);

        Fornecedor f2 = new Fornecedor(new FornecedorId((long) seq.getAndIncrement()), "Fornecedor2", "00000000000192", "contato@f2.com");
        f2.registrarCotacao(produtoAtual.getId(), 13.00, 5); // preço maior, prazo 5
        repo.salvar(f2);
        candidatos.add(f2);

        Fornecedor f3 = new Fornecedor(new FornecedorId((long) seq.getAndIncrement()), "Fornecedor3", "00000000000193", "contato@f3.com");
        f3.registrarCotacao(produtoAtual.getId(), 12.50, 10); // mesmo preço que f1, prazo pior
        repo.salvar(f3);
        candidatos.add(f3);

        // Seleciona a melhor cotação via SUT (menor preço; em empate, menor prazo)
        Optional<Cotacao> melhorCotacao = fornecedorServico.selecionarMelhorCotacao(candidatos, produtoAtual.getId());
        assertTrue(melhorCotacao.isPresent(), "Nenhuma cotação válida encontrada");

        Cotacao escolhida = melhorCotacao.get();
        // Descobre o fornecedor da melhor cotação por identidade da Cotacao (mesma instância)
        melhorFornecedorSelecionado = candidatos.stream()
                .filter(f -> f.obterCotacaoPorProduto(produtoAtual.getId()).isPresent())
                .filter(f -> f.obterCotacaoPorProduto(produtoAtual.getId()).get() == escolhida)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Fornecedor da melhor cotação não encontrado"));

        alertaAtual.atualizarFornecedorSugerido(melhorFornecedorSelecionado.getId());
        repo.salvar(alertaAtual);
    }

    @Dado("que existem {int} alertas ativos")
    public void existem_alertas_itos(int qtd) {
        // Cria entidades e gera alertas via serviço, persistindo no repositório
        for (int i = 1; i <= qtd; i++) {
            ProdutoId pid = new ProdutoId((long) seq.getAndIncrement());
            Produto p = new Produto(pid, "COD" + pid.getId(), "Produto" + i, "kg", false, 1.0);
            repo.salvar(p);
            FornecedorId fid = new FornecedorId((long) seq.getAndIncrement());
            Fornecedor f = new Fornecedor(fid, "Fornecedor" + i, "00000000000" + i, "contato@f.com");
            repo.salvar(f);
            ClienteId cid = new ClienteId((long) seq.getAndIncrement());
            EstoqueId eid = new EstoqueId((long) seq.getAndIncrement());
            Estoque est = new Estoque(eid, cid, "Estoque" + i, "Endereco", 1000);
            repo.salvar(est);
            Alerta a = alertaServico.gerarAlerta(p.getId(), est.getId(), f.getId());
            alertasAtivos.add(a);
        }
    }

    @Dado("que existe um alerta ativo para um produto")
    public void existe_alerta_ativo_para_produto() {
        existe_produto_com_rop(100);
        saldo_atual_produto(90);
        FornecedorId fid = new FornecedorId((long) seq.getAndIncrement());
        fornecedorAtual = new Fornecedor(fid, "Fornecedor1", "00000000000191", "contato@forn.com");
        repo.salvar(fornecedorAtual);
        alertaAtual = alertaServico.gerarAlerta(produtoAtual.getId(), estoqueAtual.getId(), fornecedorAtual.getId());
        alertas.put(produtoAtual.getNome(), alertaAtual);
    }

    @E("um pedido foi recebido para suprir o estoque do produto")
    public void pedido_recebido_para_produto() {
        quantidadeRecebida = 20;
    }

    // ===== Whens =====

    @Quando("o sistema verifica o estoque")
    public void sistema_verifica_estoque() {
        lastError = null;
        try {
            if (saldoAtual <= ropAtual) {
                // Gera alerta via serviço de domínio para simular verificação do estoque
                FornecedorId fid = new FornecedorId((long) seq.getAndIncrement());
                fornecedorAtual = new Fornecedor(fid, "Fornecedor1", "00000000000191", "contato@forn.com");
                repo.salvar(fornecedorAtual);
                alertaAtual = alertaServico.gerarAlerta(produtoAtual.getId(), estoqueAtual.getId(), fornecedorAtual.getId());
                alertas.put(produtoAtual.getNome(), alertaAtual);
            }
        } catch (Exception e) {
            lastError = e;
        }
    }

    @Quando("o cliente visualiza o alerta")
    public void cliente_visualiza_alerta() {
        // Simula visualização, nada a fazer
    }

    @Quando("o cliente visualiza a lista de alertas")
    public void cliente_visualiza_lista_alertas() {
        // Consulta os alertas ativos no repositório (SUT)
        alertasAtivos = repo.listarAtivos();
    }

    @Quando("o sistema atualiza o estoque")
    public void sistema_atualiza_estoque() {
        lastError = null;
        try {
            if (quantidadeRecebida != null) {
                saldoAtual += quantidadeRecebida;
                // Remove alerta se estoque suprido
                if (alertaAtual != null && saldoAtual > ropAtual) {
                    alertaServico.desativarAlerta(alertaAtual);
                }
            }
        } catch (Exception e) {
            lastError = e;
        }
    }

    // ===== Thens =====

    @Entao("um alerta deve ser gerado automaticamente")
    public void alerta_gerado_automaticamente() {
        // Verifica no repositório se há alerta ativo para o produto
        List<Alerta> porProduto = repo.listarPorProduto(produtoAtual.getId());
        assertFalse(porProduto.isEmpty(), "Alerta não foi gerado automaticamente");
        assertTrue(porProduto.stream().anyMatch(Alerta::isAtivo), "Alerta não está ativo");
    }

    @Entao("um alerta deve ser gerado")
    public void alerta_gerado() {
        alerta_gerado_automaticamente();
    }

    @Entao("nenhum alerta deve ser gerado")
    public void nenhum_alerta_gerado() {
        List<Alerta> porProduto = repo.listarPorProduto(produtoAtual.getId());
        assertTrue(porProduto.isEmpty(), "Alerta foi gerado indevidamente");
    }

    @Entao("o sistema deve exibir o nome do produto")
    public void sistema_exibe_nome_produto() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertEquals(produtoAtual.getId(), alertaAtual.getProdutoId(), "Produto do alerta incorreto");
    }

    @E("o sistema deve exibir o estoque afetado")
    public void sistema_exibe_estoque_afetado() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertEquals(estoqueAtual.getId(), alertaAtual.getEstoqueId(), "Estoque afetado não exibido corretamente");
    }

    @E("o sistema deve exibir o fornecedor com menor cotacao")
    public void sistema_exibe_fornecedor_menor_cotacao() {
        assertNotNull(alertaAtual, "Alerta não existe");
        assertNotNull(alertaAtual.getFornecedorSugerido(), "Fornecedor sugerido não exibido");
        assertNotNull(melhorFornecedorSelecionado, "Melhor fornecedor não foi selecionado");
        assertEquals(melhorFornecedorSelecionado.getId(), alertaAtual.getFornecedorSugerido(),
                "Fornecedor sugerido não corresponde à menor cotação");
    }

    @Entao("o sistema deve exibir {int} alertas")
    public void sistema_exibe_qtd_alertas(int qtd) {
        // Consulta o repositório para obter os alertas ativos reais
        List<Alerta> ativos = repo.listarAtivos();
        assertEquals(qtd, ativos.size(), "Quantidade de alertas exibidos incorreta");
    }

    @Entao("o alerta deve ser removido automaticamente")
    public void alerta_removido_automaticamente() {
        assertNotNull(alertaAtual, "Alerta não existe");
        // Recarrega do repositório para garantir persistência do estado
        Optional<Alerta> recarregado = repo.obter(alertaAtual.getId());
        assertTrue(recarregado.isPresent(), "Alerta não encontrado no repositório");
        assertFalse(recarregado.get().isAtivo(), "Alerta não foi removido automaticamente");
    }
}