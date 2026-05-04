# Investor Hub Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a polished, responsive Investor Hub web app for Ta Chegando with three investor journey tabs: pre-investment, negotiation, and post-investment.

**Architecture:** Create a standalone Vite + TypeScript app in `investor-hub/` so it can be published independently from the Kotlin Multiplatform app. Keep content local and typed in V1, render the UI with small framework-free TypeScript functions, and prepare protected-state fields for later Supabase integration.

**Tech Stack:** Vite, TypeScript, Vitest, HTML, CSS.

---

## File Structure

- Create: `investor-hub/package.json` - npm scripts and dev dependencies.
- Create: `investor-hub/index.html` - root HTML shell and app container.
- Create: `investor-hub/tsconfig.json` - TypeScript compiler config.
- Create: `investor-hub/vite.config.ts` - Vite and Vitest config.
- Create: `investor-hub/src/domain/investorHubTypes.ts` - typed content model.
- Create: `investor-hub/src/utils/formatters.ts` - escaping and fallback utilities.
- Create: `investor-hub/src/utils/formatters.test.ts` - formatter tests.
- Create: `investor-hub/src/data/investorHubContent.ts` - V1 content.
- Create: `investor-hub/src/ui/renderInvestorHub.ts` - HTML renderer for app state.
- Create: `investor-hub/src/ui/renderInvestorHub.test.ts` - renderer tests.
- Create: `investor-hub/src/main.ts` - browser initialization and tab interaction.
- Create: `investor-hub/src/styles.css` - full polished responsive design.

## Task 1: Scaffold Vite TypeScript App

**Files:**
- Create: `investor-hub/package.json`
- Create: `investor-hub/index.html`
- Create: `investor-hub/tsconfig.json`
- Create: `investor-hub/vite.config.ts`

- [ ] **Step 1: Create `package.json`**

```json
{
  "name": "tachegando-investor-hub",
  "version": "0.1.0",
  "private": true,
  "type": "module",
  "scripts": {
    "dev": "vite --host 127.0.0.1",
    "build": "tsc && vite build",
    "preview": "vite preview --host 127.0.0.1",
    "test": "vitest run",
    "test:watch": "vitest"
  },
  "devDependencies": {
    "@vitest/ui": "^3.1.4",
    "typescript": "^5.8.3",
    "vite": "^6.3.5",
    "vitest": "^3.1.4"
  }
}
```

- [ ] **Step 2: Create `index.html`**

```html
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta
      name="description"
      content="Hub de investidores do Ta Chegando com pitch, negociacao e acompanhamento pos-aporte."
    />
    <title>Ta Chegando | Investor Hub</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.ts"></script>
  </body>
</html>
```

- [ ] **Step 3: Create `tsconfig.json`**

```json
{
  "compilerOptions": {
    "target": "ES2022",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2022", "DOM", "DOM.Iterable"],
    "allowJs": false,
    "skipLibCheck": true,
    "esModuleInterop": true,
    "allowSyntheticDefaultImports": true,
    "strict": true,
    "forceConsistentCasingInFileNames": true,
    "moduleResolution": "Node",
    "resolveJsonModule": true,
    "isolatedModules": true,
    "types": ["vite/client"],
    "noEmit": true
  },
  "include": ["src"],
  "references": []
}
```

- [ ] **Step 4: Create `vite.config.ts`**

```ts
import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'node',
  },
});
```

- [ ] **Step 5: Install dependencies**

Run:

```bash
cd investor-hub
npm install
```

Expected: `node_modules/` and `package-lock.json` are created, with no install errors.

- [ ] **Step 6: Commit scaffold**

```bash
git add investor-hub/package.json investor-hub/package-lock.json investor-hub/index.html investor-hub/tsconfig.json investor-hub/vite.config.ts
git commit -m "feat(investor-hub): scaffold web app"
```

## Task 2: Define Content Types and Utilities

**Files:**
- Create: `investor-hub/src/domain/investorHubTypes.ts`
- Create: `investor-hub/src/utils/formatters.ts`
- Create: `investor-hub/src/utils/formatters.test.ts`

- [ ] **Step 1: Create failing tests for formatters**

