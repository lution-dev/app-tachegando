import type {
  InvestorDashboard,
  InvestorDashboardStat,
  InvestorHubContent,
  InvestorMonthlyStatement,
  InvestorOwnership,
} from '../domain/investorHubTypes';
import { escapeHtml } from '../utils/formatters';

export function renderInvestorHub(content: InvestorHubContent): string {
  return `
    <main class="portal-shell">
      ${renderTopbar(content)}
      <section class="dashboard-shell">
        ${renderHero(content)}
        ${renderDashboard(content.investorDashboard)}
      </section>
      ${renderFooter(content.contactEmail)}
    </main>
  `;
}

function renderTopbar(content: InvestorHubContent): string {
  return `
    <header class="topbar">
      <a class="brand" href="/" aria-label="Tá Chegando Portal do investidor">
        <div class="brand-mark" aria-hidden="true">TC</div>
        <div>
          <strong>Tá Chegando</strong>
          <span>Portal do investidor</span>
        </div>
      </a>
      <div class="topbar-actions">
        <span class="status-pill"><span aria-hidden="true"></span>${escapeHtml(content.hubStatus)}</span>
        <a class="contact-link" href="mailto:${escapeHtml(content.contactEmail)}">${escapeHtml(content.contactEmail)}</a>
      </div>
    </header>
  `;
}

function renderHero(content: InvestorHubContent): string {
  return `
    <header class="dashboard-hero">
      <p>${escapeHtml(content.hero.eyebrow)}</p>
      <h1>${escapeHtml(content.hero.title)}</h1>
      <span>${escapeHtml(content.hero.subtitle)}</span>
    </header>
  `;
}

function renderDashboard(dashboard: InvestorDashboard): string {
  return `
    <section class="investor-dashboard" aria-label="${escapeHtml(dashboard.title)}">
      <div class="dashboard-heading">
        <div>
          <p class="section-label">${escapeHtml(dashboard.eyebrow)}</p>
          <h2>${escapeHtml(dashboard.title)}</h2>
          <span>${escapeHtml(dashboard.summary)}</span>
        </div>
        <small>${escapeHtml(dashboard.updatedAt)}</small>
      </div>
      <div class="statement-grid">
        ${dashboard.statements.map(renderStatement).join('')}
      </div>
      <div class="dashboard-facts" aria-label="Indicadores e estrutura da participação">
        ${dashboard.stats.map(renderStat).join('')}
      </div>
      <aside class="ownership-summary" aria-label="Participação por investidor">
        <div>
          <p class="section-label">Participação por investidor</p>
          <h2>Participações atuais</h2>
        </div>
        <div class="ownership-list">
          ${dashboard.ownership.map(renderOwnership).join('')}
        </div>
      </aside>
    </section>
  `;
}

function renderStatement(statement: InvestorMonthlyStatement): string {
  return `
    <article class="statement-card">
      <span>${escapeHtml(statement.period)}</span>
      <strong>${escapeHtml(statement.title)}</strong>
      <p>${escapeHtml(statement.summary)}</p>
      <div>
        <small>${escapeHtml(statement.type)} · publicado em ${escapeHtml(statement.publishedAt)}</small>
        <a href="${escapeHtml(statement.href)}" aria-label="Abrir ${escapeHtml(statement.title)}">Abrir PDF ${icon('arrow')}</a>
      </div>
    </article>
  `;
}

function renderStat(stat: InvestorDashboardStat): string {
  return `
    <article class="fact-card">
      <span>${escapeHtml(stat.label)}</span>
      <strong>${escapeHtml(stat.value)}</strong>
      <small>${escapeHtml(stat.detail)}</small>
    </article>
  `;
}

function renderOwnership(investor: InvestorOwnership): string {
  return `
    <span>
      <strong>${escapeHtml(investor.name)}</strong>
      <em>${escapeHtml(investor.ownership)}</em>
      <small>${escapeHtml(investor.profile)}</small>
    </span>
  `;
}

function renderFooter(contactEmail: string): string {
  return `
    <footer class="portal-footer">
      <span>© 2026 Tá Chegando. Todos os direitos reservados.</span>
      <span>Documentos de acompanhamento para investidores confirmados.</span>
      <a href="mailto:${escapeHtml(contactEmail)}">${escapeHtml(contactEmail)}</a>
    </footer>
  `;
}

function icon(name: 'arrow'): string {
  if (name === 'arrow') {
    return '<svg class="icon icon-arrow" viewBox="0 0 24 24" aria-hidden="true"><path d="M5 12h14"/><path d="m13 6 6 6-6 6"/></svg>';
  }

  return '';
}
