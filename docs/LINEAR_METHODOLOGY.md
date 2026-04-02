# Metodologia de Gestão de Projetos no Linear

> Como estruturamos projetos no Linear usando **Labels para Produtos** e **Projetos para Módulos**.

---

## Filosofia

### O Conceito

No Linear, a hierarquia padrão é:
- **Organization** → Empresa
- **Team** → Time de trabalho (único: Evo)
- **Label** → Produto/Projeto (ex: uun, tch, opt)
- **Project** → Módulo/Feature (ex: Autenticação, App Cliente)
- **Issue** → Tarefa/Story

**Nossa abordagem:**
- Um único **Time** (Evo) para toda a organização
- Cada **produto** vira uma **Label** (ex: `uun` para U-unico, `tch` para Tá Chegando)
- Cada **módulo/feature** vira um **Project** dentro do time
- Issues são organizadas por label (produto) + project (módulo)

### Por que funciona?

1. **Organização Simples** - Um único time, sem fragmentação
2. **Filtros por Label** - Fácil ver todas as issues de um produto
3. **Projetos como Módulos** - Cada projeto é um módulo real do código
4. **Visibilidade Total** - Dashboard único para toda a organização
5. **Escalabilidade** - Novo produto = nova label, não novo time

---

## Estrutura de Labels (Produtos)

```
Organization: Evo Softwares
│
├── Label: uun (U-unico)
│   ├── Project: Autenticacao
│   ├── Project: App Cliente
│   ├── Project: App Lojista
│   ├── Project: App Entregador
│   ├── Project: Painel Admin
│   ├── Project: Expedicao
│   ├── Project: Core
│   └── Project: Infraestrutura
│
├── Label: tch (Tá Chegando)
│   ├── Project: Autenticacao
│   ├── Project: App Vendedor
│   ├── Project: App Consumidor
│   ├── Project: Mapas
│   └── Project: Infraestrutura
│
└── Label: opt (Option Serviços)
    ├── Project: Landing Page
    ├── Project: Pagamentos
    └── Project: Admin
```

---

## Projetos como Módulos

### Módulos Conectados

**Importante:** Os módulos não são isolados. Eles formam uma estrutura conectada onde cada módulo pode depender de outros.

```
┌─────────────────────────────────────────────────────────────────┐
│                     ESTRUTURA DE MÓDULOS                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│                      ┌─────────────┐                            │
│                      │     Core    │                            │
│                      │  (base)     │                            │
│                      └──────┬──────┘                            │
│                             │                                   │
│              ┌──────────────┼──────────────┐                    │
│              │              │              │                    │
│              ▼              ▼              ▼                    │
│       ┌──────────┐   ┌───────────┐   ┌───────────┐             │
│       │   Auth   │   │Infraestru-│   │  Shared   │             │
│       │          │   │   tura    │   │           │             │
│       └────┬─────┘   └───────────┘   └───────────┘             │
│            │                                                    │
│    ┌───────┼───────┬───────────────┬───────────────┐           │
│    │       │       │               │               │           │
│    ▼       ▼       ▼               ▼               ▼           │
│ ┌──────┐ ┌──────┐ ┌────────┐ ┌──────────┐ ┌──────────┐         │
│ │Cliente│ │Lojista│ │Entregador│ │  Admin   │ │Expedicao │        │
│ └───┬───┘ └───┬───┘ └────┬─────┘ └────┬─────┘ └────┬─────┘         │
│     │         │          │           │           │               │
│     └─────────┴──────────┴───────────┴───────────┘               │
│                         │                                       │
│                         ▼                                       │
│                  ┌───────────┐                                  │
│                  │  Shared   │                                  │
│                  │ (chat,    │                                  │
│                  │  reviews) │                                  │
│                  └───────────┘                                  │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Dependências entre Módulos

| Módulo | Depende de | Descrição |
|--------|------------|-----------|
| **Auth** | Core, Infraestrutura | Autenticação base |
| **App Cliente** | Auth, Core, Shared | Interface do cliente |
| **App Lojista** | Auth, Core, Expedicao, Shared | Interface do lojista |
| **App Entregador** | Auth, Core, Expedicao, Shared | Interface do entregador |
| **Painel Admin** | Auth, Core, Shared | Painel administrativo |
| **Expedicao** | Auth, Core, Infraestrutura | Logística e entregas |
| **Shared** | Core | Componentes compartilhados |
| **Core** | - | Base (sem dependências) |
| **Infraestrutura** | - | Backend (sem dependências de app) |

### Regras de Dependência

1. **Core não depende de ninguém** - É a base
2. **Auth depende de Core** - Autenticação é transversal
3. **Apps dependem de Auth** - Todos os apps precisam de autenticação
4. **Shared é usado por todos** - Chat, reviews, erros
5. **Expedicao conecta Lojista ↔ Entregador** - Fluxo de logística

### Tipos de Projetos

| Tipo | Exemplos | Quando usar |
|------|----------|-------------|
| **Core** | Core, UI, Theme | Código base compartilhado |
| **Feature** | Autenticação, Expedição | Funcionalidade completa |
| **App** | App Cliente, App Lojista | Interface de um usuário específico |
| **Business** | Pagamentos, Assinaturas | Lógica de negócio |
| **Infra** | Infraestrutura, DevOps | Backend, CI/CD |

### Nomenclatura

- **Core** - Componentes base, tema, routing, utils
- **Autenticação** - Login, cadastro, onboarding, KYC
- **App [Persona]** - Interface para tipo de usuário
- **[Feature]** - Nome da feature (Expedição, Mapas)
- **Infraestrutura** - Backend, banco de dados, CI/CD

---

## Labels por Produto

| Label | Cor | Produto |
|-------|-----|---------|
| `uun` | #FF6B35 | U-unico |
| `tch` | #06D6A0 | Tá Chegando |
| `opt` | #9B5DE5 | Option Serviços |
| `exe` | #00BBF9 | Executive Car |

---

## Labels por Prioridade

| Label | Cor | Uso |
|-------|-----|-----|
| `P0` | #FF0000 | Urgente, bloqueia release |
| `P1` | #FF6B35 | Alta prioridade |
| `P2` | #FFD166 | Média prioridade |
| `P3` | #06D6A0 | Baixa prioridade |

---

## Templates de Issues

### Feature

```markdown
## Descrição
[O que deve ser implementado e por quê]

