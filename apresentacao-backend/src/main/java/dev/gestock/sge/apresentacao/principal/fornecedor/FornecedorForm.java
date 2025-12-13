package dev.gestock.sge.apresentacao.principal.fornecedor;

public class FornecedorForm {

    // DTO para cadastro de fornecedor
    public static class FornecedorCadastroDto {
        private String nome;
        private String cnpj;
        private String contato;
        private int leadTimeDias;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }

        public String getContato() { return contato; }
        public void setContato(String contato) { this.contato = contato; }

        public int getLeadTimeDias() { return leadTimeDias; }
        public void setLeadTimeDias(int leadTimeDias) { this.leadTimeDias = leadTimeDias; }
    }

    // DTO para atualização de fornecedor
    public static class FornecedorAtualizacaoDto {
        private String nome;
        private String contato;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getContato() { return contato; }
        public void setContato(String contato) { this.contato = contato; }
    }

    // DTO para registro de cotação
    public static class CotacaoRegistroDto {
        private Long produtoId;
        private double preco;
        private int prazoDias;

        // Getters e Setters
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public double getPreco() { return preco; }
        public void setPreco(double preco) { this.preco = preco; }

        public int getPrazoDias() { return prazoDias; }
        public void setPrazoDias(int prazoDias) { this.prazoDias = prazoDias; }
    }
}
