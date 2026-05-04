import { describe, expect, it } from 'vitest';
import { investorHubContent } from '../data/investorHubContent';
import { renderInvestorHub } from './renderInvestorHub';

describe('renderInvestorHub', () => {
  it('renders all three tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html).toContain('Antes do aporte');
    expect(html).toContain('Negociação');
    expect(html).toContain('Pós-aporte');
  });

  it('marks the active tab with aria-selected', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('data-tab-id="negotiation" aria-selected="true"');
    expect(html).toContain('data-tab-id="preInvestment" aria-selected="false"');
  });

  it('shows protected state for auth-only tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Acesso restrito');
    expect(html).toContain('Área protegida');
  });
});
