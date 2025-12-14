package dev.gestock.sge.apresentacao.autenticacao.cliente;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import dev.gestock.sge.aplicacao.autenticacao.cliente.ClienteServicoAplicacao;
import dev.gestock.sge.dominio.autenticacao.cliente.*;
import dev.gestock.sge.apresentacao.BackendMapeador;
import dev.gestock.sge.apresentacao.autenticacao.cliente.ClienteFormulario.ClienteDto;
import dev.gestock.sge.apresentacao.autenticacao.cliente.ClienteFormulario.LoginDto;
import dev.gestock.sge.apresentacao.config.JwtTokenProvider;

@RestController
@RequestMapping("backend/cliente")
class ClienteControlador {

	private @Autowired ClienteServico clienteServico;
	private @Autowired ClienteServicoAplicacao clienteServicoAplicacao;
	private @Autowired BackendMapeador mapeador;
	private @Autowired JwtTokenProvider jwtTokenProvider;

	@RequestMapping(method = POST, path = "registro")
	RegistroResponse registro(@RequestBody ClienteDto dto) {
		var email = mapeador.map(dto.email, Email.class);
		var documento = mapeador.map(dto.documento, CpfCnpj.class);
		var senha = mapeador.map(dto.senha, Senha.class);
		
		// Criar cliente com ID temporário (será gerado pelo banco)
		var clienteId = new ClienteId(0);
		var cliente = new Cliente(clienteId, dto.nome, email, documento, senha);
		
		// Registrar e obter cliente salvo com ID gerado
		var clienteSalvo = clienteServico.registrarERetornar(cliente);
		
		// Gerar token JWT
		var token = jwtTokenProvider.gerarToken(clienteSalvo.getId().getId(), clienteSalvo.getEmail().getEndereco());
		
		return new RegistroResponse(token, clienteSalvo.getId().getId());
	}

	@RequestMapping(method = POST, path = "login")
	LoginResponse login(@RequestBody LoginDto dto) {
		var email = mapeador.map(dto.email, Email.class);
		var senha = mapeador.map(dto.senha, Senha.class);
		
		var cliente = clienteServico.autenticar(email, senha);
		
		// Gerar token JWT
		var token = jwtTokenProvider.gerarToken(cliente.getId().getId(), cliente.getEmail().getEndereco());
		
		return new LoginResponse(token, cliente.getId().getId());
	}

	@RequestMapping(method = GET, path = "perfil")
	ClienteResumoResponse perfil(@RequestHeader("Authorization") String authorization) {
		var token = authorization.replace("Bearer ", "");
		var clienteId = jwtTokenProvider.obterClienteIdDoToken(token);
		
		var resumo = clienteServicoAplicacao.obterResumo(clienteId);
		
		return new ClienteResumoResponse(resumo.getId(), resumo.getNome(), resumo.getEmail());
	}

	public static class RegistroResponse {
		public String token;
		public int clienteId;

		public RegistroResponse(String token, int clienteId) {
			this.token = token;
			this.clienteId = clienteId;
		}
	}

	public static class LoginResponse {
		public String token;
		public int clienteId;

		public LoginResponse(String token, int clienteId) {
			this.token = token;
			this.clienteId = clienteId;
		}
	}

	public static class ClienteResumoResponse {
		public int id;
		public String nome;
		public String email;

		public ClienteResumoResponse(int id, String nome, String email) {
			this.id = id;
			this.nome = nome;
			this.email = email;
		}
	}
}

