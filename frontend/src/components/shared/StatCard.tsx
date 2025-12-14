import { LucideIcon } from "lucide-react";
import { cn } from "@/lib/utils";

interface StatCardProps {
  title: string;
  value: string | number;
  icon: LucideIcon;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  variant?: "default" | "primary" | "success" | "warning" | "danger";
}

const variantStyles = {
  default: "bg-card",
  primary: "bg-primary/5 border-primary/20",
  success: "bg-status-active-bg border-status-active/20",
  warning: "bg-status-warning-bg border-status-warning/20",
  danger: "bg-status-critical-bg border-status-critical/20",
};

const iconStyles = {
  default: "bg-muted text-muted-foreground",
  primary: "bg-primary/10 text-primary",
  success: "bg-status-active/10 text-status-active",
  warning: "bg-status-warning/10 text-status-warning",
  danger: "bg-status-critical/10 text-status-critical",
};

export function StatCard({ title, value, icon: Icon, trend, variant = "default" }: StatCardProps) {
  return (
    <div
      className={cn(
        "rounded-xl border p-5 transition-all duration-200 hover:shadow-md animate-slide-up",
        variantStyles[variant]
      )}
    >
      <div className="flex items-start justify-between">
        <div className="space-y-2">
          <p className="text-sm text-muted-foreground font-medium">{title}</p>
          <p className="text-3xl font-bold text-foreground">{value}</p>
          {trend && (
            <p
              className={cn(
                "text-sm font-medium",
                trend.isPositive ? "text-status-active" : "text-status-critical"
              )}
            >
              {trend.isPositive ? "+" : "-"}{Math.abs(trend.value)}%
              <span className="text-muted-foreground ml-1">vs mês anterior</span>
            </p>
          )}
        </div>
        <div className={cn("p-3 rounded-lg", iconStyles[variant])}>
          <Icon className="w-6 h-6" />
        </div>
      </div>
    </div>
  );
}
