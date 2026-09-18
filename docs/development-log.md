# GhostLead — Diário de Desenvolvimento

## Sobre o projeto

O GhostLead é um SaaS desenvolvido do zero com foco em gerenciamento de leads e clientes.

O objetivo é transformar o projeto em uma aplicação real, aplicando boas práticas de desenvolvimento, arquitetura de software, testes automatizados e conceitos de segurança de aplicações.

## Stack atual

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Git / GitHub

## Arquitetura atual

O backend segue uma separação básica de responsabilidades:

- `controller` — recebe e responde às requisições HTTP
- `service` — concentra as regras de negócio
- `repository` — acesso aos dados através do Spring Data JPA
- `entity` — representação das entidades persistidas no banco
- `dto` — objetos utilizados para entrada de dados da API
- `exception` — exceções específicas da aplicação

## Objetivo do desenvolvimento

Construir o GhostLead de forma incremental, seguindo o processo:

**Entender → Implementar → Testar → Commitar → Push → Documentar**


## Dia 3 — Validação e testes de Leads

### O que foi desenvolvido

Neste dia foram ampliadas as validações da API de Leads utilizando Bean Validation.

Foram adicionadas validações para:

- Nome obrigatório
- Email obrigatório
- Email em formato válido
- Empresa obrigatória

Também foram criados testes automatizados utilizando:

- JUnit
- Spring Boot Test
- MockMvc

### Testes

Ao final da implementação:

**13 testes executados**
**13 testes aprovados**
**0 falhas**
**0 erros**

### Git

As alterações foram versionadas e enviadas para o GitHub.

Commit:

`2273337 test: validate required lead company`

O repositório ficou sincronizado com a branch `main`.

### Aprendizados

Durante esta etapa foi reforçada a importância de validar os dados recebidos pela API antes de processá-los.

Também foi praticado o uso de testes automatizados para garantir que alterações futuras não quebrem comportamentos existentes.

### Próximo objetivo

Continuar a evolução da qualidade da API, trabalhando em:

- Validação do PUT de Leads
- Tratamento global de exceções
- Padronização das respostas de erro
- Tratamento de UUID inválido
- Paginação e filtros
- Documentação da API
