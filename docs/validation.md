# Validação desta entrega

## Resultado neste ambiente

- `npm run build` (frontend): aprovado em 11/11/2026.
- `./mvnw -B verify` (backend): não executou. O ambiente não conseguiu resolver `repo.maven.apache.org` para baixar o parent Maven do Spring Boot; o erro ocorreu antes da compilação e não indica falha de teste do projeto.
- Teste E2E Playwright: existe um teste cobrindo cadastro, empresas, leads, pipeline, filtros, sessão e layout móvel. A execução final precisa ocorrer no CI, que inicia a aplicação completa com PostgreSQL e Chromium.

## Verificações automáticas no GitHub

O workflow `.github/workflows/ci.yml` executa backend, build do frontend, migração em PostgreSQL, migração de dados legados e fluxo E2E no navegador em cada push e pull request.
