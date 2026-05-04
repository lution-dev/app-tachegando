# Investor Hub Design Spec

## Contexto

O Ta Chegando precisa de uma area web apresentavel para investidores anjo acompanharem a oportunidade, a negociacao e, depois do aporte, a evolucao do negocio. O repositorio atual contem principalmente um app Kotlin Multiplatform/Compose em `KotlinProject/`, com Supabase ja presente em migracoes e codigo de app. O Hub sera separado do app mobile para ser facil de abrir em navegador, publicar e evoluir com acabamento web proprio.

## Objetivo

Criar um site responsivo chamado Investor Hub, em `investor-hub/`, usando Vite + TypeScript. A primeira versao deve ser bonita, confiavel e pronta para apresentacao a investidores, com tres abas:

1. Antes do aporte
2. Negociacao
3. Pos-aporte

## Nao Objetivos da V1

- Nao implementar login real.
- Nao integrar Supabase Auth, Storage ou banco ainda.
- Nao criar painel administrativo.
- Nao alterar o app Kotlin Multiplatform existente.
- Nao automatizar envio de emails ou assinatura de documentos.

Esses pontos devem ficar preparados no desenho de dados e UI, mas fora da primeira entrega.

## Usuarios

### Investidor anjo

Quer entender rapidamente a tese, a tracao, o mercado, o pedido da rodada e o risco. Durante a negociacao, precisa consultar documentos, status de diligencia e proximas etapas. Apos o aporte, quer acompanhar uso do capital, marcos e indicadores.

### Fundador/equipe Ta Chegando

Quer uma pagina que pareca profissional, possa ser compartilhada em reunioes e organize a narrativa da captacao sem depender de um pitch deck solto.

## Experiencia

### Estrutura geral

O Hub deve abrir em uma tela principal com identidade do Ta Chegando, CTA de interesse, status da rodada e navegacao por abas. O usuario deve conseguir alternar entre as tres abas sem recarregar a pagina.

### Aba Antes do Aporte

Conteudo:

- Tese do Ta Chegando
- Problema e solucao
- Mercado e oportunidade
- Modelo de negocio
- Tracao e principais metricas
- Uso planejado do capital
- Links para deck, one-pager, video curto e FAQ
- CTA "Tenho interesse"

Comportamento:

- Essa aba e tratada como publicavel.
- Deve parecer completa mesmo com dados iniciais estaticos.
- CTAs podem apontar para `mailto:` ou links configuraveis.

### Aba Negociacao

Conteudo:

- Data room organizado por documentos
- Status da due diligence
- Termos em discussao
- Proximas reunioes ou proximas etapas
- Perguntas frequentes de negociacao

Comportamento:

- Na V1, essa aba deve aparecer como area protegida ou semi-protegida.
- O conteudo sensivel deve ser exibido como preview bloqueado quando `requiresAuth` for verdadeiro.
- Deve haver estado visual de "login necessario" ou "acesso por convite".

### Aba Pos-Aporte

Conteudo:

- Updates mensais
- Metricas principais
- Marcos concluidos e proximos marcos
- Uso do dinheiro
- Riscos e proximas decisoes

Comportamento:

- Na V1, essa aba tambem deve aparecer como area protegida.
- O layout deve estar pronto para receber dados reais no futuro.

## Direcao Visual

A direcao aprovada usa:

- Primaria: `#FF6B00`
- Secundaria: `#1B2A4A`
- Destaque: `#FFD166`
- Sucesso: `#06D6A0`
- Erro/urgencia: `#EF476F`
- Tema claro fixo

O design deve ser moderno e denso o suficiente para investidores: abas claras, cards de metricas, data room, timeline de etapas, CTAs objetivos e responsividade forte. Evitar visual de landing page generica. O produto e a oportunidade devem aparecer no primeiro viewport.

## Arquitetura Tecnica

### Stack

- Vite
- TypeScript
- HTML
- CSS moderno sem framework obrigatorio
- Vitest para testes unitarios pequenos

### Estrutura de arquivos

```text
investor-hub/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
├── src/
│   ├── main.ts
│   ├── styles.css
│   ├── data/
│   │   └── investorHubContent.ts
│   ├── domain/
│   │   └── investorHubTypes.ts
│   ├── ui/
│   │   ├── renderInvestorHub.ts
│   │   └── renderInvestorHub.test.ts
│   └── utils/
│       ├── formatters.ts
│       └── formatters.test.ts
```

### Responsabilidades

`main.ts` inicializa o app, guarda a aba ativa e conecta eventos de UI.

`investorHubTypes.ts` define os tipos de tabs, metricas, documentos, etapas e CTAs.

`investorHubContent.ts` centraliza textos e dados da V1.

`renderInvestorHub.ts` transforma estado e conteudo em HTML seguro para inserir no DOM.

`formatters.ts` contem utilitarios pequenos para formatacao e fallback de texto.

`styles.css` contem tokens visuais, layout responsivo e estados de abas/areas protegidas.

## Modelo de Dados V1

O conteudo deve ser local e tipado:

- `hubStatus`: status geral da rodada
- `hero`: titulo, subtitulo e CTAs
- `tabs`: lista de tres abas
- `metrics`: indicadores destacados
- `documents`: data room e links
- `timeline`: etapas da jornada
- `updates`: conteudo pos-aporte

Cada aba pode ter `requiresAuth`. Na V1, esse campo controla somente a apresentacao visual de bloqueio.

## Acessibilidade

- Abas devem ser botoes com `aria-selected`.
- Conteudo principal deve ter regioes semanticas.
- Contraste deve funcionar no tema claro.
- O layout precisa ser utilizavel em mobile.
- Foco de teclado deve ser visivel.

## Tratamento de Erros

- Se uma aba nao tiver conteudo, exibir mensagem amigavel.
- Se documento nao tiver URL, exibir estado indisponivel em vez de link quebrado.
- Se o container raiz nao existir, lancar erro claro durante inicializacao.

## Testes

V1 deve incluir:

- Teste de fallback para texto vazio.
- Teste de renderizacao das tres abas.
- Teste de `aria-selected` na aba ativa.
- Teste de bloqueio visual quando uma aba exige autenticacao.
- Build com `npm run build`.

## Evolucao Posterior

Fase 2:

- Supabase Auth por convite.
- Supabase Storage para documentos.
- Tabela de investidores e permissoes.
- Eventos de interesse enviados para CRM, email ou Linear.

Fase 3:

- Metricas reais vindas do banco.
- Relatorios mensais versionados.
- Download auditado de documentos.
- Comentarios ou perguntas por investidor.

## Criterios de Aceite

- Existe um app web separado em `investor-hub/`.
- A pagina abre em navegador com visual equivalente a direcao aprovada no mockup.
- As tres abas existem e alternam sem recarregar.
- A aba Antes do Aporte tem conteudo publicavel e CTA.
- As abas Negociacao e Pos-Aporte mostram estado protegido na V1.
- O layout e responsivo para desktop e mobile.
- `npm install`, `npm test` e `npm run build` funcionam dentro de `investor-hub/`.
- Nenhum arquivo do app KMP existente e alterado para entregar a V1.
