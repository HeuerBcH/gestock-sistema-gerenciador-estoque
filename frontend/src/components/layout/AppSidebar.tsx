import {
  Package,
  Users,
  Warehouse,
  FileText,
  ShoppingCart,
  TrendingUp,
  Bell,
  ArrowLeftRight,
  Repeat2,
  Calendar,
  LogOut,
  ChevronLeft,
} from "lucide-react";
import { NavLink } from "@/components/NavLink";
import { cn } from "@/lib/utils";
import { useState } from "react";
import { Button } from "@/components/ui/button";
import { authService } from "@/services/auth";

const menuItems = [
  { title: "Estoques", url: "/", icon: Warehouse },
  { title: "Produtos", url: "/produtos", icon: Package },
  { title: "Fornecedores", url: "/fornecedores", icon: Users },
  { title: "Cotações", url: "/cotacoes", icon: FileText },
  { title: "Pedidos", url: "/pedidos", icon: ShoppingCart },
  { title: "Ponto de Ressuprimento", url: "/ressuprimento", icon: TrendingUp },
  { title: "Alertas", url: "/alertas", icon: Bell },
  { title: "Movimentações", url: "/movimentacoes", icon: ArrowLeftRight },
  { title: "Transferências", url: "/transferencias", icon: Repeat2 },
  { title: "Reservas", url: "/reservas", icon: Calendar },
];

export function AppSidebar() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <aside
      className={cn(
        "h-screen bg-sidebar sticky top-0 flex flex-col transition-all duration-300 ease-in-out",
        collapsed ? "w-16" : "w-64"
      )}
    >
      {/* Logo */}
      <div className="flex items-center justify-between h-16 px-4 border-b border-sidebar-border">
        {!collapsed && (
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-sidebar-primary flex items-center justify-center">
              <Package className="w-5 h-5 text-sidebar-primary-foreground" />
            </div>
            <span className="font-bold text-lg text-sidebar-foreground">
              gestock
            </span>
          </div>
        )}
        <Button
          variant="ghost"
          size="icon"
          className={cn(
            "text-sidebar-foreground hover:bg-sidebar-accent",
            collapsed && "mx-auto"
          )}
          onClick={() => setCollapsed(!collapsed)}
        >
          <ChevronLeft
            className={cn(
              "w-5 h-5 transition-transform",
              collapsed && "rotate-180"
            )}
          />
        </Button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 py-4 overflow-y-auto">
        <ul className="space-y-1 px-2">
          {menuItems.map((item) => (
            <li key={item.title}>
              <NavLink
                to={item.url}
                end={item.url === "/"}
                className={cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-sidebar-foreground transition-all duration-200",
                  collapsed && "justify-center px-2"
                )}
                activeClassName="bg-sidebar-accent text-sidebar-primary font-medium"
              >
                <item.icon className="w-5 h-5 shrink-0" />
                {!collapsed && <span className="text-sm">{item.title}</span>}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>

      {/* Footer */}
      <div className="border-t border-sidebar-border p-2 space-y-1">
        <button
          onClick={() => authService.logout()}
          className={cn(
            "w-full flex items-center gap-3 px-3 py-2.5 rounded-lg text-sidebar-foreground/80 hover:bg-sidebar-accent hover:text-status-inactive transition-all duration-200",
            collapsed && "justify-center px-2"
          )}
        >
          <LogOut className="w-5 h-5 shrink-0" />
          {!collapsed && <span className="text-sm">Sair</span>}
        </button>
      </div>
    </aside>
  );
}
