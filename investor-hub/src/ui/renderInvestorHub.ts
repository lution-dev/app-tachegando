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
  const variantClass = cta.variant === 'primary' ? ' primary' : '';
  return `<a class="button${variantClass}" href="${escapeHtml(cta.href)}">${escapeHtml(cta.label)}</a>`;
}

function renderTabs(tabs: InvestorHubTab[], activeTabId: InvestorHubTabId): string {
  return `
    <nav class="tabs" aria-label="Momentos do investidor">
      ${tabs
        .map((tab) => {
          const isActive = tab.id === activeTabId;
          return `
            <button class="tab ${isActive ? 'active' : ''}" type="button" data-tab-id="${tab.id}" aria-selected="${isActive ? 'true' : 'false'}">
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
  const label = locked
    ? textOrFallback(document.requiresAuth ? 'Restrito' : 'Indisponivel', 'Indisponivel')
    : 'Abrir';

  return `
    <article class="doc ${locked ? 'doc-locked' : ''}">
      <span>${type}</span>
      <strong>${title}</strong>
      <small>${description}</small>
      ${locked ? `<em>${label}</em>` : `<a href="${escapeHtml(document.href ?? '#')}">${label}</a>`}
    </article>
  `;
}
