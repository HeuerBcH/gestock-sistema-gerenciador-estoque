package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

/**
 * Aggregate Root: Produto
 *
 * Responsável por representar o item gerenciado no estoque e armazenar o
 * ponto de ressuprimento (ROP), que define quando um novo pedido deve ser feito.
 *
 * Regras cobertas:
 * - R3: cálculo do ROP.
 * - R4: alerta quando o saldo atinge o ROP.
 */
public class Produto {

    private final ProdutoId id;        // Identificador único
    private String codigo;             // Código do produto (único - R29)
    private String nome;               // Nome descritivo
    private String categoria;          // Categoria para classificação ABC
    private boolean perecivel;         // Indica se exige controle de lote/validade
    private ROP rop;                   // Value Object: Ponto de Ressuprimento (R3, R4)

    // ------------------ Construtor ------------------

    public Produto(String codigo, String nome, String categoria, boolean perecivel) {
        notBlank(codigo, "Código é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(categoria, "Categoria é obrigatória");

        this.id = new ProdutoId();
        this.codigo = codigo;
        this.nome = nome;
        this.categoria = categoria;
        this.perecivel = perecivel;
        this.rop = null; // só é definido após o cálculo inicial
    }

    // ------------------ Métodos de domínio ------------------

    /**
     * (R3) Define ou recalcula o ROP com base nos parâmetros fornecidos.
     * Exemplo: ROP = (Consumo Médio × Lead Time) + Estoque de Segurança
     */
    public void definirROP(double consumoMedio, int leadTimeDias, int estoqueSeguranca) {
        this.rop = new ROP(consumoMedio, leadTimeDias, estoqueSeguranca);
    }

    /**
     * (R4) Verifica se o estoque atual atingiu ou ficou abaixo do ROP.
     * Retorna true se for necessário acionar reposição automática.
     */
    public boolean atingiuROP(int saldoAtual) {
        return rop != null && saldoAtual <= rop.getValorROP();
    }

    // ------------------ Getters ------------------

    public ProdutoId getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public boolean isPerecivel() { return perecivel; }
    public ROP getRop() { return rop; }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")" + (rop != null ? " | " + rop : "");
    }
}
