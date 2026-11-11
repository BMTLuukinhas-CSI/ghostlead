# Migração do banco original

1. Gere um backup PostgreSQL e teste sua restauração em uma instância separada.
2. Aponte `DB_URL`, `DB_USER` e `DB_PASSWORD` para essa cópia e suba a nova API.
3. O Flyway cria seu histórico (baseline 0 se necessário), reconhece as tabelas originais e aplica V1/V2. A aplicação valida o esquema antes de atender requisições.
4. Crie a conta que será dona dos dados pelo formulário de cadastro. Copie o UUID mostrado em Minha conta.
5. Um administrador deve revisar as empresas existentes e confirmar a titularidade **fora do fluxo público**. Só então vincule explicitamente cada empresa.

Exemplo para executar manualmente no banco, substituindo os UUIDs verificados:

```sql
BEGIN;
SELECT id, name, email, owner_id FROM companies WHERE id = 'UUID_DA_EMPRESA' FOR UPDATE;
SELECT id, name, email FROM users WHERE id = 'UUID_DA_CONTA';
UPDATE companies
   SET owner_id = 'UUID_DA_CONTA'
 WHERE id = 'UUID_DA_EMPRESA' AND owner_id IS NULL;
-- Confira a linha afetada e a titularidade antes de confirmar.
COMMIT;
```

O administrador deve verificar que a conta existe e que não há conflito de e-mail com outra empresa desse mesmo dono. Não há endpoint para reivindicar registros antigos. Os leads herdam o acesso da empresa vinculada. Não vincule todas as empresas à primeira conta automaticamente.

A V2 mantém as tabelas/dados e o relacionamento legado `users.company_id`, mas passa a usar `companies.owner_id` para autorização. Os dados antigos recebem etapa `NEW`, valor zero e data da migração, pois o esquema anterior não armazenava esses atributos.

Em caso de falha, interrompa a atualização e investigue em uma cópia. Para retornar ao estado anterior, restaure o backup e a versão anterior da aplicação; não execute uma reversão destrutiva improvisada em produção.
