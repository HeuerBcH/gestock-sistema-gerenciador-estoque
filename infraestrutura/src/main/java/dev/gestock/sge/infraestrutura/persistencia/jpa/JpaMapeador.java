package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.gestock.sge.dominio.principal.alerta.Alerta;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.cliente.Cliente;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.fornecedor.LeadTime;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.pedido.StatusPedido;
import dev.gestock.sge.dominio.principal.produto.CodigoProduto;
import dev.gestock.sge.dominio.principal.produto.Produto;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

@Component
class JpaMapeador extends ModelMapper {
	private @Autowired ClienteJpaRepository clienteRepositorio;
	private @Autowired EstoqueJpaRepository estoqueRepositorio;
	private @Autowired ProdutoJpaRepository produtoRepositorio;
	private @Autowired FornecedorJpaRepository fornecedorRepositorio;
	private @Autowired PedidoJpaRepository pedidoRepositorio;

	JpaMapeador() {
		var configuracao = getConfiguration();
		configuracao.setFieldMatchingEnabled(true);
		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

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

		addConverter(new AbstractConverter<AlertaId, Long>() {
			@Override
			protected Long convert(AlertaId source) {
				return source != null ? source.getId() : null;
			}
		});

		// Conversor para String <-> CodigoProduto
		addConverter(new AbstractConverter<String, CodigoProduto>() {
			@Override
			protected CodigoProduto convert(String source) {
				return source != null ? new CodigoProduto(source) : null;
			}
		});

		// Conversor para StatusPedido (String <-> Enum)
		addConverter(new AbstractConverter<String, StatusPedido>() {
			@Override
			protected StatusPedido convert(String source) {
				return source != null ? StatusPedido.valueOf(source) : null;
			}
		});

		addConverter(new AbstractConverter<StatusPedido, String>() {
			@Override
			protected String convert(StatusPedido source) {
				return source != null ? source.name() : null;
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

		// Conversor para ClienteJpa <-> Cliente
		addConverter(new AbstractConverter<ClienteJpa, Cliente>() {
			@Override
			protected Cliente convert(ClienteJpa source) {
				var id = map(source.id, ClienteId.class);
				if (source.senhaHash != null) {
					return new Cliente(id, source.nome, source.documento, source.email, source.senhaHash);
				}
				return new Cliente(id, source.nome, source.documento, source.email);
			}
		});

		addConverter(new AbstractConverter<ClienteId, ClienteJpa>() {
			@Override
			protected ClienteJpa convert(ClienteId source) {
				return clienteRepositorio.findById(source.getId()).orElse(null);
			}
		});

		// Conversor para EstoqueJpa <-> Estoque
		addConverter(new AbstractConverter<EstoqueJpa, Estoque>() {
			@Override
			protected Estoque convert(EstoqueJpa source) {
				var id = map(source.id, EstoqueId.class);
				var clienteId = map(source.cliente.id, ClienteId.class);
				return new Estoque(id, clienteId, source.nome, source.endereco, source.capacidade, source.ativo);
			}
		});

		addConverter(new AbstractConverter<EstoqueId, EstoqueJpa>() {
			@Override
			protected EstoqueJpa convert(EstoqueId source) {
				return estoqueRepositorio.findById(source.getId()).orElse(null);
			}
		});

		// Conversor para ProdutoJpa <-> Produto
		addConverter(new AbstractConverter<ProdutoJpa, Produto>() {
			@Override
			protected Produto convert(ProdutoJpa source) {
				var id = map(source.id, ProdutoId.class);
				return new Produto(id, source.codigo, source.nome, source.unidadePeso, source.perecivel,
						source.peso != null ? source.peso.doubleValue() : 0.0);
			}
		});

		addConverter(new AbstractConverter<ProdutoId, ProdutoJpa>() {
			@Override
			protected ProdutoJpa convert(ProdutoId source) {
				return produtoRepositorio.findById(source.getId()).orElse(null);
			}
		});

		// Conversor para FornecedorJpa <-> Fornecedor
		addConverter(new AbstractConverter<FornecedorJpa, Fornecedor>() {
			@Override
			protected Fornecedor convert(FornecedorJpa source) {
				var id = map(source.id, FornecedorId.class);
				var leadTime = new LeadTime(source.leadTimeMedio);
				var fornecedor = new Fornecedor(id, source.nome, source.cnpj, source.contato, leadTime);
				if (!source.ativo) {
					fornecedor.inativar();
				}
				return fornecedor;
			}
		});

		addConverter(new AbstractConverter<FornecedorId, FornecedorJpa>() {
			@Override
			protected FornecedorJpa convert(FornecedorId source) {
				return fornecedorRepositorio.findById(source.getId()).orElse(null);
			}
		});

		// Conversor para PedidoJpa <-> Pedido
		addConverter(new AbstractConverter<PedidoJpa, Pedido>() {
			@Override
			protected Pedido convert(PedidoJpa source) {
				var id = map(source.id, PedidoId.class);
				var clienteId = map(source.cliente.id, ClienteId.class);
				var fornecedorId = map(source.fornecedor.id, FornecedorId.class);
				var pedido = new Pedido(id, clienteId, fornecedorId);
				if (source.estoque != null) {
					pedido.setEstoqueId(map(source.estoque.id, EstoqueId.class));
				}
				if (source.dataPrevistaEntrega != null) {
					pedido.setDataPrevistaEntrega(source.dataPrevistaEntrega);
				}
				// Definir status usando reflexão (campo privado)
				try {
					Field statusField = Pedido.class.getDeclaredField("status");
					statusField.setAccessible(true);
					statusField.set(pedido, StatusPedido.valueOf(source.status));
				} catch (Exception e) {
					throw new RuntimeException("Erro ao definir status do pedido", e);
				}
				return pedido;
			}
		});

		addConverter(new AbstractConverter<PedidoId, PedidoJpa>() {
			@Override
			protected PedidoJpa convert(PedidoId source) {
				return pedidoRepositorio.findById(source.getId()).orElse(null);
			}
		});

		// Conversor para AlertaJpa <-> Alerta
		addConverter(new AbstractConverter<AlertaJpa, Alerta>() {
			@Override
			protected Alerta convert(AlertaJpa source) {
				var id = map(source.id, AlertaId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var estoqueId = map(source.estoque.id, EstoqueId.class);
				var fornecedorSugeridoId = source.fornecedorSugerido != null
						? map(source.fornecedorSugerido.id, FornecedorId.class)
						: null;
				var alerta = new Alerta(id, produtoId, estoqueId, fornecedorSugeridoId);
				if (!source.ativo) {
					alerta.desativar();
				}
				return alerta;
			}
		});

		// Conversores reversos (Domínio -> JPA) para salvar

		// Conversor para Cliente -> ClienteJpa
		addConverter(new AbstractConverter<Cliente, ClienteJpa>() {
			@Override
			protected ClienteJpa convert(Cliente source) {
				var clienteJpa = new ClienteJpa();
				if (source.getId().getId() != null) {
					clienteJpa.id = source.getId().getId();
				}
				clienteJpa.nome = source.getNome();
				clienteJpa.documento = source.getDocumento();
				clienteJpa.email = source.getEmail();
				clienteJpa.senhaHash = source.getSenhaHash();
				return clienteJpa;
			}
		});

		// Conversor para Estoque -> EstoqueJpa
		addConverter(new AbstractConverter<Estoque, EstoqueJpa>() {
			@Override
			protected EstoqueJpa convert(Estoque source) {
				var estoqueJpa = new EstoqueJpa();
				if (source.getId().getId() != null) {
					estoqueJpa.id = source.getId().getId();
				}
				estoqueJpa.cliente = map(source.getClienteId(), ClienteJpa.class);
				estoqueJpa.nome = source.getNome();
				estoqueJpa.endereco = source.getEndereco();
				estoqueJpa.capacidade = source.getCapacidade();
				estoqueJpa.ativo = source.isAtivo();
				return estoqueJpa;
			}
		});

		// Conversor para Produto -> ProdutoJpa
		addConverter(new AbstractConverter<Produto, ProdutoJpa>() {
			@Override
			protected ProdutoJpa convert(Produto source) {
				var produtoJpa = new ProdutoJpa();
				if (source.getId().getId() != null) {
					produtoJpa.id = source.getId().getId();
				}
				produtoJpa.codigo = source.getCodigo();
				produtoJpa.nome = source.getNome();
				produtoJpa.unidadePeso = source.getUnidadePeso();
				produtoJpa.peso = BigDecimal.valueOf(source.getPeso());
				produtoJpa.perecivel = source.isPerecivel();
				produtoJpa.ativo = source.isAtivo();
				return produtoJpa;
			}
		});

		// Conversor para Fornecedor -> FornecedorJpa
		addConverter(new AbstractConverter<Fornecedor, FornecedorJpa>() {
			@Override
			protected FornecedorJpa convert(Fornecedor source) {
				var fornecedorJpa = new FornecedorJpa();
				if (source.getId().getId() != null) {
					fornecedorJpa.id = source.getId().getId();
				}
				fornecedorJpa.nome = source.getNome();
				fornecedorJpa.cnpj = source.getCnpj();
				fornecedorJpa.contato = source.getContato();
				fornecedorJpa.leadTimeMedio = source.getLeadTimeMedio().getDias();
				fornecedorJpa.ativo = source.isAtivo();
				return fornecedorJpa;
			}
		});

		// Conversor para Pedido -> PedidoJpa
		addConverter(new AbstractConverter<Pedido, PedidoJpa>() {
			@Override
			protected PedidoJpa convert(Pedido source) {
				var pedidoJpa = new PedidoJpa();
				if (source.getId().getId() != null) {
					pedidoJpa.id = source.getId().getId();
				}
				pedidoJpa.cliente = map(source.getClienteId(), ClienteJpa.class);
				pedidoJpa.fornecedor = map(source.getFornecedorId(), FornecedorJpa.class);
				if (source.getEstoqueId().isPresent()) {
					pedidoJpa.estoque = map(source.getEstoqueId().get(), EstoqueJpa.class);
				}
				pedidoJpa.dataCriacao = source.getDataCriacao();
				pedidoJpa.dataPrevistaEntrega = source.getDataPrevistaEntrega();
				pedidoJpa.status = source.getStatus().name();
				return pedidoJpa;
			}
		});

		// Conversor para Alerta -> AlertaJpa
		addConverter(new AbstractConverter<Alerta, AlertaJpa>() {
			@Override
			protected AlertaJpa convert(Alerta source) {
				var alertaJpa = new AlertaJpa();
				if (source.getId().getId() != null) {
					alertaJpa.id = source.getId().getId();
				}
				alertaJpa.produto = map(source.getProdutoId(), ProdutoJpa.class);
				alertaJpa.estoque = map(source.getEstoqueId(), EstoqueJpa.class);
				if (source.getFornecedorSugerido() != null) {
					alertaJpa.fornecedorSugerido = map(source.getFornecedorSugerido(), FornecedorJpa.class);
				}
				alertaJpa.dataGeracao = source.getDataGeracao();
				alertaJpa.ativo = source.isAtivo();
				return alertaJpa;
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}

