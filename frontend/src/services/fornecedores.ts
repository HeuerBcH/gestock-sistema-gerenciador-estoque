import { api } from './api';
import type { Fornecedor, FornecedorSaveRequest, Status } from '@/types/entities';

export interface FornecedorPesquisaParams {
  termo?: string;
  status?: Status;
}

export const fornecedoresService = {
  pesquisar: (params?: FornecedorPesquisaParams) =>
    api.get<Fornecedor[]>('/fornecedor/pesquisa', params),

  obter: (id: number) =>
    api.get<Fornecedor>(`/fornecedor/${id}`),

  salvar: (data: FornecedorSaveRequest) =>
    api.post<Fornecedor>('/fornecedor/salvar', data),

  remover: (id: number) =>
    api.delete<void>(`/fornecedor/${id}`),

  ativar: (id: number) =>
    api.post<void>(`/fornecedor/${id}/ativar`),

  inativar: (id: number) =>
    api.post<void>(`/fornecedor/${id}/inativar`),
};

export default fornecedoresService;

