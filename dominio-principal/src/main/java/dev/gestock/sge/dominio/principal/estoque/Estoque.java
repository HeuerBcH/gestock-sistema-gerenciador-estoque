package dev.gestock.sge.dominio.principal.estoque;

import static org.apache.commons.lang3.Validate.*;

import dev.gestock.sge.dominio.principal.cliente.ClienteId;
import dev.gestock.sge.dominio.principal.produto.ProdutoId;
import dev.gestock.sge.dominio.principal.produto.ROP;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Aggregate Root: Estoque
 *
 * Responsabilidades:
 * - Pertencer a um Cliente (clienteId).
 * - Manter saldos por Produto (fisico e reservado).
 * - Registrar Movimentacoes (Entrada, Saida, Ajuste) com auditoria.
 * - Expor operações de domínio: entrada/saída/ajuste, reservar/liberar, consumirReserva.
 *
 * Regras atendidas:
 * - R10: Toda baixa = Movimentacao de Saida.
 * - R11: Saldo disponível nunca negativo (validado em Saida/Reserva).
 * - R12/R13: Ajuste exige trilha (responsável, data, motivo).
 * - R15: SaldoDisponivel = fisico - reservado.
 */
public class Estoque {

    private final EstoqueId id;                    // Identidade imutável do estoque
    private final ClienteId clienteId;             // FK lógica: o dono deste estoque
    private String nome;                           // Nome de exibição do estoque
    private String endereco;                       // Endereço físico do estoque (R2H1)
    private int capacidadeMaxima;                  // Capacidade máxima em unidades (R1H3)
    private boolean ativo;                         // Status ativo/inativo (H2)

    // Saldos por produto (mantidos dentro do agregado Estoque)
    private final Map<ProdutoId, SaldoProduto> saldos = new HashMap<>();

    // Ponto de Ressuprimento por produto neste estoque
    private final Map<ProdutoId, ROP> rops = new HashMap<>();

    // Log de movimentações (auditoria de domínio)
    private final List<Movimentacao> movimentacoes = new ArrayList<>();

    // Log de reservas/liberações (R2H25)
    private final List<ReservaRegistro> reservas = new ArrayList<>();

    // ------------------ Construtores ------------------

    public Estoque(EstoqueId id, ClienteId clienteId, String nome, String endereco, int capacidadeMaxima) {
        notNull(id, "ID do estoque é obrigatório");
        notNull(clienteId, "Cliente do estoque é obrigatório");
        notBlank(nome, "Nome do estoque é obrigatório");
        notBlank(endereco, "Endereço do estoque é obrigatório");
        isTrue(capacidadeMaxima > 0, "Capacidade deve ser positiva");
        this.id = id;
        this.clienteId = clienteId;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativo = true; // inicia ativo por padrão
    }

    public Estoque(EstoqueId id, ClienteId clienteId, String nome, String endereco, int capacidadeMaxima, boolean ativo) {
        notNull(id, "Id do estoque é obrigatório");
        notNull(clienteId, "Cliente do estoque é obrigatório");
        notBlank(nome, "Nome do estoque é obrigatório");
        notBlank(endereco, "Endereço do estoque é obrigatório");
        isTrue(capacidadeMaxima > 0, "Capacidade deve ser positiva");
        this.id = id;
        this.clienteId = clienteId;
        this.nome = nome;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ativo = ativo;
    }

    // Getters básicos

