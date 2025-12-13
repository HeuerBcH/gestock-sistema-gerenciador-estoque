package dev.gestock.sge.apresentacao.principal.cliente;

public class ClienteForm {

    // DTO para cadastro de cliente
    public static class ClienteCadastroDto {
        private String nome;
        private String documento;
        private String email;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getDocumento() { return documento; }
        public void setDocumento(String documento) { this.documento = documento; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // DTO para atualização de cliente
    public static class ClienteAtualizacaoDto {
        private String nome;
        private String email;

        // Getters e Setters
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
