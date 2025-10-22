package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.*;

// Aggregate Root: Produto
public class Produto {

    private final ProdutoId id;
    private String codigo;
    private String nome;
    private String unidadePeso;
    private double peso;
    private boolean perecivel;
    private boolean ativo;

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
    }

    //  Getters

    public ProdutoId getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNome() { return nome; }
    public String getUnidadePeso() { return unidadePeso; }
    public String getUnidadeMedida() { return unidadePeso; }
    public double getPeso() { return peso; }
    public boolean isPerecivel() { return perecivel; }
    public boolean isAtivo() { return ativo; }

    // Atualiza informações do produto (H9)
    public void atualizar(String nome, String unidadePeso, double peso) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadePeso, "Unidade de peso é obrigatória");
        isTrue(peso > 0, "Peso deve ser > 0");
        this.nome = nome;
        this.unidadePeso = unidadePeso;
        this.peso = peso;
    }

    // Compat: atualização sem alterar peso (mantém o atual)
    public void atualizar(String nome, String unidadePeso) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadePeso, "Unidade de peso é obrigatória");
        this.nome = nome;
        this.unidadePeso = unidadePeso;
    }

    /*
    Calcula o peso total para uma quantidade de itens deste produto.
    Ex.: se peso=0.5 (kg por unidade) e quantidade=3, retorna 1.5 (kg).
     */
    public double calcularPesoTotal(int quantidade) {
        isTrue(quantidade > 0, "Quantidade deve ser > 0");
        return peso * quantidade;
    }

    // Inativa o produto (H10, R1H10, R2H10) 
    public void inativar() {
        this.ativo = false;
    }

    // Reativa o produto 
    public void ativar() {
        this.ativo = true;
    }

    @Override
    public String toString() {
        return nome + " (" + codigo + ")";
    }
}
