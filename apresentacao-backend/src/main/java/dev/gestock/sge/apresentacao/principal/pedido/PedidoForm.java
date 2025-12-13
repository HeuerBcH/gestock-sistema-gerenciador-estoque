package dev.gestock.sge.apresentacao.principal.pedido;

public class PedidoForm {

    // DTO para criação de pedido
    public static class PedidoCriacaoDto {
        private Long clienteId;
        private Long fornecedorId;
        private Long produtoId;
        private int quantidade;
        private Long estoqueId; // Opcional

        // Getters e Setters
        public Long getClienteId() { return clienteId; }
        public void setClienteId(Long clienteId) { this.clienteId = clienteId; }

        public Long getFornecedorId() { return fornecedorId; }
        public void setFornecedorId(Long fornecedorId) { this.fornecedorId = fornecedorId; }

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public Long getEstoqueId() { return estoqueId; }
        public void setEstoqueId(Long estoqueId) { this.estoqueId = estoqueId; }
    }

    // DTO para confirmação de recebimento
    public static class RecebimentoDto {
        private Long estoqueId;
        private String responsavel;

        // Getters e Setters
        public Long getEstoqueId() { return estoqueId; }
        public void setEstoqueId(Long estoqueId) { this.estoqueId = estoqueId; }

        public String getResponsavel() { return responsavel; }
        public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    }

    // DTO para cancelamento com liberação
    public static class CancelamentoDto {
        private Long estoqueId;
        private Long produtoId;
        private int quantidade;

        // Getters e Setters
        public Long getEstoqueId() { return estoqueId; }
        public void setEstoqueId(Long estoqueId) { this.estoqueId = estoqueId; }

        public Long getProdutoId() { return produtoId; }
        public void setProdutoId(Long produtoId) { this.produtoId = produtoId; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }
}
