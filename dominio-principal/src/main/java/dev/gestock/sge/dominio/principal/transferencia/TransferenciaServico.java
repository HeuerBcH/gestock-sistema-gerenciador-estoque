package dev.gestock.sge.dominio.principal.transferencia;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.Estoque;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueRepositorio;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio para operações que envolvem transferências
 * 
 * Responsabilidades:
 * - Criar transferências entre estoques do mesmo cliente
 * - Validar se o estoque de origem possui quantidade suficiente
 * - Processar transferências registrando movimentações
 * - Gerenciar o ciclo de vida das transferências
 */
public class TransferenciaServico {
    
    private final TransferenciaRepositorio transferenciaRepositorio;
    private final EstoqueRepositorio estoqueRepositorio;
    
    public TransferenciaServico(TransferenciaRepositorio transferenciaRepositorio, 
                               EstoqueRepositorio estoqueRepositorio) {
        notNull(transferenciaRepositorio, "Repositório de transferência é obrigatório");
        notNull(estoqueRepositorio, "Repositório de estoque é obrigatório");
        this.transferenciaRepositorio = transferenciaRepositorio;
        this.estoqueRepositorio = estoqueRepositorio;
    }
    
    /**
     * Cria uma transferência entre estoques (R1H22, R2H22)
     * 
     * @param clienteId ID do cliente
     * @param estoqueOrigemId ID do estoque de origem
     * @param estoqueDestinoId ID do estoque de destino
     * @param produtoId ID do produto
     * @param quantidade Quantidade a ser transferida
     * @param responsavel Responsável pela transferência
     * @param motivo Motivo da transferência
     * @return Transferencia criada
     */
    public Transferencia criarTransferencia(ClienteId clienteId, EstoqueId estoqueOrigemId, 
                                          EstoqueId estoqueDestinoId, ProdutoId produtoId, 
                                          int quantidade, String responsavel, String motivo) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueOrigemId, "Estoque de origem é obrigatório");
        notNull(estoqueDestinoId, "Estoque de destino é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");
        notBlank(motivo, "Motivo é obrigatório");
        
        // R1H22: Verifica se os estoques pertencem ao mesmo cliente
        Optional<Estoque> estoqueOrigem = estoqueRepositorio.buscarPorId(estoqueOrigemId);
        Optional<Estoque> estoqueDestino = estoqueRepositorio.buscarPorId(estoqueDestinoId);
        
        if (estoqueOrigem.isEmpty() || estoqueDestino.isEmpty()) {
            throw new IllegalArgumentException("Estoques não encontrados");
        }
        
        if (!estoqueOrigem.get().getClienteId().equals(clienteId) || 
            !estoqueDestino.get().getClienteId().equals(clienteId)) {
            throw new IllegalArgumentException("Ambos os estoques devem pertencer ao mesmo cliente");
        }
        
        // R2H22: Verifica se o estoque de origem possui quantidade suficiente
        int saldoDisponivel = estoqueOrigem.get().getSaldoDisponivel(produtoId);
        if (saldoDisponivel < quantidade) {
            throw new IllegalStateException("Saldo disponível insuficiente no estoque de origem");
        }
        
        Transferencia transferencia = new Transferencia(clienteId, estoqueOrigemId, estoqueDestinoId,
                                                       produtoId, quantidade, responsavel, motivo);
        transferenciaRepositorio.salvar(transferencia);
        
        return transferencia;
    }
    
    /**
     * Processa uma transferência (R3H22)
     * Registra as movimentações de saída e entrada
     * 
     * @param transferenciaId ID da transferência
     */
    public void processarTransferencia(TransferenciaId transferenciaId) {
        notNull(transferenciaId, "ID da transferência é obrigatório");
        
        Optional<Transferencia> transferenciaOpt = transferenciaRepositorio.buscarPorId(transferenciaId);
        if (transferenciaOpt.isEmpty()) {
            throw new IllegalArgumentException("Transferência não encontrada");
        }
        
        Transferencia transferencia = transferenciaOpt.get();
        if (!transferencia.isPendente()) {
            throw new IllegalStateException("Apenas transferências pendentes podem ser processadas");
        }
        
        // Busca os estoques
        Optional<Estoque> estoqueOrigem = estoqueRepositorio.buscarPorId(transferencia.getEstoqueOrigemId());
        Optional<Estoque> estoqueDestino = estoqueRepositorio.buscarPorId(transferencia.getEstoqueDestinoId());
        
        if (estoqueOrigem.isEmpty() || estoqueDestino.isEmpty()) {
            throw new IllegalArgumentException("Estoques não encontrados");
        }
        
        // R3H22: Registra movimentação de saída no estoque de origem
        estoqueOrigem.get().registrarSaida(transferencia.getProdutoId(), 
                                          transferencia.getQuantidade(),
                                          transferencia.getResponsavel(),
                                          "Transferência para " + transferencia.getEstoqueDestinoId());
        
        // R3H22: Registra movimentação de entrada no estoque de destino
        estoqueDestino.get().registrarEntrada(transferencia.getProdutoId(),
                                             transferencia.getQuantidade(),
                                             transferencia.getResponsavel(),
                                             "Transferência de " + transferencia.getEstoqueOrigemId(),
                                             null);
        
        // Conclui a transferência
        transferencia.concluir();
        transferenciaRepositorio.salvar(transferencia);
        
        // Salva os estoques atualizados
        estoqueRepositorio.salvar(estoqueOrigem.get());
        estoqueRepositorio.salvar(estoqueDestino.get());
    }
    
    /**
     * Cancela uma transferência (R2H23)
     * 
     * @param transferenciaId ID da transferência
     */
    public void cancelarTransferencia(TransferenciaId transferenciaId) {
        notNull(transferenciaId, "ID da transferência é obrigatório");
        
        Optional<Transferencia> transferenciaOpt = transferenciaRepositorio.buscarPorId(transferenciaId);
        if (transferenciaOpt.isEmpty()) {
            throw new IllegalArgumentException("Transferência não encontrada");
        }
        
        Transferencia transferencia = transferenciaOpt.get();
        transferencia.cancelar();
        transferenciaRepositorio.salvar(transferencia);
    }
    
    /**
     * Lista todas as transferências de um cliente
     * 
     * @param clienteId ID do cliente
     * @return Lista de transferências
     */
    public List<Transferencia> listarTransferenciasPorCliente(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        return transferenciaRepositorio.buscarTransferenciasPorCliente(clienteId);
    }
    
    /**
     * Lista todas as transferências de um estoque
     * 
     * @param estoqueId ID do estoque
     * @return Lista de transferências
     */
    public List<Transferencia> listarTransferenciasPorEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        return transferenciaRepositorio.buscarTransferenciasPorEstoqueOrigem(estoqueId);
    }
}
