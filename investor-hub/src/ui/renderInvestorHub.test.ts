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

    expect(html).toContain('data-tab-id="negotiation" data-target-id="data-room" aria-selected="true"');
    expect(html).toContain(
      'data-tab-id="preInvestment" data-target-id="stage-content" aria-selected="false"',
    );
  });

  it('links each tab to the objective it represents', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('aria-controls="stage-content"');
    expect(html).toContain('aria-controls="data-room"');
    expect(html).toContain('aria-controls="investor-journey"');
    expect(html).toContain('id="stage-content"');
    expect(html).toContain('id="data-room"');
    expect(html).toContain('id="investor-journey"');
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

  it('aligns the round snapshot with the revised business plan terms', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html).toContain('R$ 375 mil');
    expect(html).toContain('Contrato de investidor-anjo');
    expect(html).toContain('LC 182/2021');
  });

  it('prioritizes round management before the selected stage content', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html.indexOf('Gestão da rodada')).toBeLessThan(html.indexOf('Resumo executivo'));
  });

  it('renders revised business plan context without exposing protected details', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('Business Plan revisado');
    expect(html).toContain('Marketplace de proximidade');
    expect(html).toContain('Dados financeiros e jurídicos protegidos');
    expect(html).toContain('Base e receita de lançamento');
    expect(html).toContain('Interessado qualificado');

    const protectedFragments = [
      ['66', '543', '238/0001-71'].join('.'),
      ['R$ 3', '143', '700,00'].join('.'),
      ['Ya', 'kult'].join(''),
      ['Mary', ' Kay'].join(''),
    ];

    protectedFragments.forEach((fragment) => {
      expect(html).not.toContain(fragment);
    });
  });

  it('renders investor pipeline stages and tracked investors', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');

    expect(html).toContain('Pipeline da rodada');
    expect(html).toContain('Term sheet');
    expect(html).toContain('C. T.');
    expect(html).toContain('B. M.');
    expect(html).toContain('A. P. N.');
    expect(html).toContain('Próximo passo');
  });

  it('locks the revised business plan in the data room', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');
    const rowStart = html.indexOf('<strong>Business Plan revisado</strong>');
    const row = html.slice(Math.max(0, rowStart - 250), rowStart + 500);

    expect(rowStart).toBeGreaterThan(-1);
    expect(row).toContain('icon-lock');
    expect(row).toContain('DOCX');
    expect(row).toContain('Restrito');
  });

  it('renders data room access levels for round governance', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Níveis de acesso');
    expect(html).toContain('Interessado qualificado');
    expect(html).toContain('Investidor confirmado');
  });
});
