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
    private String codigo;             // Código do produto (único - R1H8)
    private String nome;               // Nome descritivo
    private String unidadeMedida;      // Unidade de medida (H8, H9)
    private boolean perecivel;         // Indica se exige controle de lote/validade
    private boolean ativo;             // Status ativo/inativo (H10)
    private ROP rop;                   // Value Object: Ponto de Ressuprimento (R3, R4)

    // ------------------ Construtor ------------------

    public Produto(ProdutoId id, String codigo, String nome, String unidadeMedida, boolean perecivel) {
        if (id == null) {
            throw new IllegalArgumentException("ID é obrigatório");
        }
        notBlank(codigo, "Código é obrigatório");
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");

        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
        this.perecivel = perecivel;
        this.ativo = true; // inicia ativo por padrão
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
    public String getUnidadeMedida() { return unidadeMedida; }
    public boolean isPerecivel() { return perecivel; }
    public boolean isAtivo() { return ativo; }
    public ROP getRop() { return rop; }

    /** Atualiza informações do produto (H9) */
    public void atualizar(String nome, String unidadeMedida) {
        notBlank(nome, "Nome é obrigatório");
        notBlank(unidadeMedida, "Unidade de medida é obrigatória");
        this.nome = nome;
        this.unidadeMedida = unidadeMedida;
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