## Critérios de Aceitação
- [ ] Critério 1
- [ ] Critério 2
- [ ] Critério 3

## Tarefas Técnicas
- [ ] Criar model/domain
- [ ] Criar repository
- [ ] Criar controller
- [ ] Criar UI
- [ ] Testes unitários
- [ ] Testes de widget

## Wireframe/Mockup
[Link ou anexo]

## Relacionado
- Projeto: [Nome do Módulo]
- Label: [Produto]
```

### Bug

```markdown
## Descrição
[O que está acontecendo de errado]

## Passos para Reproduzir
1. Abrir app
2. Navegar para X
3. Clicar em Y
4. Ver erro

## Comportamento Esperado
[O que deveria acontecer]

## Comportamento Atual
[O que está acontecendo]

## Ambiente
- App version: 1.0.0
- OS: iOS 17 / Android 14
- Device: iPhone 15 / Samsung S24

## Screenshots/Logs
[Anexar se aplicável]

## Criticidade
- [ ] P0 - Crítico (bloqueia release)
- [ ] P1 - Alto
- [ ] P2 - Médio
- [ ] P3 - Baixo
```

---

## Convenções

### Prefixo de Issues

Todas as issues usam o prefixo do time Evo: **EVO-**

### Branches

```
feature/EVO-123-descricao-da-feature
bugfix/EVO-456-descricao-do-bug
hotfix/EVO-789-descricao-critica
refactor/EVO-101-descricao-refatoracao
```

### Commits

```
feat(customer): adiciona tela de favoritos

- Implementa lista de produtos favoritos
- Adiciona botão de favoritar no produto
- Persiste favoritos localmente

Closes EVO-13
```

### Pull Requests

```
EVO-13: Adiciona tela de favoritos

## O que foi feito
- Implementação da tela de favoritos
- Integração com provider
- Testes unitários

## Screenshots
[Anexar prints]

## Checklist
- [ ] Código segue padrões do projeto
- [ ] Testes passando
- [ ] Sem warnings no analyzer
- [ ] PR revisado por pelo menos 1 pessoa

Closes EVO-13
```

---

## Status Workflow

```
Backlog → Todo → In Progress → In Review → Done
                    ↓
                 Blocked
```

| Status | Quando usar |
|--------|-------------|
| Backlog | Issue criada, sem priorização |
| Todo | Pronta para ser feita na sprint |
| In Progress | Alguém trabalhando nela |
| In Review | PR aberta, aguardando review |
| Done | Mergeada e deployada |
| Blocked | Impedimento identificado |

---

## Boas Práticas

1. **Uma issue = Uma tarefa** - Não misturar escopos
2. **Título descritivo** - "Adiciona favoritos" não "Favoritos"
3. **Descrição completa** - Qualquer pessoa deve entender
4. **Critérios de aceitação** - Como saber que está pronto
5. **Label do produto** - Sempre usar a label correta (uun, tch, etc.)
6. **Projeto certo** - Organiza por módulo
7. **Estimativa** - Ajuda no planejamento
8. **Atualizar status** - Manter workflow atualizado

---

## Referências

- [Linear API Docs](https://developers.linear.app/)
- [Linear GraphQL Explorer](https://explorer.linear.app/)
- [Linear MCP](https://mcp.linear.app/)

---

*Metodologia Evo Softwares - v2.0*