```ts
import { describe, expect, it } from 'vitest';
import { escapeHtml, textOrFallback } from './formatters';

describe('textOrFallback', () => {
  it('returns fallback for empty values', () => {
    expect(textOrFallback('', 'Indisponivel')).toBe('Indisponivel');
    expect(textOrFallback('   ', 'Indisponivel')).toBe('Indisponivel');
  });

  it('returns trimmed text when present', () => {
    expect(textOrFallback('  Rodada aberta  ', 'Indisponivel')).toBe('Rodada aberta');
  });
});

describe('escapeHtml', () => {
  it('escapes unsafe HTML characters', () => {
    expect(escapeHtml('<script>alert("x")</script>')).toBe(
      '&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt;',
    );
  });
});
```

- [ ] **Step 2: Run tests and verify failure**

Run:

```bash
cd investor-hub
npm test -- src/utils/formatters.test.ts
```

Expected: FAIL because `formatters.ts` does not exist yet.

- [ ] **Step 3: Create `investorHubTypes.ts`**

```ts
export type InvestorHubTabId = 'preInvestment' | 'negotiation' | 'postInvestment';

export type InvestorHubCta = {
  label: string;
  href: string;
  variant: 'primary' | 'secondary';
};

export type InvestorHubMetric = {
  label: string;
  value: string;
  detail: string;
  tone: 'navy' | 'blue' | 'gold' | 'green';
};

export type InvestorHubDocument = {
  title: string;
  type: string;
  description: string;
  href?: string;
  requiresAuth: boolean;
};

export type InvestorHubTimelineItem = {
  label: string;
  description: string;
  status: 'done' | 'current' | 'locked';
};

export type InvestorHubSection = {
  eyebrow: string;
  title: string;
  body: string;
  bullets: string[];
};

export type InvestorHubTab = {
  id: InvestorHubTabId;
  index: string;
  label: string;
  title: string;
  summary: string;
  requiresAuth: boolean;
  sections: InvestorHubSection[];
};

export type InvestorHubContent = {
  hubStatus: string;
  hero: {
    eyebrow: string;
    title: string;
    subtitle: string;
    ctas: InvestorHubCta[];
  };
  metrics: InvestorHubMetric[];
  tabs: InvestorHubTab[];
  documents: InvestorHubDocument[];
  timeline: InvestorHubTimelineItem[];
};
```

- [ ] **Step 4: Create `formatters.ts`**

```ts
export function textOrFallback(value: string | null | undefined, fallback: string): string {
  const normalized = value?.trim();
  return normalized && normalized.length > 0 ? normalized : fallback;
}

export function escapeHtml(value: string): string {
  return value
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}
```

- [ ] **Step 5: Run formatter tests**

Run:

```bash
cd investor-hub
npm test -- src/utils/formatters.test.ts
```

Expected: PASS.

- [ ] **Step 6: Commit types and utilities**

```bash
git add investor-hub/src/domain/investorHubTypes.ts investor-hub/src/utils/formatters.ts investor-hub/src/utils/formatters.test.ts
git commit -m "feat(investor-hub): define typed content model"
```

## Task 3: Add Investor Hub Content

**Files:**
- Create: `investor-hub/src/data/investorHubContent.ts`

- [ ] **Step 1: Create content file**

