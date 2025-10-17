package dev.gestock.sge.dominio.principal.alerta;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.estoque.EstoqueId;
import dev.gestock.sge.dominio.principal.fornecedor.FornecedorId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio para operações que envolvem alertas
 * 
 * Responsabilidades:
 * - Gerar alertas automaticamente quando estoque atinge ROP
 * - Resolver alertas após recebimento de pedidos
 * - Buscar alertas ativos para visualização
 */
public class AlertaServico {
    
    private final AlertaRepositorio alertaRepositorio;
    
    public AlertaServico(AlertaRepositorio alertaRepositorio) {
        notNull(alertaRepositorio, "Repositório de alerta é obrigatório");
        this.alertaRepositorio = alertaRepositorio;
    }
    
    /**
     * Gera alerta de estoque baixo (R1H16, R2H16)
     * 
     * @param clienteId ID do cliente
     * @param estoqueId ID do estoque
     * @param produtoId ID do produto
     * @param fornecedorSugeridoId ID do fornecedor com menor cotação
     * @param saldoAtual Saldo atual do produto
     * @param rop Ponto de ressuprimento
     * @return Alerta gerado
     */
    public Alerta gerarAlertaEstoqueBaixo(ClienteId clienteId, EstoqueId estoqueId, 
                                         ProdutoId produtoId, FornecedorId fornecedorSugeridoId,
                                         int saldoAtual, int rop) {
        notNull(clienteId, "Cliente é obrigatório");
        notNull(estoqueId, "Estoque é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        notNull(fornecedorSugeridoId, "Fornecedor sugerido é obrigatório");
        isTrue(saldoAtual >= 0, "Saldo atual não pode ser negativo");
        isTrue(rop > 0, "ROP deve ser positivo");
        
        // Verifica se já existe alerta ativo para este produto
        List<Alerta> alertasExistentes = alertaRepositorio.buscarAlertasAtivosPorProduto(produtoId);
        if (!alertasExistentes.isEmpty()) {
            throw new IllegalStateException("Já existe alerta ativo para este produto");
        }
        
        Alerta alerta = new Alerta(clienteId, estoqueId, produtoId, fornecedorSugeridoId, 
                                  saldoAtual, rop);
        alertaRepositorio.salvar(alerta);
        
        return alerta;
    }
    
    /**
     * Resolve alerta após recebimento de pedido (R1H17)
     * 
     * @param produtoId ID do produto
     */
    public void resolverAlertaPorProduto(ProdutoId produtoId) {
        notNull(produtoId, "Produto é obrigatório");
        
        List<Alerta> alertasAtivos = alertaRepositorio.buscarAlertasAtivosPorProduto(produtoId);
        for (Alerta alerta : alertasAtivos) {
            alerta.resolver();
            alertaRepositorio.salvar(alerta);
        }
    }
    
    /**
     * Lista todos os alertas ativos de um cliente (R1H17)
     * 
     * @param clienteId ID do cliente
     * @return Lista de alertas ativos
     */
    public List<Alerta> listarAlertasAtivos(ClienteId clienteId) {
        notNull(clienteId, "Cliente é obrigatório");
        return alertaRepositorio.buscarAlertasAtivosPorCliente(clienteId);
    }
    
    /**
     * Lista todos os alertas ativos de um estoque
     * 
     * @param estoqueId ID do estoque
     * @return Lista de alertas ativos
     */
    public List<Alerta> listarAlertasAtivosPorEstoque(EstoqueId estoqueId) {
        notNull(estoqueId, "Estoque é obrigatório");
        return alertaRepositorio.buscarAlertasAtivosPorEstoque(estoqueId);
    }
}
