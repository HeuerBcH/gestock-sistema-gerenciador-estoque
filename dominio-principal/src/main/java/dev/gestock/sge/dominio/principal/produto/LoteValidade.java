package dev.gestock.sge.dominio.principal.produto;

import static org.apache.commons.lang3.Validate.notBlank;
import java.time.LocalDate;

/* Value Object: Lote e validade de produtos perecíveis. (R25, R27, R30) -> rastreabilidade e alertas de vencimento */
public class LoteValidade {
    private final String lote;
    private final LocalDate validade;

    public LoteValidade(String lote, LocalDate validade) {
        notBlank(lote, "Lote é obrigatório");
        if (validade == null || validade.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Data de validade inválida");
        this.lote = lote.trim().toUpperCase();
        this.validade = validade;
    }

    public String getLote() { return lote; }
    public LocalDate getValidade() { return validade; }

    /* Retorna true se o produto vence em até 7 dias (alerta R27). */
    public boolean validadeProxima() {
        return validade.isBefore(LocalDate.now().plusDays(7));
    }

    @Override
    public String toString() {
        return "Lote " + lote + " - Validade: " + validade;
    }
}
