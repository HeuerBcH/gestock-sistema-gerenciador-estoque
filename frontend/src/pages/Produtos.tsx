import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Package,
  Pencil,
  Trash2,
  ToggleLeft,
  ToggleRight,
  Loader2,
  ArrowLeft,
  Warehouse,
  PackageX,
} from "lucide-react";
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
import { Checkbox } from "@/components/ui/checkbox";
import { toast } from "@/hooks/use-toast";
import { produtosService } from "@/services/produtos";
import { fornecedoresService } from "@/services/fornecedores";
import { estoquesService } from "@/services/estoques";
import type { Produto, Fornecedor, Status, Perecivel, Estoque } from "@/types/entities";

export default function Produtos() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const estoqueIdParam = searchParams.get("estoque");

  const [data, setData] = useState<Produto[]>([]);
  const [fornecedores, setFornecedores] = useState<Fornecedor[]>([]);
  const [estoqueAtual, setEstoqueAtual] = useState<Estoque | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState("");
  const [filter, setFilter] = useState("all");
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Produto | null>(null);
  const [formData, setFormData] = useState({
    codigo: "",
    nome: "",
    peso: "",
    perecivel: false,
    status: "ATIVO" as Status,
    fornecedorIds: [] as number[],
  });

  // Carregar dados
  const carregarDados = async () => {
    try {
      setLoading(true);

      // Se tem filtro de estoque, buscar produtos daquele estoque
      if (estoqueIdParam) {
        const estoqueId = Number(estoqueIdParam);
        const [produtosData, fornecedoresData, estoqueData] = await Promise.all([
          estoquesService.obterProdutos(estoqueId),
          fornecedoresService.pesquisar({ status: "ATIVO" }),
          estoquesService.obter(estoqueId),
        ]);
        setData(produtosData || []);
        setFornecedores(fornecedoresData || []);
        setEstoqueAtual(estoqueData);
      } else {
        // Buscar todos os produtos
        const [produtosData, fornecedoresData] = await Promise.all([
          produtosService.pesquisar(),
          fornecedoresService.pesquisar({ status: "ATIVO" }),
        ]);
        setData(produtosData || []);
        setFornecedores(fornecedoresData || []);
        setEstoqueAtual(null);
      }
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
  }, [estoqueIdParam]);

  const filteredData = data.filter((product) => {
    const matchesSearch =
      product.nome.toLowerCase().includes(search.toLowerCase()) ||
      product.codigo.toLowerCase().includes(search.toLowerCase());
    const matchesFilter =
      filter === "all" ||
      (filter === "ativo" && product.status === "ATIVO") ||
      (filter === "inativo" && product.status === "INATIVO");
    return matchesSearch && matchesFilter;
  });

  const handleOpenDialog = (product?: Produto) => {
    if (product) {
      setEditingProduct(product);
      // Tentar encontrar IDs dos fornecedores pelos nomes
      const fornecedorIds = fornecedores
        .filter((f) => product.fornecedores.includes(f.nome))
        .map((f) => f.id);

      setFormData({
        codigo: product.codigo,
        nome: product.nome,
        peso: product.peso.toString(),
        perecivel: product.perecivel === "SIM",
        status: product.status,
        fornecedorIds,
      });
    } else {
      setEditingProduct(null);
      setFormData({
        codigo: "",
        nome: "",
        peso: "",
        perecivel: false,
        status: "ATIVO",
        fornecedorIds: [],
      });
    }
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    if (!formData.codigo || !formData.nome || !formData.peso) {
      toast({
        title: "Preencha todos os campos obrigatórios",
        variant: "destructive",
      });
      return;
    }

    try {
      setSaving(true);
      await produtosService.salvar({
        id: editingProduct?.id,
        codigo: formData.codigo,
        nome: formData.nome,
        peso: Number(formData.peso),
        perecivel: formData.perecivel ? "SIM" : ("NAO" as Perecivel),
        status: formData.status,
        fornecedores: formData.fornecedorIds,
      });

      toast({
        title: editingProduct ? "Produto atualizado!" : "Produto cadastrado!",
      });
      setIsDialogOpen(false);
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao salvar",
        description: error.message,
        variant: "destructive",
      });
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await produtosService.remover(id);
      toast({ title: "Produto removido!" });
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao remover",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  const handleToggleStatus = async (product: Produto) => {
    try {
      if (product.status === "ATIVO") {
        await produtosService.inativar(product.id);
      } else {
        await produtosService.ativar(product.id);
      }
    toast({ title: "Status atualizado!" });
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao alterar status",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  const handleFornecedorToggle = (fornecedorId: number) => {
    setFormData((prev) => ({
      ...prev,
      fornecedorIds: prev.fornecedorIds.includes(fornecedorId)
        ? prev.fornecedorIds.filter((id) => id !== fornecedorId)
        : [...prev.fornecedorIds, fornecedorId],
    }));
  };

  const handleVoltar = () => {
    navigate("/");
  };

  const columns = [
    { key: "codigo", label: "Código" },
    { key: "nome", label: "Nome" },
    {
      key: "peso",
      label: "Peso",
      render: (item: Produto) => `${item.peso}g`,
    },
    {
      key: "perecivel",
      label: "Perecível",
      render: (item: Produto) => (
        <Badge variant={item.perecivel === "SIM" ? "warning" : "secondary"}>
          {item.perecivel === "SIM" ? "Sim" : "Não"}
        </Badge>
      ),
    },
    {
      key: "fornecedores",
      label: "Fornecedores",
      render: (item: Produto) => (
        <span className="text-sm text-muted-foreground">
          {item.fornecedores?.join(", ") || "-"}
        </span>
      ),
    },
    {
      key: "status",
      label: "Status",
      render: (item: Produto) => (
        <Badge variant={item.status === "ATIVO" ? "active" : "inactive"}>
          {item.status.toLowerCase()}
        </Badge>
      ),
    },
    {
      key: "actions",
      label: "Ações",
      render: (item: Produto) => (
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
      {/* Header com contexto de estoque */}
      {estoqueAtual ? (
        <div className="space-y-4">
          {/* Botão Voltar */}
          <Button
            variant="ghost"
            size="sm"
            onClick={handleVoltar}
            className="gap-2 text-muted-foreground hover:text-foreground"
          >
            <ArrowLeft className="w-4 h-4" />
            Voltar para Estoques
          </Button>

          {/* Card do Estoque */}
          <div className="rounded-xl border bg-card p-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-primary/10 flex items-center justify-center">
                <Warehouse className="w-5 h-5 text-primary" />
              </div>
              <div>
                <h2 className="font-semibold text-lg">
                  Produtos em: {estoqueAtual.nome}
                </h2>
                <p className="text-sm text-muted-foreground">
                  {estoqueAtual.endereco}
                </p>
              </div>
              <Badge
                variant={estoqueAtual.status === "ATIVO" ? "active" : "inactive"}
                className="ml-auto"
              >
                {estoqueAtual.status.toLowerCase()}
              </Badge>
            </div>
          </div>

          <PageHeader
            title={`${data.length} produto(s) encontrado(s)`}
            description="Produtos associados a este estoque"
            icon={Package}
          />
        </div>
      ) : (
      <PageHeader
        title="Produtos"
        description="Gerencie os produtos cadastrados no sistema"
        icon={Package}
        action={{
          label: "Novo Produto",
          onClick: () => handleOpenDialog(),
        }}
      />
      )}

      {/* Empty State para estoque sem produtos */}
      {estoqueAtual && data.length === 0 ? (
        <div className="rounded-xl border bg-card p-12 text-center">
          <div className="flex justify-center mb-4">
            <div className="w-16 h-16 rounded-full bg-muted flex items-center justify-center">
              <PackageX className="w-8 h-8 text-muted-foreground" />
            </div>
          </div>
          <h3 className="text-lg font-semibold mb-2">
            Não existem produtos neste estoque
          </h3>
          <p className="text-muted-foreground mb-6 max-w-md mx-auto">
            Este estoque ainda não possui produtos associados. Registre
            movimentações de entrada para adicionar produtos.
          </p>
          <div className="flex gap-3 justify-center">
            <Button variant="outline" onClick={handleVoltar}>
              <ArrowLeft className="w-4 h-4 mr-2" />
              Voltar para Estoques
            </Button>
            <Button onClick={() => navigate("/movimentacoes")}>
              Ir para Movimentações
            </Button>
          </div>
        </div>
      ) : (
      <DataTable
        data={filteredData}
        columns={columns}
        searchPlaceholder="Buscar por nome ou código..."
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
      )}

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>
              {editingProduct ? "Editar Produto" : "Novo Produto"}
            </DialogTitle>
            <DialogDescription>
              Preencha os dados do produto abaixo.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <Label htmlFor="code">Código</Label>
                <Input
                  id="code"
                  value={formData.codigo}
                  onChange={(e) =>
                    setFormData({ ...formData, codigo: e.target.value })
                  }
                  disabled={!!editingProduct}
                />
              </div>
              <div className="grid gap-2">
                <Label htmlFor="name">Nome</Label>
                <Input
                  id="name"
                  value={formData.nome}
                  onChange={(e) =>
                    setFormData({ ...formData, nome: e.target.value })
                  }
                />
              </div>
            </div>
            <div className="grid gap-2">
              <Label htmlFor="weight">Peso (gramas)</Label>
              <Input
                id="weight"
                type="number"
                value={formData.peso}
                onChange={(e) =>
                  setFormData({ ...formData, peso: e.target.value })
                }
              />
            </div>
            <div className="flex items-center gap-2">
              <Checkbox
                id="perishable"
                checked={formData.perecivel}
                onCheckedChange={(checked) =>
                  setFormData({ ...formData, perecivel: checked as boolean })
                }
              />
              <Label htmlFor="perishable">Produto perecível</Label>
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
            <div className="grid gap-2">
              <Label>Fornecedores</Label>
              <div className="border rounded-lg p-3 space-y-2 max-h-32 overflow-y-auto">
                {fornecedores.length === 0 ? (
                  <p className="text-sm text-muted-foreground">
                    Nenhum fornecedor disponível
                  </p>
                ) : (
                  fornecedores.map((fornecedor) => (
                    <div key={fornecedor.id} className="flex items-center gap-2">
                      <Checkbox
                        id={`supplier-${fornecedor.id}`}
                        checked={formData.fornecedorIds.includes(fornecedor.id)}
                        onCheckedChange={() =>
                          handleFornecedorToggle(fornecedor.id)
                        }
                      />
                      <Label
                        htmlFor={`supplier-${fornecedor.id}`}
                        className="text-sm font-normal"
                      >
                        {fornecedor.nome}
                      </Label>
                    </div>
                  ))
                )}
              </div>
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
            <Button onClick={handleSave} disabled={saving}>
              {saving ? (
                <Loader2 className="w-4 h-4 animate-spin mr-2" />
              ) : null}
              Salvar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
