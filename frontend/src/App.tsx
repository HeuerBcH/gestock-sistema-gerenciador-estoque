import { Toaster } from "@/components/ui/toaster";
import { Toaster as Sonner } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { MainLayout } from "@/components/layout/MainLayout";
import { ProtectedRoute } from "@/components/ProtectedRoute";
import Login from "./pages/Login";
import Fornecedores from "./pages/Fornecedores";
import Produtos from "./pages/Produtos";
import Estoques from "./pages/Estoques";
import Pedidos from "./pages/Pedidos";
import Alertas from "./pages/Alertas";
import Movimentacoes from "./pages/Movimentacoes";
import Cotacoes from "./pages/Cotacoes";
import Ressuprimento from "./pages/Ressuprimento";
import Transferencias from "./pages/Transferencias";
import Reservas from "./pages/Reservas";
import NotFound from "./pages/NotFound";

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 30000,
    },
  },
});

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster />
      <Sonner />
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<Login />} />

          {/* Protected routes */}
          <Route element={<ProtectedRoute />}>
            <Route element={<MainLayout />}>
              <Route path="/" element={<Estoques />} />
              <Route path="/produtos" element={<Produtos />} />
              <Route path="/fornecedores" element={<Fornecedores />} />
              <Route path="/cotacoes" element={<Cotacoes />} />
              <Route path="/pedidos" element={<Pedidos />} />
              <Route path="/ressuprimento" element={<Ressuprimento />} />
              <Route path="/alertas" element={<Alertas />} />
              <Route path="/movimentacoes" element={<Movimentacoes />} />
              <Route path="/transferencias" element={<Transferencias />} />
              <Route path="/reservas" element={<Reservas />} />
            </Route>
          </Route>

          <Route path="*" element={<NotFound />} />
        </Routes>
      </BrowserRouter>
    </TooltipProvider>
  </QueryClientProvider>
);

export default App;
