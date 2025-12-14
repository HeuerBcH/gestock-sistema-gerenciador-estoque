import { api } from './api';
import type { Pedido, PedidoCriarRequest, PedidoCriarAutomaticoRequest, StatusPedido } from '@/types/entities';

export const pedidosService = {
  pesquisar: () =>
    api.get<Pedido[]>('/pedido/pesquisa'),

  obter: (id: number) =>
    api.get<Pedido>(`/pedido/${id}`),

  criar: (data: PedidoCriarRequest) =>
    api.post<Pedido>('/pedido/criar', data),

  criarAutomatico: (data: PedidoCriarAutomaticoRequest) =>
    api.post<Pedido[]>('/pedido/criar-automatico', data),

  alterarStatus: (id: number, status: StatusPedido) =>
    api.put<void>(`/pedido/${id}/status`, status),

  confirmarRecebimento: (id: number) =>
    api.post<void>(`/pedido/${id}/confirmar-recebimento`),

  cancelar: (id: number) =>
    api.post<void>(`/pedido/${id}/cancelar`),
};

export default pedidosService;

