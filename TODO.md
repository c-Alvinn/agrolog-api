## ✅ TO-DO LIST DO PROJETO AGROLOG API

Este documento rastreia o progresso do projeto, detalhando as atividades concluídas e as que estão no planejamento.

---

### 🟢 FASE 1: SETUP E ESTRUTURA INICIAL (CONCLUÍDA)

| Tópico | Atividade | Status | Detalhes |
| :--- | :--- | :--- | :--- |
| **F1.1 - Inicialização** | Criação do Projeto Spring Boot. | ✅ Feito | Configuração de dependências básicas (Web, JPA, Security). |
| **F1.2 - Dependencies** | Adição de dependências de segurança e utilitários. | ✅ Feito | Inclusão de Spring Security, JWT e Validação (`jakarta.validation`). |
| **F1.3 - Estrutura** | Definição da Estrutura de Packages (controllers, service, repository, model). | ✅ Feito | Organização inicial dos pacotes core. |
| **F1.4 - Exceções** | Implementação de handlers de exceção customizados. | ✅ Feito | Classes `ResourceNotFoundException`, `ValidationException`, `UnauthorizedAccessException`. |

---

### 🟢 FASE 2: FUNDAÇÃO E ENTIDADES CORE (CONCLUÍDA)

| Tópico | Atividade | Status | Detalhes |
| :--- | :--- | :--- | :--- |
| **F2.1 - Entidades Base** | Criação das Entidades `User`, `Company`, `Branch`, `Carrier`. | ✅ Feito | Mapeamento ORM e relacionamentos iniciais. |
| **F2.2 - Roles** | Definição do Enum `Role`. | ✅ Feito | `ADMIN`, `MANAGER`, `GATE_KEEPER`, `SCALE_OPERATOR`, `CARRIER`, `DRIVER`. |
| **F2.3 - Repositórios** | Criação das interfaces `JpaRepository`. | ✅ Feito | `UserRepository`, `CompanyRepository`, `BranchRepository`, `CarrierRepository`. |
| **F2.4 - DTOs Base** | Definição de DTOs de requisição e resposta básicos. | ✅ Feito | DTOs de login e registro de motorista. |

---

### 🟢 FASE 3: SEGURANÇA E ESTRUTURA ORGANIZACIONAL (CONCLUÍDA)

| Tópico                          | Atividade | Status | Detalhes |
|:--------------------------------| :--- | :--- | :--- |
| **F3.1 - Segurança**            | Implementação de JWT Authentication. | ✅ Feito | Configuração do `SecurityConfig` (Stateless, filtro JWT) e `TokenService`. |
| **F3.2 - Auth/Login**           | Refatoração de Login e Mapeamento. | ✅ Feito | `AuthService.authenticate` retorna `LoginResponseDTO` (Token + `UserResponseDTO`). |
| **F3.3 - Carrier CRUD**         | CRUD da Entidade `Carrier`. | ✅ Feito | CRUD básico com validação de unicidade por nome e regras de acesso por URL. |
| **F3.4 - Company CRUD**         | CRUD da Entidade `Company`. | ✅ Feito | Regras de acesso: `ADMIN` (CRUD), `MANAGER` (PUT, GET), Outros (GET). |
| **F3.5 - Branch CRUD & Escopo** | CRUD da Entidade `Branch` com Escopo. | ✅ Feito | Lógica no `BranchService` para restringir `MANAGER`, `SCALE_OPERATOR` e `GATE_KEEPER` ao escopo da sua `Company`. Criação de endpoint filtrado para `DRIVER`/`CARRIER`. |
| **F3.6 - Cadastro Usuários**    | Lógica de Escopo para `User` e `CarrierUser`. | ✅ Feito | `UserService` restringe o cadastro de `CARRIER` ao seu próprio escopo e garante os campos `null` para `ROLE_CARRIER`. |
| **F3.7 - Autorização**          | Centralização das regras de acesso. | ✅ Feito | Todas as regras de acesso (RBAC) definidas por URL no `SecurityConfig.java`. |

---

### 🟡 FASE 4: MÓDULO DE AGENDAMENTO (EM PROGRESSO)

| Tópico | Atividade | Status | Detalhes |
| :--- | :--- | :--- | :--- |
| **F4.1 - Entidade** | Criação da Entidade `Scheduling` (`Agendamento`). | ⏳ Próxima | Mapeamento completo com vínculos necessários (`Branch`, `Carrier`, `Driver`, etc.). |
| **F4.2 - CRUD Básico** | Implementação do `SchedulingService` e `Controller`. | ⬜ Pendente | Métodos para criação, leitura e regras de validação iniciais. |
| **F4.3 - Lógica de Status** | Gerenciamento do Fluxo de Status (Workflow). | ⬜ Pendente | Definição das transições de status: `AGENDADO` -> `EM PÁTIO` -> `CARREGANDO` -> `CONCLUÍDO`. |
| **F4.4 - Autorização de Status** | Restrição de Transições por Role. | ⬜ Pendente | Restrições de quem pode mover para `EM PÁTIO` (`GATE_KEEPER`) e para `CARREGANDO`/`CONCLUÍDO` (`SCALE_OPERATOR`). |
| **F4.5 - Cancelamento** | Implementação de regras de cancelamento. | ⬜ Pendente | Regras e motivos para cancelamento de agendamentos. |

---

### ⚪ FASES FUTURAS (BACKLOG)

* **F5.0 - Relatórios e Eficiência:** Criação de *endpoints* para relatórios de eficiência de pátio, tempo médio de permanência e volume agendado.
* **F6.0 - Otimização e Performance:** Otimização de consultas, paginação e implementação de *caching*.
* **F7.0 - Monitoramento:** Configuração de *logging* e *tracing* da aplicação.