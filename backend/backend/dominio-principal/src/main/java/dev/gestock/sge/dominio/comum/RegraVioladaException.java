package dev.gestock.sge.dominio.comum;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 * O formato da mensagem é: "CODIGO_REGRA: Mensagem de erro"
 */
public class RegraVioladaException extends RuntimeException {
    private final String codigoRegra;

    public RegraVioladaException(String codigoRegra, String mensagem) {
        super(codigoRegra + ": " + mensagem);
        this.codigoRegra = codigoRegra;
    }

    public String getCodigoRegra() {
        return codigoRegra;
    }
}

