package dev.gestock.sge.dominio.principal.fornecedor;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.*;

/**
 * Aggregate Root: Fornecedor
 *
 * Responsabilidades:
 * - Representar os dados cadastrais de um fornecedor.
 * - Manter as cotações (preço + prazo) associadas a produtos.
 * - Garantir regras de unicidade e atualização de lead time (R2, R14).
 * - Permitir escolha automática da melhor cotação (R5, R6).
 */
public class Fornecedor {

    private final FornecedorId id;                 // Identificador único do fornecedor
    private String nome;                           // Nome do fornecedor
    private String cnpj;                           // CNPJ ou identificador fiscal
    private String contato;                        // Contato do fornecedor
    private LeadTime leadTimeMedio;                // Lead time médio (R2H5, R1H6)
    private boolean ativo;                         // Status ativo/inativo (H7)
    private final Map<ProdutoId, Cotacao> cotacoes;// Cotações por produto

    // ------------------ Construtores ------------------

    public Fornecedor(FornecedorId id, String nome, String cnpj, String contato) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório");
        }
        notBlank(nome, "Nome do fornecedor é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");

        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.contato = contato;
        this.leadTimeMedio = new LeadTime(0);
        this.ativo = true; // inicia ativo por padrão
        this.cotacoes = new HashMap<>();
    }

    public Fornecedor(FornecedorId id, String nome, String cnpj, String contato, LeadTime leadTimeMedio) {
        notNull(id, "Id é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(cnpj, "CNPJ é obrigatório");

        this.id = id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.contato = contato;
        this.leadTimeMedio = leadTimeMedio != null ? leadTimeMedio : new LeadTime(0);
        this.ativo = true;
        this.cotacoes = new HashMap<>();
    }

    // ------------------ Getters ------------------

    public FornecedorId getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getContato() { return contato; }
    public LeadTime getLeadTimeMedio() { return leadTimeMedio; }
    public boolean isAtivo() { return ativo; }
    public Map<ProdutoId, Cotacao> getCotacoesSnapshot() { return Map.copyOf(cotacoes); }

    // ------------------ Métodos de domínio ------------------

    /** Atualiza dados cadastrais básicos */
    public void atualizarDados(String nome, String contato) {
        notBlank(nome, "Nome é obrigatório");
        this.nome = nome;
        this.contato = contato;
    }

    /** Registra ou atualiza cotação de produto (R2) */
    public void registrarCotacao(ProdutoId produtoId, double preco, int prazoDias) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(preco > 0, "Preço deve ser positivo");
        isTrue(prazoDias > 0, "Prazo deve ser positivo");

        // R2: apenas uma cotação por produto
        // NOTA: ID será gerado pela camada de persistência
        Cotacao nova = new Cotacao(new CotacaoId(1L), produtoId, preco, prazoDias);
        cotacoes.put(produtoId, nova);
    }

    /** Seleciona a melhor cotação entre todos os fornecedores (R5, R6) */
    public Optional<Cotacao> obterMelhorCotacao() {
        return cotacoes.values().stream()
                .min(Comparator.comparingDouble(Cotacao::getPreco)
                        .thenComparingInt(Cotacao::getPrazoDias));
    }

    /** Atualiza o Lead Time médio (R14) */
    public void recalibrarLeadTime(List<Integer> historicoEntregasDias) {
        if (historicoEntregasDias == null || historicoEntregasDias.isEmpty()) {
            throw new IllegalStateException("Histórico insuficiente para recalibrar lead time");
        }
        double media = historicoEntregasDias.stream().mapToInt(i -> i).average().orElse(0);
        this.leadTimeMedio = new LeadTime((int) Math.round(media));
    }

    /** Retorna a cotação de um produto específico */
    public Optional<Cotacao> obterCotacaoPorProduto(ProdutoId produtoId) {
        return Optional.ofNullable(cotacoes.get(produtoId));
    }

    /** Remove cotação de um produto */
    public void removerCotacao(ProdutoId produtoId) {
        cotacoes.remove(produtoId);
    }

    /** Inativa o fornecedor (H7, R1H7) */
    public void inativar() {
        this.ativo = false;
    }

    /** Reativa o fornecedor */
    public void ativar() {
        this.ativo = true;
    }

    @Override
    public String toString() {
        return String.format("%s (CNPJ: %s)", nome, cnpj);
    }
}
