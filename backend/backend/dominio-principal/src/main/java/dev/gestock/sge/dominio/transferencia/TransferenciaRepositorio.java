package dev.gestock.sge.dominio.transferencia;

public interface TransferenciaRepositorio {
	void salvar(Transferencia transferencia);

	Transferencia obter(TransferenciaId id);
}