```ts
import type { InvestorHubContent } from '../domain/investorHubTypes';

export const investorHubContent: InvestorHubContent = {
  hubStatus: 'Rodada em preparacao',
  hero: {
    eyebrow: 'Hub para investidores anjo',
    title: 'O mapa vivo da economia itinerante.',
    subtitle:
      'Uma area apresentavel para investidores acompanharem tese, documentos, negociacao e resultados do Ta Chegando em um unico lugar.',
    ctas: [
      {
        label: 'Tenho interesse',
        href: 'mailto:fundadores@tachegando.app?subject=Interesse%20em%20investir%20no%20Ta%20Chegando',
        variant: 'primary',
      },
      {
        label: 'Baixar one-pager',
        href: '#data-room',
        variant: 'secondary',
      },
    ],
  },
  metrics: [
    {
      label: 'Mercado',
      value: 'local-first',
      detail: 'Vendedores itinerantes conectados a consumidores proximos.',
      tone: 'navy',
    },
    {
      label: 'Modelo',
      value: 'SaaS + B2B',
      detail: 'Assinatura para vendedores e planos de frota.',
      tone: 'blue',
    },
    {
      label: 'Uso do capital',
      value: 'produto',
      detail: 'Produto, geolocalizacao, aquisicao e operacao inicial.',
      tone: 'gold',
    },
  ],
  tabs: [
    {
      id: 'preInvestment',
      index: '01',
      label: 'Antes do aporte',
      title: 'Convencer e qualificar investidores',
      summary:
        'Pitch, tese, mercado, tracao, uso de capital e chamada clara para investidores interessados.',
      requiresAuth: false,
      sections: [
        {
          eyebrow: 'Oportunidade',
          title: 'A rua tem oferta, demanda e pouca inteligencia de rota.',
          body:
            'O Ta Chegando conecta vendedores itinerantes a consumidores proximos com mapa, alertas, demanda por regiao e ferramentas para vender melhor.',
          bullets: ['Consumidor encontra vendedores proximos', 'Vendedor otimiza rota e recorrencia', 'Frotas acompanham operacao em tempo real'],
        },
        {
          eyebrow: 'Rodada',
          title: 'Capital para acelerar produto e validacao comercial.',
          body:
            'A rodada anjo financia refinamento do app, dados de demanda, funil de vendedores e preparacao das primeiras frotas.',
          bullets: ['Produto e geolocalizacao', 'Aquisicao local', 'Operacao e suporte inicial'],
        },
      ],
    },
    {
      id: 'negotiation',
      index: '02',
      label: 'Negociacao',
      title: 'Data room e due diligence',
      summary:
        'Documentos, termos, perguntas, agenda e status de negociacao em uma area preparada para convite.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Data room',
          title: 'Documentos sensiveis ficam em area protegida.',
          body:
            'Na V1, mostramos o preview do fluxo. Na evolucao, investidores convidados acessam arquivos e status por permissao.',
          bullets: ['Deck completo', 'Termos da rodada', 'Perguntas de diligencia'],
        },
      ],
    },
    {
      id: 'postInvestment',
      index: '03',
      label: 'Pos-aporte',
      title: 'Prestacao de contas e proximos marcos',
      summary:
        'Updates mensais, indicadores, uso do dinheiro, riscos e decisoes que investidores precisam acompanhar.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Governanca',
          title: 'Depois do aporte, o Hub vira cadencia de acompanhamento.',
          body:
            'A area pos-aporte organiza resultados, aprendizados, proximos marcos e uso do capital investido.',
          bullets: ['Updates mensais', 'Indicadores de crescimento', 'Uso do capital por frente'],
        },
      ],
    },
  ],
  timeline: [
    {
      label: 'Pitch e tese',
      description: 'Problema, solucao, mercado, tracao e pedido.',
      status: 'current',
    },
    {
      label: 'Due diligence',
      description: 'Documentos, perguntas, termos e proximas reunioes.',
      status: 'locked',
    },
    {
      label: 'Prestacao de contas',
      description: 'Metricas, marcos, uso do capital e updates mensais.',
      status: 'locked',
    },
  ],
  documents: [
    {
      title: 'Deck da rodada',
      type: 'PDF',
      description: 'Narrativa completa para reunioes de captacao.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Resumo executivo',
      type: '1P',
      description: 'One-pager para compartilhamento rapido.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Uso do capital',
      type: '$',
      description: 'Plano de alocacao por frente de crescimento.',
      requiresAuth: true,
    },
    {
      title: 'FAQ do investidor',
      type: 'Q',
      description: 'Perguntas comuns sobre modelo, risco e mercado.',
      href: '#',
      requiresAuth: false,
    },
  ],
};
```

- [ ] **Step 2: Type-check the content**

Run:

```bash
cd investor-hub
npx tsc --noEmit
```

Expected: PASS with no content type errors.

- [ ] **Step 3: Commit content**

```bash
git add investor-hub/src/data/investorHubContent.ts
git commit -m "feat(investor-hub): add initial investor content"
```

## Task 4: Build Renderer with Tests

**Files:**
- Create: `investor-hub/src/ui/renderInvestorHub.ts`
- Create: `investor-hub/src/ui/renderInvestorHub.test.ts`

- [ ] **Step 1: Create failing renderer tests**

```ts
import { describe, expect, it } from 'vitest';
import { investorHubContent } from '../data/investorHubContent';
import { renderInvestorHub } from './renderInvestorHub';

describe('renderInvestorHub', () => {
  it('renders all three tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html).toContain('Antes do aporte');
    expect(html).toContain('Negociacao');
    expect(html).toContain('Pos-aporte');
  });

  it('marks the active tab with aria-selected', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('data-tab-id="negotiation" aria-selected="true"');
    expect(html).toContain('data-tab-id="preInvestment" aria-selected="false"');
  });

  it('shows protected state for auth-only tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Acesso por convite');
    expect(html).toContain('Area protegida');
  });
});
```

