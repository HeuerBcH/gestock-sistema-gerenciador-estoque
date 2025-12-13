package dev.gestock.sge.apresentacao.principal.produto;

public class ProdutoForm {

    // DTO para cadastro de produto
    public static class ProdutoCadastroDto {
        private String codigo;
        private String nome;
        private String unidadePeso;
        private double peso;
        private boolean perecivel;

        // Getters e Setters
        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getUnidadePeso() { return unidadePeso; }
        public void setUnidadePeso(String unidadePeso) { this.unidadePeso = unidadePeso; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }

        public boolean isPerecivel() { return perecivel; }
        public void setPerecivel(boolean perecivel) { this.perecivel = perecivel; }
    }

    // DTO para atualização de produto
    public static class ProdutoAtualizacaoDto {
        private String nome;
        private String unidadePeso;
        private double peso;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getUnidadePeso() { return unidadePeso; }
        public void setUnidadePeso(String unidadePeso) { this.unidadePeso = unidadePeso; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }
    }
}
