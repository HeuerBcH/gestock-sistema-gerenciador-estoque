package dev.gestock.sge.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;

@Component
public class BackendMapeador extends ModelMapper {

	public BackendMapeador() {
		// Conversor de Long para ClienteId
		addConverter(new AbstractConverter<Long, ClienteId>() {
			@Override
			protected ClienteId convert(Long source) {
				return new ClienteId(source);
			}
		});

		// Conversor de Long para ProdutoId
		addConverter(new AbstractConverter<Long, ProdutoId>() {
			@Override
			protected ProdutoId convert(Long source) {
				return new ProdutoId(source);
			}
		});

		// Conversor de Long para EstoqueId
		addConverter(new AbstractConverter<Long, EstoqueId>() {
			@Override
			protected EstoqueId convert(Long source) {
				return new EstoqueId(source);
			}
		});

		// Conversor de Long para FornecedorId
		addConverter(new AbstractConverter<Long, FornecedorId>() {
			@Override
			protected FornecedorId convert(Long source) {
				return new FornecedorId(source);
			}
		});

		// Conversor de Long para PedidoId
		addConverter(new AbstractConverter<Long, PedidoId>() {
			@Override
			protected PedidoId convert(Long source) {
				return new PedidoId(source);
			}
		});

		// Conversor de Long para AlertaId
		addConverter(new AbstractConverter<Long, AlertaId>() {
			@Override
			protected AlertaId convert(Long source) {
				return new AlertaId(source);
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}