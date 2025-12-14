import { api } from './api';
import type { Transferencia, TransferenciaTotais } from '@/types/entities';

export interface TransferenciaPesquisaParams {
  busca?: string;
}

export interface TransferenciaRegistrarRequest {
  produtoId: number;
  estoqueOrigemId: number;
  estoqueDestinoId: number;
  quantidade: number;
  responsavel: string;
  motivo: string;
}

export interface TransferenciaRegistrarResponse {
  sucesso: boolean;
  mensagem: string;
  transferencia: Transferencia;
}

export const transferenciasService = {
  pesquisar: (params?: TransferenciaPesquisaParams) =>
    api.get<Transferencia[]>('/transferencia/pesquisa', params),

  obterTotais: () =>
    api.get<TransferenciaTotais>('/transferencia/totais'),

  registrar: (data: TransferenciaRegistrarRequest) =>
    api.post<TransferenciaRegistrarResponse>('/transferencia/registrar', data),
};

export default transferenciasService;

