import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Bell, AlertTriangle, AlertCircle, Info, Loader2 } from "lucide-react";
import { StatCard } from "@/components/shared/StatCard";
import { toast } from "@/hooks/use-toast";
import { alertasService } from "@/services/alertas";
import type { Alerta, AlertaTotais, NivelAlerta } from "@/types/entities";

const levelConfig: Record<NivelAlerta, { label: string; variant: "critical" | "high" | "medium"; icon: typeof AlertTriangle }> = {
  CRITICO: { label: "Crítico", variant: "critical", icon: AlertTriangle },
  ALTO: { label: "Alto", variant: "high", icon: AlertCircle },
  MEDIO: { label: "Médio", variant: "medium", icon: Info },
};

export default function Alertas() {
  const [data, setData] = useState<Alerta[]>([]);
  const [totais, setTotais] = useState<AlertaTotais | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("all");

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [alertas, totaisData] = await Promise.all([
        alertasService.pesquisar(),
        alertasService.obterTotais(),
      ]);
      setData(alertas || []);
      setTotais(totaisData);
    } catch (error: any) {
      toast({ title: "Erro ao carregar dados", description: error.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const filteredData = data.filter((alert) => {
    return filter === "all" || 
      (filter === "critical" && alert.nivel === "CRITICO") ||
      (filter === "high" && alert.nivel === "ALTO") ||
      (filter === "medium" && alert.nivel === "MEDIO");
  });

  const columns = [
    {
      key: "nivel",
      label: "Nível",
      render: (item: Alerta) => {
        const config = levelConfig[item.nivel];
        const Icon = config.icon;
        return (
          <div className="flex items-center gap-2">
            <Icon className={`w-4 h-4 ${
              item.nivel === "CRITICO" ? "text-status-critical" :
              item.nivel === "ALTO" ? "text-status-high" : "text-status-medium"
            }`} />
            <Badge variant={config.variant}>{config.label}</Badge>
          </div>
        );
      },
    },
    {
      key: "produtoNome",
      label: "Produto",
      render: (item: Alerta) => item.produtoNome,
    },
    {
      key: "estoqueNome",
      label: "Estoque",
      render: (item: Alerta) => item.estoqueNome,
    },
    {
      key: "quantidadeAtual",
      label: "Qtd Atual",
      render: (item: Alerta) => item.quantidadeAtual,
    },
    {
      key: "rop",
      label: "ROP",
      render: (item: Alerta) => item.rop,
    },
    {
      key: "percentualAbaixoRop",
      label: "Abaixo do ROP",
      render: (item: Alerta) => (
        <span className={`font-medium ${
          item.nivel === "CRITICO" ? "text-status-critical" :
          item.nivel === "ALTO" ? "text-status-high" : "text-status-medium"
        }`}>
          -{Math.round(item.percentualAbaixoRop)}%
        </span>
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
        title="Alertas"
        description="Monitore os produtos abaixo do ponto de ressuprimento"
        icon={Bell}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Alertas Críticos"
          value={totais?.totalCriticos || 0}
          icon={AlertTriangle}
          variant="danger"
        />
        <StatCard
          title="Alertas Altos"
          value={totais?.totalAltos || 0}
          icon={AlertCircle}
          variant="warning"
        />
        <StatCard
          title="Alertas Médios"
          value={totais?.totalMedios || 0}
          icon={Info}
          variant="default"
        />
      </div>

      <DataTable
        data={filteredData}
        columns={columns}
        filterOptions={[
          { value: "all", label: "Todos" },
          { value: "critical", label: "Críticos" },
          { value: "high", label: "Altos" },
          { value: "medium", label: "Médios" },
        ]}
        filterValue={filter}
        onFilterChange={setFilter}
        filterLabel="Nível"
      />
    </div>
  );
}
