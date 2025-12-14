import { useState, useEffect, useMemo } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  ShoppingCart,
  Check,
  X,
  Loader2,
  Plus,
  Trash2,
  Package,
  Sparkles,
  AlertTriangle,
  Warehouse,
} from "lucide-react";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "@/hooks/use-toast";
import { pedidosService } from "@/services/pedidos";
import { produtosService } from "@/services/produtos";
import { estoquesService } from "@/services/estoques";
import { cotacoesService } from "@/services/cotacoes";
import type { Pedido, StatusPedido, Produto, Estoque, CotacaoPorProduto } from "@/types/entities";

const statusConfig: Record<
  StatusPedido,
  { label: string; variant: "active" | "inactive" | "warning" | "secondary" | "high" }
> = {
  CRIADO: { label: "Criado", variant: "secondary" },
  ENVIADO: { label: "Enviado", variant: "high" },
  EM_TRANSPORTE: { label: "Em Transporte", variant: "warning" },
  RECEBIDO: { label: "Recebido", variant: "active" },
  CANCELADO: { label: "Cancelado", variant: "inactive" },
};

interface ItemPedido {
  produtoId: string;
  quantidade: string;
}

interface CotacaoInfo {
  fornecedorNome: string;
  preco: number;
  leadTime: number;
  aprovada: boolean;
}

