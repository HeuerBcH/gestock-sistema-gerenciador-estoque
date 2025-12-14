import { api } from './api';
import type { Movimentacao, MovimentacaoRegistrarRequest, MovimentacaoTotais, TipoMovimentacao } from '@/types/entities';

export interface MovimentacaoPesquisaParams {
  tipo?: TipoMovimentacao;
  dataInicio?: string;
  dataFim?: string;
}

export const movimentacoesService = {
  pesquisar: (params?: MovimentacaoPesquisaParams) =>
    api.get<Movimentacao[]>('/movimentacao/pesquisa', params),

  obterTotais: (params?: { dataInicio?: string; dataFim?: string }) =>
    api.get<MovimentacaoTotais>('/movimentacao/totais', params),

  obter: (id: number) =>
    api.get<Movimentacao>(`/movimentacao/${id}`),

  registrar: (data: MovimentacaoRegistrarRequest) =>
    api.post<Movimentacao>('/movimentacao/registrar', data),

  remover: (id: number) =>
    api.delete<void>(`/movimentacao/${id}`),
};

export default movimentacoesService;

