import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Warehouse, Pencil, Trash2, ToggleLeft, ToggleRight, Eye, Loader2 } from "lucide-react";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { toast } from "@/hooks/use-toast";
import { Progress } from "@/components/ui/progress";
import { Link } from "react-router-dom";
import { estoquesService } from "@/services/estoques";
import type { Estoque, Status } from "@/types/entities";

export default function Estoques() {
  const [data, setData] = useState<Estoque[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingStock, setEditingStock] = useState<Estoque | null>(null);
  const [formData, setFormData] = useState({
    nome: "",
    endereco: "",
    capacidade: "",
    status: "ATIVO" as Status,
  });

  const carregarDados = async () => {
    try {
      setLoading(true);
      const estoques = await estoquesService.pesquisar();
      setData(estoques || []);
    } catch (error: any) {
      toast({ title: "Erro ao carregar dados", description: error.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const filteredData = data.filter((stock) => {
    const matchesSearch =
      stock.nome.toLowerCase().includes(search.toLowerCase()) ||
      stock.endereco.toLowerCase().includes(search.toLowerCase());
    const matchesFilter = 
      filter === "all" || 
      (filter === "ativo" && stock.status === "ATIVO") ||
      (filter === "inativo" && stock.status === "INATIVO");
    return matchesSearch && matchesFilter;
  });

  const handleOpenDialog = (stock?: Estoque) => {
    if (stock) {
      setEditingStock(stock);
      setFormData({
        nome: stock.nome,
        endereco: stock.endereco,
        capacidade: stock.capacidade.toString(),
        status: stock.status,
      });
    } else {
      setEditingStock(null);
      setFormData({
        nome: "",
        endereco: "",
        capacidade: "",
        status: "ATIVO",
      });
    }
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    if (!formData.nome || !formData.endereco || !formData.capacidade) {
      toast({ title: "Preencha todos os campos obrigatórios", variant: "destructive" });
      return;
    }

    try {
      setSaving(true);
      await estoquesService.salvar({
        id: editingStock?.id,
        nome: formData.nome,
        endereco: formData.endereco,
        capacidade: Number(formData.capacidade),
        status: formData.status,
      });
      
      toast({ title: editingStock ? "Estoque atualizado!" : "Estoque cadastrado!" });
      setIsDialogOpen(false);
      carregarDados();
    } catch (error: any) {
      toast({ title: "Erro ao salvar", description: error.message, variant: "destructive" });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await estoquesService.remover(id);
      toast({ title: "Estoque removido!" });
      carregarDados();
    } catch (error: any) {
      toast({ title: "Erro ao remover", description: error.message, variant: "destructive" });
    }
  };

  const handleToggleStatus = async (stock: Estoque) => {
    try {
      if (stock.status === "ATIVO") {
        await estoquesService.inativar(stock.id);
      } else {
        await estoquesService.ativar(stock.id);
      }
    toast({ title: "Status atualizado!" });
      carregarDados();
    } catch (error: any) {
      toast({ title: "Erro ao alterar status", description: error.message, variant: "destructive" });
    }
  };

  const columns = [
    { key: "nome", label: "Nome" },
    { key: "endereco", label: "Endereço" },
    {
      key: "capacidade",
      label: "Capacidade",
      render: (item: Estoque) => item.capacidade.toLocaleString("pt-BR"),
    },
    {
      key: "ocupacao",
      label: "Ocupação",
      render: (item: Estoque) => {
        const percentage = Math.round(item.ocupacao || 0);
        return (
          <div className="flex items-center gap-3 min-w-[150px]">
            <Progress value={percentage} className="h-2 flex-1" />
            <span className="text-sm text-muted-foreground w-10">{percentage}%</span>
          </div>
        );
      },
    },
    {
      key: "status",
      label: "Status",
      render: (item: Estoque) => (
        <Badge variant={item.status === "ATIVO" ? "active" : "inactive"}>
          {item.status.toLowerCase()}
        </Badge>
      ),
    },
    {
      key: "actions",
      label: "Ações",
      render: (item: Estoque) => (
        <div className="flex items-center gap-1">
          <Button variant="ghost" size="icon" asChild>
            <Link to={`/produtos?estoque=${item.id}`}>
              <Eye className="w-4 h-4" />
            </Link>
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => handleOpenDialog(item)}
          >
            <Pencil className="w-4 h-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => handleToggleStatus(item)}
          >
            {item.status === "ATIVO" ? (
              <ToggleRight className="w-4 h-4 text-status-active" />
            ) : (
              <ToggleLeft className="w-4 h-4 text-muted-foreground" />
            )}
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => handleDelete(item.id)}
          >
            <Trash2 className="w-4 h-4 text-destructive" />
          </Button>
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
        title="Estoques"
        description="Gerencie os estoques e centros de distribuição"
        icon={Warehouse}
        action={{
          label: "Novo Estoque",
          onClick: () => handleOpenDialog(),
        }}
      />

      <DataTable
        data={filteredData}
        columns={columns}
        searchPlaceholder="Buscar por nome ou endereço..."
        searchValue={search}
        onSearchChange={setSearch}
        filterOptions={[
          { value: "all", label: "Todos" },
          { value: "ativo", label: "Ativos" },
          { value: "inativo", label: "Inativos" },
        ]}
        filterValue={filter}
        onFilterChange={setFilter}
        filterLabel="Status"
      />

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>
              {editingStock ? "Editar Estoque" : "Novo Estoque"}
            </DialogTitle>
            <DialogDescription>
              Preencha os dados do estoque abaixo.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="nome">Nome</Label>
              <Input
                id="nome"
                value={formData.nome}
                onChange={(e) =>
                  setFormData({ ...formData, nome: e.target.value })
                }
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="endereco">Endereço</Label>
              <Input
                id="endereco"
                value={formData.endereco}
                onChange={(e) =>
                  setFormData({ ...formData, endereco: e.target.value })
                }
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="capacidade">Capacidade (unidades)</Label>
              <Input
                id="capacidade"
                type="number"
                value={formData.capacidade}
                onChange={(e) =>
                  setFormData({ ...formData, capacidade: e.target.value })
                }
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="status">Status</Label>
              <Select
                value={formData.status}
                onValueChange={(value: Status) =>
                  setFormData({ ...formData, status: value })
                }
              >
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ATIVO">Ativo</SelectItem>
                  <SelectItem value="INATIVO">Inativo</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsDialogOpen(false)} disabled={saving}>
              Cancelar
            </Button>
            <Button onClick={handleSave} disabled={saving}>
              {saving ? <Loader2 className="w-4 h-4 animate-spin mr-2" /> : null}
              Salvar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
