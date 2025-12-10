//package dev.gestock.sge.infraestrutura.persistencia.jpa;
//
//import java.util.List;
//import java.util.Set;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import dev.gestock.sge.aplicacao.acervo.autor.AutorRepositorioAplicacao;
//import dev.gestock.sge.aplicacao.acervo.autor.AutorResumo;
//import dev.gestock.sge.dominio.principal.cliente.Cliente;
//import dev.gestock.sge.dominio.principal.cliente.ClienteId;
//import dev.gestock.sge.dominio.principal.cliente.ClienteRepositorio;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToMany;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "AUTOR")
//class AutorJpa {
//	@Id
//	int id;
//
//	String nome;
//
//	@ManyToMany(mappedBy = "autores")
//	Set<LivroJpa> livros;
//
//	@Override
//	public String toString() {
//		return nome;
//	}
//}
//
//interface AutorJpaRepository extends JpaRepository<AutorJpa, Integer> {
//	List<AutorResumo> findAutorResumoByOrderByNome();
//}
//
//@Repository
//class AutorRepositorioImpl implements ClienteRepositorio, AutorRepositorioAplicacao {
//	@Autowired
//	AutorJpaRepository repositorio;
//
//	@Autowired
//	JpaMapeador mapeador;
//
//	@Override
//	public void salvar(Cliente autor) {
//		var autorJpa = mapeador.map(autor, AutorJpa.class);
//		repositorio.save(autorJpa);
//	}
//
//	@Override
//	public Cliente obter(ClienteId id) {
//		var autorJpa = repositorio.findById(id.getId()).get();
//		return mapeador.map(autorJpa, Cliente.class);
//	}
//
//	@Override
//	public List<AutorResumo> pesquisarResumos() {
//		return repositorio.findAutorResumoByOrderByNome();
//	}
//}