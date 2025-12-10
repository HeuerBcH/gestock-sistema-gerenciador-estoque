//package dev.gestock.sge.infraestrutura.persistencia.jpa;
//
//import java.util.List;
//
//import org.modelmapper.AbstractConverter;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeToken;
//import org.modelmapper.config.Configuration.AccessLevel;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import dev.gestock.dominio.analise.emprestimo.EmprestimoRegistro;
//import dev.gestock.dominio.analise.emprestimo.EmprestimoRegistroId;
//import dev.gestock.sge.dominio.principal.cliente.Cliente;
//import dev.gestock.sge.dominio.principal.cliente.ClienteId;
//import dev.gestock.sge.dominio.principal.estoque.Emprestimo;
//import dev.gestock.sge.dominio.principal.estoque.Estoque;
//import dev.gestock.sge.dominio.principal.estoque.ExemplarId;
//import dev.gestock.sge.dominio.principal.estoque.Periodo;
//import dev.gestock.sge.dominio.principal.livro.Isbn;
//import dev.gestock.sge.dominio.principal.livro.IsbnFabrica;
//import dev.gestock.sge.dominio.principal.livro.Livro;
//import dev.gestock.sge.dominio.administracao.socio.Email;
//import dev.gestock.sge.dominio.administracao.socio.Socio;
//import dev.gestock.sge.dominio.administracao.socio.SocioId;
//
//@Component
//class JpaMapeador extends ModelMapper {
//	private IsbnFabrica isbnFabrica;
//
//	private @Autowired LivroJpaRepository livroRepositorio;
//	private @Autowired SocioJpaRepository socioRepositorio;
//
//	JpaMapeador() {
//		isbnFabrica = new IsbnFabrica();
//
//		var configuracao = getConfiguration();
//		configuracao.setFieldMatchingEnabled(true);
//		configuracao.setFieldAccessLevel(AccessLevel.PRIVATE);
//
//		addConverter(new AbstractConverter<AutorJpa, Cliente>() {
//			@Override
//			protected Cliente convert(AutorJpa source) {
//				var id = map(source.id, ClienteId.class);
//				return new Cliente(id, source.nome);
//			}
//		});
//
//		addConverter(new AbstractConverter<Integer, ClienteId>() {
//			@Override
//			protected ClienteId convert(Integer source) {
//				return new ClienteId(source);
//			}
//		});
//
//		addConverter(new AbstractConverter<AutorJpa, ClienteId>() {
//			@Override
//			protected ClienteId convert(AutorJpa source) {
//				return map(source.id, ClienteId.class);
//			}
//		});
//
//		addConverter(new AbstractConverter<LivroJpa, Livro>() {
//			@Override
//			protected Livro convert(LivroJpa source) {
//				var id = map(source.id, Isbn.class);
//				List<ClienteId> autores = map(source.autores, new TypeToken<List<ClienteId>>() {
//				}.getType());
//				return new Livro(id, source.titulo, source.subtitulo, autores);
//			}
//		});
//
//		addConverter(new AbstractConverter<String, Isbn>() {
//			@Override
//			protected Isbn convert(String source) {
//				return isbnFabrica.construir(source);
//			}
//		});
//
//		addConverter(new AbstractConverter<LivroJpa, Isbn>() {
//			@Override
//			protected Isbn convert(LivroJpa source) {
//				return map(source.id, Isbn.class);
//			}
//		});
//
//		addConverter(new AbstractConverter<SocioJpa, Socio>() {
//			@Override
//			protected Socio convert(SocioJpa source) {
//				var id = map(source.id, SocioId.class);
//				var email = map(source.email, Email.class);
//				return new Socio(id, source.nome, email);
//			}
//		});
//
//		addConverter(new AbstractConverter<Integer, SocioId>() {
//			@Override
//			protected SocioId convert(Integer source) {
//				return new SocioId(source);
//			}
//		});
//
//		addConverter(new AbstractConverter<String, Email>() {
//			@Override
//			protected Email convert(String source) {
//				return new Email(source);
//			}
//		});
//
//		addConverter(new AbstractConverter<ExemplarJpa, Estoque>() {
//			@Override
//			protected Estoque convert(ExemplarJpa source) {
//				var id = map(source.id, ExemplarId.class);
//				var livro = map(source.livro, Isbn.class);
//				var emprestimo = map(source.emprestimo, Emprestimo.class);
//				return new Estoque(id, livro, emprestimo);
//			}
//		});
//
//		addConverter(new AbstractConverter<Integer, ExemplarId>() {
//			@Override
//			protected ExemplarId convert(Integer source) {
//				return new ExemplarId(source);
//			}
//		});
//
//		addConverter(new AbstractConverter<Isbn, LivroJpa>() {
//			@Override
//			protected LivroJpa convert(Isbn source) {
//				return livroRepositorio.findById(source.getCodigo()).get();
//			}
//		});
//
//		addConverter(new AbstractConverter<EmprestimoJpa, Emprestimo>() {
//			@Override
//			protected Emprestimo convert(EmprestimoJpa source) {
//				var periodo = map(source.periodo, Periodo.class);
//				var tomador = map(source.tomador, SocioId.class);
//				return new Emprestimo(periodo, tomador);
//			}
//		});
//
//		addConverter(new AbstractConverter<PeriodoJpa, Periodo>() {
//			@Override
//			protected Periodo convert(PeriodoJpa source) {
//				return new Periodo(source.inicio, source.fim);
//			}
//		});
//
//		addConverter(new AbstractConverter<SocioJpa, SocioId>() {
//			@Override
//			protected SocioId convert(SocioJpa source) {
//				return map(source.id, SocioId.class);
//			}
//		});
//
//		addConverter(new AbstractConverter<SocioId, SocioJpa>() {
//			@Override
//			protected SocioJpa convert(SocioId source) {
//				return socioRepositorio.findById(source.getId()).get();
//			}
//		});
//
//		addConverter(new AbstractConverter<EmprestimoRegistroJpa, EmprestimoRegistro>() {
//			@Override
//			protected EmprestimoRegistro convert(EmprestimoRegistroJpa source) {
//				var id = map(source.id, EmprestimoRegistroId.class);
//				var exemplar = map(source.exemplar.id, ExemplarId.class);
//				var emprestimo = map(source.emprestimo, Emprestimo.class);
//				return new EmprestimoRegistro(id, exemplar, emprestimo, source.devolucao);
//			}
//		});
//
//		addConverter(new AbstractConverter<Integer, EmprestimoRegistroId>() {
//			@Override
//			protected EmprestimoRegistroId convert(Integer source) {
//				return new EmprestimoRegistroId(source);
//			}
//		});
//	}
//
//	@Override
//	public <D> D map(Object source, Class<D> destinationType) {
//		return source != null ? super.map(source, destinationType) : null;
//	}
//}