- [ ] **Step 2: Run renderer tests and verify failure**

Run:

```bash
cd investor-hub
npm test -- src/ui/renderInvestorHub.test.ts
```

Expected: FAIL because `renderInvestorHub.ts` does not exist yet.

- [ ] **Step 3: Create renderer**

```ts
import type {
  InvestorHubContent,
  InvestorHubDocument,
  InvestorHubTab,
  InvestorHubTabId,
  InvestorHubTimelineItem,
} from '../domain/investorHubTypes';
import { escapeHtml, textOrFallback } from '../utils/formatters';

export function renderInvestorHub(content: InvestorHubContent, activeTabId: InvestorHubTabId): string {
  const activeTab = content.tabs.find((tab) => tab.id === activeTabId) ?? content.tabs[0];

  if (!activeTab) {
    return '<main class="shell"><section class="empty-state">Conteudo indisponivel.</section></main>';
  }

  return `
    <main class="shell">
      ${renderTopbar(content)}
      <section class="hero">
        <div class="hero-panel">
          <div>
            <div class="eyebrow">${escapeHtml(content.hero.eyebrow)}</div>
            <h1>${escapeHtml(content.hero.title)}</h1>
            <p class="lead">${escapeHtml(content.hero.subtitle)}</p>
            <div class="hero-actions">${content.hero.ctas.map(renderCta).join('')}</div>
          </div>
          ${renderTabs(content.tabs, activeTab.id)}
          ${renderMetrics(content)}
        </div>
        <aside class="side">
          ${renderActiveTab(activeTab)}
          ${renderTimeline(content.timeline)}
          ${renderDocuments(content.documents)}
        </aside>
      </section>
    </main>
  `;
}

function renderTopbar(content: InvestorHubContent): string {
  return `
    <header class="topbar">
      <div class="brand">
        <div class="brand-mark" aria-hidden="true">TC</div>
        <div>
          <strong>Ta Chegando</strong>
          <span>Investor Hub</span>
        </div>
      </div>
      <div class="top-actions">
        <div class="status-pill">${escapeHtml(content.hubStatus)}</div>
        <a class="button" href="#data-room">Data room</a>
        <a class="button primary" href="${escapeHtml(content.hero.ctas[0]?.href ?? '#')}">Tenho interesse</a>
      </div>
    </header>
  `;
}

function renderCta(cta: InvestorHubContent['hero']['ctas'][number]): string {
  return `<a class="button ${cta.variant === 'primary' ? 'primary' : ''}" href="${escapeHtml(cta.href)}">${escapeHtml(cta.label)}</a>`;
}

function renderTabs(tabs: InvestorHubTab[], activeTabId: InvestorHubTabId): string {
  return `
    <nav class="tabs" aria-label="Momentos do investidor">
      ${tabs
        .map((tab) => {
          const isActive = tab.id === activeTabId;
          return `
            <button
              class="tab ${isActive ? 'active' : ''}"
              type="button"
              data-tab-id="${tab.id}"
              aria-selected="${isActive ? 'true' : 'false'}"
            >
              <span>${escapeHtml(tab.index)}</span>
              <strong>${escapeHtml(tab.label)}</strong>
            </button>
          `;
        })
        .join('')}
    </nav>
  `;
}

function renderMetrics(content: InvestorHubContent): string {
  return `
    <section class="insight-grid" aria-label="Indicadores principais">
      ${content.metrics
        .map(
          (metric) => `
            <article class="metric metric-${metric.tone}">
              <span>${escapeHtml(metric.label)}</span>
              <strong>${escapeHtml(metric.value)}</strong>
              <small>${escapeHtml(metric.detail)}</small>
            </article>
          `,
        )
        .join('')}
    </section>
  `;
}

function renderActiveTab(tab: InvestorHubTab): string {
  return `
    <section class="card tab-detail" aria-live="polite">
      <div class="card-heading">
        <div>
          <p class="eyebrow compact">${escapeHtml(tab.label)}</p>
          <h2>${escapeHtml(tab.title)}</h2>
        </div>
        ${tab.requiresAuth ? '<span class="lock">Acesso por convite</span>' : ''}
      </div>
      <p>${escapeHtml(tab.summary)}</p>
      ${tab.requiresAuth ? '<div class="protected-banner"><strong>Area protegida</strong><span>Na V1, este conteudo aparece como preview. A versao seguinte conecta login por convite.</span></div>' : ''}
      <div class="section-list">
        ${tab.sections
          .map(
            (section) => `
              <article class="content-section">
                <span>${escapeHtml(section.eyebrow)}</span>
                <h3>${escapeHtml(section.title)}</h3>
                <p>${escapeHtml(section.body)}</p>
                <ul>${section.bullets.map((bullet) => `<li>${escapeHtml(bullet)}</li>`).join('')}</ul>
              </article>
            `,
          )
          .join('')}
      </div>
    </section>
  `;
}

function renderTimeline(items: InvestorHubTimelineItem[]): string {
  return `
    <section class="card">
      <h2>Jornada do investidor</h2>
      <p>Cada aba mostra apenas o que faz sentido para o momento atual da conversa.</p>
      <div class="timeline">
        ${items
          .map(
            (item, index) => `
              <div class="step step-${item.status}">
                <div class="step-icon">${index + 1}</div>
                <div>
                  <strong>${escapeHtml(item.label)}</strong>
                  <small>${escapeHtml(item.description)}</small>
                </div>
                ${item.status === 'locked' ? '<span class="lock">login</span>' : ''}
              </div>
            `,
          )
          .join('')}
      </div>
    </section>
  `;
}

function renderDocuments(documents: InvestorHubDocument[]): string {
  return `
    <section class="card" id="data-room">
      <h2>Data room essencial</h2>
      <p>Arquivos organizados por contexto, com acesso restrito quando necessario.</p>
      <div class="data-room">
        ${documents.map(renderDocument).join('')}
      </div>
    </section>
  `;
}

function renderDocument(document: InvestorHubDocument): string {
  const title = escapeHtml(document.title);
  const type = escapeHtml(document.type);
  const description = escapeHtml(document.description);
  const locked = document.requiresAuth || !document.href;
  const label = locked ? textOrFallback(document.requiresAuth ? 'Restrito' : 'Indisponivel', 'Indisponivel') : 'Abrir';

  return `
    <article class="doc ${locked ? 'doc-locked' : ''}">
      <span>${type}</span>
      <strong>${title}</strong>
      <small>${description}</small>
      ${locked ? `<em>${label}</em>` : `<a href="${escapeHtml(document.href ?? '#')}">${label}</a>`}
    </article>
  `;
}
```

