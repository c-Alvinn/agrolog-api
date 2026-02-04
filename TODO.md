# AGROLOG API - PLANEJAMENTO DO PROJETO

## ✅ Fase 1: Configuração e Usuários (Concluída)
- [x] Configuração inicial do projeto (Spring Boot, dependências).
- [x] Implementação das entidades básicas (User, Company, Carrier).
- [x] Sistema de Autenticação JWT.
- [x] Criação de Roles (ADMIN, MANAGER, CARRIER, SCALE_OPERATOR, GATE_KEEPER, DRIVER).
- [x] Endpoint de registro para DRIVER (acesso público).
- [x] Endpoint de registro para Usuários Internos (ADMIN, MANAGER).
- [x] Endpoint de registro para Usuários Carrier (ADMIN, CARRIER).

## ✅ Fase 2: Módulos Base (Company e Carrier) (Concluída)
- [x] CRUD para `Company` (Regras de acesso e escopo: ADMIN, MANAGER).
- [x] CRUD para `Carrier` (Regras de acesso e escopo: ADMIN, CARRIER).

## ✅ Fase 3: Módulo Filial (Branch) (Concluída)
- [x] Entidade `Branch` com vínculo à `Company`.
- [x] CRUD para `Branch`.
- [x] Implementação do escopo de `MANAGER` (só gerencia filiais da sua Company).
- [x] Regras de acesso e leitura para `SCALE_OPERATOR` e `GATE_KEEPER`.

## ✅ Fase 4: Agendamento e Fila (Schedule) (Concluída)
- [x] Entidade `Schedule` com `Branch`, `Driver`, `Carrier` e `QueueStatus`.
- [x] Enum `QueueStatus` (`SCHEDULED`, `IN_SERVICE`, `COMPLETED`, `CANCELED`).
- [x] Criação do endpoint `POST /schedules`.
- [x] Implementação da lógica de **Escopo na Criação** para todas as Roles.
- [x] Lógica de atribuição da **posição na fila** (`queuePosition`) no método `create`.
- [x] Implementação dos endpoints de Leitura (`GET /schedules` e `GET /schedules/{id}`) com filtro de escopo.
- [x] Implementação das transições de status (`PATCH /in-service`, `/completed`, `/cancel`).
- [x] Lógica de **reordenação da fila** ao mover para `IN_SERVICE` ou `CANCELED`.
- [x] Implementação do endpoint de exclusão (`DELETE /schedules/{id}`) restrito a `ADMIN`.
- [x] Atualização do `SecurityConfig` e `SchedulingController`.

## ✅ Fase 5: Relatórios e Dashboards (Concluída)

### F5.1 - Estrutura de Relatórios
- [x] Criação de DTOs de métricas (`QueueStatusReportDTO`) e base do `ReportingService`.

### F5.2 - Relatório de Performance (PDF)
- [x] Implementar exportação de agendamentos `IN_SERVICE` ou `COMPLETED` em formato PDF.
- [x] Criar Enum `ReportPeriod` para gestão de filtros temporais (Hoje, Ontem, 7 dias).
- [x] Implementar validação de escopo rigorosa (apenas dados da `Company` do utilizador logado).
- [x] Formatação de tabela PDF com tradução para Português, nome da empresa e data de geração no título/ficheiro.

### F5.3 - Relatório de Status da Fila
- [x] Implementar método para retornar a contagem de agendamentos por `QueueStatus` para dashboards.
- [x] Criar o endpoint `GET /reports/queue-status` com retorno "achatado" para facilitar a integração com o front-end.

## ⚙️ Fase 6: Otimização do Fluxo e Auditoria

### F6.1 - Comunicação e UX
- [ ] Implementar mecanismo de notificação básico para avisar o **DRIVER** quando o seu agendamento passar para o status `IN_SERVICE`.
- [ ] Criar um endpoint simplificado para o `GATE_KEEPER` que permite buscar e transicionar o status de um agendamento pela **placa do caminhão**.

### F6.2 - Auditoria
- [ ] Adicionar entidade/estrutura para registrar logs de auditoria de **transição de status** (quem, quando e qual status).

### F6.3 - Finalização de Mapeamento
- [ ] **Revisão e finalização de todos os mapeamentos de relacionamento entre entidades** (OneToMany, ManyToOne, etc.) para garantir a integridade total do modelo.

## 💾 Fase 7: Migração de Banco de Dados (Liquibase)

### F7.1 - Configuração
- [ ] Configurar o Liquibase no projeto.

### F7.2 - Criação de Tabelas
- [ ] Criar *changelogs* do Liquibase para a criação de todas as tabelas: `COMPANY`, `CARRIER`, `USER`, `BRANCH`, `SCHEDULE`, e outras que surgiram na Fase 6.
- [ ] **Garantir a criação de todos os vínculos (Foreign Keys).**

### F7.3 - Configuração de Sequences
- [ ] Criar sequences para as chaves primárias (`id`).
- [ ] **Configurar as sequences para iniciarem com um número aleatório de até 3 dígitos.**