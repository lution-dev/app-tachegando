import { describe, expect, it } from 'vitest';
import { investorHubContent } from '../data/investorHubContent';
import { renderInvestorHub } from './renderInvestorHub';

const statementPlaceholders = import.meta.glob('/public/statements/*.pdf', {
  eager: true,
  import: 'default',
  query: '?url',
});
const statementPlaceholderPaths = new Set(
  Object.keys(statementPlaceholders).map((path) => path.replace('/public', '')),
);

describe('renderInvestorHub', () => {
  it('renders a post-investment monitoring dashboard', () => {
    const html = renderInvestorHub(investorHubContent);

    expect(html).toContain('Portal do investidor');
    expect(html).toContain('Acompanhamento do investidor');
    expect(html).toContain('Extratos dos últimos meses');
    expect(html).toContain('Visão consolidada para quem já acompanha a empresa.');
  });

  it('renders the six latest monthly statements newest first', () => {
    const html = renderInvestorHub(investorHubContent);

    const labels = ['Maio 2026', 'Abril 2026', 'Março 2026', 'Fevereiro 2026', 'Janeiro 2026', 'Dezembro 2025'];
    const positions = labels.map((label) => html.indexOf(label));

    positions.forEach((position) => expect(position).toBeGreaterThan(-1));
    expect(positions).toEqual([...positions].sort((a, b) => a - b));
    expect((html.match(/class="statement-card"/g) ?? []).length).toBe(6);
    expect((html.match(new RegExp('href="/statements/', 'g')) ?? []).length).toBe(6);
  });

  it('keeps the useful investor facts in the first screen', () => {
    const html = renderInvestorHub(investorHubContent);

    expect(html).toContain('Ativos hoje (DAU)');
    expect(html).toContain('A validar');
    expect(html).toContain('Online agora');
    expect(html).toContain('32.454');
    expect(html).toContain('Vendedores cadastrados');
    expect(html).toContain('56.325');
    expect(html).toContain('Participações');
    expect(html).toContain('5% / 2% / 1%');
    expect(html).toContain('Contrato');
    expect(html).toContain('Investidor-anjo');
    expect(html).toContain('Base legal');
    expect(html).toContain('LC 182/2021');
  });

  it('does not expose pre-investment pitch navigation on the homepage', () => {
    const html = renderInvestorHub(investorHubContent);

    expect(html).not.toContain('Resumo para decisão');
    expect(html).not.toContain('Apresentação');
    expect(html).not.toContain('Antes do aporte');
    expect(html).not.toContain('Negociação');
    expect(html).not.toContain('Pós-aporte');
    expect(html).not.toContain('pitch-deck');
    expect(html).not.toContain('Pipeline da rodada');
    expect(html).not.toContain('Tenho interesse');
  });

  it('keeps ownership private by initials and percentages only', () => {
    const html = renderInvestorHub(investorHubContent);

    expect(html).toContain('C. T.');
    expect(html).toContain('B. M.');
    expect(html).toContain('A. P. N.');
    expect(html).toContain('5%');
    expect(html).toContain('2%');
    expect(html).toContain('1%');
    expect(html).not.toContain('R$ 30 mil');
    expect(html).not.toContain('R$ 15 mil');
  });

  it('points monthly statements to existing PDF placeholders', () => {
    investorHubContent.investorDashboard.statements.forEach((statement) => {
      expect(statement.href).toMatch(/^\/statements\/.+\.pdf$/);
      expect(statementPlaceholderPaths.has(statement.href)).toBe(true);
    });
  });
});
