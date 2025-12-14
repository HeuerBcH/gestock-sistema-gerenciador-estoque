// Fornecedores
export interface Supplier {
  id: string;
  name: string;
  cnpj: string;
  contact: string;
  leadTime: number;
  cost: number;
  status: "ativo" | "inativo";
}

export const suppliers: Supplier[] = [
  { id: "1", name: "Distribuidora ABC", cnpj: "12.345.678/0001-90", contact: "contato@abc.com", leadTime: 5, cost: 25.50, status: "ativo" },
  { id: "2", name: "Fornecedor XYZ Ltda", cnpj: "98.765.432/0001-10", contact: "vendas@xyz.com", leadTime: 7, cost: 22.00, status: "ativo" },
  { id: "3", name: "Importadora Global", cnpj: "11.222.333/0001-44", contact: "global@import.com", leadTime: 15, cost: 18.75, status: "inativo" },
  { id: "4", name: "Atacado Premium", cnpj: "55.666.777/0001-88", contact: "premium@atacado.com", leadTime: 3, cost: 28.00, status: "ativo" },
  { id: "5", name: "Logística Express", cnpj: "99.888.777/0001-22", contact: "express@logistica.com", leadTime: 2, cost: 32.00, status: "ativo" },
];

// Produtos
export interface Product {
  id: string;
  code: string;
  name: string;
  weight: number;
  quantity: number;
  perishable: boolean;
  status: "ativo" | "inativo";
  supplierIds: string[];
}

export const products: Product[] = [
  { id: "1", code: "PRD001", name: "Arroz Integral 1kg", weight: 1000, quantity: 500, perishable: false, status: "ativo", supplierIds: ["1", "2"] },
  { id: "2", code: "PRD002", name: "Feijão Preto 1kg", weight: 1000, quantity: 350, perishable: false, status: "ativo", supplierIds: ["1"] },
  { id: "3", code: "PRD003", name: "Leite UHT 1L", weight: 1050, quantity: 200, perishable: true, status: "ativo", supplierIds: ["2", "4"] },
  { id: "4", code: "PRD004", name: "Azeite Extra Virgem 500ml", weight: 500, quantity: 150, perishable: false, status: "ativo", supplierIds: ["3"] },
  { id: "5", code: "PRD005", name: "Macarrão Espaguete 500g", weight: 500, quantity: 800, perishable: false, status: "ativo", supplierIds: ["1", "4"] },
  { id: "6", code: "PRD006", name: "Queijo Mussarela 500g", weight: 500, quantity: 80, perishable: true, status: "inativo", supplierIds: ["5"] },
];

// Estoques
export interface Stock {
  id: string;
  name: string;
  address: string;
  capacity: number;
  currentQuantity: number;
  status: "ativo" | "inativo";
}

export const stocks: Stock[] = [
  { id: "1", name: "Centro de Distribuição São Paulo", address: "Av. Industrial, 1000 - SP", capacity: 10000, currentQuantity: 6500, status: "ativo" },
  { id: "2", name: "Armazém Rio de Janeiro", address: "Rua do Porto, 500 - RJ", capacity: 5000, currentQuantity: 3200, status: "ativo" },
  { id: "3", name: "Depósito Minas Gerais", address: "Rod. BR-040, km 50 - MG", capacity: 8000, currentQuantity: 4100, status: "ativo" },
  { id: "4", name: "Estoque Reserva Sul", address: "Av. das Nações, 2000 - PR", capacity: 3000, currentQuantity: 0, status: "inativo" },
];

// Pedidos
export interface Order {
  id: string;
  productIds: string[];
  quantities: number[];
  totalValue: number;
  supplierId: string;
  orderDate: string;
  expectedDate: string;
  status: "criado" | "enviado" | "em_transporte" | "recebido" | "cancelado";
}

export const orders: Order[] = [
  { id: "PED001", productIds: ["1", "2"], quantities: [100, 50], totalValue: 3825.00, supplierId: "1", orderDate: "2024-01-10", expectedDate: "2024-01-15", status: "recebido" },
  { id: "PED002", productIds: ["3"], quantities: [200], totalValue: 4400.00, supplierId: "2", orderDate: "2024-01-12", expectedDate: "2024-01-19", status: "em_transporte" },
  { id: "PED003", productIds: ["4", "5"], quantities: [30, 100], totalValue: 3362.50, supplierId: "3", orderDate: "2024-01-14", expectedDate: "2024-01-29", status: "enviado" },
  { id: "PED004", productIds: ["1"], quantities: [500], totalValue: 12750.00, supplierId: "1", orderDate: "2024-01-15", expectedDate: "2024-01-20", status: "criado" },
];

// Movimentações
export interface Movement {
  id: string;
  dateTime: string;
  productId: string;
  quantity: number;
  type: "entrada" | "saida";
  reason: string;
  stockId: string;
  responsible: string;
}

export const movements: Movement[] = [
  { id: "1", dateTime: "2024-01-15T09:30:00", productId: "1", quantity: 100, type: "entrada", reason: "Recebimento de pedido", stockId: "1", responsible: "João Silva" },
  { id: "2", dateTime: "2024-01-15T11:00:00", productId: "3", quantity: 50, type: "saida", reason: "Venda", stockId: "1", responsible: "Maria Santos" },
  { id: "3", dateTime: "2024-01-15T14:30:00", productId: "2", quantity: 30, type: "saida", reason: "Transferência", stockId: "2", responsible: "Pedro Costa" },
  { id: "4", dateTime: "2024-01-16T08:00:00", productId: "5", quantity: 200, type: "entrada", reason: "Recebimento de pedido", stockId: "1", responsible: "Ana Oliveira" },
  { id: "5", dateTime: "2024-01-16T10:15:00", productId: "1", quantity: 25, type: "saida", reason: "Venda", stockId: "3", responsible: "Carlos Mendes" },
];

// Alertas
export interface Alert {
  id: string;
  productId: string;
  stockId: string;
  level: "critical" | "high" | "medium";
  currentQuantity: number;
  ropValue: number;
  percentageBelowROP: number;
  createdAt: string;
}

export const alerts: Alert[] = [
  { id: "1", productId: "6", stockId: "1", level: "critical", currentQuantity: 80, ropValue: 200, percentageBelowROP: 60, createdAt: "2024-01-16T08:00:00" },
  { id: "2", productId: "3", stockId: "2", level: "high", currentQuantity: 200, ropValue: 350, percentageBelowROP: 43, createdAt: "2024-01-16T09:30:00" },
  { id: "3", productId: "4", stockId: "1", level: "medium", currentQuantity: 150, ropValue: 200, percentageBelowROP: 25, createdAt: "2024-01-16T10:00:00" },
];
