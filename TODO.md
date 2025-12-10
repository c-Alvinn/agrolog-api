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

## ⏭️ Fase 5: Relatórios e Dashboards (Em Andamento)

### F5.1 - Estrutura de Relatórios
- [ ] Criar DTOs de retorno para métricas (`TimeMetricsDTO`, `QueueReportDTO`).
- [ ] Adicionar métodos de relatório ao `ScheduleService` ou criar um novo `ReportingService`.

### F5.2 - Relatório de Tempo Médio de Permanência
- [ ] Implementar o método para calcular o tempo médio entre `ScheduledAt` e `ReleasedAt` (para status `COMPLETED`).
- [ ] Criar o endpoint `GET /schedules/reports/average-time` com filtros de `Branch` e período.
- [ ] Implementar validação de escopo para relatórios (somente dados da Company do usuário logado).

### F5.3 - Relatório de Status da Fila
- [ ] Implementar o método para retornar a contagem de agendamentos por `QueueStatus` para uma determinada `Branch`.
- [ ] Criar o endpoint `GET /schedules/reports/queue-status` com filtro de `Branch`.