export default function Pedidos() {
  const [data, setData] = useState<Pedido[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("all");

  // Modal state
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [estoques, setEstoques] = useState<Estoque[]>([]);
  const [cotacoesPorProduto, setCotacoesPorProduto] = useState<CotacaoPorProduto[]>([]);
  const [estoqueId, setEstoqueId] = useState<string>("");
  const [itens, setItens] = useState<ItemPedido[]>([{ produtoId: "", quantidade: "" }]);

  const carregarDados = async () => {
    try {
      setLoading(true);
      const pedidos = await pedidosService.pesquisar();
      setData(pedidos || []);
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao carregar dados",
        description: message,
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  const carregarDadosModal = async () => {
    try {
      const [produtosData, estoquesData, cotacoesData] = await Promise.all([
        produtosService.pesquisar({ status: "ATIVO" }),
        estoquesService.pesquisar({ status: "ATIVO" }),
        cotacoesService.pesquisar(),
      ]);
      setProdutos(produtosData || []);
      setEstoques(estoquesData || []);
      setCotacoesPorProduto(cotacoesData || []);
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao carregar dados",
        description: message,
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  // Mapear produto ID para melhor cotação aprovada
  const cotacaoMap = useMemo(() => {
    const map = new Map<number, CotacaoInfo>();
    
    for (const grupo of cotacoesPorProduto) {
      // Filtrar cotações aprovadas e ativas
      const aprovadas = grupo.cotacoes.filter(
        (c) => c.statusAprovacao === "APROVADA" && c.validade === "ATIVA"
      );
      
      if (aprovadas.length > 0) {
        // Ordenar por preço, leadTime, id
        const melhor = aprovadas.sort((a, b) => {
          if (a.preco !== b.preco) return a.preco - b.preco;
          if (a.leadTime !== b.leadTime) return a.leadTime - b.leadTime;
          return a.id - b.id;
        })[0];
        
        map.set(grupo.produtoId, {
          fornecedorNome: melhor.fornecedorNome,
          preco: melhor.preco,
          leadTime: melhor.leadTime,
          aprovada: true,
        });
      } else if (grupo.cotacoes.length > 0) {
        // Tem cotação mas nenhuma aprovada
        map.set(grupo.produtoId, {
          fornecedorNome: "",
          preco: 0,
          leadTime: 0,
          aprovada: false,
        });
      }
    }
    
    return map;
  }, [cotacoesPorProduto]);

  const handleOpenDialog = () => {
    setItens([{ produtoId: "", quantidade: "" }]);
    setEstoqueId("");
    carregarDadosModal();
    setIsDialogOpen(true);
  };

  const handleAddItem = () => {
    setItens([...itens, { produtoId: "", quantidade: "" }]);
  };

  const handleRemoveItem = (index: number) => {
    if (itens.length > 1) {
      setItens(itens.filter((_, i) => i !== index));
    }
  };

  const handleItemChange = (
    index: number,
    field: keyof ItemPedido,
    value: string
  ) => {
    const newItens = [...itens];
    newItens[index][field] = value;
    setItens(newItens);
  };

  const getProdutoNome = (produtoId: string) => {
    const produto = produtos.find((p) => String(p.id) === produtoId);
    return produto ? `${produto.codigo} - ${produto.nome}` : "";
  };

  const getCotacaoInfo = (produtoId: string): CotacaoInfo | null => {
    return cotacaoMap.get(Number(produtoId)) || null;
  };

  // Verificar se todos os itens têm cotação aprovada
  const itensValidos = useMemo(() => {
    return itens.filter(
      (item) => item.produtoId && item.quantidade && Number(item.quantidade) > 0
    );
  }, [itens]);

  const itensSemCotacaoAprovada = useMemo(() => {
    return itensValidos.filter((item) => {
      const info = getCotacaoInfo(item.produtoId);
      return !info || !info.aprovada;
    });
  }, [itensValidos, cotacaoMap]);

  const valorTotalEstimado = useMemo(() => {
    return itensValidos.reduce((total, item) => {
      const info = getCotacaoInfo(item.produtoId);
      if (info?.aprovada) {
        return total + (info.preco * Number(item.quantidade));
      }
      return total;
    }, 0);
  }, [itensValidos, cotacaoMap]);

  // Verificar capacidade do estoque
  const estoqueInfo = useMemo(() => {
    if (!estoqueId) return null;
    return estoques.find(e => String(e.id) === estoqueId);
  }, [estoqueId, estoques]);

  const quantidadeTotal = useMemo(() => {
    return itensValidos.reduce((total, item) => total + Number(item.quantidade), 0);
  }, [itensValidos]);

  const capacidadeExcedida = useMemo(() => {
    if (!estoqueInfo) return false;
    return quantidadeTotal > estoqueInfo.capacidadeDisponivel;
  }, [estoqueInfo, quantidadeTotal]);

  const handleSave = async () => {
    if (!estoqueId) {
      toast({
        title: "Selecione um estoque de destino",
        variant: "destructive",
      });
      return;
    }

    if (itensValidos.length === 0) {
      toast({
        title: "Adicione pelo menos um produto ao pedido",
        variant: "destructive",
      });
      return;
    }

    if (itensSemCotacaoAprovada.length > 0) {
      const nomes = itensSemCotacaoAprovada
        .map((item) => getProdutoNome(item.produtoId))
        .join(", ");
      toast({
        title: "Produtos sem cotação aprovada",
        description: `Aprove cotações para: ${nomes}`,
        variant: "destructive",
      });
      return;
    }

    if (capacidadeExcedida) {
      toast({
        title: "Capacidade do estoque excedida",
        description: `O estoque tem apenas ${estoqueInfo?.capacidadeDisponivel} unidades disponíveis, mas você está pedindo ${quantidadeTotal}.`,
        variant: "destructive",
      });
      return;
    }

    try {
      setSaving(true);
      const pedidosCriados = await pedidosService.criarAutomatico({
        estoqueId: Number(estoqueId),
        itens: itensValidos.map((item) => ({
          produtoId: Number(item.produtoId),
          quantidade: Number(item.quantidade),
        })),
      });

      if (pedidosCriados.length === 1) {
        toast({ 
          title: "Pedido criado com sucesso!",
          description: `Fornecedor: ${pedidosCriados[0].fornecedorNome} | Estoque: ${pedidosCriados[0].estoqueNome} | Valor: R$ ${pedidosCriados[0].valorTotal.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`,
        });
      } else {
        toast({ 
          title: `${pedidosCriados.length} pedidos criados!`,
          description: "Os produtos foram agrupados por melhor fornecedor.",
        });
      }
      
      setIsDialogOpen(false);
      carregarDados();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao criar pedido",
        description: message,
        variant: "destructive",
      });
    } finally {
      setSaving(false);
    }
  };

  const filteredData = data.filter((order) => {
    return filter === "all" || order.status === filter.toUpperCase();
  });

  const handleConfirmReceipt = async (id: number) => {
    try {
      await pedidosService.confirmarRecebimento(id);
      toast({ title: "Recebimento confirmado! Produtos adicionados ao estoque." });
      carregarDados();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao confirmar",
        description: message,
        variant: "destructive",
      });
    }
  };

  const handleCancel = async (id: number) => {
    try {
      await pedidosService.cancelar(id);
      toast({ title: "Pedido cancelado!" });
      carregarDados();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao cancelar",
        description: message,
        variant: "destructive",
      });
    }
  };

  const handleStatusChange = async (id: number, newStatus: StatusPedido) => {
    try {
      await pedidosService.alterarStatus(id, newStatus);
      toast({ title: "Status atualizado!" });
      carregarDados();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({
        title: "Erro ao atualizar status",
        description: message,
        variant: "destructive",
      });
    }
  };

  const getProductNames = (pedido: Pedido) => {
    return pedido.itens.map((item) => item.produtoNome).join(", ");
  };

  const getQuantities = (pedido: Pedido) => {
    return pedido.itens.map((item) => item.quantidade).join(", ");
  };

  const columns = [
    {
      key: "id",
      label: "ID",
      render: (item: Pedido) => `PED${String(item.id).padStart(3, "0")}`,
    },
    {
      key: "produtos",
      label: "Produtos",
      render: (item: Pedido) => (
        <span className="text-sm max-w-[200px] truncate block">
          {getProductNames(item)}
        </span>
      ),
    },
    {
      key: "quantidades",
      label: "Qtd",
      render: (item: Pedido) => getQuantities(item),
    },
    {
      key: "valorTotal",
      label: "Valor Total",
      render: (item: Pedido) =>
        `R$ ${item.valorTotal.toLocaleString("pt-BR", {
          minimumFractionDigits: 2,
        })}`,
    },
    {
      key: "fornecedorNome",
      label: "Fornecedor",
      render: (item: Pedido) => item.fornecedorNome,
    },
    {
      key: "estoqueNome",
      label: "Estoque",
      render: (item: Pedido) => item.estoqueNome || "-",
    },
    {
      key: "dataPedido",
      label: "Data Pedido",
      render: (item: Pedido) =>
        new Date(item.dataPedido).toLocaleDateString("pt-BR"),
    },
    {
      key: "dataPrevista",
      label: "Previsão",
      render: (item: Pedido) =>
        new Date(item.dataPrevista).toLocaleDateString("pt-BR"),
    },
    {
      key: "status",
      label: "Status",
      render: (item: Pedido) => {
        const config = statusConfig[item.status];
        return (
          <Select
            value={item.status}
            onValueChange={(value: StatusPedido) =>
              handleStatusChange(item.id, value)
            }
            disabled={item.status === "RECEBIDO" || item.status === "CANCELADO"}
          >
            <SelectTrigger className="w-[140px] h-8">
              <Badge variant={config.variant}>{config.label}</Badge>
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="CRIADO">Criado</SelectItem>
              <SelectItem value="ENVIADO">Enviado</SelectItem>
              <SelectItem value="EM_TRANSPORTE">Em Transporte</SelectItem>
            </SelectContent>
          </Select>
        );
      },
    },
    {
      key: "actions",
      label: "Ações",
      render: (item: Pedido) => (
        <div className="flex items-center gap-1">
          {item.status !== "RECEBIDO" && item.status !== "CANCELADO" && (
            <>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => handleConfirmReceipt(item.id)}
                title="Confirmar recebimento (adiciona produtos ao estoque)"
              >
                <Check className="w-4 h-4 text-status-active" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={() => handleCancel(item.id)}
                title="Cancelar pedido"
              >
                <X className="w-4 h-4 text-destructive" />
              </Button>
            </>
          )}
        </div>
      ),
    },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      <PageHeader
        title="Pedidos"
        description="Gerencie os pedidos de compra"
        icon={ShoppingCart}
        action={{
          label: "Novo Pedido",
          onClick: handleOpenDialog,
        }}
      />

      <DataTable
        data={filteredData}
        columns={columns}
        filterOptions={[
          { value: "all", label: "Todos" },
          { value: "CRIADO", label: "Criados" },
          { value: "ENVIADO", label: "Enviados" },
          { value: "EM_TRANSPORTE", label: "Em Transporte" },
          { value: "RECEBIDO", label: "Recebidos" },
          { value: "CANCELADO", label: "Cancelados" },
        ]}
        filterValue={filter}
        onFilterChange={setFilter}
        filterLabel="Status"
      />

      {/* Modal de Novo Pedido */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[700px] max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Novo Pedido</DialogTitle>
            <DialogDescription>
              Selecione o estoque de destino e os produtos. O sistema escolherá automaticamente
              o melhor fornecedor baseado nas cotações aprovadas.
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-6 py-4">
            {/* Info box */}
            <div className="flex items-start gap-3 p-3 rounded-lg bg-primary/10 border border-primary/20">
              <Sparkles className="w-5 h-5 text-primary mt-0.5" />
              <div className="text-sm">
                <p className="font-medium text-primary">Seleção automática de fornecedor</p>
                <p className="text-muted-foreground mt-1">
                  O sistema irá escolher o fornecedor com a melhor cotação <strong>aprovada</strong> para cada produto.
                  Ao confirmar o recebimento, os produtos serão automaticamente adicionados ao estoque selecionado.
                </p>
              </div>
            </div>

            {/* Seleção de Estoque */}
            <div className="grid gap-2">
              <Label className="flex items-center gap-2">
                <Warehouse className="w-4 h-4" />
                Estoque de Destino *
              </Label>
              <Select value={estoqueId} onValueChange={setEstoqueId}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o estoque que receberá os produtos" />
                </SelectTrigger>
                <SelectContent>
                  {estoques.map((estoque) => (
                    <SelectItem key={estoque.id} value={String(estoque.id)}>
                      <div className="flex items-center justify-between w-full gap-4">
                        <span>{estoque.nome}</span>
                        <span className="text-xs text-muted-foreground">
                          Disp: {estoque.capacidadeDisponivel}/{estoque.capacidade} un
                        </span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {estoqueInfo && (
                <p className="text-xs text-muted-foreground">
                  Capacidade disponível: <strong>{estoqueInfo.capacidadeDisponivel}</strong> de {estoqueInfo.capacidade} unidades ({estoqueInfo.ocupacao}% ocupado)
                </p>
              )}
            </div>

            {/* Itens do Pedido */}
            <div className="grid gap-3">
              <div className="flex items-center justify-between">
                <Label>Produtos do Pedido</Label>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={handleAddItem}
                  className="gap-1"
                >
                  <Plus className="w-4 h-4" />
                  Adicionar Produto
                </Button>
              </div>

              <div className="space-y-3">
                {itens.map((item, index) => {
                  const cotacaoInfo = item.produtoId ? getCotacaoInfo(item.produtoId) : null;
                  const temCotacao = cotacaoInfo !== null;
                  const cotacaoAprovada = cotacaoInfo?.aprovada;
                  
                  return (
                    <div
                      key={index}
                      className="flex gap-3 items-start p-3 rounded-lg border bg-muted/30"
                    >
                      <div className="flex-1 grid gap-2">
                        <Label className="text-xs text-muted-foreground">
                          Produto
                        </Label>
                        <Select
                          value={item.produtoId}
                          onValueChange={(value) =>
                            handleItemChange(index, "produtoId", value)
                          }
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Selecione um produto" />
                          </SelectTrigger>
                          <SelectContent>
                            {produtos.map((produto) => (
                              <SelectItem
                                key={produto.id}
                                value={String(produto.id)}
                              >
                                <div className="flex items-center gap-2">
                                  <Package className="w-4 h-4 text-muted-foreground" />
                                  {produto.codigo} - {produto.nome}
                                </div>
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        
                        {/* Info da cotação */}
                        {item.produtoId && (
                          <div className="text-xs mt-1">
                            {!temCotacao ? (
                              <span className="text-destructive flex items-center gap-1">
                                <AlertTriangle className="w-3 h-3" />
                                Sem cotação cadastrada
                              </span>
                            ) : !cotacaoAprovada ? (
                              <span className="text-orange-500 flex items-center gap-1">
                                <AlertTriangle className="w-3 h-3" />
                                Nenhuma cotação aprovada
                              </span>
                            ) : (
                              <span className="text-status-active">
                                ✓ {cotacaoInfo.fornecedorNome} - R$ {cotacaoInfo.preco.toLocaleString("pt-BR", { minimumFractionDigits: 2 })} ({cotacaoInfo.leadTime} dias)
                              </span>
                            )}
                          </div>
                        )}
                      </div>

                      <div className="w-28 grid gap-2">
                        <Label className="text-xs text-muted-foreground">
                          Quantidade
                        </Label>
                        <Input
                          type="number"
                          min="1"
                          placeholder="Qtd"
                          value={item.quantidade}
                          onChange={(e) =>
                            handleItemChange(index, "quantidade", e.target.value)
                          }
                        />
                      </div>

                      <div className="pt-6">
                        <Button
                          type="button"
                          variant="ghost"
                          size="icon"
                          onClick={() => handleRemoveItem(index)}
                          disabled={itens.length === 1}
                          className="text-destructive hover:text-destructive"
                        >
                          <Trash2 className="w-4 h-4" />
                        </Button>
                      </div>
                    </div>
                  );
                })}
              </div>

              {/* Resumo dos itens */}
              {itensValidos.length > 0 && (
                <div className="rounded-lg border bg-card p-4">
                  <h4 className="text-sm font-medium mb-3">Resumo do Pedido</h4>
                  <div className="space-y-2">
                    {itensValidos.map((item, index) => {
                      const cotacaoInfo = getCotacaoInfo(item.produtoId);
                      const subtotal = cotacaoInfo?.aprovada 
                        ? cotacaoInfo.preco * Number(item.quantidade) 
                        : 0;
                      
                      return (
                        <div
                          key={index}
                          className="flex justify-between text-sm items-center"
                        >
                          <div className="flex-1">
                            <span className="text-muted-foreground">
                              {getProdutoNome(item.produtoId)}
                            </span>
                            {cotacaoInfo?.aprovada && (
                              <span className="text-xs text-muted-foreground ml-2">
                                ({cotacaoInfo.fornecedorNome})
                              </span>
                            )}
                          </div>
                          <div className="text-right">
                            <span className="font-medium mr-4">
                              {item.quantidade} un
                            </span>
                            {cotacaoInfo?.aprovada ? (
                              <span className="font-medium text-status-active">
                                R$ {subtotal.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                              </span>
                            ) : (
                              <span className="text-orange-500 text-xs">
                                Sem cotação
                              </span>
                            )}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                  
                  <div className="mt-3 pt-3 border-t space-y-2">
                    <div className="flex justify-between items-center">
                      <span className="text-sm">Quantidade total:</span>
                      <span className={`font-medium ${capacidadeExcedida ? "text-destructive" : ""}`}>
                        {quantidadeTotal} unidades
                      </span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm font-medium">Valor total estimado:</span>
                      <span className="text-lg font-bold text-primary">
                        R$ {valorTotalEstimado.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}
                      </span>
                    </div>
                  </div>

                  {capacidadeExcedida && estoqueInfo && (
                    <div className="mt-3 p-2 rounded bg-destructive/10 border border-destructive/20 text-destructive text-xs">
                      <AlertTriangle className="w-3 h-3 inline mr-1" />
                      Capacidade do estoque excedida! O estoque "{estoqueInfo.nome}" tem apenas {estoqueInfo.capacidadeDisponivel} unidades disponíveis (ocupação atual: {estoqueInfo.ocupacao}%).
                    </div>
                  )}

                  {itensSemCotacaoAprovada.length > 0 && (
                    <div className="mt-3 p-2 rounded bg-orange-500/10 border border-orange-500/20 text-orange-600 text-xs">
                      <AlertTriangle className="w-3 h-3 inline mr-1" />
                      {itensSemCotacaoAprovada.length} produto(s) sem cotação aprovada. 
                      Aprove as cotações na tela de Cotações antes de criar o pedido.
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>

          <DialogFooter>
            <Button
              variant="outline"
              onClick={() => setIsDialogOpen(false)}
              disabled={saving}
            >
              Cancelar
            </Button>
            <Button 
              onClick={handleSave} 
              disabled={saving || !estoqueId || itensSemCotacaoAprovada.length > 0 || itensValidos.length === 0 || capacidadeExcedida}
            >
              {saving ? (
                <Loader2 className="w-4 h-4 animate-spin mr-2" />
              ) : (
                <ShoppingCart className="w-4 h-4 mr-2" />
              )}
              Criar Pedido
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
