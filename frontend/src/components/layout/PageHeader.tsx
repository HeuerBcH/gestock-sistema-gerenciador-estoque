import { LucideIcon, Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { ReactNode } from "react";

interface PageHeaderProps {
  title: string;
  description?: string;
  icon?: LucideIcon;
  action?: {
    label: string;
    onClick: () => void;
    icon?: ReactNode;
    disabled?: boolean;
  };
}

export function PageHeader({ title, description, icon: Icon, action }: PageHeaderProps) {
  return (
    <div className="flex items-center justify-between mb-6">
      <div className="flex items-center gap-4">
        {Icon && (
          <div className="w-12 h-12 rounded-xl bg-primary/10 flex items-center justify-center">
            <Icon className="w-6 h-6 text-primary" />
          </div>
        )}
        <div>
          <h1 className="text-2xl font-bold text-foreground">{title}</h1>
          {description && (
            <p className="text-muted-foreground mt-0.5">{description}</p>
          )}
        </div>
      </div>
      {action && (
        <Button onClick={action.onClick} disabled={action.disabled}>
          {action.icon || <Plus className="w-4 h-4" />}
          {action.label}
        </Button>
      )}
    </div>
  );
}