- [ ] **Step 4: Run renderer tests**

Run:

```bash
cd investor-hub
npm test -- src/ui/renderInvestorHub.test.ts
```

Expected: PASS.

- [ ] **Step 5: Commit renderer**

```bash
git add investor-hub/src/ui/renderInvestorHub.ts investor-hub/src/ui/renderInvestorHub.test.ts
git commit -m "feat(investor-hub): render investor journey tabs"
```

## Task 5: Wire Browser Interaction

**Files:**
- Create: `investor-hub/src/main.ts`

- [ ] **Step 1: Create `main.ts`**

```ts
import { investorHubContent } from './data/investorHubContent';
import type { InvestorHubTabId } from './domain/investorHubTypes';
import './styles.css';
import { renderInvestorHub } from './ui/renderInvestorHub';

const root = document.querySelector<HTMLDivElement>('#app');

if (!root) {
  throw new Error('Investor Hub root element #app was not found.');
}

let activeTabId: InvestorHubTabId = 'preInvestment';

function render(): void {
  root.innerHTML = renderInvestorHub(investorHubContent, activeTabId);
  bindTabButtons();
}

function bindTabButtons(): void {
  const buttons = root.querySelectorAll<HTMLButtonElement>('[data-tab-id]');

  buttons.forEach((button) => {
    button.addEventListener('click', () => {
      const nextTabId = button.dataset.tabId as InvestorHubTabId | undefined;

      if (!nextTabId || nextTabId === activeTabId) {
        return;
      }

      activeTabId = nextTabId;
      render();
    });
  });
}

render();
```

- [ ] **Step 2: Run TypeScript build**

Run:

```bash
cd investor-hub
npm run build
```

Expected: FAIL only because `styles.css` does not exist yet. There should be no TypeScript errors in `main.ts`.

