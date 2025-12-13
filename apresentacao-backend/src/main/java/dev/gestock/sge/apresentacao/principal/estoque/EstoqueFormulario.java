package dev.gestock.sge.apresentacao.principal.estoque;

import java.util.Map;

public class EstoqueFormulario {

    // DTO para cadastro de estoque
    public static class EstoqueCadastroDto {
        private Long clienteId;
        private String nome;
        private String endereco;
        private int capacidade;

        // Getters e Setters
        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEndereco() { return endereco; }
        public void setEndereco(String endereco) { this.endereco = endereco; }

        public int getCapacidade() { return capacidade; }
        public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    }

    // DTO para atualização de capacidade
    public static class CapacidadeAtualizacaoDto {
        private int novaCapacidade;

        // Getters e Setters
        public int getNovaCapacidade() { return novaCapacidade; }
        public void setNovaCapacidade(int novaCapacidade) { this.novaCapacidade = novaCapacidade; }
    }

    // DTO para entrada de produtos
    public static class EntradaDto {
        private Long produtoId;
        private int quantidade;
        private String responsavel;
        private String motivo;
        private Map<String, String> metadados;

        // Getters e Setters
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getResponsavel() { return responsavel; }
        public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }

        public Map<String, String> getMetadados() { return metadados; }
        public void setMetadados(Map<String, String> metadados) { this.metadados = metadados; }
    }

    // DTO para saída de produtos
    public static class SaidaDto {
        private Long produtoId;
        private int quantidade;
        private String responsavel;
        private String motivo;

        // Getters e Setters
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getResponsavel() { return responsavel; }
        public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    // DTO para transferência entre estoques
    public static class TransferenciaDto {
        private Long estoqueOrigemId;
        private Long estoqueDestinoId;
        private Long produtoId;
        private int quantidade;
        private String responsavel;
        private String motivo;

        // Getters e Setters
        public Long getEstoqueOrigemId() { return estoqueOrigemId; }
        public void setEstoqueOrigemId(Long estoqueOrigemId) { this.estoqueOrigemId = estoqueOrigemId; }

        public Long getEstoqueDestinoId() { return estoqueDestinoId; }
        public void setEstoqueDestinoId(Long estoqueDestinoId) { this.estoqueDestinoId = estoqueDestinoId; }

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getResponsavel() { return responsavel; }
        public void setResponsavel(String responsavel) { this.responsavel = responsavel; }

        public String getMotivo() { return motivo; }
        public void setMotivo(String motivo) { this.motivo = motivo; }
    }

    // DTO para definição de ROP
    public static class RopDefinicaoDto {
        private Long produtoId;
        private double consumoMedio;
        private int leadTimeDias;
        private int estoqueSeguranca;

        // Getters e Setters
        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public double getConsumoMedio() { return consumoMedio; }
        public void setConsumoMedio(double consumoMedio) { this.consumoMedio = consumoMedio; }

        public int getLeadTimeDias() { return leadTimeDias; }
        public void setLeadTimeDias(int leadTimeDias) { this.leadTimeDias = leadTimeDias; }

        public int getEstoqueSeguranca() { return estoqueSeguranca; }
        public void setEstoqueSeguranca(int estoqueSeguranca) { this.estoqueSeguranca = estoqueSeguranca; }
    }
}
