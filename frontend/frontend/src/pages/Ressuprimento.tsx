import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { StatCard } from "@/components/shared/StatCard";
import { TrendingUp, CheckCircle, AlertTriangle, Calculator, Loader2, RefreshCw, PackageX } from "lucide-react";
import { Progress } from "@/components/ui/progress";
import { toast } from "@/hooks/use-toast";
import { ropService } from "@/services/rop";
import type { PontoRessuprimento, PontoRessuprimentoTotais } from "@/types/entities";

export default function Ressuprimento() {
  const [data, setData] = useState<PontoRessuprimento[]>([]);
  const [totais, setTotais] = useState<PontoRessuprimentoTotais | null>(null);
  const [loading, setLoading] = useState(true);
  const [syncing, setSyncing] = useState(false);
  const [filter, setFilter] = useState("all");
  const [search, setSearch] = useState("");

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [pontosData, totaisData] = await Promise.all([
        ropService.pesquisar(),
        ropService.obterTotais(),
      ]);
      setData(pontosData || []);
      setTotais(totaisData);
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({ title: "Erro ao carregar dados", description: message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  const handleSincronizar = async () => {
    try {
      setSyncing(true);
      const resultado = await ropService.sincronizar();
      toast({ 
        title: "Sincronização concluída", 
        description: resultado.mensagem 
      });
      carregarDados();
    } catch (error: unknown) {
      const message = error instanceof Error ? error.message : "Erro desconhecido";
      toast({ title: "Erro ao sincronizar", description: message, variant: "destructive" });
    } finally {
      setSyncing(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const filteredData = data.filter((rp) => {
    const matchesSearch =
      rp.produtoNome.toLowerCase().includes(search.toLowerCase()) ||
      rp.estoqueNome.toLowerCase().includes(search.toLowerCase());
    const matchesFilter = 
      filter === "all" || 
      (filter === "adequado" && rp.status === "ADEQUADO") ||
      (filter === "inadequado" && rp.status === "INADEQUADO");
    return matchesSearch && matchesFilter;
  });

  const columns = [
    {
      key: "estoqueNome",
      label: "Estoque",
      render: (item: PontoRessuprimento) => item.estoqueNome,
    },
    {
      key: "produtoNome",
      label: "Produto",
      render: (item: PontoRessuprimento) => item.produtoNome,
    },
    {
      key: "consumoMedioDiario",
      label: "Consumo Médio/Dia",
      render: (item: PontoRessuprimento) => `${item.consumoMedioDiario.toFixed(2)} un`,
    },
    {
      key: "consumoMaximoDiario",
      label: "Consumo Máx/Dia",
      render: (item: PontoRessuprimento) => `${item.consumoMaximoDiario.toFixed(2)} un`,
    },
    {
      key: "leadTimeMedio",
      label: "Lead Time Médio",
      render: (item: PontoRessuprimento) => `${item.leadTimeMedio} dias`,
    },
    {
      key: "leadTimeMaximo",
      label: "Lead Time Máx",
      render: (item: PontoRessuprimento) => `${item.leadTimeMaximo} dias`,
    },
    {
      key: "estoqueSeguranca",
      label: "Estoque Segurança",
      render: (item: PontoRessuprimento) => `${item.estoqueSeguranca} un`,
    },
    {
      key: "ropCalculado",
      label: "ROP Calculado",
      render: (item: PontoRessuprimento) => (
        <div className="flex items-center gap-2">
          <Calculator className="w-4 h-4 text-muted-foreground" />
          <span className="font-mono font-medium">{item.ropCalculado}</span>
        </div>
      ),
    },
    {
      key: "saldoAtual",
      label: "Saldo Atual",
      render: (item: PontoRessuprimento) => {
        const percentage = item.ropCalculado > 0 
          ? Math.min((item.saldoAtual / item.ropCalculado) * 100, 100) 
          : 100;
        const isLow = item.saldoAtual < item.ropCalculado;
        return (
          <div className="flex items-center gap-3 min-w-[150px]">
            <Progress 
              value={percentage} 
              className={`h-2 flex-1 ${isLow ? "[&>div]:bg-status-critical" : "[&>div]:bg-status-active"}`}
            />
            <span className={`font-medium ${isLow ? "text-status-critical" : "text-status-active"}`}>
              {item.saldoAtual}
            </span>
          </div>
        );
      },
    },
    {
      key: "status",
      label: "Status",
      render: (item: PontoRessuprimento) => (
        <Badge variant={item.status === "ADEQUADO" ? "active" : "critical"}>
          {item.status.toLowerCase()}
        </Badge>
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
        title="Ponto de Ressuprimento"
        description="Monitore o ROP (Reorder Point) dos produtos em estoque"
        icon={TrendingUp}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Total Monitorado"
          value={totais?.totalMonitorado || 0}
          icon={TrendingUp}
          variant="primary"
        />
        <StatCard
          title="Estoque Adequado"
          value={totais?.estoqueAdequado || 0}
          icon={CheckCircle}
          variant="success"
        />
        <StatCard
          title="Abaixo do ROP"
          value={totais?.abaixoDoRop || 0}
          icon={AlertTriangle}
          variant="danger"
        />
      </div>

      {/* Formula explanation */}
      <div className="rounded-xl border bg-card p-4">
        <div className="flex items-center justify-between mb-3">
          <h3 className="font-semibold flex items-center gap-2">
            <Calculator className="w-5 h-5 text-primary" />
            Fórmulas de Cálculo
          </h3>
          <Button 
            variant="outline" 
            size="sm" 
            onClick={handleSincronizar}
            disabled={syncing}
            className="gap-2"
          >
            <RefreshCw className={`w-4 h-4 ${syncing ? "animate-spin" : ""}`} />
            {syncing ? "Sincronizando..." : "Sincronizar ROP"}
          </Button>
        </div>
        <div className="space-y-3">
          <div className="bg-muted/50 rounded-lg p-3 font-mono text-sm">
            <span className="text-primary font-semibold">ROP</span> = (Consumo Médio Diário × Lead Time Médio) + Estoque de Segurança
          </div>
          <div className="bg-muted/50 rounded-lg p-3 font-mono text-sm">
            <span className="text-orange-500 font-semibold">Estoque de Segurança</span> = (Consumo Máximo Diário × Lead Time Máximo) − (Consumo Médio Diário × Lead Time Médio)
          </div>
        </div>
        <p className="text-xs text-muted-foreground mt-3">
          O <strong>Consumo Médio/Máximo Diário</strong> é calculado automaticamente baseado nas saídas dos últimos 90 dias.
          O <strong>Lead Time Médio/Máximo</strong> é obtido a partir dos fornecedores ativos do produto.
          Clique em "Sincronizar ROP" para criar/atualizar pontos de ressuprimento para todos os produtos em estoque.
        </p>
      </div>

      {/* Empty state */}
      {data.length === 0 ? (
        <div className="rounded-xl border bg-card p-12 flex flex-col items-center justify-center text-center">
          <PackageX className="w-16 h-16 text-muted-foreground mb-4" />
          <h3 className="text-lg font-semibold mb-2">Nenhum ponto de ressuprimento cadastrado</h3>
          <p className="text-muted-foreground mb-4 max-w-md">
            Para monitorar o ROP dos produtos, primeiro adicione produtos aos estoques através de movimentações de entrada.
            Em seguida, clique em "Sincronizar ROP" para criar automaticamente os pontos de ressuprimento.
          </p>
          <Button onClick={handleSincronizar} disabled={syncing} className="gap-2">
            <RefreshCw className={`w-4 h-4 ${syncing ? "animate-spin" : ""}`} />
            {syncing ? "Sincronizando..." : "Sincronizar Agora"}
          </Button>
        </div>
      ) : (
        <DataTable
          data={filteredData}
          columns={columns}
          searchPlaceholder="Buscar por produto ou estoque..."
          searchValue={search}
          onSearchChange={setSearch}
          filterOptions={[
            { value: "all", label: "Todos" },
            { value: "adequado", label: "Adequados" },
            { value: "inadequado", label: "Inadequados" },
          ]}
          filterValue={filter}
          onFilterChange={setFilter}
          filterLabel="Status"
        />
      )}
    </div>
  );
}
