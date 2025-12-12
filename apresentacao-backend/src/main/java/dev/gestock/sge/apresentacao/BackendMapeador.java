package dev.gestock.sge.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import dev.gestock.sge.apresentacao.principal.cliente.ClienteForm.ClienteDto;
import dev.gestock.sge.apresentacao.principal.estoque.EstoqueFormulario.EstoqueDto;
import dev.gestock.sge.apresentacao.principal.fornecedor.FornecedorForm.FornecedorDto;
import dev.gestock.sge.apresentacao.principal.produto.ProdutoForm.ProdutoDto;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.produto.CodigoProduto;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

@Component
public class BackendMapeador extends ModelMapper {

	BackendMapeador() {
		// Conversores para IDs (Long <-> Value Objects)
		addConverter(new AbstractConverter<Long, ClienteId>() {
			@Override
			protected ClienteId convert(Long source) {
				return source != null ? new ClienteId(source) : null;
			}
		});

		addConverter(new AbstractConverter<ClienteId, Long>() {
			@Override
			protected Long convert(ClienteId source) {
				return source != null ? source.getId() : null;
			}
		});

		addConverter(new AbstractConverter<Long, EstoqueId>() {
			@Override
			protected EstoqueId convert(Long source) {
				return source != null ? new EstoqueId(source) : null;
			}
		});

		addConverter(new AbstractConverter<EstoqueId, Long>() {
			@Override
			protected Long convert(EstoqueId source) {
				return source != null ? source.getId() : null;
			}
		});

		addConverter(new AbstractConverter<Long, ProdutoId>() {
			@Override
			protected ProdutoId convert(Long source) {
				return source != null ? new ProdutoId(source) : null;
			}
		});

		addConverter(new AbstractConverter<ProdutoId, Long>() {
			@Override
			protected Long convert(ProdutoId source) {
				return source != null ? source.getId() : null;
			}
		});

		addConverter(new AbstractConverter<Long, FornecedorId>() {
			@Override
			protected FornecedorId convert(Long source) {
				return source != null ? new FornecedorId(source) : null;
			}
		});

		addConverter(new AbstractConverter<FornecedorId, Long>() {
			@Override
			protected Long convert(FornecedorId source) {
				return source != null ? source.getId() : null;
			}
		});

		addConverter(new AbstractConverter<Long, PedidoId>() {
			@Override
			protected PedidoId convert(Long source) {
				return source != null ? new PedidoId(source) : null;
			}
		});

		addConverter(new AbstractConverter<PedidoId, Long>() {
			@Override
			protected Long convert(PedidoId source) {
				return source != null ? source.getId() : null;
			}
		});

		addConverter(new AbstractConverter<Long, AlertaId>() {
			@Override
			protected AlertaId convert(Long source) {
				return source != null ? new AlertaId(source) : null;
			}
		});

		// Conversor para String <-> CodigoProduto
		addConverter(new AbstractConverter<String, CodigoProduto>() {
			@Override
			protected CodigoProduto convert(String source) {
				return source != null ? new CodigoProduto(source) : null;
			}
		});

		// Conversor para LeadTime (Integer <-> LeadTime)
		addConverter(new AbstractConverter<Integer, LeadTime>() {
			@Override
			protected LeadTime convert(Integer source) {
				return source != null ? new LeadTime(source) : null;
			}
		});

		addConverter(new AbstractConverter<LeadTime, Integer>() {
			@Override
			protected Integer convert(LeadTime source) {
				return source != null ? source.getDias() : null;
			}
		});

		// Conversor para ClienteDto -> Cliente
		addConverter(new AbstractConverter<ClienteDto, Cliente>() {
			@Override
			protected Cliente convert(ClienteDto source) {
				// Usa um ID temporário muito grande (999999999999L) para novas entidades
				// Este valor nunca será usado como ID real, permitindo que o JPA gere um novo ID
				// O JpaMapeador atribui este ID ao ClienteJpa, mas como não existe no banco,
				// o JPA fará INSERT e gerará um novo ID automaticamente
				var id = source.id != null ? new ClienteId(source.id) : new ClienteId(999999999999L);
				return new Cliente(id, source.nome, source.documento, source.email);
			}
		});

		// Conversor para EstoqueDto -> Estoque
		addConverter(new AbstractConverter<EstoqueDto, Estoque>() {
			@Override
			protected Estoque convert(EstoqueDto source) {
				// Usa um ID temporário muito grande para novas entidades
				var id = source.id != null ? new EstoqueId(source.id) : new EstoqueId(999999999999L);
				var clienteId = map(source.clienteId, ClienteId.class);
				return new Estoque(id, clienteId, source.nome, source.endereco, source.capacidade, true);
			}
		});

		// Conversor para ProdutoDto -> Produto
		addConverter(new AbstractConverter<ProdutoDto, Produto>() {
			@Override
			protected Produto convert(ProdutoDto source) {
				// Usa um ID temporário muito grande para novas entidades
				var id = source.id != null ? new ProdutoId(source.id) : new ProdutoId(999999999999L);
				return new Produto(id, source.codigo, source.nome, source.unidadePeso, 
					source.perecivel, source.peso);
			}
		});

		// Conversor para FornecedorDto -> Fornecedor
		addConverter(new AbstractConverter<FornecedorDto, Fornecedor>() {
			@Override
			protected Fornecedor convert(FornecedorDto source) {
				// Usa um ID temporário muito grande para novas entidades
				var id = source.id != null ? new FornecedorId(source.id) : new FornecedorId(999999999999L);
				var leadTime = source.leadTimeMedio != null ? new LeadTime(source.leadTimeMedio) : new LeadTime(0);
				return new Fornecedor(id, source.nome, source.cnpj, source.contato, leadTime);
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}
