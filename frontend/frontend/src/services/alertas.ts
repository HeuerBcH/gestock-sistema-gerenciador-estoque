import { api } from './api';
import type { Alerta, AlertaTotais, NivelAlerta } from '@/types/entities';

export interface AlertaPesquisaParams {
  nivel?: NivelAlerta;
}

export const alertasService = {
  pesquisar: (params?: AlertaPesquisaParams) =>
    api.get<Alerta[]>('/alerta/pesquisa', params),

  obterTotais: () =>
    api.get<AlertaTotais>('/alerta/totais'),
};

export default alertasService;