- [ ] **Step 3: Commit interaction wiring**

```bash
git add investor-hub/src/main.ts
git commit -m "feat(investor-hub): wire tab interaction"
```

## Task 6: Implement Polished Responsive CSS

**Files:**
- Create: `investor-hub/src/styles.css`

- [ ] **Step 1: Create `styles.css`**

```css
:root {
  color: #101828;
  background: #f7f9fc;
  font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
  font-synthesis: none;
  text-rendering: optimizeLegibility;
  --orange: #ff6b00;
  --navy: #1b2a4a;
  --gold: #ffd166;
  --green: #06d6a0;
  --pink: #ef476f;
  --ink: #101828;
  --muted: #667085;
  --line: #e6eaf0;
  --surface: #ffffff;
  --soft: #f6f8fb;
  --soft-orange: #fff3ea;
  --shadow: 0 18px 60px rgba(16, 24, 40, .10);
}

* {
  box-sizing: border-box;
}

body {
  margin: 0;
  min-width: 320px;
  min-height: 100vh;
  color: var(--ink);
  background:
    radial-gradient(circle at 12% 8%, rgba(255, 107, 0, .10), transparent 28%),
    linear-gradient(135deg, #fffaf6 0%, #f7f9fc 42%, #eef6f4 100%);
  letter-spacing: 0;
}

a {
  color: inherit;
  text-decoration: none;
}

button,
a {
  -webkit-tap-highlight-color: transparent;
}

button:focus-visible,
a:focus-visible {
  outline: 3px solid rgba(255, 107, 0, .36);
  outline-offset: 3px;
}

.shell {
  width: min(1180px, calc(100vw - 32px));
  margin: 0 auto;
  padding: 28px 0 46px;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  margin-bottom: 28px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: grid;
  place-items: center;
  color: #fff;
  background: linear-gradient(135deg, var(--orange), #ff8e3c);
  box-shadow: 0 14px 28px rgba(255, 107, 0, .22);
  font-weight: 900;
  flex: 0 0 auto;
}

.brand strong,
.brand span {
  display: block;
}

.brand strong {
  font-size: 15px;
  color: var(--navy);
}

.brand span {
  font-size: 13px;
  color: var(--muted);
  margin-top: 2px;
}

.top-actions,
.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.top-actions {
  justify-content: flex-end;
}

.status-pill,
.button {
  min-height: 40px;
  border-radius: 10px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 14px;
  font-size: 13px;
  font-weight: 700;
  line-height: 1.2;
}

.status-pill {
  color: #027a48;
  background: rgba(6, 214, 160, .13);
  border: 1px solid rgba(6, 214, 160, .32);
}

.button {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--navy);
  box-shadow: 0 8px 20px rgba(16, 24, 40, .05);
}

.button.primary {
  border-color: transparent;
  color: #fff;
  background: var(--orange);
  box-shadow: 0 14px 28px rgba(255, 107, 0, .22);
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.04fr) minmax(320px, .96fr);
  gap: 22px;
  align-items: stretch;
}

.hero-panel,
.card {
  background: rgba(255, 255, 255, .84);
  border: 1px solid rgba(230, 234, 240, .90);
  box-shadow: var(--shadow);
  backdrop-filter: blur(18px);
}

.hero-panel {
  border-radius: 24px;
  padding: clamp(24px, 4vw, 42px);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 470px;
}

.eyebrow {
  color: var(--orange);
  font-size: 12px;
  font-weight: 900;
  text-transform: uppercase;
  letter-spacing: .08em;
  margin: 0 0 14px;
}

.eyebrow.compact {
  margin-bottom: 8px;
}

h1,
h2,
h3,
p {
  margin-top: 0;
}

h1 {
  margin-bottom: 0;
  color: var(--navy);
  font-size: clamp(38px, 6vw, 72px);
  line-height: .96;
  letter-spacing: 0;
  max-width: 760px;
}

h2 {
  color: var(--navy);
  font-size: 20px;
  letter-spacing: 0;
  margin-bottom: 8px;
}

h3 {
  color: var(--navy);
  font-size: 17px;
  line-height: 1.25;
  letter-spacing: 0;
  margin-bottom: 8px;
}

.lead {
  margin: 18px 0 0;
  color: var(--muted);
  font-size: 18px;
  line-height: 1.6;
  max-width: 650px;
}

.hero-actions {
  margin-top: 26px;
}

.tabs {
  margin-top: 34px;
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.tab {
  min-height: 86px;
  border-radius: 14px;
  border: 1px solid var(--line);
  background: #fff;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  text-align: left;
  cursor: pointer;
}

.tab.active {
  border-color: rgba(255, 107, 0, .38);
  background: var(--soft-orange);
}

.tab span {
  color: var(--muted);
  font-size: 12px;
  font-weight: 800;
}

.tab strong {
  color: var(--navy);
  font-size: 15px;
  line-height: 1.2;
}

.insight-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  margin-top: 28px;
}

.metric {
  border-radius: 16px;
  background: var(--navy);
  color: #fff;
  padding: 18px;
  min-height: 132px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  overflow: hidden;
}

.metric-blue {
  background: #22415c;
}

.metric-gold {
  color: var(--navy);
  background: var(--gold);
}

.metric-green {
  color: var(--navy);
  background: var(--green);
}

.metric span {
  opacity: .78;
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
}

.metric strong {
  font-size: 28px;
  line-height: 1;
  letter-spacing: 0;
}

.metric small {
  font-size: 12px;
  line-height: 1.35;
  opacity: .78;
}

.side {
  display: grid;
  gap: 16px;
}

.card {
  border-radius: 20px;
  padding: 20px;
}

.card p {
  color: var(--muted);
  line-height: 1.5;
  font-size: 14px;
  margin-bottom: 0;
}

.card-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.protected-banner {
  margin-top: 14px;
  border: 1px solid rgba(239, 71, 111, .20);
  border-radius: 14px;
  background: rgba(239, 71, 111, .08);
  padding: 12px;
}

.protected-banner strong,
.protected-banner span {
  display: block;
}

.protected-banner strong {
  color: var(--pink);
  font-size: 13px;
}

.protected-banner span {
  color: var(--muted);
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}

.section-list {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.content-section {
  border: 1px solid var(--line);
  border-radius: 16px;
  background: #fff;
  padding: 14px;
}

.content-section span {
  display: block;
  color: var(--orange);
  font-size: 11px;
  font-weight: 900;
  text-transform: uppercase;
  margin-bottom: 8px;
}

.content-section ul {
  margin: 12px 0 0;
  padding-left: 18px;
  color: var(--navy);
  font-size: 13px;
  line-height: 1.6;
}

.timeline {
  display: grid;
  gap: 12px;
  margin-top: 16px;
}

.step {
  display: grid;
  grid-template-columns: 34px 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 12px;
  border: 1px solid var(--line);
  border-radius: 14px;
  background: #fff;
}

.step-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: grid;
  place-items: center;
  color: var(--orange);
  background: var(--soft-orange);
  font-weight: 900;
}

.step strong,
.step small {
  display: block;
}

.step strong {
  color: var(--navy);
  font-size: 14px;
}

.step small {
  color: var(--muted);
  font-size: 12px;
}

.lock {
  color: var(--pink);
  font-size: 12px;
  font-weight: 900;
  background: rgba(239, 71, 111, .10);
  padding: 6px 8px;
  border-radius: 999px;
  white-space: nowrap;
}

.data-room {
  margin-top: 22px;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.doc {
  min-height: 136px;
  border-radius: 16px;
  background: #fff;
  border: 1px solid var(--line);
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  gap: 8px;
}

.doc > span {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: var(--soft);
  display: grid;
  place-items: center;
  color: var(--navy);
  font-weight: 900;
}

.doc strong {
  color: var(--navy);
  font-size: 13px;
  line-height: 1.25;
}

.doc small {
  color: var(--muted);
  font-size: 12px;
  line-height: 1.35;
}

.doc a,
.doc em {
  color: var(--orange);
  font-size: 12px;
  font-style: normal;
  font-weight: 900;
}

.doc-locked {
  background: #fcfcfd;
}

.empty-state {
  border-radius: 20px;
  background: #fff;
  border: 1px solid var(--line);
  padding: 24px;
  color: var(--muted);
}

@media (max-width: 980px) {
  .hero {
    grid-template-columns: 1fr;
  }

  .hero-panel {
    min-height: auto;
  }
}

@media (max-width: 720px) {
  .shell {
    width: min(100% - 24px, 1180px);
    padding-top: 18px;
  }

  .topbar,
  .card-heading {
    align-items: flex-start;
    flex-direction: column;
  }

  .top-actions {
    justify-content: flex-start;
  }

  .tabs,
  .insight-grid,
  .data-room {
    grid-template-columns: 1fr;
  }

  .hero-panel,
  .card {
    border-radius: 18px;
  }

  h1 {
    font-size: 42px;
  }

  .lead {
    font-size: 16px;
  }
}
```

