// Extended mock data for new features
import { suppliers, products, stocks } from "./mockData";

// Cotações
export interface Quotation {
  id: string;
  productId: string;
  supplierId: string;
  price: number;
  leadTime: number;
  validity: "ativa" | "expirada";
  approved: boolean;
  createdAt: string;
}

export const quotations: Quotation[] = [
  { id: "1", productId: "1", supplierId: "1", price: 25.50, leadTime: 5, validity: "ativa", approved: false, createdAt: "2024-01-10" },
  { id: "2", productId: "1", supplierId: "2", price: 22.00, leadTime: 7, validity: "ativa", approved: true, createdAt: "2024-01-10" },
  { id: "3", productId: "2", supplierId: "1", price: 18.00, leadTime: 5, validity: "ativa", approved: false, createdAt: "2024-01-11" },
  { id: "4", productId: "2", supplierId: "4", price: 19.50, leadTime: 3, validity: "expirada", approved: false, createdAt: "2024-01-05" },
  { id: "5", productId: "3", supplierId: "2", price: 4.50, leadTime: 7, validity: "ativa", approved: false, createdAt: "2024-01-12" },
  { id: "6", productId: "3", supplierId: "4", price: 4.20, leadTime: 3, validity: "ativa", approved: true, createdAt: "2024-01-12" },
  { id: "7", productId: "4", supplierId: "3", price: 35.00, leadTime: 15, validity: "ativa", approved: true, createdAt: "2024-01-08" },
  { id: "8", productId: "5", supplierId: "1", price: 8.00, leadTime: 5, validity: "ativa", approved: false, createdAt: "2024-01-13" },
  { id: "9", productId: "5", supplierId: "4", price: 7.50, leadTime: 3, validity: "ativa", approved: true, createdAt: "2024-01-13" },
];

// Ponto de Ressuprimento
export interface ReorderPoint {
  id: string;
  stockId: string;
  productId: string;
  averageDailyConsumption: number;
  leadTime: number;
  safetyStock: number;
  ropCalculated: number;
  currentBalance: number;
  status: "adequado" | "inadequado";
}

export const reorderPoints: ReorderPoint[] = [
  { id: "1", stockId: "1", productId: "1", averageDailyConsumption: 20, leadTime: 5, safetyStock: 50, ropCalculated: 150, currentBalance: 200, status: "adequado" },
  { id: "2", stockId: "1", productId: "2", averageDailyConsumption: 15, leadTime: 5, safetyStock: 40, ropCalculated: 115, currentBalance: 150, status: "adequado" },
  { id: "3", stockId: "1", productId: "3", averageDailyConsumption: 30, leadTime: 7, safetyStock: 60, ropCalculated: 270, currentBalance: 80, status: "inadequado" },
  { id: "4", stockId: "2", productId: "1", averageDailyConsumption: 10, leadTime: 5, safetyStock: 30, ropCalculated: 80, currentBalance: 100, status: "adequado" },
  { id: "5", stockId: "2", productId: "4", averageDailyConsumption: 5, leadTime: 15, safetyStock: 25, ropCalculated: 100, currentBalance: 50, status: "inadequado" },
  { id: "6", stockId: "3", productId: "5", averageDailyConsumption: 25, leadTime: 3, safetyStock: 30, ropCalculated: 105, currentBalance: 300, status: "adequado" },
  { id: "7", stockId: "1", productId: "6", averageDailyConsumption: 8, leadTime: 2, safetyStock: 20, ropCalculated: 36, currentBalance: 20, status: "inadequado" },
];

// Transferências
export interface Transfer {
  id: string;
  dateTime: string;
  productId: string;
  quantity: number;
  originStockId: string;
  destinationStockId: string;
  responsible: string;
  reason: string;
}

export const transfers: Transfer[] = [
  { id: "1", dateTime: "2024-01-15T09:00:00", productId: "1", quantity: 50, originStockId: "1", destinationStockId: "2", responsible: "João Silva", reason: "Reposição de estoque" },
  { id: "2", dateTime: "2024-01-15T11:30:00", productId: "3", quantity: 100, originStockId: "1", destinationStockId: "3", responsible: "Maria Santos", reason: "Demanda regional" },
  { id: "3", dateTime: "2024-01-16T08:45:00", productId: "2", quantity: 30, originStockId: "2", destinationStockId: "1", responsible: "Pedro Costa", reason: "Consolidação" },
  { id: "4", dateTime: "2024-01-16T14:20:00", productId: "5", quantity: 200, originStockId: "3", destinationStockId: "1", responsible: "Ana Oliveira", reason: "Redistribuição" },
  { id: "5", dateTime: "2024-01-17T10:00:00", productId: "4", quantity: 25, originStockId: "1", destinationStockId: "2", responsible: "Carlos Mendes", reason: "Pedido urgente" },
];

// Reservas
export interface Reservation {
  id: string;
  productId: string;
  quantity: number;
  dateTime: string;
  orderId: string;
  status: "ativa" | "liberada";
  liberationType?: "recebido" | "cancelado";
  liberatedAt?: string;
}

export const reservations: Reservation[] = [
  { id: "1", productId: "1", quantity: 100, dateTime: "2024-01-10T10:00:00", orderId: "PED001", status: "liberada", liberationType: "recebido", liberatedAt: "2024-01-15T14:00:00" },
  { id: "2", productId: "2", quantity: 50, dateTime: "2024-01-10T10:00:00", orderId: "PED001", status: "liberada", liberationType: "recebido", liberatedAt: "2024-01-15T14:00:00" },
  { id: "3", productId: "3", quantity: 200, dateTime: "2024-01-12T09:30:00", orderId: "PED002", status: "ativa" },
  { id: "4", productId: "4", quantity: 30, dateTime: "2024-01-14T11:00:00", orderId: "PED003", status: "ativa" },
  { id: "5", productId: "5", quantity: 100, dateTime: "2024-01-14T11:00:00", orderId: "PED003", status: "ativa" },
  { id: "6", productId: "1", quantity: 500, dateTime: "2024-01-15T08:00:00", orderId: "PED004", status: "ativa" },
];
