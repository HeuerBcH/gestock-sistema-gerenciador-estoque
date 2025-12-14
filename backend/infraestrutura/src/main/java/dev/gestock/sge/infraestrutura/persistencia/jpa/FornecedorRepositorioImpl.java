package dev.gestock.sge.infraestrutura.persistencia.jpa;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorRepositorioAplicacao;
import dev.gestock.sge.aplicacao.fornecedor.FornecedorResumo;
import dev.gestock.sge.dominio.fornecedor.Cnpj;
import dev.gestock.sge.dominio.fornecedor.Fornecedor;
import dev.gestock.sge.dominio.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.fornecedor.FornecedorRepositorio;

@Repository
class FornecedorRepositorioImpl implements FornecedorRepositorio, FornecedorRepositorioAplicacao {
	@Autowired
	FornecedorJpaRepository repositorio;

	@Autowired
	CotacaoJpaRepository cotacaoRepositorio;

	@Autowired
	JpaMapeador mapeador;

	@Override
	public Fornecedor salvar(Fornecedor fornecedor) {
		var fornecedorJpa = mapeador.map(fornecedor, FornecedorJpa.class);
		fornecedorJpa = repositorio.save(fornecedorJpa);
		return mapeador.map(fornecedorJpa, Fornecedor.class);
	}

	@Transactional
	@Override
	public Fornecedor obter(FornecedorId id) {
		var fornecedorJpa = repositorio.findById(id.getId()).orElse(null);
		return mapeador.map(fornecedorJpa, Fornecedor.class);
	}

	@Transactional
	@Override
	public Fornecedor obterPorCnpj(Cnpj cnpj) {
		var fornecedorJpa = repositorio.findByCnpj(cnpj.getNumero()).orElse(null);
		return mapeador.map(fornecedorJpa, Fornecedor.class);
	}

	@Transactional
	@Override
	public void remover(FornecedorId id) {
		// Primeiro remove cotações associadas ao fornecedor
		cotacaoRepositorio.deleteByFornecedorId(id.getId());
		// Depois remove o fornecedor
		repositorio.deleteById(id.getId());
	}

	@Override
	public boolean possuiPedidosPendentes(int fornecedorId) {
		return repositorio.existsPedidosPendentesByFornecedorId(fornecedorId);
	}

	@Override
	public List<FornecedorResumo> pesquisarResumos() {
		var fornecedoresJpa = repositorio.findAll();
		return criarResumos(fornecedoresJpa);
	}

	@Override
	public List<FornecedorResumo> pesquisarPorNomeOuCnpj(String termo) {
		var fornecedoresJpa = repositorio.findByNomeContainingIgnoreCaseOrCnpjContaining(termo);
		return criarResumos(fornecedoresJpa);
	}

	@Override
	public List<FornecedorResumo> pesquisarPorStatus(String status) {
		var fornecedoresJpa = repositorio.findByStatus(status);
		return criarResumos(fornecedoresJpa);
	}

	@Override
	public FornecedorResumo obterResumo(int id) {
		var fornecedorJpa = repositorio.findById(id).orElse(null);
		if (fornecedorJpa == null) {
			return null;
		}
		return criarResumo(fornecedorJpa);
	}

	private List<FornecedorResumo> criarResumos(List<FornecedorJpa> fornecedoresJpa) {
		return fornecedoresJpa.stream()
			.map(this::criarResumo)
			.toList();
	}

	private FornecedorResumo criarResumo(FornecedorJpa fornecedorJpa) {
		return new FornecedorResumo() {
			@Override
			public int getId() {
				return fornecedorJpa.id;
			}

			@Override
			public String getNome() {
				return fornecedorJpa.nome;
			}

			@Override
			public String getCnpj() {
				return fornecedorJpa.cnpj;
			}

			@Override
			public String getContato() {
				return fornecedorJpa.contato;
			}

			@Override
			public int getLeadTime() {
				return fornecedorJpa.leadTime;
			}

			@Override
			public java.math.BigDecimal getCusto() {
				return fornecedorJpa.custo;
			}

			@Override
			public String getStatus() {
				return fornecedorJpa.status;
			}
		};
	}
}

