import { useState, useEffect } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  ArrowLeftRight,
  ArrowUpRight,
  ArrowDownLeft,
  Loader2,
} from "lucide-react";
import { StatCard } from "@/components/shared/StatCard";
import { toast } from "@/hooks/use-toast";
import { movimentacoesService } from "@/services/movimentacoes";
import { produtosService } from "@/services/produtos";
import { estoquesService } from "@/services/estoques";
import type {
  Movimentacao,
  MovimentacaoTotais,
  Produto,
  Estoque,
  TipoMovimentacao,
} from "@/types/entities";
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
import { Textarea } from "@/components/ui/textarea";

export default function Movimentacoes() {
  const [data, setData] = useState<Movimentacao[]>([]);
  const [totais, setTotais] = useState<MovimentacaoTotais | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("all");

  // Modal state
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [estoques, setEstoques] = useState<Estoque[]>([]);
  const [formData, setFormData] = useState({
    produtoId: "",
    estoqueId: "",
    quantidade: "",
    tipo: "ENTRADA" as TipoMovimentacao,
    motivo: "",
    responsavel: "",
  });

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [movimentacoes, totaisData] = await Promise.all([
        movimentacoesService.pesquisar(),
        movimentacoesService.obterTotais(),
      ]);
      setData(movimentacoes || []);
      setTotais(totaisData);
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

  const carregarDadosModal = async () => {
    try {
      const [produtosData, estoquesData] = await Promise.all([
        produtosService.pesquisar({ status: "ATIVO" }),
        estoquesService.pesquisar({ status: "ATIVO" }),
      ]);
      setProdutos(produtosData || []);
      setEstoques(estoquesData || []);
    } catch (error: any) {
      toast({
        title: "Erro ao carregar dados",
        description: error.message,
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  const handleOpenDialog = () => {
    setFormData({
      produtoId: "",
      estoqueId: "",
      quantidade: "",
      tipo: "ENTRADA",
      motivo: "",
      responsavel: "",
    });
    carregarDadosModal();
    setIsDialogOpen(true);
  };

  const handleSave = async () => {
    if (
      !formData.produtoId ||
      !formData.estoqueId ||
      !formData.quantidade ||
      !formData.motivo ||
      !formData.responsavel
    ) {
      toast({
        title: "Preencha todos os campos obrigatórios",
        variant: "destructive",
      });
      return;
    }

    try {
      setSaving(true);
      await movimentacoesService.registrar({
        produtoId: Number(formData.produtoId),
        estoqueId: Number(formData.estoqueId),
        quantidade: Number(formData.quantidade),
        tipo: formData.tipo,
        motivo: formData.motivo,
        responsavel: formData.responsavel,
      });

      toast({
        title:
          formData.tipo === "ENTRADA"
            ? "Entrada registrada com sucesso!"
            : "Saída registrada com sucesso!",
      });
      setIsDialogOpen(false);
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao registrar movimentação",
        description: error.message,
        variant: "destructive",
      });
    } finally {
      setSaving(false);
    }
  };

  const filteredData = data.filter((movement) => {
    return (
      filter === "all" ||
      (filter === "entrada" && movement.tipo === "ENTRADA") ||
      (filter === "saida" && movement.tipo === "SAIDA")
    );
  });

  const columns = [
    {
      key: "dataHora",
      label: "Data/Hora",
      render: (item: Movimentacao) =>
        new Date(item.dataHora).toLocaleString("pt-BR"),
    },
    {
      key: "tipo",
      label: "Tipo",
      render: (item: Movimentacao) => (
        <div className="flex items-center gap-2">
          {item.tipo === "ENTRADA" ? (
            <ArrowDownLeft className="w-4 h-4 text-status-active" />
          ) : (
            <ArrowUpRight className="w-4 h-4 text-status-critical" />
          )}
          <Badge variant={item.tipo === "ENTRADA" ? "active" : "inactive"}>
            {item.tipo === "ENTRADA" ? "Entrada" : "Saída"}
          </Badge>
        </div>
      ),
    },
    {
      key: "produtoNome",
      label: "Produto",
      render: (item: Movimentacao) => item.produtoNome,
    },
    {
      key: "quantidade",
      label: "Quantidade",
      render: (item: Movimentacao) => (
        <span
          className={
            item.tipo === "ENTRADA" ? "text-status-active" : "text-status-critical"
          }
        >
          {item.tipo === "ENTRADA" ? "+" : "-"}
          {item.quantidade}
        </span>
      ),
    },
    { key: "motivo", label: "Motivo" },
    {
      key: "estoqueNome",
      label: "Estoque",
      render: (item: Movimentacao) => item.estoqueNome,
    },
    { key: "responsavel", label: "Responsável" },
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
        title="Movimentações"
        description="Registre e acompanhe entradas e saídas de produtos"
        icon={ArrowLeftRight}
        action={{
          label: "Nova Movimentação",
          onClick: handleOpenDialog,
        }}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Total de Movimentações"
          value={totais?.totalMovimentacoes || 0}
          icon={ArrowLeftRight}
          variant="primary"
        />
        <StatCard
          title="Total de Entradas"
          value={totais?.totalEntradas || 0}
          icon={ArrowDownLeft}
          variant="success"
        />
        <StatCard
          title="Total de Saídas"
          value={totais?.totalSaidas || 0}
          icon={ArrowUpRight}
          variant="danger"
        />
      </div>

      <DataTable
        data={filteredData}
        columns={columns}
        filterOptions={[
          { value: "all", label: "Histórico" },
          { value: "entrada", label: "Entradas" },
          { value: "saida", label: "Saídas" },
        ]}
        filterValue={filter}
        onFilterChange={setFilter}
        filterLabel="Tipo"
      />

      {/* Modal de Nova Movimentação */}
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle>Nova Movimentação</DialogTitle>
            <DialogDescription>
              Registre uma entrada ou saída de produtos no estoque.
            </DialogDescription>
          </DialogHeader>

          <div className="grid gap-4 py-4">
            {/* Tipo de Movimentação */}
            <div className="grid gap-2">
              <Label htmlFor="tipo">Tipo de Movimentação</Label>
              <div className="flex gap-2">
                <Button
                  type="button"
                  variant={formData.tipo === "ENTRADA" ? "default" : "outline"}
                  className={
                    formData.tipo === "ENTRADA"
                      ? "flex-1 bg-status-active hover:bg-status-active/90"
                      : "flex-1"
                  }
                  onClick={() => setFormData({ ...formData, tipo: "ENTRADA" })}
                >
                  <ArrowDownLeft className="w-4 h-4 mr-2" />
                  Entrada
                </Button>
                <Button
                  type="button"
                  variant={formData.tipo === "SAIDA" ? "default" : "outline"}
                  className={
                    formData.tipo === "SAIDA"
                      ? "flex-1 bg-status-critical hover:bg-status-critical/90"
                      : "flex-1"
                  }
                  onClick={() => setFormData({ ...formData, tipo: "SAIDA" })}
                >
                  <ArrowUpRight className="w-4 h-4 mr-2" />
                  Saída
                </Button>
              </div>
            </div>

            {/* Produto */}
            <div className="grid gap-2">
              <Label htmlFor="produto">Produto</Label>
              <Select
                value={formData.produtoId}
                onValueChange={(value) =>
                  setFormData({ ...formData, produtoId: value })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um produto" />
                </SelectTrigger>
                <SelectContent>
                  {produtos.map((produto) => (
                    <SelectItem key={produto.id} value={String(produto.id)}>
                      {produto.codigo} - {produto.nome}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Estoque */}
            <div className="grid gap-2">
              <Label htmlFor="estoque">Estoque</Label>
              <Select
                value={formData.estoqueId}
                onValueChange={(value) =>
                  setFormData({ ...formData, estoqueId: value })
                }
              >
                <SelectTrigger>
                  <SelectValue placeholder="Selecione um estoque" />
                </SelectTrigger>
                <SelectContent>
                  {estoques.map((estoque) => (
                    <SelectItem key={estoque.id} value={String(estoque.id)}>
                      {estoque.nome}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Quantidade */}
            <div className="grid gap-2">
              <Label htmlFor="quantidade">Quantidade</Label>
              <Input
                id="quantidade"
                type="number"
                min="1"
                placeholder="Digite a quantidade"
                value={formData.quantidade}
                onChange={(e) =>
                  setFormData({ ...formData, quantidade: e.target.value })
                }
              />
            </div>

            {/* Responsável */}
            <div className="grid gap-2">
              <Label htmlFor="responsavel">Responsável</Label>
              <Input
                id="responsavel"
                placeholder="Nome do responsável"
                value={formData.responsavel}
                onChange={(e) =>
                  setFormData({ ...formData, responsavel: e.target.value })
                }
              />
            </div>

            {/* Motivo */}
            <div className="grid gap-2">
              <Label htmlFor="motivo">Motivo</Label>
              <Textarea
                id="motivo"
                placeholder="Descreva o motivo da movimentação"
                value={formData.motivo}
                onChange={(e) =>
                  setFormData({ ...formData, motivo: e.target.value })
                }
                rows={3}
              />
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
              ) : formData.tipo === "ENTRADA" ? (
                <ArrowDownLeft className="w-4 h-4 mr-2" />
              ) : (
                <ArrowUpRight className="w-4 h-4 mr-2" />
              )}
              {formData.tipo === "ENTRADA" ? "Registrar Entrada" : "Registrar Saída"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