- [ ] **Step 2: Run full test suite**

Run:

```bash
cd investor-hub
npm test
```

Expected: PASS.

- [ ] **Step 3: Run production build**

Run:

```bash
cd investor-hub
npm run build
```

Expected: PASS and `dist/` is created.

- [ ] **Step 4: Commit CSS**

```bash
git add investor-hub/src/styles.css
git commit -m "feat(investor-hub): style investor hub"
```

## Task 7: Browser QA and Polish

**Files:**
- Modify only if QA finds issues: `investor-hub/src/styles.css`, `investor-hub/src/ui/renderInvestorHub.ts`, or `investor-hub/src/data/investorHubContent.ts`

- [ ] **Step 1: Start dev server**

Run:

```bash
cd investor-hub
npm run dev -- --port 5174
```

Expected: Vite serves the Hub at `http://127.0.0.1:5174/`.

- [ ] **Step 2: Verify desktop manually or with browser tooling**

Open:

```text
http://127.0.0.1:5174/
```

Expected:

- First viewport shows Ta Chegando, Investor Hub, status, CTA, headline and tabs.
- Clicking each tab updates the side card without page reload.
- Negociacao and Pos-aporte show "Acesso por convite" and "Area protegida".
- No text overlaps or escapes cards.
- The visual direction matches the approved polished mockup.

