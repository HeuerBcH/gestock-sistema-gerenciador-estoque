import { api } from './api';
import type { Reserva, ReservaTotais } from '@/types/entities';

export interface ReservaPesquisaParams {
  busca?: string;
}

export const reservasService = {
  pesquisar: (params?: ReservaPesquisaParams) =>
    api.get<Reserva[]>('/reserva/pesquisa', params),

  obterTotais: () =>
    api.get<ReservaTotais>('/reserva/totais'),
};

export default reservasService;

