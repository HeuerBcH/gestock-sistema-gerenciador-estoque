package dev.gestock.sge;

import static org.springframework.boot.SpringApplication.run;

import java.io.IOException;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

// Imports comentados - módulos antigos removidos
// import dev.sauloaraujo.dominio.analise.emprestimo.EmprestimoRegistroRepositorio;
// import dev.gestock.sge.aplicacao.acervo.autor.AutorRepositorioAplicacao;
// import dev.gestock.sge.aplicacao.acervo.autor.AutorServicoAplicacao;
// import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarRepositorioAplicacao;
// import dev.gestock.sge.aplicacao.acervo.exemplar.ExemplarServicoAplicacao;
// import dev.gestock.sge.aplicacao.acervo.livro.LivroRepositorioAplicacao;
// import dev.gestock.sge.aplicacao.acervo.livro.LivroServicoAplicacao;
// import dev.gestock.sge.aplicacao.analise.EmprestimoRegistroRepositorioAplicacao;
// import dev.gestock.sge.aplicacao.analise.EmprestimoRegistroServicoAplicacao;
// import dev.gestock.sge.dominio.acervo.autor.AutorRepositorio;
// import dev.gestock.sge.dominio.acervo.autor.AutorServico;
// import dev.gestock.sge.dominio.acervo.exemplar.EmprestimoServico;
// import dev.gestock.sge.dominio.acervo.exemplar.ExemplarRepositorio;
// import dev.gestock.sge.dominio.acervo.exemplar.ExemplarServico;
// import dev.gestock.sge.dominio.acervo.livro.LivroRepositorio;
// import dev.gestock.sge.dominio.acervo.livro.LivroServico;
import dev.gestock.sge.dominio.evento.EventoBarramento;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteRepositorioAplicacao;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteServicoAplicacao;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteRepositorio;
import dev.gestock.sge.dominio.autenticacao.cliente.ClienteServico;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorServicoAplicacao;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.fornecedor.FornecedorServico;
import dev.gestock.sge.aplicacao.produto.ProdutoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.produto.ProdutoServicoAplicacao;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;
import dev.gestock.sge.dominio.produto.ProdutoServico;
import dev.gestock.sge.aplicacao.estoque.EstoqueRepositorioAplicacao;
import dev.gestock.sge.aplicacao.estoque.EstoqueServicoAplicacao;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.estoque.EstoqueServico;
import dev.gestock.sge.dominio.estoque.IEstoqueServico;
import dev.gestock.sge.dominio.estoque.EstoqueServicoComLogging;
import dev.gestock.sge.aplicacao.cotacao.CotacaoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.cotacao.CotacaoServicoAplicacao;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;
import dev.gestock.sge.dominio.produto.ProdutoRepositorio;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.pontoresuprimento.PontoRessuprimentoServicoAplicacao;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoRepositorio;
import dev.gestock.sge.dominio.pontoresuprimento.PontoRessuprimentoServico;
import dev.gestock.sge.aplicacao.alerta.AlertaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.alerta.AlertaServicoAplicacao;
import dev.gestock.sge.dominio.alerta.AlertaServico;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.movimentacao.MovimentacaoServicoAplicacao;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoRepositorio;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoServico;
import dev.gestock.sge.dominio.estoque.EstoqueRepositorio;
import dev.gestock.sge.aplicacao.pedido.PedidoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.pedido.PedidoServicoAplicacao;
import dev.gestock.sge.dominio.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.pedido.PedidoServico;
import dev.gestock.sge.dominio.pedido.IPedidoServico;
import dev.gestock.sge.dominio.pedido.PedidoServicoProxy;
import dev.gestock.sge.dominio.cotacao.CotacaoRepositorio;
import dev.gestock.sge.dominio.cotacao.CotacaoServico;
import dev.gestock.sge.aplicacao.reserva.ReservaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.reserva.ReservaServicoAplicacao;
import dev.gestock.sge.dominio.reserva.ReservaRepositorio;
import dev.gestock.sge.dominio.reserva.ReservaServico;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.transferencia.TransferenciaServicoAplicacao;
import dev.gestock.sge.dominio.transferencia.TransferenciaRepositorio;
import dev.gestock.sge.dominio.transferencia.TransferenciaServico;
import dev.gestock.sge.dominio.movimentacao.MovimentacaoRepositorio;

