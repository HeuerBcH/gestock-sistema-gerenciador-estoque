package dev.gestock.sge.dominio.evento;

public interface EventoObservador<E> {
	void observarEvento(E evento);
}

