import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Package, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { toast } from "@/hooks/use-toast";
import { authService } from "@/services/auth";

export default function Login() {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  
  // Login form
  const [loginEmail, setLoginEmail] = useState("");
  const [loginSenha, setLoginSenha] = useState("");
  
  // Registro form
  const [registerNome, setRegisterNome] = useState("");
  const [registerEmail, setRegisterEmail] = useState("");
  const [registerDocumento, setRegisterDocumento] = useState("");
  const [registerSenha, setRegisterSenha] = useState("");
  const [registerConfirmarSenha, setRegisterConfirmarSenha] = useState("");

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!loginEmail || !loginSenha) {
      toast({ title: "Preencha todos os campos", variant: "destructive" });
      return;
    }

    try {
      setIsLoading(true);
      await authService.login({ email: loginEmail, senha: loginSenha });
      toast({ title: "Login realizado com sucesso!" });
      navigate("/");
    } catch (error: any) {
      toast({ 
        title: "Erro ao fazer login", 
        description: error.message || "Verifique suas credenciais",
        variant: "destructive" 
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!registerNome || !registerEmail || !registerDocumento || !registerSenha) {
      toast({ title: "Preencha todos os campos", variant: "destructive" });
      return;
    }

    if (registerSenha !== registerConfirmarSenha) {
      toast({ title: "As senhas não coincidem", variant: "destructive" });
      return;
    }

    try {
      setIsLoading(true);
      await authService.registro({
        nome: registerNome,
        email: registerEmail,
        documento: registerDocumento,
        senha: registerSenha,
      });
      toast({ title: "Conta criada com sucesso!" });
      navigate("/");
    } catch (error: any) {
      toast({ 
        title: "Erro ao criar conta", 
        description: error.message || "Tente novamente",
        variant: "destructive" 
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-slate-800 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {/* Logo */}
        <div className="flex items-center justify-center gap-3 mb-8">
          <div className="w-12 h-12 rounded-xl bg-primary flex items-center justify-center">
            <Package className="w-7 h-7 text-primary-foreground" />
          </div>
          <span className="text-3xl font-bold text-white">gestock</span>
        </div>

        <Card className="border-slate-700 bg-slate-800/50 backdrop-blur">
          <CardHeader className="text-center">
            <CardTitle className="text-white text-xl">Bem-vindo</CardTitle>
            <CardDescription className="text-slate-400">
              Faça login ou crie sua conta
            </CardDescription>
          </CardHeader>
          <CardContent>
            <Tabs defaultValue="login" className="w-full">
              <TabsList className="grid w-full grid-cols-2 bg-slate-700/50">
                <TabsTrigger value="login" className="data-[state=active]:bg-primary">
                  Login
                </TabsTrigger>
                <TabsTrigger value="register" className="data-[state=active]:bg-primary">
                  Criar Conta
                </TabsTrigger>
              </TabsList>
              
              {/* Login Tab */}
              <TabsContent value="login">
                <form onSubmit={handleLogin} className="space-y-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="login-email" className="text-slate-200">Email</Label>
                    <Input
                      id="login-email"
                      type="email"
                      placeholder="seu@email.com"
                      value={loginEmail}
                      onChange={(e) => setLoginEmail(e.target.value)}
                      className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                      disabled={isLoading}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="login-senha" className="text-slate-200">Senha</Label>
                    <Input
                      id="login-senha"
                      type="password"
                      placeholder="••••••••"
                      value={loginSenha}
                      onChange={(e) => setLoginSenha(e.target.value)}
                      className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                      disabled={isLoading}
                    />
                  </div>
                  <Button type="submit" className="w-full" disabled={isLoading}>
                    {isLoading ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Entrando...
                      </>
                    ) : (
                      "Entrar"
                    )}
                  </Button>
                </form>
              </TabsContent>

              {/* Register Tab */}
              <TabsContent value="register">
                <form onSubmit={handleRegister} className="space-y-4 mt-4">
                  <div className="space-y-2">
                    <Label htmlFor="register-nome" className="text-slate-200">Nome</Label>
                    <Input
                      id="register-nome"
                      type="text"
                      placeholder="Seu nome completo"
                      value={registerNome}
                      onChange={(e) => setRegisterNome(e.target.value)}
                      className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                      disabled={isLoading}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="register-email" className="text-slate-200">Email</Label>
                    <Input
                      id="register-email"
                      type="email"
                      placeholder="seu@email.com"
                      value={registerEmail}
                      onChange={(e) => setRegisterEmail(e.target.value)}
                      className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                      disabled={isLoading}
                    />
                  </div>
                  <div className="space-y-2">
                    <Label htmlFor="register-documento" className="text-slate-200">CPF/CNPJ</Label>
                    <Input
                      id="register-documento"
                      type="text"
                      placeholder="000.000.000-00"
                      value={registerDocumento}
                      onChange={(e) => setRegisterDocumento(e.target.value)}
                      className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                      disabled={isLoading}
                    />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                      <Label htmlFor="register-senha" className="text-slate-200">Senha</Label>
                      <Input
                        id="register-senha"
                        type="password"
                        placeholder="••••••••"
                        value={registerSenha}
                        onChange={(e) => setRegisterSenha(e.target.value)}
                        className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                        disabled={isLoading}
                      />
                    </div>
                    <div className="space-y-2">
                      <Label htmlFor="register-confirmar" className="text-slate-200">Confirmar</Label>
                      <Input
                        id="register-confirmar"
                        type="password"
                        placeholder="••••••••"
                        value={registerConfirmarSenha}
                        onChange={(e) => setRegisterConfirmarSenha(e.target.value)}
                        className="bg-slate-700/50 border-slate-600 text-white placeholder:text-slate-500"
                        disabled={isLoading}
                      />
                    </div>
                  </div>
                  <Button type="submit" className="w-full" disabled={isLoading}>
                    {isLoading ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Criando conta...
                      </>
                    ) : (
                      "Criar Conta"
                    )}
                  </Button>
                </form>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>

        <p className="text-center text-slate-500 text-sm mt-6">
          Sistema de Gestão de Estoques
        </p>
      </div>
    </div>
  );
}