@SpringBootApplication
public class BackendAplicacao {

	@Bean
	public ClienteServico clienteServico(ClienteRepositorio repositorio) {
		return new ClienteServico(repositorio);
	}

	@Bean
	public ClienteServicoAplicacao clienteServicoAplicacao(ClienteRepositorioAplicacao repositorio) {
		return new ClienteServicoAplicacao(repositorio);
	}

	@Bean
	public FornecedorServico fornecedorServico(FornecedorRepositorio repositorio) {
		return new FornecedorServico(repositorio);
	}

	@Bean
	public FornecedorServicoAplicacao fornecedorServicoAplicacao(FornecedorRepositorioAplicacao repositorio) {
		return new FornecedorServicoAplicacao(repositorio);
	}

	@Bean
	public ProdutoServico produtoServico(ProdutoRepositorio repositorio) {
		return new ProdutoServico(repositorio);
	}

	@Bean
	public ProdutoServicoAplicacao produtoServicoAplicacao(ProdutoRepositorioAplicacao repositorio) {
		return new ProdutoServicoAplicacao(repositorio);
	}

	@Bean
	public EstoqueServico estoqueServico(EstoqueRepositorio repositorio) {
		return new EstoqueServico(repositorio);
	}

	/**
	 * Exemplo opcional: Bean que usa o padrão Decorator para adicionar logging.
	 * 
	 * Este é um exemplo de como usar o padrão Decorator com EstoqueServico.
	 * Para ativar, descomente este método e comente o método estoqueServico acima.
	 * 
	 * Pattern: Decorator
	 * Funcionalidade: Gerenciar Estoques (SILVIO)
	 * 
	 * Exemplo de uso do Decorator:
	 * <pre>
	 * IEstoqueServico servicoBase = new EstoqueServico(repositorio);
	 * IEstoqueServico servicoComLog = new EstoqueServicoComLogging(servicoBase);
	 * return servicoComLog; // Retorna o serviço decorado com logging
	 * </pre>
	 */
	// @Bean
	// public IEstoqueServico estoqueServicoComLogging(EstoqueRepositorio repositorio) {
	// 	IEstoqueServico servicoBase = new EstoqueServico(repositorio);
	// 	return new EstoqueServicoComLogging(servicoBase);
	// }

	@Bean
	public EstoqueServicoAplicacao estoqueServicoAplicacao(EstoqueRepositorioAplicacao repositorio) {
		return new EstoqueServicoAplicacao(repositorio);
	}

	@Bean
	public CotacaoServico cotacaoServico(CotacaoRepositorio repositorio, ProdutoRepositorio produtoRepositorio,
			FornecedorRepositorio fornecedorRepositorio) {
		return new CotacaoServico(repositorio, produtoRepositorio, fornecedorRepositorio);
	}

	@Bean
	public CotacaoServicoAplicacao cotacaoServicoAplicacao(CotacaoRepositorioAplicacao repositorio,
			CotacaoServico cotacaoServico) {
		return new CotacaoServicoAplicacao(repositorio, cotacaoServico);
	}

	@Bean
	public MovimentacaoServico movimentacaoServico(MovimentacaoRepositorio repositorio,
			ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio,
			EventoBarramento barramento) {
		return new MovimentacaoServico(repositorio, produtoRepositorio, estoqueRepositorio, barramento);
	}

	@Bean
	public MovimentacaoServicoAplicacao movimentacaoServicoAplicacao(MovimentacaoRepositorioAplicacao repositorio) {
		return new MovimentacaoServicoAplicacao(repositorio);
	}

	@Bean
	public PontoRessuprimentoServico pontoRessuprimentoServico(PontoRessuprimentoRepositorio repositorio,
			ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio) {
		return new PontoRessuprimentoServico(repositorio, produtoRepositorio, estoqueRepositorio);
	}

	@Bean
	public PontoRessuprimentoServicoAplicacao pontoRessuprimentoServicoAplicacao(PontoRessuprimentoRepositorioAplicacao repositorio) {
		return new PontoRessuprimentoServicoAplicacao(repositorio);
	}

