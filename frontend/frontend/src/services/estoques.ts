import { api } from './api';
import type { Estoque, EstoqueSaveRequest, Produto, Status } from '@/types/entities';

export interface EstoquePesquisaParams {
  termo?: string;
  status?: Status;
}

export const estoquesService = {
  pesquisar: (params?: EstoquePesquisaParams) =>
    api.get<Estoque[]>('/estoque/pesquisa', params),

  obter: (id: number) =>
    api.get<Estoque>(`/estoque/${id}`),

  salvar: (data: EstoqueSaveRequest) =>
    api.post<Estoque>('/estoque/salvar', data),

  remover: (id: number) =>
    api.delete<void>(`/estoque/${id}`),

  ativar: (id: number) =>
    api.post<void>(`/estoque/${id}/ativar`),

  inativar: (id: number) =>
    api.post<void>(`/estoque/${id}/inativar`),

  obterProdutos: (estoqueId: number) =>
    api.get<Produto[]>(`/estoque/${estoqueId}/produtos`),
};

export default estoquesService;

