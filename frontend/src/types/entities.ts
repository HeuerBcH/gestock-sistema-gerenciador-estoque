// Enums do backend
export type Status = "ATIVO" | "INATIVO";
export type Perecivel = "SIM" | "NAO";
export type TipoMovimentacao = "ENTRADA" | "SAIDA";
export type StatusPedido =
  | "CRIADO"
  | "ENVIADO"
  | "EM_TRANSPORTE"
  | "RECEBIDO"
  | "CANCELADO";
export type StatusCotacao = "APROVADA" | "PENDENTE";
export type ValidadeCotacao = "ATIVA" | "EXPIRADA";
export type StatusROP = "ADEQUADO" | "INADEQUADO";
export type NivelAlerta = "CRITICO" | "ALTO" | "MEDIO";
export type StatusReserva = "ATIVA" | "LIBERADA";
export type TipoLiberacao = "RECEBIDO" | "CANCELADO";

// Auth
export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  documento: string;
  senha: string;
}

export interface AuthResponse {
  token: string;
  clienteId: number;
}

export interface ClientePerfil {
  id: number;
  nome: string;
  email: string;
}

// Fornecedor
export interface Fornecedor {
  id: number;
  nome: string;
  cnpj: string;
  contato: string;
  leadTime: number;
  custo: number;
  status: Status;
}

export interface FornecedorSaveRequest {
  id?: number;
  nome: string;
  cnpj: string;
  contato: string;
  leadTime: number;
  custo: number;
  status: Status;
}

// Produto
export interface Produto {
  id: number;
  codigo: string;
  nome: string;
  peso: number;
  perecivel: Perecivel;
  status: Status;
  fornecedores: string[]; // Nomes dos fornecedores
}

export interface ProdutoSaveRequest {
  id?: number;
  codigo: string;
  nome: string;
  peso: number;
  perecivel: Perecivel;
  status: Status;
  fornecedores: number[]; // IDs dos fornecedores
}

// Estoque
export interface Estoque {
  id: number;
  nome: string;
  endereco: string;
  capacidade: number;
  quantidadeAtual: number;
  capacidadeDisponivel: number;
  ocupacao: number;
  status: Status;
}

export interface EstoqueSaveRequest {
  id?: number;
  nome: string;
  endereco: string;
  capacidade: number;
  status: Status;
}

// Cotação
export interface Cotacao {
  id: number;
  fornecedorId: number;
  fornecedorNome: string;
  preco: number;
  leadTime: number;
  validade: ValidadeCotacao;
  statusAprovacao: StatusCotacao;
  maisVantajosa: boolean;
}

export interface CotacaoPorProduto {
  produtoId: number;
  produtoNome: string;
  cotacoes: Cotacao[];
}

export interface CotacaoSaveRequest {
  id?: number;
  produtoId: number;
  fornecedorId: number;
  preco: number;
  leadTime: number;
  validade: ValidadeCotacao;
  statusAprovacao: StatusCotacao;
}

// Movimentação
export interface Movimentacao {
  id: number;
  dataHora: string;
  produtoId: number;
  produtoNome: string;
  estoqueId: number;
  estoqueNome: string;
  quantidade: number;
  tipo: TipoMovimentacao;
  motivo: string;
  responsavel: string;
}

export interface MovimentacaoTotais {
  totalMovimentacoes: number;
  totalEntradas: number;
  totalSaidas: number;
}

export interface MovimentacaoRegistrarRequest {
  produtoId: number;
  estoqueId: number;
  quantidade: number;
  tipo: TipoMovimentacao;
  motivo: string;
  responsavel: string;
  dataHora?: string;
}

// Ponto de Ressuprimento
export interface PontoRessuprimento {
  id: number;
  estoqueId: number;
  estoqueNome: string;
  produtoId: number;
  produtoNome: string;
  consumoMedioDiario: number;
  consumoMaximoDiario: number;
  leadTimeMedio: number;
  leadTimeMaximo: number;
  estoqueSeguranca: number;
  ropCalculado: number;
  saldoAtual: number;
  status: StatusROP;
}

export interface PontoRessuprimentoTotais {
  totalMonitorado: number;
  estoqueAdequado: number;
  abaixoDoRop: number;
}

export interface PontoRessuprimentoRegistrarRequest {
  estoqueId: number;
  produtoId: number;
  estoqueSeguranca: number;
}

// Alerta
export interface Alerta {
  nivel: NivelAlerta;
  produtoId: number;
  produtoNome: string;
  estoqueId: number;
  estoqueNome: string;
  quantidadeAtual: number;
  rop: number;
  percentualAbaixoRop: number;
  data: string;
}

export interface AlertaTotais {
  totalCriticos: number;
  totalAltos: number;
  totalMedios: number;
}

// Pedido
export interface PedidoItem {
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  precoUnitario: number;
}

export interface Pedido {
  id: number;
  fornecedorId: number;
  fornecedorNome: string;
  estoqueId: number;
  estoqueNome: string;
  itens: PedidoItem[];
  valorTotal: number;
  dataPedido: string;
  dataPrevista: string;
  status: StatusPedido;
}

export interface PedidoItemRequest {
  produtoId: number;
  quantidade: number;
}

export interface PedidoCriarRequest {
  fornecedorId: number;
  itens: PedidoItemRequest[];
  dataPedido?: string;
}

export interface PedidoCriarAutomaticoRequest {
  estoqueId: number;
  itens: PedidoItemRequest[];
}

// Reserva
export interface Reserva {
  id: number;
  pedidoId: number;
  pedidoCodigo: string;
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  dataHoraReserva: string;
  status: StatusReserva;
  tipoLiberacao: TipoLiberacao | null;
  dataHoraLiberacao: string | null;
}

export interface ReservaTotais {
  totalReservas: number;
  reservasAtivas: number;
  reservasLiberadas: number;
  quantidadeReservadaAtiva: number;
}

// Transferência
export interface Transferencia {
  id: number;
  dataHoraTransferencia: string;
  produtoId: number;
  produtoNome: string;
  quantidade: number;
  estoqueOrigemId: number;
  estoqueOrigemNome: string;
  estoqueDestinoId: number;
  estoqueDestinoNome: string;
  responsavel: string;
  motivo: string;
}

export interface TransferenciaTotais {
  totalTransferencias: number;
  unidadesMovidas: number;
  produtosDistintos: number;
}