    public EstoqueId getId() {
        return id;
    }
    public ClienteId getClienteId() {
        return clienteId;
    }
    public String getNome() {
        return nome;
    }
    public String getEndereco() {
        return endereco;
    }
    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }
    public boolean isAtivo() {
        return ativo;
    }

    public void renomear(String novoNome) {
        notBlank(novoNome, "Nome do estoque é obrigatório");
        this.nome = novoNome;
    }

    /** Inativa o estoque (H2, R1H2, R2H2) */
    public void inativar() {
        if (temProdutosEmEstoque()) {
            throw new IllegalStateException("Estoque com produtos não pode ser inativado (R1H2)");
        }
        this.ativo = false;
    }

    /** Reativa um estoque inativado */
    public void ativar() {
        this.ativo = true;
    }

    /** Verifica se o estoque possui produtos */
    private boolean temProdutosEmEstoque() {
        return saldos.values().stream()
                .anyMatch(s -> s.fisico() > 0);
    }

    /** Altera a capacidade máxima (R1H3) */
    public void alterarCapacidade(int novaCapacidade) {
        isTrue(novaCapacidade > 0, "Capacidade deve ser positiva");
        int ocupacaoAtual = calcularOcupacaoTotal();
        if (novaCapacidade < ocupacaoAtual) {
            throw new IllegalArgumentException(
                    "Capacidade não pode ser reduzida abaixo da ocupação atual (R1H3): " +
                            novaCapacidade + " < " + ocupacaoAtual
            );
        }
        this.capacidadeMaxima = novaCapacidade;
    }

    /** Calcula a ocupação total do estoque */
    private int calcularOcupacaoTotal() {
        return saldos.values().stream()
                .mapToInt(SaldoProduto::fisico)
                .sum();
    }

    public Map<ProdutoId, SaldoProduto> getSaldosSnapshot() {
        // snapshot imutável (protege invariantes)
        return Map.copyOf(saldos);
    }

    public List<Movimentacao> getMovimentacoesSnapshot() {
        return List.copyOf(movimentacoes);
    }

    public List<ReservaRegistro> getReservasSnapshot() {
        return List.copyOf(reservas);
    }

    // Consultas de saldo

    public int getSaldoFisico(ProdutoId produtoId) {
        return saldos.getOrDefault(produtoId, SaldoProduto.zero()).fisico();
    }

    public int getSaldoReservado(ProdutoId produtoId) {
        return saldos.getOrDefault(produtoId, SaldoProduto.zero()).reservado();
    }

    public int getSaldoDisponivel(ProdutoId produtoId) {
        // R15: disponível = físico − reservado
        SaldoProduto sp = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        return sp.disponivel();
    }

    // ROP por produto neste estoque

    /** Define ou recalcula o ROP para um produto específico neste estoque. */
    public void definirROP(ProdutoId produtoId, double consumoMedio, int leadTimeDias, int estoqueSeguranca) {
        notNull(produtoId, "Produto é obrigatório");
        ROP novo = new ROP(consumoMedio, leadTimeDias, estoqueSeguranca);
        rops.put(produtoId, novo);
    }

    /** Obtém o ROP do produto neste estoque (ou null se não definido). */
    public ROP getROP(ProdutoId produtoId) {
        return rops.get(produtoId);
    }

    /** Verifica se o saldo físico atual do produto neste estoque atingiu ou ficou abaixo do ROP. */
    public boolean atingiuROP(ProdutoId produtoId) {
        ROP r = rops.get(produtoId);
        if (r == null) return false;
        return getSaldoFisico(produtoId) <= r.getValorROP();
    }

    /** Versão de verificação com saldo informado explicitamente (suporte a testes). */
    public boolean atingiuROP(ProdutoId produtoId, int saldoAtual) {
        ROP r = rops.get(produtoId);
        if (r == null) return false;
        return saldoAtual <= r.getValorROP();
    }

    // Operações de domínio

    /**
     * ENTRADA de mercadorias (R8).
     * - aumenta o saldo físico.
     * - registra movimentação de ENTRADA com auditoria.
     * - pode carregar metadata (ex.: lote/validade) via campos opcionais.
     */
    public void registrarEntrada(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional, Map<String, String> metaOpcional) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");

        SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        SaldoProduto novo  = atual.comEntrada(quantidade);
        saldos.put(produtoId, novo);

        // NOTA: ID será gerado pela camada de persistência
        Movimentacao mov = new Movimentacao(
                1L,
                TipoMovimentacao.ENTRADA,
                produtoId,
                quantidade,
                LocalDateTime.now(),
                responsavel,
                motivoOpcional,
                metaOpcional == null ? Map.of() : Map.copyOf(metaOpcional)
        );
        movimentacoes.add(mov);
    }

    /**
     * SAÍDA (R10, R11).
     * - valida saldo disponível (não pode negativar).
     * - diminui físico diretamente (saída imediata).
     * - registra movimentação de SAIDA.
     */
    public void registrarSaida(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");

        SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        // impede saldo disponível negativo (R11)
        isTrue(atual.disponivel() >= quantidade, "Saldo disponível insuficiente para saída");
        SaldoProduto novo = atual.comSaida(quantidade);
        saldos.put(produtoId, novo);

        // NOTA: ID será gerado pela camada de persistência
        Movimentacao mov = new Movimentacao(
                1L,
                TipoMovimentacao.SAIDA,
                produtoId,
                quantidade,
                LocalDateTime.now(),
                responsavel,
                motivoOpcional,
                Map.of()
        );
        movimentacoes.add(mov);
    }


    /**
     * Reserva preventiva (R15, R16): diminui o disponível sem mexer no físico.
     * - usado quando um Pedido em andamento deve “segurar” o item.
     */
    public void reservar(ProdutoId produtoId, int quantidade) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");

        SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        isTrue(atual.disponivel() >= quantidade, "Saldo disponível insuficiente para reserva");
        SaldoProduto novo = atual.comReserva(quantidade);
        saldos.put(produtoId, novo);
        reservas.add(ReservaRegistro.reserva(produtoId, quantidade));
    }

    /**
     * Liberação de reserva (R16): reverte a reserva (ex.: cancelamento de pedido).
     */
    public void liberarReserva(ProdutoId produtoId, int quantidade) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");

        SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        isTrue(atual.reservado() >= quantidade, "Quantidade a liberar excede o reservado");
        SaldoProduto novo = atual.comLiberacao(quantidade);
        saldos.put(produtoId, novo);
        reservas.add(ReservaRegistro.liberacao(produtoId, quantidade));
    }

    /**
     * Consumo de reserva: quando a saída é para atender a reserva:
     * - reduz o reservado.
     * - reduz o físico na mesma quantidade.
     * - mantém disponível coerente (não negativando).
     */
    public void consumirReservaComoSaida(ProdutoId produtoId, int quantidade, String responsavel, String motivoOpcional) {
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");

        SaldoProduto atual = saldos.getOrDefault(produtoId, SaldoProduto.zero());
        isTrue(atual.reservado() >= quantidade, "Reserva insuficiente para consumo");
        // reduzir reservado e fisico simultaneamente
        SaldoProduto apósReserva = atual.comLiberacao(quantidade);
        isTrue(apósReserva.disponivel() >= quantidade, "Saldo disponível insuficiente para saída");
        SaldoProduto novo = apósReserva.comSaida(quantidade);
        saldos.put(produtoId, novo);

        // NOTA: ID será gerado pela camada de persistência
        Movimentacao mov = new Movimentacao(
                1L,
                TipoMovimentacao.SAIDA,
                produtoId,
                quantidade,
                LocalDateTime.now(),
                responsavel,
                motivoOpcional,
                Map.of("consumoReserva", "true")
        );
        movimentacoes.add(mov);
    }

    /**
     * Alias para consumirReservaComoSaida (compatibilidade com testes).
     */
    public void consumirReserva(ProdutoId produtoId, int quantidade) {
        consumirReservaComoSaida(produtoId, quantidade, "Sistema", "Consumo de reserva");
    }

    /**
     * Transfere produtos para outro estoque (versão simplificada no agregado).
     * Nota: Para transferências entre clientes diferentes, use EstoqueServico.
     */
    public void transferir(ProdutoId produtoId, Estoque destino, int quantidade, String responsavel, String motivo) {
        notNull(destino, "Estoque de destino é obrigatório");
        notNull(produtoId, "Produto é obrigatório");
        isTrue(quantidade > 0, "Quantidade deve ser positiva");
        notBlank(responsavel, "Responsável é obrigatório");

        // Registra saída na origem
        this.registrarSaida(produtoId, quantidade, responsavel, motivo);

        // Registra entrada no destino
        destino.registrarEntrada(produtoId, quantidade, responsavel, "Transferência de estoque", Map.of(
                "transferencia", "true",
                "origem", this.id.toString()
        ));
    }
}
