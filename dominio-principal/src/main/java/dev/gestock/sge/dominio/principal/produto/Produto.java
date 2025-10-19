package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

/* Aggregate Root: Produto
   Responsável por representar o item gerenciado no estoque e armazenar o
   ponto de ressuprimento (ROP), que define quando um novo pedido deve ser feito.

   Regras cobertas:
   R3: cálculo do ROP.
   R4: alerta quando o saldo atinge o ROP. */
public class Produto {

    private final ProdutoId id;
    private String codigo;
    private String nome;
    private String unidadePeso; // ex.: kg, g, lb
    private double peso;        // peso por unidade do produto, na unidadePeso
    private boolean perecivel;
    private boolean ativo;
    private ROP rop;

    // Construtor

    public Produto(ProdutoId id, String codigo, String nome, String unidadePeso, boolean perecivel, double peso) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório");
        }
        notBlank(codigo, "Código é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadePeso, "Unidade de peso é obrigatória");
        isTrue(peso > 0, "Peso deve ser > 0");

        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.unidadePeso = unidadePeso;
        this.peso = peso;
        this.perecivel = perecivel;
        this.ativo = true; // inicia ativo por padrão
        this.rop = null; // só é definido após o cálculo inicial
    }

    // Métodos de domínio

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
    public String getUnidadePeso() { return unidadePeso; }
    /** Compat: mantém antigo nome do getter. */
    public String getUnidadeMedida() { return unidadePeso; }
    public double getPeso() { return peso; }
    public boolean isPerecivel() { return perecivel; }
    public boolean isAtivo() { return ativo; }
    public ROP getRop() { return rop; }

    /** Atualiza informações do produto (H9) */
    public void atualizar(String nome, String unidadePeso, double peso) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadePeso, "Unidade de peso é obrigatória");
        isTrue(peso > 0, "Peso deve ser > 0");
        this.nome = nome;
        this.unidadePeso = unidadePeso;
        this.peso = peso;
    }

    /** Compat: atualização sem alterar peso (mantém o atual). */
    public void atualizar(String nome, String unidadePeso) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadePeso, "Unidade de peso é obrigatória");
        this.nome = nome;
        this.unidadePeso = unidadePeso;
    }

    /**
     * Calcula o peso total para uma quantidade de itens deste produto.
     * Ex.: se peso=0.5 (kg por unidade) e quantidade=3, retorna 1.5 (kg).
     */
    public double calcularPesoTotal(int quantidade) {
        isTrue(quantidade > 0, "Quantidade deve ser > 0");
        return peso * quantidade;
    }

    /** Inativa o produto (H10, R1H10, R2H10) */
    public void inativar() {
        this.ativo = false;
    }

    /** Reativa o produto */
    public void ativar() {
        this.ativo = true;
    }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")" + (rop != null ? " | " + rop : "");
    }
}
