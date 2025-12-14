import { api } from './api';
import type { Produto, ProdutoSaveRequest, Status } from '@/types/entities';

export interface ProdutoPesquisaParams {
  termo?: string;
  status?: Status;
}

export const produtosService = {
  pesquisar: (params?: ProdutoPesquisaParams) =>
    api.get<Produto[]>('/produto/pesquisa', params),

  obter: (id: number) =>
    api.get<Produto>(`/produto/${id}`),

  salvar: (data: ProdutoSaveRequest) =>
    api.post<Produto>('/produto/salvar', data),

  remover: (id: number) =>
    api.delete<void>(`/produto/${id}`),

  ativar: (id: number) =>
    api.post<void>(`/produto/${id}/ativar`),

  inativar: (id: number) =>
    api.post<void>(`/produto/${id}/inativar`),
};

export default produtosService;

