import { useState, useEffect, useMemo } from "react";
import { PageHeader } from "@/components/layout/PageHeader";
import { DataTable } from "@/components/shared/DataTable";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { StatCard } from "@/components/shared/StatCard";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
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
import { Repeat2, ArrowRight, Package, Warehouse, Loader2, Plus, AlertTriangle, ArrowRightLeft } from "lucide-react";
import { toast } from "@/hooks/use-toast";
import { transferenciasService, TransferenciaRegistrarRequest } from "@/services/transferencias";
import { produtosService } from "@/services/produtos";
import { estoquesService } from "@/services/estoques";
import type { Transferencia, TransferenciaTotais, Produto, Estoque } from "@/types/entities";

export default function Transferencias() {
  const [data, setData] = useState<Transferencia[]>([]);
  const [totais, setTotais] = useState<TransferenciaTotais | null>(null);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [saving, setSaving] = useState(false);
  const [produtos, setProdutos] = useState<Produto[]>([]);
  const [estoques, setEstoques] = useState<Estoque[]>([]);

  // Form state
  const [produtoId, setProdutoId] = useState<string>("");
  const [estoqueOrigemId, setEstoqueOrigemId] = useState<string>("");
  const [estoqueDestinoId, setEstoqueDestinoId] = useState<string>("");
  const [quantidade, setQuantidade] = useState<string>("");
  const [responsavel, setResponsavel] = useState<string>("");
  const [motivo, setMotivo] = useState<string>("");

  const carregarDados = async () => {
    try {
      setLoading(true);
      const [transferencias, totaisData] = await Promise.all([
        transferenciasService.pesquisar(),
        transferenciasService.obterTotais(),
      ]);
      setData(transferencias || []);
      setTotais(totaisData);
    } catch (error: any) {
      toast({ title: "Erro ao carregar dados", description: error.message, variant: "destructive" });
    } finally {
      setLoading(false);
    }
  };

  const carregarProdutosEEstoques = async () => {
    try {
      const [produtosData, estoquesData] = await Promise.all([
        produtosService.pesquisar(),
        estoquesService.pesquisar(),
      ]);
      setProdutos(produtosData.filter(p => p.status === "ATIVO") || []);
      setEstoques(estoquesData.filter(e => e.status === "ATIVO") || []);
    } catch (error: any) {
      toast({ title: "Erro ao carregar dados", description: error.message, variant: "destructive" });
    }
  };

  useEffect(() => {
    carregarDados();
  }, []);

  // Calcular informações do estoque de origem selecionado
  const estoqueOrigemInfo = useMemo(() => {
    if (!estoqueOrigemId) return null;
    return estoques.find(e => e.id === Number(estoqueOrigemId));
  }, [estoqueOrigemId, estoques]);

  // Calcular informações do estoque de destino selecionado
  const estoqueDestinoInfo = useMemo(() => {
    if (!estoqueDestinoId) return null;
    return estoques.find(e => e.id === Number(estoqueDestinoId));
  }, [estoqueDestinoId, estoques]);

  // Quantidade disponível do produto no estoque de origem
  // (como não temos essa informação diretamente, vamos mostrar a quantidade atual do estoque)
  const quantidadeNum = Number(quantidade) || 0;

  // Validações
  const mesmoEstoque = estoqueOrigemId && estoqueDestinoId && estoqueOrigemId === estoqueDestinoId;
  const quantidadeExcedeOrigem = estoqueOrigemInfo && quantidadeNum > estoqueOrigemInfo.quantidadeAtual;
  const quantidadeExcedeDestino = estoqueDestinoInfo && quantidadeNum > estoqueDestinoInfo.capacidadeDisponivel;

  const handleOpenModal = () => {
    // Reset form
    setProdutoId("");
    setEstoqueOrigemId("");
    setEstoqueDestinoId("");
    setQuantidade("");
    setResponsavel("");
    setMotivo("");
    
    carregarProdutosEEstoques();
    setIsModalOpen(true);
  };

  const handleSave = async () => {
    // Validações
    if (!produtoId || !estoqueOrigemId || !estoqueDestinoId || !quantidade || !responsavel || !motivo) {
      toast({
        title: "Campos obrigatórios",
        description: "Preencha todos os campos para realizar a transferência.",
        variant: "destructive",
      });
      return;
    }

    if (mesmoEstoque) {
      toast({
        title: "Estoques iguais",
        description: "O estoque de origem e destino devem ser diferentes.",
        variant: "destructive",
      });
      return;
    }

    if (quantidadeNum <= 0) {
      toast({
        title: "Quantidade inválida",
        description: "A quantidade deve ser maior que zero.",
        variant: "destructive",
      });
      return;
    }

    if (quantidadeExcedeDestino) {
      toast({
        title: "Capacidade insuficiente",
        description: `O estoque de destino tem apenas ${estoqueDestinoInfo?.capacidadeDisponivel} unidades disponíveis.`,
        variant: "destructive",
      });
      return;
    }

    try {
      setSaving(true);
      const request: TransferenciaRegistrarRequest = {
        produtoId: Number(produtoId),
        estoqueOrigemId: Number(estoqueOrigemId),
        estoqueDestinoId: Number(estoqueDestinoId),
        quantidade: quantidadeNum,
        responsavel,
        motivo,
      };

      const response = await transferenciasService.registrar(request);
      
      toast({
        title: "Transferência realizada!",
        description: response.mensagem || `${quantidadeNum} unidades transferidas com sucesso.`,
      });

      setIsModalOpen(false);
      carregarDados();
    } catch (error: any) {
      toast({
        title: "Erro ao realizar transferência",
        description: error.message || "Ocorreu um erro ao processar a transferência.",
        variant: "destructive",
      });
    } finally {
      setSaving(false);
    }
  };

  const filteredData = data.filter((t) => {
    return (
      t.produtoNome.toLowerCase().includes(search.toLowerCase()) ||
      t.estoqueOrigemNome.toLowerCase().includes(search.toLowerCase()) ||
      t.estoqueDestinoNome.toLowerCase().includes(search.toLowerCase()) ||
      t.responsavel.toLowerCase().includes(search.toLowerCase())
    );
  });

  const columns = [
    {
      key: "dataHoraTransferencia",
      label: "Data/Hora",
      render: (item: Transferencia) => new Date(item.dataHoraTransferencia).toLocaleString("pt-BR"),
    },
    {
      key: "produtoNome",
      label: "Produto",
      render: (item: Transferencia) => (
        <div className="flex items-center gap-2">
          <Package className="w-4 h-4 text-muted-foreground" />
          <span>{item.produtoNome}</span>
        </div>
      ),
    },
    {
      key: "quantidade",
      label: "Quantidade",
      render: (item: Transferencia) => (
        <span className="font-medium">{item.quantidade} un</span>
      ),
    },
    {
      key: "route",
      label: "Origem → Destino",
      render: (item: Transferencia) => (
        <div className="flex items-center gap-2 text-sm">
          <div className="flex items-center gap-1">
            <Warehouse className="w-4 h-4 text-muted-foreground" />
            <span className="max-w-[120px] truncate" title={item.estoqueOrigemNome}>
              {item.estoqueOrigemNome}
            </span>
          </div>
          <ArrowRight className="w-4 h-4 text-primary" />
          <div className="flex items-center gap-1">
            <Warehouse className="w-4 h-4 text-muted-foreground" />
            <span className="max-w-[120px] truncate" title={item.estoqueDestinoNome}>
              {item.estoqueDestinoNome}
            </span>
          </div>
        </div>
      ),
    },
    { key: "responsavel", label: "Responsável" },
    {
      key: "motivo",
      label: "Motivo",
      render: (item: Transferencia) => (
        <Badge variant="secondary">{item.motivo}</Badge>
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
        title="Transferências"
        description="Acompanhe as transferências de produtos entre estoques"
        icon={Repeat2}
        action={{
          label: "Nova Transferência",
          onClick: handleOpenModal,
          icon: <Plus className="w-4 h-4" />,
        }}
      />

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Total de Transferências"
          value={totais?.totalTransferencias || 0}
          icon={Repeat2}
          variant="primary"
        />
        <StatCard
          title="Unidades Movidas"
          value={(totais?.unidadesMovidas || 0).toLocaleString("pt-BR")}
          icon={Package}
          variant="success"
        />
        <StatCard
          title="Produtos Distintos"
          value={totais?.produtosDistintos || 0}
          icon={Warehouse}
          variant="default"
        />
      </div>

      <DataTable
        data={filteredData}
        columns={columns}
        searchPlaceholder="Buscar por produto, estoque ou responsável..."
        searchValue={search}
        onSearchChange={setSearch}
      />

      {/* Modal de Nova Transferência */}
      <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
        <DialogContent className="sm:max-w-[500px]">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <ArrowRightLeft className="w-5 h-5 text-primary" />
              Nova Transferência
            </DialogTitle>
            <DialogDescription>
              Transfira produtos de um estoque para outro.
            </DialogDescription>
          </DialogHeader>

          <div className="space-y-4 py-4">
            {/* Produto */}
            <div className="space-y-2">
              <Label htmlFor="produto">Produto *</Label>
              <Select value={produtoId} onValueChange={setProdutoId}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o produto" />
                </SelectTrigger>
                <SelectContent>
                  {produtos.map((produto) => (
                    <SelectItem key={produto.id} value={String(produto.id)}>
                      <div className="flex items-center gap-2">
                        <Package className="w-4 h-4" />
                        <span>{produto.nome}</span>
                        <span className="text-xs text-muted-foreground">({produto.codigo})</span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            {/* Estoque Origem */}
            <div className="space-y-2">
              <Label htmlFor="estoqueOrigem">Estoque de Origem *</Label>
              <Select value={estoqueOrigemId} onValueChange={setEstoqueOrigemId}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o estoque de origem" />
                </SelectTrigger>
                <SelectContent>
                  {estoques.map((estoque) => (
                    <SelectItem key={estoque.id} value={String(estoque.id)}>
                      <div className="flex items-center justify-between w-full gap-4">
                        <span>{estoque.nome}</span>
                        <span className="text-xs text-muted-foreground">
                          {estoque.quantidadeAtual}/{estoque.capacidade} un
                        </span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {estoqueOrigemInfo && (
                <p className="text-xs text-muted-foreground">
                  Quantidade atual: <strong>{estoqueOrigemInfo.quantidadeAtual}</strong> unidades ({estoqueOrigemInfo.ocupacao}% ocupado)
                </p>
              )}
            </div>

            {/* Estoque Destino */}
            <div className="space-y-2">
              <Label htmlFor="estoqueDestino">Estoque de Destino *</Label>
              <Select value={estoqueDestinoId} onValueChange={setEstoqueDestinoId}>
                <SelectTrigger>
                  <SelectValue placeholder="Selecione o estoque de destino" />
                </SelectTrigger>
                <SelectContent>
                  {estoques
                    .filter(e => e.id !== Number(estoqueOrigemId))
                    .map((estoque) => (
                    <SelectItem key={estoque.id} value={String(estoque.id)}>
                      <div className="flex items-center justify-between w-full gap-4">
                        <span>{estoque.nome}</span>
                        <span className="text-xs text-muted-foreground">
                          Disp: {estoque.capacidadeDisponivel} un
                        </span>
                      </div>
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              {estoqueDestinoInfo && (
                <p className="text-xs text-muted-foreground">
                  Capacidade disponível: <strong>{estoqueDestinoInfo.capacidadeDisponivel}</strong> de {estoqueDestinoInfo.capacidade} unidades
                </p>
              )}
            </div>

            {/* Alerta de mesmo estoque */}
            {mesmoEstoque && (
              <div className="p-2 rounded bg-destructive/10 border border-destructive/20 text-destructive text-xs">
                <AlertTriangle className="w-3 h-3 inline mr-1" />
                Os estoques de origem e destino devem ser diferentes.
              </div>
            )}

            {/* Quantidade */}
            <div className="space-y-2">
              <Label htmlFor="quantidade">Quantidade *</Label>
              <Input
                id="quantidade"
                type="number"
                min="1"
                value={quantidade}
                onChange={(e) => setQuantidade(e.target.value)}
                placeholder="Quantidade a transferir"
              />
              {quantidadeExcedeOrigem && (
                <p className="text-xs text-destructive">
                  <AlertTriangle className="w-3 h-3 inline mr-1" />
                  Quantidade excede o disponível no estoque de origem ({estoqueOrigemInfo?.quantidadeAtual} un)
                </p>
              )}
              {quantidadeExcedeDestino && (
                <p className="text-xs text-destructive">
                  <AlertTriangle className="w-3 h-3 inline mr-1" />
                  Quantidade excede a capacidade disponível no destino ({estoqueDestinoInfo?.capacidadeDisponivel} un)
                </p>
              )}
            </div>

            {/* Responsável */}
            <div className="space-y-2">
              <Label htmlFor="responsavel">Responsável *</Label>
              <Input
                id="responsavel"
                value={responsavel}
                onChange={(e) => setResponsavel(e.target.value)}
                placeholder="Nome do responsável pela transferência"
              />
            </div>

            {/* Motivo */}
            <div className="space-y-2">
              <Label htmlFor="motivo">Motivo *</Label>
              <Textarea
                id="motivo"
                value={motivo}
                onChange={(e) => setMotivo(e.target.value)}
                placeholder="Descreva o motivo da transferência"
                rows={2}
              />
            </div>

            {/* Resumo da transferência */}
            {produtoId && estoqueOrigemId && estoqueDestinoId && quantidadeNum > 0 && (
              <div className="p-3 rounded-lg bg-primary/5 border border-primary/20">
                <p className="text-sm font-medium mb-2">Resumo da Transferência:</p>
                <div className="flex items-center gap-2 text-sm">
                  <Badge variant="outline">{quantidadeNum} un</Badge>
                  <span>de</span>
                  <span className="font-medium">{produtos.find(p => p.id === Number(produtoId))?.nome}</span>
                </div>
                <div className="flex items-center gap-2 mt-2 text-sm text-muted-foreground">
                  <Warehouse className="w-4 h-4" />
                  <span>{estoqueOrigemInfo?.nome}</span>
                  <ArrowRight className="w-4 h-4 text-primary" />
                  <Warehouse className="w-4 h-4" />
                  <span>{estoqueDestinoInfo?.nome}</span>
                </div>
              </div>
            )}
          </div>

          <DialogFooter>
            <Button variant="outline" onClick={() => setIsModalOpen(false)}>
              Cancelar
            </Button>
            <Button 
              onClick={handleSave} 
              disabled={
                saving || 
                !produtoId || 
                !estoqueOrigemId || 
                !estoqueDestinoId || 
                !quantidade || 
                !responsavel || 
                !motivo ||
                mesmoEstoque ||
                quantidadeExcedeDestino ||
                quantidadeNum <= 0
              }
            >
              {saving ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin mr-2" />
                  Transferindo...
                </>
              ) : (
                <>
                  <ArrowRightLeft className="w-4 h-4 mr-2" />
                  Realizar Transferência
                </>
              )}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
