# AGROLOG API - PLANEJAMENTO DO PROJETO

## ✅ Fase 1: Configuração e Usuários (Concluída)
- [x] Configuração inicial do projeto.
- [x] Autenticação JWT e Roles.
- [x] Endpoints de registro.

## ✅ Fase 2: Módulos Base (Concluída)
- [x] CRUD Company e Carrier.

## ✅ Fase 3: Módulo Filial (Concluída)
- [x] CRUD Branch e regras de escopo.

## ✅ Fase 4: Agendamento e Fila (Concluída)
- [x] Regras de negócio, filas e endpoints operacionais.
- [x] Refatoração para uso de Placa + Filial (UX).

## ✅ Fase 5: Relatórios e Dashboards (Concluída)
- [x] Relatórios de Performance (PDF) e Status.

## ✅ Fase 6: Otimização do Fluxo e Auditoria (Concluída)

### F6.1 - Comunicação e UX
- [x] Adaptação dos endpoints (`in-service`, `completed`, `cancel`) para funcionar via **Placa** (Supre a necessidade do Gate Keeper).
- [ ] *(PAUSADO)* Implementar notificação (WhatsApp).

### F6.2 - Auditoria
- [x] Implementação da entidade `ScheduleHistory` para registrar logs de transição de status.
- [x] Ajuste nos services para gravar histórico automaticamente.

### F6.3 - Finalização de Mapeamento
- [x] Revisão dos relacionamentos bidirecionais (`@OneToMany`) em Company, Carrier e Schedule.

## ✅ Fase 7: Migração de Banco de Dados (Flyway) (Concluída)

### F7.1 - Configuração:
- [x] Substituição do plano original (Liquibase) pelo **Flyway**.
- [x] Configuração de `spring.jpa.hibernate.ddl-auto=validate`.
- [x] Configuração de Schema isolado (`agrolog`).

###  F7.2 - Script V1:
- [x] Criação do script `V1__Create_Initial_Schema.sql`.
- [x] Correção de tabela reservada (`User` -> `users`).
- [x] Definição de `search_path` e constraints.
- [x] Execução e validação da primeira migração.