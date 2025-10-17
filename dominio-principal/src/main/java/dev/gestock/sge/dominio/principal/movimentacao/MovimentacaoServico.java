package dev.gestock.sge.dominio.principal.movimentacao;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Serviço de domínio para operações que envolvem movimentações
 * 
 * Responsabilidades:
 * - Registrar movimentações de entrada e saída
 * - Manter histórico completo de movimentações
 * - Calcular estatísticas de movimentação
 * - Gerenciar o ciclo de vida das movimentações
 */
public class MovimentacaoServico {
    
    private final MovimentacaoRepositorio movimentacaoRepositorio;
    
    public MovimentacaoServico(MovimentacaoRepositorio movimentacaoRepositorio) {
        notNull(movimentacaoRepositorio, "Repositório de movimentação é obrigatório");
        this.movimentacaoRepositorio = movimentacaoRepositorio;
    }
    
    /**
     * Registra uma movimentação de entrada (R1H20)
     * 
     * @param clienteId ID do cliente
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param quantidade Quantidade movimentada
     * @param responsavel Responsável pela movimentação
     * @param motivo Motivo da movimentação
     * @param metadados Metadados adicionais
     * @return Movimentacao registrada
     */
    public Movimentacao registrarEntrada(ClienteId clienteId, EstoqueId estoqueId, 
                                        ProdutoId produtoId, int quantidade, 
                                        String responsavel, String motivo, 
                                        Map<String, String> metadados) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");
        notBlank(motivo, "Motivo é obrigatório");
        
        Movimentacao movimentacao = new Movimentacao(clienteId, estoqueId, produtoId,
                                                   TipoMovimentacao.ENTRADA, quantidade,
                                                   responsavel, motivo, metadados);
        movimentacaoRepositorio.salvar(movimentacao);
        
        return movimentacao;
    }
    
    /**
     * Registra uma movimentação de saída (R2H20)
     * 
     * @param clienteId ID do cliente
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param quantidade Quantidade movimentada
     * @param responsavel Responsável pela movimentação
     * @param motivo Motivo da movimentação (venda, consumo interno, perda)
     * @param metadados Metadados adicionais
     * @return Movimentacao registrada
     */
    public Movimentacao registrarSaida(ClienteId clienteId, EstoqueId estoqueId, 
                                      ProdutoId produtoId, int quantidade, 
                                      String responsavel, String motivo, 
                                      Map<String, String> metadados) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");
        notBlank(motivo, "Motivo é obrigatório");
        
        Movimentacao movimentacao = new Movimentacao(clienteId, estoqueId, produtoId,
                                                   TipoMovimentacao.SAIDA, quantidade,
                                                   responsavel, motivo, metadados);
        movimentacaoRepositorio.salvar(movimentacao);
        
        return movimentacao;
    }
    
    /**
     * Registra um ajuste de inventário
     * 
     * @param clienteId ID do cliente
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param quantidade Quantidade do ajuste (positiva ou negativa)
     * @param responsavel Responsável pelo ajuste
     * @param motivo Motivo do ajuste
     * @param metadados Metadados adicionais
     * @return Movimentacao registrada
     */
    public Movimentacao registrarAjuste(ClienteId clienteId, EstoqueId estoqueId, 
                                       ProdutoId produtoId, int quantidade, 
                                       String responsavel, String motivo, 
                                       Map<String, String> metadados) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade != 0, "Quantidade não pode ser zero");
        notBlank(responsavel, "Responsável é obrigatório");
        notBlank(motivo, "Motivo é obrigatório");
        
        Movimentacao movimentacao = new Movimentacao(clienteId, estoqueId, produtoId,
                                                   TipoMovimentacao.AJUSTE, quantidade,
                                                   responsavel, motivo, metadados);
        movimentacaoRepositorio.salvar(movimentacao);
        
        return movimentacao;
    }
    
    /**
     * Lista todas as movimentações de um cliente
     * 
     * @param clienteId ID do cliente
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarMovimentacoesPorCliente(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        return movimentacaoRepositorio.buscarMovimentacoesPorCliente(clienteId);
    }
    
    /**
     * Lista todas as movimentações de um estoque
     * 
     * @param estoqueId ID do estoque
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarMovimentacoesPorEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        return movimentacaoRepositorio.buscarMovimentacoesPorEstoque(estoqueId);
    }
    
    /**
     * Lista todas as movimentações de um produto (R1H21)
     * 
     * @param produtoId ID do produto
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarMovimentacoesPorProduto(ProdutoId produtoId) {
        notNull(produtoId, "Produto é obrigatório");
        return movimentacaoRepositorio.buscarMovimentacoesPorProduto(produtoId);
    }
    
    /**
     * Lista movimentações por período
     * 
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarMovimentacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        notNull(dataInicio, "Data de início é obrigatória");
        notNull(dataFim, "Data de fim é obrigatória");
        isTrue(!dataInicio.isAfter(dataFim), "Data de início não pode ser posterior à data de fim");
        
        return movimentacaoRepositorio.buscarMovimentacoesPorPeriodo(dataInicio, dataFim);
    }
    
    /**
     * Lista movimentações de um produto por período
     * 
     * @param produtoId ID do produto
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Lista de movimentações
     */
    public List<Movimentacao> listarMovimentacoesPorProdutoEPeriodo(ProdutoId produtoId, 
                                                                   LocalDateTime dataInicio, 
                                                                   LocalDateTime dataFim) {
        notNull(produtoId, "Produto é obrigatório");
        notNull(dataInicio, "Data de início é obrigatória");
        notNull(dataFim, "Data de fim é obrigatória");
        isTrue(!dataInicio.isAfter(dataFim), "Data de início não pode ser posterior à data de fim");
        
        return movimentacaoRepositorio.buscarMovimentacoesPorProdutoEPeriodo(produtoId, dataInicio, dataFim);
    }
    
    /**
     * Calcula o total de movimentações de entrada de um produto em um período
     * 
     * @param produtoId ID do produto
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Total de entradas
     */
    public int calcularTotalEntradas(ProdutoId produtoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Movimentacao> movimentacoes = listarMovimentacoesPorProdutoEPeriodo(produtoId, dataInicio, dataFim);
        
        return movimentacoes.stream()
                .filter(Movimentacao::isEntrada)
                .mapToInt(Movimentacao::getQuantidade)
                .sum();
    }
    
    /**
     * Calcula o total de movimentações de saída de um produto em um período
     * 
     * @param produtoId ID do produto
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Total de saídas
     */
    public int calcularTotalSaidas(ProdutoId produtoId, LocalDateTime dataInicio, LocalDateTime dataFim) {
        List<Movimentacao> movimentacoes = listarMovimentacoesPorProdutoEPeriodo(produtoId, dataInicio, dataFim);
        
        return movimentacoes.stream()
                .filter(Movimentacao::isSaida)
                .mapToInt(Movimentacao::getQuantidade)
                .sum();
    }
}
