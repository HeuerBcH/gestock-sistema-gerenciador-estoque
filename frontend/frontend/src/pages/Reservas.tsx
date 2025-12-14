import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { StatCard } from "@/components/shared/StatCard";
import { Calendar, Lock, Unlock, Package, ShoppingCart, Loader2 } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { reservasService } from "@/services/reservas";
import type { Reserva, ReservaTotais } from "@/types/entities";

export default function Reservas() {
  const [data, setData] = useState<Reserva[]>([]);
  const [totais, setTotais] = useState<ReservaTotais | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("all");
  const [search, setSearch] = useState("");

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [reservas, totaisData] = await Promise.all([
        reservasService.pesquisar(),
        reservasService.obterTotais(),
      ]);
      setData(reservas || []);
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

  const filteredData = data.filter((r) => {
    const matchesSearch =
      r.produtoNome.toLowerCase().includes(search.toLowerCase()) ||
      r.pedidoCodigo.toLowerCase().includes(search.toLowerCase());
    const matchesFilter = 
      filter === "all" || 
      (filter === "ativa" && r.status === "ATIVA") ||
      (filter === "liberada" && r.status === "LIBERADA");
    return matchesSearch && matchesFilter;
  });

  const columns = [
    {
      key: "dataHoraReserva",
      label: "Data/Hora Reserva",
      render: (item: Reserva) => new Date(item.dataHoraReserva).toLocaleString("pt-BR"),
    },
    {
      key: "pedidoCodigo",
      label: "Pedido",
      render: (item: Reserva) => (
        <div className="flex items-center gap-2">
          <ShoppingCart className="w-4 h-4 text-muted-foreground" />
          <span className="font-mono font-medium">{item.pedidoCodigo}</span>
        </div>
      ),
    },
    {
      key: "produtoNome",
      label: "Produto",
      render: (item: Reserva) => (
        <div className="flex items-center gap-2">
          <Package className="w-4 h-4 text-muted-foreground" />
          <span>{item.produtoNome}</span>
        </div>
      ),
    },
    {
      key: "quantidade",
      label: "Quantidade",
      render: (item: Reserva) => (
        <span className="font-medium">{item.quantidade} un</span>
      ),
    },
    {
      key: "status",
      label: "Status",
      render: (item: Reserva) => (
        <div className="flex items-center gap-2">
          {item.status === "ATIVA" ? (
            <Lock className="w-4 h-4 text-status-warning" />
          ) : (
            <Unlock className="w-4 h-4 text-status-active" />
          )}
          <Badge variant={item.status === "ATIVA" ? "warning" : "active"}>
            {item.status.toLowerCase()}
          </Badge>
        </div>
      ),
    },
    {
      key: "tipoLiberacao",
      label: "Tipo Liberação",
      render: (item: Reserva) =>
        item.tipoLiberacao ? (
          <Badge
            variant={item.tipoLiberacao === "RECEBIDO" ? "active" : "inactive"}
          >
            {item.tipoLiberacao.toLowerCase()}
          </Badge>
        ) : (
          <span className="text-muted-foreground">-</span>
        ),
    },
    {
      key: "dataHoraLiberacao",
      label: "Data Liberação",
      render: (item: Reserva) =>
        item.dataHoraLiberacao
          ? new Date(item.dataHoraLiberacao).toLocaleString("pt-BR")
          : "-",
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
        title="Reservas"
        description="Monitore reservas de produtos vinculadas a pedidos pendentes"
        icon={Calendar}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          title="Total de Reservas"
          value={totais?.totalReservas || 0}
          icon={Calendar}
          variant="primary"
        />
        <StatCard
          title="Reservas Ativas"
          value={totais?.reservasAtivas || 0}
          icon={Lock}
          variant="warning"
        />
        <StatCard
          title="Reservas Liberadas"
          value={totais?.reservasLiberadas || 0}
          icon={Unlock}
          variant="success"
        />
        <StatCard
          title="Qtd. Reservada Ativa"
          value={(totais?.quantidadeReservadaAtiva || 0).toLocaleString("pt-BR")}
          icon={Package}
          variant="default"
        />
      </div>

      {/* Info Card */}
      <div className="rounded-xl border bg-card p-4">
        <h3 className="font-semibold mb-2">Como funcionam as reservas?</h3>
        <ul className="space-y-1 text-sm text-muted-foreground">
          <li className="flex items-center gap-2">
            <Lock className="w-4 h-4 text-status-warning" />
            <span>Reserva é criada automaticamente ao gerar um pedido</span>
          </li>
          <li className="flex items-center gap-2">
            <Unlock className="w-4 h-4 text-status-active" />
            <span>Reserva é liberada quando o pedido é recebido ou cancelado</span>
          </li>
        </ul>
      </div>

      <DataTable
        data={filteredData}
        columns={columns}
        searchPlaceholder="Buscar por produto ou pedido..."
        searchValue={search}
        onSearchChange={setSearch}
        filterOptions={[
          { value: "all", label: "Histórico Completo" },
          { value: "ativa", label: "Ativas" },
          { value: "liberada", label: "Liberadas" },
        ]}
        filterValue={filter}
        onFilterChange={setFilter}
        filterLabel="Status"
      />
    </div>
  );
}
