import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Users, Pencil, Trash2, ToggleLeft, ToggleRight, Loader2 } from "lucide-react";
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
import { fornecedoresService } from "@/services/fornecedores";
import type { Fornecedor, Status } from "@/types/entities";

export default function Fornecedores() {
  const [data, setData] = useState<Fornecedor[]>([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingSupplier, setEditingSupplier] = useState<Fornecedor | null>(null);
  const [formData, setFormData] = useState({
    nome: "",
    cnpj: "",
    contato: "",
    leadTime: "",
    custo: "",
    status: "ATIVO" as Status,
  });

  const carregarDados = async () => {
    try {
      setLoading(true);
      const fornecedores = await fornecedoresService.pesquisar();
      setData(fornecedores || []);
    } catch (error: any) {
      toast({ title: "Erro ao carregar dados", description: error.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const filteredData = data.filter((supplier) => {
    const matchesSearch =
      supplier.nome.toLowerCase().includes(search.toLowerCase()) ||
      supplier.cnpj.includes(search);
    const matchesFilter =
      filter === "all" || 
      (filter === "ativo" && supplier.status === "ATIVO") ||
      (filter === "inativo" && supplier.status === "INATIVO");
    return matchesSearch && matchesFilter;
  });

  const handleOpenDialog = (supplier?: Fornecedor) => {
    if (supplier) {
      setEditingSupplier(supplier);
      setFormData({
        nome: supplier.nome,
        cnpj: supplier.cnpj,
        contato: supplier.contato,
        leadTime: supplier.leadTime.toString(),
        custo: supplier.custo.toString(),
        status: supplier.status,
      });
    } else {
      setEditingSupplier(null);
      setFormData({
        nome: "",
        cnpj: "",
        contato: "",
        leadTime: "",
        custo: "",
        status: "ATIVO",
      });
    }
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    if (!formData.nome || !formData.cnpj) {
      toast({ title: "Preencha todos os campos obrigatórios", variant: "destructive" });
      return;
    }

    try {
      setSaving(true);
      await fornecedoresService.salvar({
        id: editingSupplier?.id,
        nome: formData.nome,
        cnpj: formData.cnpj,
        contato: formData.contato,
        leadTime: Number(formData.leadTime) || 0,
        custo: Number(formData.custo) || 0,
        status: formData.status,
      });
      
      toast({ title: editingSupplier ? "Fornecedor atualizado!" : "Fornecedor cadastrado!" });
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
      await fornecedoresService.remover(id);
      toast({ title: "Fornecedor removido!" });
      carregarDados();
    } catch (error: any) {
      toast({ title: "Erro ao remover", description: error.message, variant: "destructive" });
    }
  };

  const handleToggleStatus = async (supplier: Fornecedor) => {
    try {
      if (supplier.status === "ATIVO") {
        await fornecedoresService.inativar(supplier.id);
      } else {
        await fornecedoresService.ativar(supplier.id);
      }
    toast({ title: "Status atualizado!" });
      carregarDados();
    } catch (error: any) {
      toast({ title: "Erro ao alterar status", description: error.message, variant: "destructive" });
    }
  };

  const columns = [
    { key: "nome", label: "Nome" },
    { key: "cnpj", label: "CNPJ" },
    { key: "contato", label: "Contato" },
    {
      key: "leadTime",
      label: "Lead Time",
      render: (item: Fornecedor) => `${item.leadTime} dias`,
    },
    {
      key: "custo",
      label: "Custo/Unidade",
      render: (item: Fornecedor) =>
        `R$ ${item.custo.toLocaleString("pt-BR", { minimumFractionDigits: 2 })}`,
    },
    {
      key: "status",
      label: "Status",
      render: (item: Fornecedor) => (
        <Badge variant={item.status === "ATIVO" ? "active" : "inactive"}>
          {item.status.toLowerCase()}
        </Badge>
      ),
    },
    {
      key: "actions",
      label: "Ações",
      render: (item: Fornecedor) => (
        <div className="flex items-center gap-1">
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
        title="Fornecedores"
        description="Gerencie os fornecedores cadastrados no sistema"
        icon={Users}
        action={{
          label: "Novo Fornecedor",
          onClick: () => handleOpenDialog(),
        }}
      />

      <DataTable
        data={filteredData}
        columns={columns}
        searchPlaceholder="Buscar por nome ou CNPJ..."
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
              {editingSupplier ? "Editar Fornecedor" : "Novo Fornecedor"}
            </DialogTitle>
            <DialogDescription>
              Preencha os dados do fornecedor abaixo.
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
              <Label htmlFor="cnpj">CNPJ</Label>
              <Input
                id="cnpj"
                value={formData.cnpj}
                onChange={(e) =>
                  setFormData({ ...formData, cnpj: e.target.value })
                }
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="contato">E-mail de Contato</Label>
              <Input
                id="contato"
                type="email"
                value={formData.contato}
                onChange={(e) =>
                  setFormData({ ...formData, contato: e.target.value })
                }
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <Label htmlFor="leadTime">Lead Time (dias)</Label>
                <Input
                  id="leadTime"
                  type="number"
                  value={formData.leadTime}
                  onChange={(e) =>
                    setFormData({ ...formData, leadTime: e.target.value })
                  }
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="custo">Custo/Unidade (R$)</Label>
                <Input
                  id="custo"
                  type="number"
                  step="0.01"
                  value={formData.custo}
                  onChange={(e) =>
                    setFormData({ ...formData, custo: e.target.value })
                  }
                />
              </div>
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