	@Bean
	public AlertaServico alertaServico() {
		return new AlertaServico();
	}

	@Bean
	public AlertaServicoAplicacao alertaServicoAplicacao(AlertaRepositorioAplicacao repositorio) {
		return new AlertaServicoAplicacao(repositorio);
	}

	/**
	 * Configura o serviço de pedidos usando o padrão Proxy.
	 *
	 * <p>O padrão Proxy permite controlar o acesso ao objeto real (PedidoServico),
	 * adicionando funcionalidades como controle de acesso e auditoria sem modificar
	 * o código do objeto real.</p>
	 *
	 * <p>Este método retorna IPedidoServico, permitindo substituição transparente
	 * entre PedidoServico (Subject Real) e PedidoServicoProxy (Proxy).</p>
	 *
	 * <p><strong>Opções de uso:</strong></p>
	 * <ul>
	 *   <li><strong>Opção 1 (atual):</strong> Retorna PedidoServico diretamente,
	 *       sem proxy. Use quando não precisar de controle de acesso ou auditoria.</li>
	 *   <li><strong>Opção 2:</strong> Retorna PedidoServicoProxy envolvendo PedidoServico.
	 *       Use quando precisar de controle de acesso e auditoria.</li>
	 * </ul>
	 *
	 * <p><strong>Exemplo de uso com Proxy:</strong></p>
	 * <pre>{@code
	 * // Para usar o proxy, descomente as linhas abaixo e comente a linha atual:
	 * IPedidoServico servicoBase = new PedidoServico(repositorio, ...);
	 * return new PedidoServicoProxy(repositorio, fornecedorRepositorio, ...);
	 * }</pre>
	 *
	 */
	@Bean
	public IPedidoServico pedidoServico(PedidoRepositorio repositorio, FornecedorRepositorio fornecedorRepositorio,
			ProdutoRepositorio produtoRepositorio, EstoqueRepositorio estoqueRepositorio,
			CotacaoRepositorio cotacaoRepositorio, CotacaoServico cotacaoServico,
			MovimentacaoServico movimentacaoServico, EventoBarramento barramento) {
		// Opção 1: Retornar PedidoServico diretamente (comportamento atual)
		return new PedidoServico(repositorio, fornecedorRepositorio, produtoRepositorio, estoqueRepositorio,
				cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
		
		// Opção 2: Retornar PedidoServicoProxy para adicionar controle de acesso e auditoria
		// IPedidoServico servicoBase = new PedidoServico(repositorio, fornecedorRepositorio, produtoRepositorio,
		//		estoqueRepositorio, cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
		// return new PedidoServicoProxy(repositorio, fornecedorRepositorio, produtoRepositorio,
		//		estoqueRepositorio, cotacaoRepositorio, cotacaoServico, movimentacaoServico, barramento);
	}

	@Bean
	public PedidoServicoAplicacao pedidoServicoAplicacao(PedidoRepositorioAplicacao repositorio) {
		return new PedidoServicoAplicacao(repositorio);
	}

	@Bean
	public ReservaServico reservaServico(ReservaRepositorio repositorio) {
		return new ReservaServico(repositorio);
	}

	@Bean
	public ReservaServicoAplicacao reservaServicoAplicacao(ReservaRepositorioAplicacao repositorioAplicacao,
			ReservaServico reservaServico, EventoBarramento barramento) {
		return new ReservaServicoAplicacao(repositorioAplicacao, reservaServico, barramento);
	}

	@Bean
	public TransferenciaServico transferenciaServico(TransferenciaRepositorio repositorio) {
		return new TransferenciaServico(repositorio);
	}

	@Bean
	public TransferenciaServicoAplicacao transferenciaServicoAplicacao(TransferenciaRepositorioAplicacao repositorioAplicacao,
			TransferenciaServico transferenciaServico, MovimentacaoRepositorio movimentacaoRepositorio,
			MovimentacaoServico movimentacaoServico, EventoBarramento barramento) {
		return new TransferenciaServicoAplicacao(repositorioAplicacao, transferenciaServico, movimentacaoRepositorio,
			movimentacaoServico, barramento);
	}

	public static void main(String[] args) throws IOException {
		run(BackendAplicacao.class, args);
	}
}