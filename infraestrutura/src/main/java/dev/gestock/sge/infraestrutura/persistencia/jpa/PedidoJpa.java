package dev.gestock.sge.infraestrutura.persistencia.jpa;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.pedido.PedidoRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.pedido.PedidoResumo;
import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.pedido.Pedido;
import dev.gestock.sge.dominio.principal.pedido.PedidoId;
import dev.gestock.sge.dominio.principal.pedido.PedidoRepositorio;
import dev.gestock.sge.dominio.principal.pedido.StatusPedido;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PEDIDO")
class PedidoJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "CLIENTE_ID", nullable = false)
	ClienteJpa cliente;

	@ManyToOne
	@JoinColumn(name = "FORNECEDOR_ID", nullable = false)
	FornecedorJpa fornecedor;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID")
	EstoqueJpa estoque;

	@Column(name = "DATA_CRIACAO", nullable = false)
	LocalDate dataCriacao;

	@Column(name = "DATA_PREVISTA_ENTREGA")
	LocalDate dataPrevistaEntrega;

	@Column(nullable = false, length = 20)
	String status;


	@Override
	public String toString() {
		return "Pedido " + id + " | " + status;
	}
}

interface PedidoJpaRepository extends JpaRepository<PedidoJpa, Long> {
	List<PedidoJpa> findByClienteId(Long clienteId);

	List<PedidoJpa> findByFornecedorId(Long fornecedorId);

	List<PedidoJpa> findByStatus(String status);

	List<PedidoJpa> findByEstoqueId(Long estoqueId);

	@Query("SELECT COUNT(p) > 0 FROM PedidoJpa p WHERE p.fornecedor.id = :fornecedorId AND p.status IN ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE')")
	boolean existsPedidoPendenteParaFornecedor(Long fornecedorId);

	@Query("SELECT COUNT(p) > 0 FROM PedidoJpa p WHERE p.estoque.id = :estoqueId AND p.status IN ('CRIADO', 'ENVIADO', 'EM_TRANSPORTE')")
	boolean existsPedidoPendentePorEstoqueId(Long estoqueId);

	@Query("SELECT p.id as id, p.cliente.id as clienteId, p.fornecedor.id as fornecedorId, p.dataCriacao as dataCriacao, p.dataPrevistaEntrega as dataPrevistaEntrega, p.estoque.id as estoqueId, p.status as status FROM PedidoJpa p ORDER BY p.dataCriacao DESC")
	List<PedidoResumo> findPedidoResumoByOrderByDataCriacaoDesc();
}

@Repository
class PedidoRepositorioImpl implements PedidoRepositorio, PedidoRepositorioAplicacao {
	@Autowired
	PedidoJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Pedido pedido) {
		var pedidoJpa = mapeador.map(pedido, PedidoJpa.class);
		repositorio.save(pedidoJpa);
	}

	@Override
	public Optional<Pedido> buscarPorId(PedidoId id) {
		return repositorio.findById(id.getId())
				.map(p -> mapeador.map(p, Pedido.class));
	}

	@Override
	public List<Pedido> listarTodos() {
		var pedidos = repositorio.findAll();
		return mapeador.map(pedidos, new TypeToken<List<Pedido>>() {
		}.getType());
	}

	@Override
	public void cancelar(Pedido pedido) {
		var pedidoJpa = mapeador.map(pedido, PedidoJpa.class);
		pedidoJpa.status = StatusPedido.CANCELADO.name();
		repositorio.save(pedidoJpa);
	}

	@Override
	public List<Pedido> buscarPorStatus(StatusPedido status) {
		var pedidos = repositorio.findByStatus(status.name());
		return mapeador.map(pedidos, new TypeToken<List<Pedido>>() {
		}.getType());
	}

	@Override
	public List<Pedido> buscarPorFornecedorId(FornecedorId fornecedorId) {
		var pedidos = repositorio.findByFornecedorId(fornecedorId.getId());
		return mapeador.map(pedidos, new TypeToken<List<Pedido>>() {
		}.getType());
	}

	@Override
	public List<Pedido> buscarPedidosPorClienteId(ClienteId clienteId) {
		var pedidos = repositorio.findByClienteId(clienteId.getId());
		return mapeador.map(pedidos, new TypeToken<List<Pedido>>() {
		}.getType());
	}

	@Override
	public boolean existePedidoPendenteParaFornecedor(FornecedorId fornecedorId) {
		return repositorio.existsPedidoPendenteParaFornecedor(fornecedorId.getId());
	}

	@Override
	public List<Pedido> buscarPedidosPorEstoqueId(EstoqueId estoqueId) {
		var pedidos = repositorio.findByEstoqueId(estoqueId.getId());
		return mapeador.map(pedidos, new TypeToken<List<Pedido>>() {
		}.getType());
	}

	@Override
	public boolean existePedidoPendentePorEstoqueId(EstoqueId estoqueId) {
		return repositorio.existsPedidoPendentePorEstoqueId(estoqueId.getId());
	}

	@Override
	public List<PedidoResumo> pesquisarResumos() {
		return repositorio.findPedidoResumoByOrderByDataCriacaoDesc();
	}
}
