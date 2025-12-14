package dev.gestock.sge.infraestrutura.persistencia.jpa;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration.AccessLevel;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import dev.gestock.sge.dominio.autenticacao.cliente.Cliente;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteId;
import dev.gestock.sge.dominio.autenticacao.cliente.CpfCnpj;
import dev.gestock.sge.dominio.autenticacao.cliente.Senha;
import dev.gestock.sge.dominio.fornecedor.Cnpj;
import dev.gestock.sge.dominio.fornecedor.Custo;
import dev.gestock.sge.dominio.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.LeadTime;
import dev.gestock.sge.dominio.fornecedor.Status;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorResumo;
import dev.gestock.sge.dominio.produto.Codigo;
import dev.gestock.sge.dominio.produto.Peso;
import dev.gestock.sge.dominio.produto.Quantidade;
import dev.gestock.sge.dominio.produto.Perecivel;
import dev.gestock.sge.dominio.produto.Produto;
import dev.gestock.sge.dominio.produto.ProdutoId;
import dev.gestock.sge.aplicacao.produto.ProdutoResumo;
import dev.gestock.sge.dominio.estoque.Endereco;
import dev.gestock.sge.dominio.estoque.Capacidade;
import dev.gestock.sge.dominio.estoque.Estoque;
import dev.gestock.sge.dominio.estoque.EstoqueId;
import dev.gestock.sge.aplicacao.estoque.EstoqueResumo;
import dev.gestock.sge.dominio.cotacao.Cotacao;
import dev.gestock.sge.dominio.cotacao.CotacaoId;
import dev.gestock.sge.dominio.cotacao.Validade;
import dev.gestock.sge.dominio.cotacao.StatusAprovacao;
import dev.gestock.sge.dominio.movimentacao.Movimentacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import dev.gestock.sge.dominio.movimentacao.TipoMovimentacao;
import dev.gestock.sge.dominio.movimentacao.Motivo;
import dev.gestock.sge.dominio.movimentacao.Responsavel;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimento;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoId;
import dev.gestock.sge.dominio.pontoresuprimento.ConsumoMedioDiario;
import dev.gestock.sge.dominio.pontoresuprimento.EstoqueSeguranca;
import dev.gestock.sge.dominio.pontoresuprimento.RopCalculado;
import dev.gestock.sge.dominio.pontoresuprimento.SaldoAtual;
import dev.gestock.sge.dominio.pontoresuprimento.StatusRop;
import dev.gestock.sge.dominio.pedido.Pedido;
import dev.gestock.sge.dominio.pedido.PedidoId;
import dev.gestock.sge.dominio.pedido.ItemPedido;
import dev.gestock.sge.dominio.pedido.ValorTotal;
import dev.gestock.sge.dominio.pedido.DataPedido;
import dev.gestock.sge.dominio.pedido.DataPrevista;
import dev.gestock.sge.dominio.pedido.StatusPedido;
import dev.gestock.sge.dominio.reserva.Reserva;
import dev.gestock.sge.dominio.reserva.ReservaId;
import dev.gestock.sge.dominio.reserva.DataHoraReserva;
import dev.gestock.sge.dominio.reserva.DataHoraLiberacao;
import dev.gestock.sge.dominio.reserva.StatusReserva;
import dev.gestock.sge.dominio.reserva.TipoLiberacao;
import dev.gestock.sge.aplicacao.reserva.ReservaResumo;
import dev.gestock.sge.dominio.transferencia.Transferencia;
import dev.gestock.sge.dominio.transferencia.TransferenciaId;
import dev.gestock.sge.dominio.transferencia.DataHoraTransferencia;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoId;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
class JpaMapeador extends ModelMapper {

	JpaMapeador() {
		var configuracao = getConfiguration();
		configuracao.setFieldMatchingEnabled(true);
		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);

