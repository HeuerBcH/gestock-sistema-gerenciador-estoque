package dev.gestock.sge.dominio.principal.alerta;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;

/**
 * Aggregate Root: Alerta
 *
 * Responsabilidades:
 * - Gerar alertas de estoque baixo
 * - Controlar status do alerta
 * - Manter informações do fornecedor sugerido
 * - Registrar data de geração
 */
public class Alerta {

    private final AlertaId id;
    private ProdutoId produto;
    private EstoqueId estoque;
    private FornecedorId fornecedorSugerido;
    private double quantidadeAtual;
    private double pontoRessuprimento;
    private boolean ativo;
    private LocalDateTime dataGeracao;

    public Alerta(ProdutoId produto, EstoqueId estoque, FornecedorId fornecedorSugerido,
                 double quantidadeAtual, double pontoRessuprimento) {
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(fornecedorSugerido, "Fornecedor sugerido é obrigatório");
        isTrue(quantidadeAtual >= 0, "Quantidade atual não pode ser negativa");
        isTrue(pontoRessuprimento > 0, "Ponto de ressuprimento deve ser positivo");

        this.id = new AlertaId();
        this.produto = produto;
        this.estoque = estoque;
        this.fornecedorSugerido = fornecedorSugerido;
        this.quantidadeAtual = quantidadeAtual;
        this.pontoRessuprimento = pontoRessuprimento;
        this.ativo = true;
        this.dataGeracao = LocalDateTime.now();
    }

    public Alerta(AlertaId id, ProdutoId produto, EstoqueId estoque, 
                  FornecedorId fornecedorSugerido, double quantidadeAtual, 
                  double pontoRessuprimento, boolean ativo, LocalDateTime dataGeracao) {
        notNull(id, "ID é obrigatório");
        notNull(produto, "Produto é obrigatório");
        notNull(estoque, "Estoque é obrigatório");
        notNull(fornecedorSugerido, "Fornecedor sugerido é obrigatório");
        isTrue(quantidadeAtual >= 0, "Quantidade atual não pode ser negativa");
        isTrue(pontoRessuprimento > 0, "Ponto de ressuprimento deve ser positivo");
        notNull(dataGeracao, "Data de geração é obrigatória");

        this.id = id;
        this.produto = produto;
        this.estoque = estoque;
        this.fornecedorSugerido = fornecedorSugerido;
        this.quantidadeAtual = quantidadeAtual;
        this.pontoRessuprimento = pontoRessuprimento;
        this.ativo = ativo;
        this.dataGeracao = dataGeracao;
    }

    /**
     * Gera um alerta de estoque baixo
     * R1H16: O alerta é gerado automaticamente ao atingir o ROP
     */
    public static Alerta gerarAlerta(ProdutoId produto, EstoqueId estoque, 
                                     FornecedorId fornecedorSugerido, 
                                     double quantidadeAtual, double pontoRessuprimento) {
        return new Alerta(produto, estoque, fornecedorSugerido, quantidadeAtual, pontoRessuprimento);
    }

    /**
     * Resolve o alerta (marca como inativo)
     * R1H17: Um alerta é removido automaticamente após o recebimento do pedido correspondente
     */
    public void resolverAlerta() {
        this.ativo = false;
    }

    /**
     * Verifica se o alerta está ativo
     */
    public boolean isAtivo() {
        return ativo;
    }

    /**
     * Verifica se o alerta está inativo
     */
    public boolean isInativo() {
        return !ativo;
    }

    /**
     * Verifica se o estoque está abaixo do ponto de ressuprimento
     */
    public boolean isEstoqueBaixo() {
        return quantidadeAtual <= pontoRessuprimento;
    }

    // Getters
    public AlertaId getId() { return id; }
    public ProdutoId getProduto() { return produto; }
    public EstoqueId getEstoque() { return estoque; }
    public FornecedorId getFornecedorSugerido() { return fornecedorSugerido; }
    public double getQuantidadeAtual() { return quantidadeAtual; }
    public double getPontoRessuprimento() { return pontoRessuprimento; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }

    @Override
    public String toString() {
        return String.format("Alerta[%s] - Produto: %s, Estoque: %s, Quantidade: %.2f, ROP: %.2f, Status: %s", 
                           id, produto, estoque, quantidadeAtual, pontoRessuprimento, 
                           ativo ? "ATIVO" : "INATIVO");
    }
}
