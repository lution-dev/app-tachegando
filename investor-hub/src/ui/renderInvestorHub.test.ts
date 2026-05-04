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

  it('links tab controls to the objective content section', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('aria-controls="stage-content"');
    expect(html).toContain('id="stage-content"');
    expect(html).toContain('tabindex="-1"');
  });

  it('shows protected state for auth-only tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Acesso restrito');
    expect(html).toContain('Área protegida');
  });

  it('renders the current round management snapshot', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html).toContain('Gestão da rodada');
    expect(html).toContain(investorHubContent.roundManager.currentRound.name);
    expect(html).toContain(investorHubContent.roundManager.currentRound.targetAmount);
    expect(html).toContain(investorHubContent.roundManager.currentRound.committedAmount);
  });

  it('prioritizes round management before the selected stage content', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html.indexOf('Gestão da rodada')).toBeLessThan(html.indexOf('Resumo executivo'));
  });

  it('renders investor pipeline stages and tracked investors', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('Pipeline da rodada');
    expect(html).toContain('Term sheet');
    expect(html).toContain('Camila Torres');
    expect(html).toContain('Próximo passo');
  });

  it('renders data room access levels for round governance', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Níveis de acesso');
    expect(html).toContain('Interessado qualificado');
    expect(html).toContain('Investidor confirmado');
  });
});