- [ ] **Step 3: Verify mobile viewport**

Use a browser viewport around `390x844`.

Expected:

- Topbar wraps cleanly.
- Tabs stack vertically.
- Metrics stack vertically.
- Data room cards stack or remain readable.
- No horizontal scrolling.

- [ ] **Step 4: Fix QA issues if present**

If tab text wraps badly on mobile, adjust `.tab` and headings:

```css
.tab {
  min-height: 76px;
}

.tab strong {
  overflow-wrap: anywhere;
}
```

If cards feel too tall on mobile, adjust spacing:

```css
@media (max-width: 720px) {
  .hero-panel,
  .card {
    padding: 18px;
  }
}
```

- [ ] **Step 5: Re-run verification**

Run:

```bash
cd investor-hub
npm test
npm run build
```

Expected: both PASS.

- [ ] **Step 6: Commit QA polish**

If files changed:

```bash
git add investor-hub/src/styles.css investor-hub/src/ui/renderInvestorHub.ts investor-hub/src/data/investorHubContent.ts
git commit -m "fix(investor-hub): polish responsive presentation"
```

If no files changed, do not create an empty commit.

## Task 8: Documentation Handoff

**Files:**
- Create: `investor-hub/README.md`

- [ ] **Step 1: Create README**

````md
# Ta Chegando Investor Hub

Web area for angel investors to review the Ta Chegando opportunity, negotiation materials, and post-investment updates.

## Commands

```bash
npm install
npm run dev
npm test
npm run build
```

## Structure

- `src/data/investorHubContent.ts` stores the V1 content.
- `src/domain/investorHubTypes.ts` defines the typed content model.
- `src/ui/renderInvestorHub.ts` renders the app.
- `src/styles.css` contains the visual system and responsive layout.

## V1 Scope

- Three investor journey tabs.
- Public pre-investment content.
- Protected visual state for negotiation and post-investment tabs.
- Local typed content.

## Later

- Supabase Auth invitations.
- Supabase Storage documents.
- Investor permissions.
- Real metrics and monthly reports.
````

- [ ] **Step 2: Run final checks**

Run:

```bash
cd investor-hub
npm test
npm run build
```

Expected: both PASS.

- [ ] **Step 3: Commit README**

```bash
git add investor-hub/README.md
git commit -m "docs(investor-hub): add usage notes"
```

## Final Verification

Run:

```bash
cd investor-hub
npm test
npm run build
```

Expected:

- `npm test` passes all tests.
- `npm run build` completes successfully.
- `dist/` is generated.

Then run:

```bash
git status --short
```

Expected:

- No unstaged changes from `investor-hub/`.
- Existing unrelated repository changes may still appear and must not be reverted.