		// Conversores para Cliente
		addConverter(new AbstractConverter<ClienteJpa, Cliente>() {
			@Override
			protected Cliente convert(ClienteJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, ClienteId.class);
				var email = map(source.email, dev.gestock.sge.dominio.autenticacao.cliente.Email.class);
				var documento = map(source.documento, CpfCnpj.class);
				var senha = map(source.senha, Senha.class);
				return new Cliente(id, source.nome, email, documento, senha);
			}
		});

		addConverter(new AbstractConverter<Cliente, ClienteJpa>() {
			@Override
			protected ClienteJpa convert(Cliente source) {
				if (source == null) {
					return null;
				}
				var clienteJpa = new ClienteJpa();
				clienteJpa.id = source.getId().getId();
				clienteJpa.nome = source.getNome();
				clienteJpa.email = source.getEmail().getEndereco();
				clienteJpa.documento = source.getDocumento().getDocumento();
				clienteJpa.senha = source.getSenha().getValor();
				return clienteJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, ClienteId>() {
			@Override
			protected ClienteId convert(Integer source) {
				return new ClienteId(source);
			}
		});

		addConverter(new AbstractConverter<ClienteJpa, ClienteId>() {
			@Override
			protected ClienteId convert(ClienteJpa source) {
				if (source == null) {
					return null;
				}
				return map(source.id, ClienteId.class);
			}
		});

		addConverter(new AbstractConverter<String, dev.gestock.sge.dominio.autenticacao.cliente.Email>() {
			@Override
			protected dev.gestock.sge.dominio.autenticacao.cliente.Email convert(String source) {
				if (source == null) {
					return null;
				}
				return new dev.gestock.sge.dominio.autenticacao.cliente.Email(source);
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

		// Conversores para Fornecedor
		addConverter(new AbstractConverter<FornecedorJpa, Fornecedor>() {
			@Override
			protected Fornecedor convert(FornecedorJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, FornecedorId.class);
				var cnpj = map(source.cnpj, Cnpj.class);
				var contato = map(source.contato, dev.gestock.sge.dominio.autenticacao.cliente.Email.class);
				var leadTime = map(source.leadTime, LeadTime.class);
				var custo = map(source.custo, Custo.class);
				var status = map(source.status, Status.class);
				return new Fornecedor(id, source.nome, cnpj, contato, leadTime, custo, status);
			}
		});

		addConverter(new AbstractConverter<Fornecedor, FornecedorJpa>() {
			@Override
			protected FornecedorJpa convert(Fornecedor source) {
				if (source == null) {
					return null;
				}
				var fornecedorJpa = new FornecedorJpa();
				fornecedorJpa.id = source.getId().getId();
				fornecedorJpa.nome = source.getNome();
				fornecedorJpa.cnpj = source.getCnpj().getNumero();
				fornecedorJpa.contato = source.getContato().getEndereco();
				fornecedorJpa.leadTime = source.getLeadTime().getDias();
				fornecedorJpa.custo = source.getCusto().getValor();
				fornecedorJpa.status = source.getStatus().name();
				return fornecedorJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, FornecedorId>() {
			@Override
			protected FornecedorId convert(Integer source) {
				return new FornecedorId(source);
			}
		});

		addConverter(new AbstractConverter<FornecedorJpa, FornecedorId>() {
			@Override
			protected FornecedorId convert(FornecedorJpa source) {
				if (source == null) {
					return null;
				}
				return map(source.id, FornecedorId.class);
			}
		});

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

		addConverter(new AbstractConverter<String, Status>() {
			@Override
			protected Status convert(String source) {
				if (source == null) {
					return null;
				}
				return Status.valueOf(source);
			}
		});

		// Conversor para projeção FornecedorResumo
		addConverter(new AbstractConverter<FornecedorJpa, FornecedorResumo>() {
			@Override
			protected FornecedorResumo convert(FornecedorJpa source) {
				if (source == null) {
					return null;
				}
				return new FornecedorResumo() {
					@Override
					public int getId() {
						return source.id;
					}

					@Override
					public String getNome() {
						return source.nome;
					}

					@Override
					public String getCnpj() {
						return source.cnpj;
					}

					@Override
					public String getContato() {
						return source.contato;
					}

					@Override
					public int getLeadTime() {
						return source.leadTime;
					}

					@Override
					public BigDecimal getCusto() {
						return source.custo;
					}

					@Override
					public String getStatus() {
						return source.status;
					}
				};
			}
		});

		// Conversores para Produto
		addConverter(new AbstractConverter<ProdutoJpa, Produto>() {
			@Override
			protected Produto convert(ProdutoJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, ProdutoId.class);
				var codigo = map(source.codigo, Codigo.class);
				var peso = map(source.peso, Peso.class);
				var perecivel = map(source.perecivel, Perecivel.class);
				var status = map(source.status, Status.class);
				List<FornecedorId> fornecedores = map(
					source.fornecedores,
					new org.modelmapper.TypeToken<List<FornecedorId>>() {}.getType()
				);
				return new Produto(id, codigo, source.nome, peso, perecivel, status, fornecedores);
			}
		});

		addConverter(new AbstractConverter<Produto, ProdutoJpa>() {
			@Override
			protected ProdutoJpa convert(Produto source) {
				if (source == null) {
					return null;
				}
				var produtoJpa = new ProdutoJpa();
				produtoJpa.id = source.getId().getId();
				produtoJpa.codigo = source.getCodigo().getValor();
				produtoJpa.nome = source.getNome();
				produtoJpa.peso = source.getPeso().getGramas();
				produtoJpa.perecivel = source.getPerecivel().name();
				produtoJpa.status = source.getStatus().name();
				// Fornecedores serão buscados no repositório (não mapear aqui)
				produtoJpa.fornecedores = null;
				return produtoJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, ProdutoId>() {
			@Override
			protected ProdutoId convert(Integer source) {
				return new ProdutoId(source);
			}
		});

		addConverter(new AbstractConverter<ProdutoJpa, ProdutoId>() {
			@Override
			protected ProdutoId convert(ProdutoJpa source) {
				if (source == null) {
					return null;
				}
				return map(source.id, ProdutoId.class);
			}
		});

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
				return Perecivel.valueOf(source);
			}
		});

		// Conversor para List<FornecedorJpa> -> List<FornecedorId>
		addConverter(new AbstractConverter<List<FornecedorJpa>, List<FornecedorId>>() {
			@Override
			protected List<FornecedorId> convert(List<FornecedorJpa> source) {
				if (source == null) {
					return null;
				}
				var resultado = new java.util.ArrayList<FornecedorId>();
				for (var fornecedorJpa : source) {
					resultado.add(map(fornecedorJpa.id, FornecedorId.class));
				}
				return resultado;
			}
		});

		// Conversor para List<FornecedorId> -> List<FornecedorJpa>
		// Este conversor não é necessário pois o ModelMapper consegue mapear automaticamente
		// através do FornecedorId -> FornecedorJpa usando o ID

		// Conversor para projeção ProdutoResumo
		addConverter(new AbstractConverter<ProdutoJpa, ProdutoResumo>() {
			@Override
			protected ProdutoResumo convert(ProdutoJpa source) {
				if (source == null) {
					return null;
				}
				return new ProdutoResumo() {
					@Override
					public int getId() {
						return source.id;
					}

					@Override
					public String getCodigo() {
						return source.codigo;
					}

					@Override
					public String getNome() {
						return source.nome;
					}

					@Override
					public int getPeso() {
						return source.peso;
					}

					@Override
					public String getPerecivel() {
						return source.perecivel;
					}

					@Override
					public String getStatus() {
						return source.status;
					}

					@Override
					public java.util.List<String> getFornecedores() {
						if (source.fornecedores == null) {
							return java.util.Collections.emptyList();
						}
						return source.fornecedores.stream()
							.map(f -> f.nome)
							.collect(java.util.stream.Collectors.toList());
					}
				};
			}
		});

		// Conversores para Estoque
		addConverter(new AbstractConverter<EstoqueJpa, Estoque>() {
			@Override
			protected Estoque convert(EstoqueJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, EstoqueId.class);
				var endereco = map(source.endereco, Endereco.class);
				var capacidade = map(source.capacidade, Capacidade.class);
				var status = map(source.status, Status.class);
				return new Estoque(id, source.nome, endereco, capacidade, status);
			}
		});

		addConverter(new AbstractConverter<Estoque, EstoqueJpa>() {
			@Override
			protected EstoqueJpa convert(Estoque source) {
				if (source == null) {
					return null;
				}
				var estoqueJpa = new EstoqueJpa();
				estoqueJpa.id = source.getId().getId();
				estoqueJpa.nome = source.getNome();
				estoqueJpa.endereco = source.getEndereco().getValor();
				estoqueJpa.capacidade = source.getCapacidade().getValor();
				estoqueJpa.status = source.getStatus().name();
				return estoqueJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, EstoqueId>() {
			@Override
			protected EstoqueId convert(Integer source) {
				return new EstoqueId(source);
			}
		});

		addConverter(new AbstractConverter<EstoqueJpa, EstoqueId>() {
			@Override
			protected EstoqueId convert(EstoqueJpa source) {
				if (source == null) {
					return null;
				}
				return map(source.id, EstoqueId.class);
			}
		});

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

		// Conversor para projeção EstoqueResumo
		// Nota: A ocupação será calculada no EstoqueRepositorioImpl usando query

		// Conversores para Cotacao
		addConverter(new AbstractConverter<CotacaoJpa, Cotacao>() {
			@Override
			protected Cotacao convert(CotacaoJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, CotacaoId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var fornecedorId = map(source.fornecedor.id, FornecedorId.class);
				var preco = map(source.preco, Custo.class);
				var leadTime = map(source.leadTime, LeadTime.class);
				var validade = map(source.validade, Validade.class);
				var statusAprovacao = map(source.statusAprovacao, StatusAprovacao.class);
				return new Cotacao(id, produtoId, fornecedorId, preco, leadTime, validade, statusAprovacao);
			}
		});

		addConverter(new AbstractConverter<Cotacao, CotacaoJpa>() {
			@Override
			protected CotacaoJpa convert(Cotacao source) {
				if (source == null) {
					return null;
				}
				var cotacaoJpa = new CotacaoJpa();
				cotacaoJpa.id = source.getId().getId();
				// produto e fornecedor serão carregados do banco
				cotacaoJpa.preco = source.getPreco().getValor();
				cotacaoJpa.leadTime = source.getLeadTime().getDias();
				cotacaoJpa.validade = source.getValidade().name();
				cotacaoJpa.statusAprovacao = source.getStatusAprovacao().name();
				return cotacaoJpa;
			}
		});

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
				return Validade.valueOf(source);
			}
		});

		addConverter(new AbstractConverter<String, StatusAprovacao>() {
			@Override
			protected StatusAprovacao convert(String source) {
				if (source == null) {
					return null;
				}
				return StatusAprovacao.valueOf(source);
			}
		});

		// Conversores para Movimentacao
		addConverter(new AbstractConverter<MovimentacaoJpa, Movimentacao>() {
			@Override
			protected Movimentacao convert(MovimentacaoJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, MovimentacaoId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var estoqueId = map(source.estoque.id, EstoqueId.class);
				var quantidade = map(source.quantidade, Quantidade.class);
				var tipo = map(source.tipo, TipoMovimentacao.class);
				var motivo = map(source.motivo, Motivo.class);
				var responsavel = map(source.responsavel, Responsavel.class);
				return new Movimentacao(id, source.dataHora, produtoId, estoqueId, quantidade, tipo, motivo, responsavel);
			}
		});

		addConverter(new AbstractConverter<Movimentacao, MovimentacaoJpa>() {
			@Override
			protected MovimentacaoJpa convert(Movimentacao source) {
				if (source == null) {
					return null;
				}
				var movimentacaoJpa = new MovimentacaoJpa();
				movimentacaoJpa.id = source.getId().getId();
				movimentacaoJpa.dataHora = source.getDataHora();
				movimentacaoJpa.quantidade = source.getQuantidade().getValor();
				movimentacaoJpa.tipo = source.getTipo().name();
				movimentacaoJpa.motivo = source.getMotivo().getValor();
				movimentacaoJpa.responsavel = source.getResponsavel().getValor();
				// produto e estoque serão carregados do banco
				return movimentacaoJpa;
			}
		});

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
				return TipoMovimentacao.valueOf(source);
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
		addConverter(new AbstractConverter<PontoRessuprimentoJpa, PontoRessuprimento>() {
			@Override
			protected PontoRessuprimento convert(PontoRessuprimentoJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, PontoRessuprimentoId.class);
				var estoqueId = map(source.estoque.id, EstoqueId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var estoqueSeguranca = map(source.estoqueSeguranca, EstoqueSeguranca.class);
				return new PontoRessuprimento(id, estoqueId, produtoId, estoqueSeguranca);
			}
		});

		addConverter(new AbstractConverter<PontoRessuprimento, PontoRessuprimentoJpa>() {
			@Override
			protected PontoRessuprimentoJpa convert(PontoRessuprimento source) {
				if (source == null) {
					return null;
				}
				var pontoJpa = new PontoRessuprimentoJpa();
				pontoJpa.id = source.getId().getId();
				pontoJpa.estoqueSeguranca = source.getEstoqueSeguranca().getValor();
				// estoque e produto serão carregados do banco
				return pontoJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, PontoRessuprimentoId>() {
			@Override
			protected PontoRessuprimentoId convert(Integer source) {
				return new PontoRessuprimentoId(source);
			}
		});

		addConverter(new AbstractConverter<Double, ConsumoMedioDiario>() {
			@Override
			protected ConsumoMedioDiario convert(Double source) {
				if (source == null) {
					return null;
				}
				return new ConsumoMedioDiario(source);
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

		addConverter(new AbstractConverter<Integer, RopCalculado>() {
			@Override
			protected RopCalculado convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new RopCalculado(source);
			}
		});

		addConverter(new AbstractConverter<Integer, SaldoAtual>() {
			@Override
			protected SaldoAtual convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new SaldoAtual(source);
			}
		});

		addConverter(new AbstractConverter<String, StatusRop>() {
			@Override
			protected StatusRop convert(String source) {
				if (source == null) {
					return null;
				}
				return StatusRop.valueOf(source);
			}
		});

		// Conversores para Pedido
		addConverter(new AbstractConverter<PedidoJpa, Pedido>() {
			@Override
			protected Pedido convert(PedidoJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, PedidoId.class);
				var fornecedorId = map(source.fornecedor.id, FornecedorId.class);
				var estoqueId = source.estoque != null ? map(source.estoque.id, EstoqueId.class) : new EstoqueId(0);
				var itens = new ArrayList<ItemPedido>();
				if (source.itens != null) {
					for (var itemJpa : source.itens) {
						var produtoId = map(itemJpa.produtoId, ProdutoId.class);
						var quantidade = map(itemJpa.quantidade, Quantidade.class);
						var precoUnitario = map(itemJpa.precoUnitario, Custo.class);
						itens.add(new ItemPedido(produtoId, quantidade, precoUnitario));
					}
				}
				var dataPedido = map(source.dataPedido, DataPedido.class);
				var status = map(source.status, StatusPedido.class);
				var pedido = new Pedido(id, fornecedorId, estoqueId, itens, dataPedido, status);
				// Usar reflection ou métodos setter se necessário
				// Por enquanto, calcular valor total e data prevista após criar
				pedido.calcularValorTotal();
				if (source.dataPrevista != null) {
					// Data prevista será calculada no serviço
					// Usar reflection para definir se necessário
					try {
						var field = Pedido.class.getDeclaredField("dataPrevista");
						field.setAccessible(true);
						field.set(pedido, map(source.dataPrevista, DataPrevista.class));
					} catch (Exception e) {
						// Ignorar se não conseguir acessar
					}
				}
				return pedido;
			}
		});

		addConverter(new AbstractConverter<Pedido, PedidoJpa>() {
			@Override
			protected PedidoJpa convert(Pedido source) {
				if (source == null) {
					return null;
				}
				var pedidoJpa = new PedidoJpa();
				pedidoJpa.id = source.getId().getId();
				// Fornecedor e Estoque serão carregados separadamente no repositório
				pedidoJpa.valorTotal = source.getValorTotal().getValor();
				pedidoJpa.dataPedido = source.getDataPedido().getValor();
				if (source.getDataPrevista() != null) {
					pedidoJpa.dataPrevista = source.getDataPrevista().getValor();
				}
				pedidoJpa.status = source.getStatus().name();
				var itensJpa = new ArrayList<ItemPedidoJpa>();
				for (var item : source.getItens()) {
					var itemJpa = new ItemPedidoJpa();
					itemJpa.produtoId = item.getProdutoId().getId();
					itemJpa.quantidade = item.getQuantidade().getValor();
					itemJpa.precoUnitario = item.getPrecoUnitario().getValor();
					itensJpa.add(itemJpa);
				}
				pedidoJpa.itens = itensJpa;
				return pedidoJpa;
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

		addConverter(new AbstractConverter<PedidoJpa, PedidoId>() {
			@Override
			protected PedidoId convert(PedidoJpa source) {
				if (source == null) {
					return null;
				}
				return new PedidoId(source.id);
			}
		});

		addConverter(new AbstractConverter<BigDecimal, ValorTotal>() {
			@Override
			protected ValorTotal convert(BigDecimal source) {
				if (source == null) {
					return null;
				}
				return new ValorTotal(source);
			}
		});

		addConverter(new AbstractConverter<ValorTotal, BigDecimal>() {
			@Override
			protected BigDecimal convert(ValorTotal source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});

		addConverter(new AbstractConverter<LocalDate, DataPedido>() {
			@Override
			protected DataPedido convert(LocalDate source) {
				if (source == null) {
					return null;
				}
				return new DataPedido(source);
			}
		});

		addConverter(new AbstractConverter<DataPedido, LocalDate>() {
			@Override
			protected LocalDate convert(DataPedido source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});

		addConverter(new AbstractConverter<LocalDate, DataPrevista>() {
			@Override
			protected DataPrevista convert(LocalDate source) {
				if (source == null) {
					return null;
				}
				return new DataPrevista(source);
			}
		});

		addConverter(new AbstractConverter<DataPrevista, LocalDate>() {
			@Override
			protected LocalDate convert(DataPrevista source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});

		addConverter(new AbstractConverter<String, StatusPedido>() {
			@Override
			protected StatusPedido convert(String source) {
				if (source == null) {
					return null;
				}
				return StatusPedido.valueOf(source);
			}
		});

		// Conversores para Reserva
		addConverter(new AbstractConverter<ReservaJpa, Reserva>() {
			@Override
			protected Reserva convert(ReservaJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, ReservaId.class);
				var pedidoId = map(source.pedido.id, PedidoId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var quantidade = map(source.quantidade, Quantidade.class);
				var dataHoraReserva = map(source.dataHoraReserva, DataHoraReserva.class);
				var status = map(source.status, StatusReserva.class);
				var reserva = new Reserva(id, pedidoId, produtoId, quantidade, dataHoraReserva, status);
				// Definir tipoLiberacao e dataHoraLiberacao se existirem
				if (source.tipoLiberacao != null) {
					try {
						var tipoLiberacaoField = Reserva.class.getDeclaredField("tipoLiberacao");
						tipoLiberacaoField.setAccessible(true);
						tipoLiberacaoField.set(reserva, map(source.tipoLiberacao, TipoLiberacao.class));
					} catch (Exception e) {
						// Ignorar se não conseguir acessar
					}
				}
				if (source.dataHoraLiberacao != null) {
					try {
						var dataHoraLiberacaoField = Reserva.class.getDeclaredField("dataHoraLiberacao");
						dataHoraLiberacaoField.setAccessible(true);
						dataHoraLiberacaoField.set(reserva, map(source.dataHoraLiberacao, DataHoraLiberacao.class));
					} catch (Exception e) {
						// Ignorar se não conseguir acessar
					}
				}
				return reserva;
			}
		});

		addConverter(new AbstractConverter<Reserva, ReservaJpa>() {
			@Override
			protected ReservaJpa convert(Reserva source) {
				if (source == null) {
					return null;
				}
				var reservaJpa = new ReservaJpa();
				reservaJpa.id = source.getId().getId();
				// Pedido e Produto serão carregados separadamente
				reservaJpa.quantidade = source.getQuantidade().getValor();
				reservaJpa.dataHoraReserva = source.getDataHoraReserva().getValor();
				reservaJpa.status = source.getStatus().name();
				if (source.getTipoLiberacao() != null) {
					reservaJpa.tipoLiberacao = source.getTipoLiberacao().name();
				}
				if (source.getDataHoraLiberacao() != null) {
					reservaJpa.dataHoraLiberacao = source.getDataHoraLiberacao().getValor();
				}
				return reservaJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, ReservaId>() {
			@Override
			protected ReservaId convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new ReservaId(source);
			}
		});

		addConverter(new AbstractConverter<LocalDateTime, DataHoraReserva>() {
			@Override
			protected DataHoraReserva convert(LocalDateTime source) {
				if (source == null) {
					return null;
				}
				return new DataHoraReserva(source);
			}
		});

		addConverter(new AbstractConverter<DataHoraReserva, LocalDateTime>() {
			@Override
			protected LocalDateTime convert(DataHoraReserva source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});

		addConverter(new AbstractConverter<LocalDateTime, DataHoraLiberacao>() {
			@Override
			protected DataHoraLiberacao convert(LocalDateTime source) {
				if (source == null) {
					return null;
				}
				return new DataHoraLiberacao(source);
			}
		});

		addConverter(new AbstractConverter<DataHoraLiberacao, LocalDateTime>() {
			@Override
			protected LocalDateTime convert(DataHoraLiberacao source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});

		addConverter(new AbstractConverter<String, StatusReserva>() {
			@Override
			protected StatusReserva convert(String source) {
				if (source == null) {
					return null;
				}
				return StatusReserva.valueOf(source);
			}
		});

		addConverter(new AbstractConverter<String, TipoLiberacao>() {
			@Override
			protected TipoLiberacao convert(String source) {
				if (source == null) {
					return null;
				}
				return TipoLiberacao.valueOf(source);
			}
		});

		// Conversores para Transferencia
		addConverter(new AbstractConverter<TransferenciaJpa, Transferencia>() {
			@Override
			protected Transferencia convert(TransferenciaJpa source) {
				if (source == null) {
					return null;
				}
				var id = map(source.id, TransferenciaId.class);
				var produtoId = map(source.produto.id, ProdutoId.class);
				var quantidade = map(source.quantidade, Quantidade.class);
				var estoqueOrigem = map(source.estoqueOrigem.id, EstoqueId.class);
				var estoqueDestino = map(source.estoqueDestino.id, EstoqueId.class);
				var dataHora = map(source.dataHoraTransferencia, DataHoraTransferencia.class);
				var responsavel = map(source.responsavel, Responsavel.class);
				var motivo = map(source.motivo, Motivo.class);
				var movimentacaoSaidaId = source.movimentacaoSaida != null
					? map(source.movimentacaoSaida.id, MovimentacaoId.class)
					: null;
				var movimentacaoEntradaId = source.movimentacaoEntrada != null
					? map(source.movimentacaoEntrada.id, MovimentacaoId.class)
					: null;
				return new Transferencia(id, produtoId, quantidade, estoqueOrigem, estoqueDestino, dataHora,
					responsavel, motivo, movimentacaoSaidaId, movimentacaoEntradaId);
			}
		});

		addConverter(new AbstractConverter<Transferencia, TransferenciaJpa>() {
			@Override
			protected TransferenciaJpa convert(Transferencia source) {
				if (source == null) {
					return null;
				}
				var transferenciaJpa = new TransferenciaJpa();
				transferenciaJpa.id = source.getId().getId();
				// Produto, estoques e movimentações serão carregados separadamente
				transferenciaJpa.quantidade = source.getQuantidade().getValor();
				transferenciaJpa.dataHoraTransferencia = source.getDataHora().getValor();
				transferenciaJpa.responsavel = source.getResponsavel().getValor();
				transferenciaJpa.motivo = source.getMotivo().getValor();
				return transferenciaJpa;
			}
		});

		addConverter(new AbstractConverter<Integer, TransferenciaId>() {
			@Override
			protected TransferenciaId convert(Integer source) {
				if (source == null) {
					return null;
				}
				return new TransferenciaId(source);
			}
		});

		addConverter(new AbstractConverter<LocalDateTime, DataHoraTransferencia>() {
			@Override
			protected DataHoraTransferencia convert(LocalDateTime source) {
				if (source == null) {
					return null;
				}
				return new DataHoraTransferencia(source);
			}
		});

		addConverter(new AbstractConverter<DataHoraTransferencia, LocalDateTime>() {
			@Override
			protected LocalDateTime convert(DataHoraTransferencia source) {
				if (source == null) {
					return null;
				}
				return source.getValor();
			}
		});
	}

	@Override
	public <D> D map(Object source, Class<D> destinationType) {
		return source != null ? super.map(source, destinationType) : null;
	}
}
