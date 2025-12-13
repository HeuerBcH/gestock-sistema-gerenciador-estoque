package dev.gestock.sge.infraestrutura.persistencia.jpa;

import static jakarta.persistence.GenerationType.IDENTITY;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import dev.gestock.sge.aplicacao.dominio.alerta.AlertaRepositorioAplicacao;
import dev.gestock.sge.aplicacao.dominio.alerta.AlertaResumo;
import dev.gestock.sge.dominio.principal.alerta.Alerta;
import dev.gestock.sge.dominio.principal.alerta.AlertaId;
import dev.gestock.sge.dominio.principal.alerta.AlertaRepositorio;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ALERTA")
class AlertaJpa {
	@Id
	@GeneratedValue(strategy = IDENTITY)
	Long id;

	@ManyToOne
	@JoinColumn(name = "PRODUTO_ID", nullable = false)
	ProdutoJpa produto;

	@ManyToOne
	@JoinColumn(name = "ESTOQUE_ID", nullable = false)
	EstoqueJpa estoque;

	@ManyToOne
	@JoinColumn(name = "FORNECEDOR_SUGERIDO_ID")
	FornecedorJpa fornecedorSugerido;

	@Column(name = "DATA_GERACAO", nullable = false)
	LocalDateTime dataGeracao;

	@Column(nullable = false)
	Boolean ativo;

	@Override
	public String toString() {
		return "Alerta{produto=" + (produto != null ? produto.id : "null") +
				", estoque=" + (estoque != null ? estoque.id : "null") +
				", ativo=" + ativo + "}";
	}
}

interface AlertaJpaRepository extends JpaRepository<AlertaJpa, Long> {
	List<AlertaJpa> findByAtivoTrue();

	List<AlertaJpa> findByProdutoId(Long produtoId);

	List<AlertaJpa> findByEstoqueId(Long estoqueId);

	@Query("SELECT a.id as id, a.produto.id as produtoId, a.estoque.id as estoqueId, a.dataGeracao as dataGeracao, a.fornecedorSugerido.id as fornecedorSugeridoId, a.ativo as ativo FROM AlertaJpa a WHERE a.ativo = true ORDER BY a.dataGeracao DESC")
	List<AlertaResumo> findAlertaResumoByAtivoTrueOrderByDataGeracaoDesc();
}

@Repository
class AlertaRepositorioImpl implements AlertaRepositorio, AlertaRepositorioAplicacao {
	@Autowired
	AlertaJpaRepository repositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public void salvar(Alerta alerta) {
		var alertaJpa = mapeador.map(alerta, AlertaJpa.class);
		repositorio.save(alertaJpa);
	}

	@Override
	public Optional<Alerta> obter(AlertaId id) {
		return repositorio.findById(id.getId())
				.map(a -> mapeador.map(a, Alerta.class));
	}

	@Override
	public List<Alerta> listarAtivos() {
		var alertas = repositorio.findByAtivoTrue();
		return mapeador.map(alertas, new TypeToken<List<Alerta>>() {
		}.getType());
	}

	@Override
	public List<Alerta> listarPorProduto(ProdutoId produtoId) {
		var alertas = repositorio.findByProdutoId(produtoId.getId());
		return mapeador.map(alertas, new TypeToken<List<Alerta>>() {
		}.getType());
	}

	@Override
	public List<Alerta> listarPorEstoque(EstoqueId estoqueId) {
		var alertas = repositorio.findByEstoqueId(estoqueId.getId());
		return mapeador.map(alertas, new TypeToken<List<Alerta>>() {
		}.getType());
	}

	@Override
	public AlertaId novoAlertaId() {
		var ultimoId = repositorio.findAll().stream()
				.mapToLong(a -> a.id)
				.max()
				.orElse(0L);
		return new AlertaId(ultimoId + 1);
	}

	@Override
	public List<AlertaResumo> pesquisarResumos() {
		return repositorio.findAlertaResumoByAtivoTrueOrderByDataGeracaoDesc();
	}
}
