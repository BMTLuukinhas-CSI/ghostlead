# GhostLead 👻

CRM em português com Java 21 / Spring Boot 4.1.1, PostgreSQL e React + TypeScript. Interface responsiva com uma identidade leve e um fantasma amigável.

## O que está implementado

- Cadastro, login e logout com senha BCrypt, sessão HTTP-only e proteção CSRF.
- Espaço pessoal por conta: empresas, leads e indicadores isolados no servidor.
- Empresas: criação, edição, busca e exclusão protegida quando há leads vinculados.
- Leads: CRUD, telefone, anotações, valor da oportunidade e vínculo com empresa.
- Pipeline: Novos → Em contato → Proposta → Ganhos / Perdidos; alteração de etapa pelo menu do cartão.
- Busca por nome/e-mail, filtros por empresa/etapa e paginação no servidor.
- Dashboard calculado do banco: total de leads/empresas, valor em aberto, valor ganho e conversão entre negociações encerradas.
- Interface com estados vazios, carregamento, erros, confirmação de exclusão e navegação por teclado.
- Migrações Flyway, testes de regressão/isolamento, Docker Compose e CI.

**Escopo:** MVP funcional para um usuário por espaço. Não inclui cobrança, convites de equipe, envio de mensagens, recuperação de senha por e-mail, verificação de e-mail ou automações. “Valor conquistado” é a soma de oportunidades marcadas como ganhas, não uma confirmação de recebimento financeiro.

## Rodar com Docker

Requisitos: Docker Engine/Desktop e Compose v2.

```bash
cp .env.example .env
# Edite .env e defina uma senha forte e exclusiva para DB_PASSWORD.
docker compose up --build -d
```

Acesse **http://localhost:8088** e crie sua conta. Não existe usuário/senha padrão. O banco permanece no volume `ghostlead-data`; `docker compose down` mantém os dados. Não use `down -v` se quiser preservá-los.

```bash
docker compose logs -f api
docker compose down
```

O backend e o banco não expõem portas públicas no Compose; a interface é publicada apenas em `127.0.0.1`. Para produção, coloque um proxy HTTPS na frente, defina `COOKIE_SECURE=true` e configure o domínio. As sessões ficam em memória: reiniciar a API exige login novamente. Não escale horizontalmente sem armazenamento compartilhado de sessões e rate limiting.

## Desenvolvimento local

Requisitos: **JDK 21 completo**, Node 22.12+ e PostgreSQL 17.

Crie um banco e usuário `ghostlead` no PostgreSQL. No terminal do backend:

```bash
cd backend
export DB_PASSWORD='sua-senha-local'
# Opcionais: DB_URL=jdbc:postgresql://localhost:5432/ghostlead e DB_USER=ghostlead
./mvnw spring-boot:run
```

Em outro terminal:

```bash
cd frontend
npm ci
npm run dev
```

Abra o endereço informado pelo Vite (normalmente `http://localhost:5173`). `/api` é encaminhado para `localhost:8080`, preservando a mesma origem para sessão e CSRF. No Windows, use `mvnw.cmd` e `$env:DB_PASSWORD` no PowerShell.

## Verificações

```bash
cd backend
./mvnw verify
cd ../frontend
npm ci
npm run build
```

Os testes usuais usam H2 isolado e não precisam de uma instância PostgreSQL. A CI também executa o teste de inicialização/migração contra PostgreSQL 17, com `ddl-auto=validate` e Flyway habilitado. Veja [docs/validation.md](docs/validation.md) para o resultado da validação desta entrega.

## Atualizar um banco antigo

**Faça backup e ensaie a migração em uma cópia antes de atualizar um banco com dados reais.** O projeto original usava `ddl-auto=update`; agora o Flyway controla o esquema. As migrações suportam o esquema original do repositório, não alterações manuais desconhecidas.

As empresas existentes recebem `owner_id` nulo. Isso é intencional: elas e seus leads ficam inacessíveis pela API até uma pessoa administradora verificar o dono e associá-las. Nenhuma conta nova ganha acesso aos dados antigos automaticamente. Procedimento em [docs/migration.md](docs/migration.md).

## API

- `GET /api/health` — verificação básica de disponibilidade.
- `GET /api/auth/csrf` — token e nome do cabeçalho exigido em alteraçõe