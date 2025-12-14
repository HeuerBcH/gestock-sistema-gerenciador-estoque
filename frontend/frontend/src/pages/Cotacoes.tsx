import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  FileText,
  Check,
  X,
  Star,
  ChevronDown,
  ChevronRight,
  Loader2,
  RefreshCw,
} from "lucide-react";
import { toast } from "@/hooks/use-toast";
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { cn } from "@/lib/utils";
import { cotacoesService } from "@/services/cotacoes";
import type { CotacaoPorProduto } from "@/types/entities";

export default function Cotacoes() {
  const [data, setData] = useState<CotacaoPorProduto[]>([]);
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [openProducts, setOpenProducts] = useState<number[]>([]);

  const carregarDados = async () => {
    try {
      setLoading(true);
      const cotacoes = await cotacoesService.pesquisar();
      setData(cotacoes || []);
    } catch (error: any) {
      toast({
        title: "Erro ao carregar dados",
        description: error.message,
        variant: "destructive",
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const handleApprove = async (id: number) => {
    try {
      await cotacoesService.aprovar(id);
      toast({ title: "Cotação aprovada!" });
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao aprovar",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  const handleDisapprove = async (id: number) => {
    try {
      await cotacoesService.desaprovar(id);
      toast({ title: "Cotação desaprovada!" });
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao desaprovar",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  const toggleProduct = (productId: number) => {
    setOpenProducts((prev) =>
      prev.includes(productId)
        ? prev.filter((id) => id !== productId)
        : [...prev, productId]
    );
  };

  const handleSync = async () => {
    try {
      setSyncing(true);
      const result = await cotacoesService.sincronizar();
      toast({
        title: result.mensagem,
        description:
          result.cotacoesCriadas > 0
            ? `${result.cotacoesCriadas} cotação(ões) criada(s)`
            : undefined,
      });
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao sincronizar",
        description: error.message,
        variant: "destructive",
      });
    } finally {
      setSyncing(false);
    }
  };

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
        title="Cotações"
        description="Compare cotações de fornecedores para cada produto"
        icon={FileText}
        action={{
          label: syncing ? "Sincronizando..." : "Sincronizar Cotações",
          onClick: handleSync,
          icon: (
            <RefreshCw className={cn("w-4 h-4", syncing && "animate-spin")} />
          ),
          disabled: syncing,
        }}
      />

      <div className="space-y-4">
        {data.map((productGroup) => {
          const isOpen = openProducts.includes(productGroup.produtoId);

          return (
            <Collapsible
              key={productGroup.produtoId}
              open={isOpen}
              onOpenChange={() => toggleProduct(productGroup.produtoId)}
            >
              <div className="rounded-xl border bg-card overflow-hidden">
                <CollapsibleTrigger asChild>
                  <button className="w-full flex items-center justify-between p-4 hover:bg-muted/50 transition-colors">
                    <div className="flex items-center gap-3">
                      {isOpen ? (
                        <ChevronDown className="w-5 h-5 text-muted-foreground" />
                      ) : (
                        <ChevronRight className="w-5 h-5 text-muted-foreground" />
                      )}
                      <div className="text-left">
                        <h3 className="font-semibold">
                          {productGroup.produtoNome}
                        </h3>
                        <p className="text-sm text-muted-foreground">
                          {productGroup.cotacoes.length} cotação(ões)
                          disponível(is)
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant="secondary">
                        {
                          productGroup.cotacoes.filter(
                            (q) => q.statusAprovacao === "APROVADA"
                          ).length
                        }{" "}
                        aprovada(s)
                      </Badge>
                    </div>
                  </button>
                </CollapsibleTrigger>

                <CollapsibleContent>
                  <div className="border-t">
                    <table className="w-full">
                      <thead>
                        <tr className="bg-muted/50">
                          <th className="text-left p-3 text-sm font-semibold">
                            Fornecedor
                          </th>
                          <th className="text-left p-3 text-sm font-semibold">
                            Preço
                          </th>
                          <th className="text-left p-3 text-sm font-semibold">
                            Lead Time
                          </th>
                          <th className="text-left p-3 text-sm font-semibold">
                            Validade
                          </th>
                          <th className="text-left p-3 text-sm font-semibold">
                            Status
                          </th>
                          <th className="text-left p-3 text-sm font-semibold">
                            Ações
                          </th>
                        </tr>
                      </thead>
                      <tbody>
                        {productGroup.cotacoes.map((quotation) => {
                          const isBest = quotation.maisVantajosa;
                          return (
                            <tr
                              key={quotation.id}
                              className={cn(
                                "border-t transition-colors",
                                isBest && "bg-status-active-bg/50"
                              )}
                            >
                              <td className="p-3">
                                <div className="flex items-center gap-2">
                                  {isBest && (
                                    <Star className="w-4 h-4 text-status-warning fill-status-warning" />
                                  )}
                                  <span className={cn(isBest && "font-medium")}>
                                    {quotation.fornecedorNome}
                                  </span>
                                </div>
                              </td>
                              <td className="p-3">
                                <span
                                  className={cn(
                                    isBest && "font-semibold text-status-active"
                                  )}
                                >
                                  R${" "}
                                  {quotation.preco.toLocaleString("pt-BR", {
                                    minimumFractionDigits: 2,
                                  })}
                                </span>
                              </td>
                              <td className="p-3">{quotation.leadTime} dias</td>
                              <td className="p-3">
                                <Badge
                                  variant={
                                    quotation.validade === "ATIVA"
                                      ? "active"
                                      : "inactive"
                                  }
                                >
                                  {quotation.validade.toLowerCase()}
                                </Badge>
                              </td>
                              <td className="p-3">
                                {quotation.statusAprovacao === "APROVADA" ? (
                                  <Badge variant="active">Aprovada</Badge>
                                ) : (
                                  <Badge variant="secondary">Pendente</Badge>
                                )}
                              </td>
                              <td className="p-3">
                                <div className="flex items-center gap-1">
                                  {quotation.statusAprovacao !== "APROVADA" ? (
                                    <Button
                                      variant="ghost"
                                      size="icon"
                                      onClick={() =>
                                        handleApprove(quotation.id)
                                      }
                                      title="Aprovar cotação"
                                    >
                                      <Check className="w-4 h-4 text-status-active" />
                                    </Button>
                                  ) : (
                                    <Button
                                      variant="ghost"
                                      size="icon"
                                      onClick={() =>
                                        handleDisapprove(quotation.id)
                                      }
                                      title="Desaprovar cotação"
                                    >
                                      <X className="w-4 h-4 text-destructive" />
                                    </Button>
                                  )}
                                </div>
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                </CollapsibleContent>
              </div>
            </Collapsible>
          );
        })}
      </div>

      {data.length === 0 && (
        <div className="text-center py-12 space-y-4">
          <FileText className="w-16 h-16 mx-auto text-muted-foreground/50" />
          <div>
            <h3 className="text-lg font-medium text-muted-foreground">
              Nenhuma cotação encontrada
            </h3>
            <p className="text-sm text-muted-foreground/80 mt-1">
              Clique em "Sincronizar Cotações" para gerar cotações
              automaticamente
              <br />
              baseadas nos produtos e fornecedores cadastrados.
            </p>
          </div>
          <Button onClick={handleSync} disabled={syncing} className="mt-4">
            {syncing ? (
              <Loader2 className="w-4 h-4 mr-2 animate-spin" />
            ) : (
              <RefreshCw className="w-4 h-4 mr-2" />
            )}
            Sincronizar Cotações
          </Button>
        </div>
      )}

      {/* Legend */}
      <div className="flex items-center gap-4 p-4 rounded-lg bg-muted/50 text-sm">
        <div className="flex items-center gap-2">
          <Star className="w-4 h-4 text-status-warning fill-status-warning" />
          <span className="text-muted-foreground">
            Melhor cotação (menor preço, prazo e validade ativa)
          </span>
        </div>
      </div>
    </div>
  );
}
