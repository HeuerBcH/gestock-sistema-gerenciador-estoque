package dev.gestock.sge.apresentacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import dev.gestock.sge.apresentacao.autenticacao.cliente.ClienteFormulario.ClienteDto;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteId;
import dev.gestock.sge.dominio.autenticacao.cliente.CpfCnpj;
import dev.gestock.sge.dominio.autenticacao.cliente.Email;
import dev.gestock.sge.dominio.autenticacao.cliente.Senha;
import dev.gestock.sge.dominio.fornecedor.Cnpj;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.produto.Codigo;
import dev.gestock.sge.dominio.produto.Peso;
import dev.gestock.sge.dominio.produto.Quantidade;
import dev.gestock.sge.dominio.produto.Perecivel;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.dominio.estoque.Endereco;
import dev.gestock.sge.dominio.estoque.Capacidade;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.dominio.cotacao.CotacaoId;
import dev.gestock.sge.dominio.cotacao.Validade;
import dev.gestock.sge.dominio.cotacao.StatusAprovacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Responsavel;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoId;
import dev.gestock.sge.dominio.pontoresuprimento.EstoqueSeguranca;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.ItemPedido;
import dev.gestock.sge.dominio.pedido.ValorTotal;
import dev.gestock.sge.dominio.pedido.DataPedido;
import dev.gestock.sge.dominio.pedido.DataPrevista;
import dev.gestock.sge.dominio.pedido.StatusPedido;
import dev.gestock.sge.apresentacao.pedido.PedidoFormulario.PedidoDto;
import dev.gestock.sge.apresentacao.pedido.PedidoFormulario.ItemPedidoDto;
import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class BackendMapeador extends ModelMapper {

	BackendMapeador() {
		// Conversores para Cliente
		addConverter(new AbstractConverter<String, Email>() {
			@Override
			protected Email convert(String source) {
				if (source == null) {
					return null;
				}
				return new Email(source);
			}
		});

		addConverter(new AbstractConverter<String, CpfCnpj>() {
			@Override
			protected CpfCnpj convert(String source) {
				if (source == null) {
					return null;
				}
				return new CpfCnpj(source);
			}
		});

		addConverter(new AbstractConverter<String, Senha>() {
			@Override
			protected Senha convert(String source) {
				if (source == null) {
					return null;
				}
				return new Senha(source);
			}
		});

		addConverter(new AbstractConverter<Integer, ClienteId>() {
			@Override
			protected ClienteId convert(Integer source) {
				return new ClienteId(source);
			}
		});

		// Conversores para Fornecedor
		addConverter(new AbstractConverter<String, Cnpj>() {
			@Override
			protected Cnpj convert(String source) {
				if (source == null) {
					return null;
				}
				return new Cnpj(source);
			}
		});

		addConverter(new AbstractConverter<Integer, LeadTime>() {
			@Override
			protected LeadTime convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new LeadTime(source);
			}
		});

		addConverter(new AbstractConverter<BigDecimal, Custo>() {
			@Override
			protected Custo convert(BigDecimal source) {
				if (source == null) {
					return null;
				}
				return new Custo(source);
			}
		});

		addConverter(new AbstractConverter<Integer, FornecedorId>() {
			@Override
			protected FornecedorId convert(Integer source) {
				return new FornecedorId(source);
			}
		});

		// Conversores para Produto
		addConverter(new AbstractConverter<String, Codigo>() {
			@Override
			protected Codigo convert(String source) {
				if (source == null) {
					return null;
				}
				return new Codigo(source);
			}
		});

		addConverter(new AbstractConverter<Integer, Peso>() {
			@Override
			protected Peso convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new Peso(source);
			}
		});

		addConverter(new AbstractConverter<Integer, Quantidade>() {
			@Override
			protected Quantidade convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new Quantidade(source);
			}
		});

		addConverter(new AbstractConverter<String, Perecivel>() {
			@Override
			protected Perecivel convert(String source) {
				if (source == null) {
					return null;
				}
				return Perecivel.valueOf(source.toUpperCase());
			}
		});

		addConverter(new AbstractConverter<Integer, ProdutoId>() {
			@Override
			protected ProdutoId convert(Integer source) {
				return new ProdutoId(source);
			}
		});

		// Conversor para List<Integer> -> List<FornecedorId>
		addConverter(new AbstractConverter<List<Integer>, List<FornecedorId>>() {
			@Override
			protected List<FornecedorId> convert(List<Integer> source) {
				if (source == null) {
					return null;
				}
				var resultado = new java.util.ArrayList<FornecedorId>();
				for (var id : source) {
					resultado.add(new FornecedorId(id));
				}
				return resultado;
			}
		});

		// Conversores para Estoque
		addConverter(new AbstractConverter<String, Endereco>() {
			@Override
			protected Endereco convert(String source) {
				if (source == null) {
					return null;
				}
				return new Endereco(source);
			}
		});

		addConverter(new AbstractConverter<Integer, Capacidade>() {
			@Override
			protected Capacidade convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new Capacidade(source);
			}
		});

		addConverter(new AbstractConverter<Integer, EstoqueId>() {
			@Override
			protected EstoqueId convert(Integer source) {
				return new EstoqueId(source);
			}
		});

		// Conversores para Cotacao
		addConverter(new AbstractConverter<Integer, CotacaoId>() {
			@Override
			protected CotacaoId convert(Integer source) {
				return new CotacaoId(source);
			}
		});

		addConverter(new AbstractConverter<String, Validade>() {
			@Override
			protected Validade convert(String source) {
				if (source == null) {
					return null;
				}
				return Validade.valueOf(source.toUpperCase());
			}
		});

		addConverter(new AbstractConverter<String, StatusAprovacao>() {
			@Override
			protected StatusAprovacao convert(String source) {
				if (source == null) {
					return null;
				}
				return StatusAprovacao.valueOf(source.toUpperCase());
			}
		});

		// Conversores para Movimentacao
		addConverter(new AbstractConverter<Integer, MovimentacaoId>() {
			@Override
			protected MovimentacaoId convert(Integer source) {
				return new MovimentacaoId(source);
			}
		});

		addConverter(new AbstractConverter<String, TipoMovimentacao>() {
			@Override
			protected TipoMovimentacao convert(String source) {
				if (source == null) {
					return null;
				}
				return TipoMovimentacao.valueOf(source.toUpperCase());
			}
		});

		addConverter(new AbstractConverter<String, Motivo>() {
			@Override
			protected Motivo convert(String source) {
				if (source == null) {
					return null;
				}
				return new Motivo(source);
			}
		});

		addConverter(new AbstractConverter<String, Responsavel>() {
			@Override
			protected Responsavel convert(String source) {
				if (source == null) {
					return null;
				}
				return new Responsavel(source);
			}
		});

		// Conversores para PontoRessuprimento
		addConverter(new AbstractConverter<Integer, PontoRessuprimentoId>() {
			@Override
			protected PontoRessuprimentoId convert(Integer source) {
				return new PontoRessuprimentoId(source);
			}
		});

		addConverter(new AbstractConverter<Integer, EstoqueSeguranca>() {
			@Override
			protected EstoqueSeguranca convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new EstoqueSeguranca(source);
			}
		});

		// Conversores para Pedido
		addConverter(new AbstractConverter<PedidoDto, Pedido>() {
			@Override
			protected Pedido convert(PedidoDto source) {
				if (source == null) {
					return null;
				}
				var id = source.id != null ? new PedidoId(source.id) : new PedidoId(0);
				var fornecedorId = map(source.fornecedorId, FornecedorId.class);
				var estoqueId = source.estoqueId != null 
					? new dev.gestock.sge.dominio.estoque.EstoqueId(source.estoqueId)
					: new dev.gestock.sge.dominio.estoque.EstoqueId(0);
				var itens = new ArrayList<ItemPedido>();
				if (source.itens != null) {
					for (var itemDto : source.itens) {
						var produtoId = map(itemDto.produtoId, ProdutoId.class);
						var quantidade = map(itemDto.quantidade, Quantidade.class);
						// Preço unitário placeholder - será substituído pelo real no PedidoServico.criar()
						var precoUnitario = new Custo(BigDecimal.ONE);
						itens.add(new ItemPedido(produtoId, quantidade, precoUnitario));
					}
				}
				var dataPedido = source.dataPedido != null 
					? new DataPedido(source.dataPedido) 
					: new DataPedido(LocalDate.now());
				var status = source.status != null 
					? StatusPedido.valueOf(source.status.toUpperCase()) 
					: StatusPedido.CRIADO;
				return new Pedido(id, fornecedorId, estoqueId, itens, dataPedido, status);
			}
		});

		addConverter(new AbstractConverter<Integer, PedidoId>() {
			@Override
			protected PedidoId convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new PedidoId(source);
			}
		});

		addConverter(new AbstractConverter<String, StatusPedido>() {
			@Override
			protected StatusPedido convert(String source) {
				if (source == null) {
					return null;
				}
				// Remove aspas se presentes (pode vir com aspas do JSON)
				var limpo = source.replace("\"", "").trim().toUpperCase();
				return StatusPedido.valueOf(limpo);
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}
