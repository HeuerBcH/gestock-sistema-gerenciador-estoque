import { api } from './api';
import type { Cotacao, CotacaoPorProduto, CotacaoSaveRequest } from '@/types/entities';

export const cotacoesService = {
  pesquisar: () =>
    api.get<CotacaoPorProduto[]>('/cotacao/pesquisa'),

  pesquisarPorProduto: (produtoId: number) =>
    api.get<Cotacao[]>(`/cotacao/produto/${produtoId}`),

  obter: (id: number) =>
    api.get<Cotacao>(`/cotacao/${id}`),

  salvar: (data: CotacaoSaveRequest) =>
    api.post<Cotacao>('/cotacao/salvar', data),

  remover: (id: number) =>
    api.delete<void>(`/cotacao/${id}`),

  aprovar: (id: number) =>
    api.post<void>(`/cotacao/${id}/aprovar`),

  desaprovar: (id: number) =>
    api.post<void>(`/cotacao/${id}/desaprovar`),

  sincronizar: () =>
    api.post<{ cotacoesCriadas: number; mensagem: string }>('/cotacao/sincronizar'),
};

export default cotacoesService;

