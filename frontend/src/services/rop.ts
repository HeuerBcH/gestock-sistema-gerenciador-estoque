import { api } from './api';
import type { PontoRessuprimento, PontoRessuprimentoRegistrarRequest, PontoRessuprimentoTotais, StatusROP } from '@/types/entities';

export interface ROPPesquisaParams {
  termo?: string;
  status?: StatusROP;
}

export const ropService = {
  pesquisar: (params?: ROPPesquisaParams) =>
    api.get<PontoRessuprimento[]>('/ponto-ressuprimento/pesquisa', params),

  obterTotais: () =>
    api.get<PontoRessuprimentoTotais>('/ponto-ressuprimento/totais'),

  obter: (id: number) =>
    api.get<PontoRessuprimento>(`/ponto-ressuprimento/${id}`),

  registrar: (data: PontoRessuprimentoRegistrarRequest) =>
    api.post<PontoRessuprimento>('/ponto-ressuprimento/registrar', data),

  atualizarEstoqueSeguranca: (id: number, estoqueSeguranca: number) =>
    api.put<PontoRessuprimento>(`/ponto-ressuprimento/${id}/estoque-seguranca`, estoqueSeguranca),

  remover: (id: number) =>
    api.delete<void>(`/ponto-ressuprimento/${id}`),

  sincronizar: () =>
    api.post<{ registrosCriados: number; mensagem: string }>('/ponto-ressuprimento/sincronizar'),
};

export default ropService;

