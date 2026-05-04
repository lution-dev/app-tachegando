import type {
  InvestorHubContent,
  InvestorHubDocument,
  InvestorHubMetric,
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
    <main class="portal-shell">
      ${renderTopbar(content, activeTab.id)}
      <section class="memo-grid">
        <section class="memo-main">
          ${renderHero(content)}
          ${renderTabs(content.tabs, activeTab.id)}
          ${renderExecutiveSummary(activeTab, content.metrics)}
        </section>
        <aside class="memo-side">
          ${renderActionRail()}
          ${renderDocuments(content.documents)}
          ${renderTimeline(content.timeline)}
        </aside>
      </section>
      ${renderFooter()}
    </main>
  `;
}

function renderTopbar(content: InvestorHubContent, activeTabId: InvestorHubTabId): string {
  return `
    <header class="topbar">
      <a class="brand" href="/" aria-label="Tá Chegando Investor Hub">
        <div class="brand-mark" aria-hidden="true">TC</div>
        <div class="brand-divider" aria-hidden="true"></div>
        <div>
        <strong>Tá Chegando</strong>
          <span>Investor Hub</span>
        </div>
      </a>
      <div class="topbar-center">
        ${renderNavTab('preInvestment', 'Antes do aporte', activeTabId)}
        ${renderNavTab('negotiation', 'Negociação', activeTabId)}
        ${renderNavTab('postInvestment', 'Pós-aporte', activeTabId)}
      </div>
      <div class="top-actions">
        <div class="status-pill"><span aria-hidden="true"></span>${escapeHtml(content.hubStatus)}</div>
        <a class="button primary" href="${escapeHtml(content.hero.ctas[0]?.href ?? '#')}">Tenho interesse ${icon('arrow')}</a>
      </div>
    </header>
  `;
}

function renderNavTab(id: InvestorHubTabId, label: string, activeTabId: InvestorHubTabId): string {
  const isActive = id === activeTabId;
  return `<button class="nav-tab ${isActive ? 'active' : ''}" type="button" data-tab-id="${id}" aria-selected="${isActive ? 'true' : 'false'}">${escapeHtml(label)}</button>`;
}

function renderHero(content: InvestorHubContent): string {
  return `
    <header class="memo-hero">
      <h1>${escapeHtml(content.hero.title)}</h1>
      <p>${escapeHtml(content.hero.subtitle)}</p>
    </header>
  `;
}

function renderTabs(tabs: InvestorHubTab[], activeTabId: InvestorHubTabId): string {
  return `
    <nav class="stage-tabs" aria-label="Momentos do investidor">
      ${tabs
        .map((tab) => {
          const isActive = tab.id === activeTabId;
          return `
            <button class="stage-tab ${isActive ? 'active' : ''}" type="button" data-tab-id="${tab.id}" aria-selected="${isActive ? 'true' : 'false'}">
              ${icon(tab.requiresAuth ? 'lock' : 'document')}
              <span>
                <strong>${escapeHtml(tab.label)}</strong>
                <small>${tab.requiresAuth ? 'Acesso restrito' : 'Público'}</small>
              </span>
            </button>
          `;
        })
        .join('')}
    </nav>
  `;
}

function renderExecutiveSummary(tab: InvestorHubTab, metrics: InvestorHubMetric[]): string {
  const primarySection = tab.sections[0];
  const secondarySection = tab.sections[1];

  return `
    <section class="executive-panel" aria-live="polite">
      <div class="summary-copy">
        <p class="section-label">${escapeHtml(tab.requiresAuth ? 'Area restrita' : 'Resumo executivo')}</p>
        <h2>${escapeHtml(tab.requiresAuth ? tab.title : 'Resumo executivo')}</h2>
        <p>${escapeHtml(tab.summary)}</p>
        ${tab.requiresAuth ? renderProtectedBanner() : ''}
        ${primarySection ? renderBulletList(primarySection.bullets) : ''}
      </div>
      ${renderMetricGrid(metrics)}
      ${secondarySection ? renderInvestmentNote(secondarySection.title) : ''}
    </section>
  `;
}

function renderBulletList(bullets: string[]): string {
  return `
    <ul class="proof-list">
      ${bullets.map((bullet, index) => `<li>${icon(proofIcon(index))}<span>${escapeHtml(bullet)}</span></li>`).join('')}
    </ul>
  `;
}

function renderMetricGrid(metrics: InvestorHubMetric[]): string {
  return `
    <dl class="metric-grid">
      ${metrics
        .map(
          (metric) => `
            <div>
              <dt>${escapeHtml(metric.label)}</dt>
              <dd>${escapeHtml(metric.value)}</dd>
              <small>${escapeHtml(metric.detail)}</small>
            </div>
          `,
        )
        .join('')}
    </dl>
  `;
}

function renderInvestmentNote(title: string): string {
  return `
    <div class="investment-note">
      ${icon('shield')}
      <div>
        <strong>${escapeHtml(title)}</strong>
        <span>Escalavel, defensavel e com impacto real.</span>
      </div>
    </div>
  `;
}

function renderProtectedBanner(): string {
  return `
    <div class="protected-banner">
      ${icon('lock')}
      <div>
        <strong>Área protegida</strong>
        <span>Na V1, este conteúdo aparece como preview. A versão seguinte conecta login por convite.</span>
      </div>
    </div>
  `;
}

function renderActionRail(): string {
  const actions = [
    {
      iconName: 'document',
      title: 'Tese',
      body: 'O Tá Chegando organiza e digitaliza o comércio itinerante.',
      cta: 'Ver tese completa',
    },
    {
      iconName: 'trend',
      title: 'Tração',
      body: 'Crescimento consistente em vendedores, consumidores e engajamento.',
      cta: 'Ver métricas',
    },
    {
      iconName: 'pie',
      title: 'Uso do capital',
      body: 'Onde vamos investir e como cada real será alocado.',
      cta: 'Ver plano de uso',
    },
  ] as const;

  return `
    <section class="action-rail" aria-label="Atalhos de analise">
      ${actions
        .map(
          (action) => `
            <article class="analysis-row">
              ${icon(action.iconName)}
              <div>
                <h2>${escapeHtml(action.title)}</h2>
                <p>${escapeHtml(action.body)}</p>
              </div>
              <a href="#data-room">${escapeHtml(action.cta)} ${icon('chevron')}</a>
            </article>
          `,
        )
        .join('')}
    </section>
  `;
}

function renderDocuments(documents: InvestorHubDocument[]): string {
  return `
    <section class="data-panel" id="data-room">
      <div class="panel-heading">
        <div>
          ${icon('folder')}
          <h2>Data room</h2>
        </div>
        <a href="#data-room">Ver todos os documentos ${icon('chevron')}</a>
      </div>
      <div class="document-table" role="table" aria-label="Documentos do data room">
        <div class="document-row table-head" role="row">
          <span>Documento</span>
          <span>Tipo</span>
          <span>Atualizado</span>
          <span>Acesso</span>
        </div>
        ${documents.map(renderDocument).join('')}
      </div>
    </section>
  `;
}

function renderDocument(document: InvestorHubDocument): string {
  const locked = document.requiresAuth || !document.href;
  const access = locked
    ? textOrFallback(document.requiresAuth ? 'Restrito' : 'Indisponível', 'Indisponível')
    : 'Público';

  return `
    <div class="document-row ${locked ? 'locked' : ''}" role="row">
      <span>${icon(locked ? 'lock' : 'file')}<strong>${escapeHtml(document.title)}</strong></span>
      <span>${escapeHtml(document.type)}</span>
      <span>${locked ? '-' : '24/04/2025'}</span>
      <span>${escapeHtml(access)}</span>
    </div>
  `;
}

function renderTimeline(items: InvestorHubTimelineItem[]): string {
  return `
    <section class="journey-panel">
      <div class="panel-heading">
        <div>
          ${icon('flag')}
          <h2>Jornada do investidor</h2>
        </div>
      </div>
      <ol class="journey-steps">
        ${items
          .map(
            (item, index) => `
              <li class="${item.status}">
                <span>${index + 1}</span>
                <strong>${escapeHtml(item.label)}</strong>
                <small>${escapeHtml(item.description)}</small>
              </li>
            `,
          )
          .join('')}
      </ol>
    </section>
  `;
}

function renderFooter(): string {
  return `
    <footer class="portal-footer">
      <span>© 2025 Tá Chegando. Todos os direitos reservados.</span>
      <nav aria-label="Links legais">
        <a href="/">Termos de uso</a>
        <a href="/">Política de privacidade</a>
      </nav>
      <span>Contato: investidores@tachegando.app</span>
    </footer>
  `;
}

function proofIcon(index: number): IconName {
  const icons: IconName[] = ['target', 'users', 'bars', 'trophy'];
  return icons[index % icons.length];
}

type IconName =
  | 'arrow'
  | 'bars'
  | 'chevron'
  | 'document'
  | 'file'
  | 'flag'
  | 'folder'
  | 'lock'
  | 'pie'
  | 'shield'
  | 'target'
  | 'trend'
  | 'trophy'
  | 'users';

function icon(name: IconName): string {
  const paths: Record<IconName, string> = {
    arrow: '<path d="M5 12h14"/><path d="m13 6 6 6-6 6"/>',
    bars: '<path d="M5 19V9"/><path d="M12 19V5"/><path d="M19 19v-7"/>',
    chevron: '<path d="m9 18 6-6-6-6"/>',
    document: '<path d="M7 3h7l5 5v13H7z"/><path d="M14 3v5h5"/><path d="M10 13h7"/><path d="M10 17h5"/>',
    file: '<path d="M7 3h7l5 5v13H7z"/><path d="M14 3v5h5"/>',
    flag: '<path d="M5 21V4"/><path d="M5 5h11l-2 4 2 4H5"/>',
    folder: '<path d="M3 7h7l2 2h9v10H3z"/>',
    lock: '<rect x="6" y="10" width="12" height="10" rx="2"/><path d="M8 10V8a4 4 0 0 1 8 0v2"/>',
    pie: '<path d="M12 3v9h9"/><path d="M20.5 15A9 9 0 1 1 9 3.5"/>',
    shield: '<path d="M12 3 20 6v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z"/><path d="m9 12 2 2 4-5"/>',
    target: '<circle cx="12" cy="12" r="8"/><circle cx="12" cy="12" r="3"/><path d="M12 2v4"/><path d="M12 18v4"/><path d="M2 12h4"/><path d="M18 12h4"/>',
    trend: '<path d="m4 16 5-5 4 4 7-8"/><path d="M15 7h5v5"/>',
    trophy: '<path d="M8 4h8v4a4 4 0 0 1-8 0z"/><path d="M8 6H5a3 3 0 0 0 3 3"/><path d="M16 6h3a3 3 0 0 1-3 3"/><path d="M12 12v5"/><path d="M9 21h6"/><path d="M10 17h4"/>',
    users: '<path d="M16 21v-2a4 4 0 0 0-8 0v2"/><circle cx="12" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/>',
  };

  return `<svg class="icon icon-${name}" viewBox="0 0 24 24" aria-hidden="true">${paths[name]}</svg>`;
}
