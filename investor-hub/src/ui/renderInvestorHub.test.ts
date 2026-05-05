import { describe, expect, it } from 'vitest';
import { investorHubContent } from '../data/investorHubContent';
import { renderInvestorHub } from './renderInvestorHub';

describe('renderInvestorHub', () => {
  it('renders all four tabs', () => {
    const html = renderInvestorHub(investorHubContent, 'preInvestment');

    expect(html).toContain('Apresentação');
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

    expect(html).toContain('aria-controls="pitch-deck"');
    expect(html).toContain('aria-controls="stage-content"');
    expect(html).toContain('aria-controls="data-room"');
    expect(html).toContain('aria-controls="investor-journey"');
    expect(html).toContain('id="stage-content"');
    expect(html).toContain('id="data-room"');
    expect(html).toContain('id="investor-journey"');
    expect(html).toContain('tabindex="-1"');
  });

  it('shows public state for all tabs when unlocked', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).not.toContain('Acesso restrito');
    expect(html).not.toContain('Área protegida');
    expect(html).toContain('Resumo executivo');
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

  it('shows the revised business plan as public in the data room', () => {
    const html = renderInvestorHub(investorHubContent, 'negotiation');
    const rowStart = html.indexOf('<strong>Business Plan revisado</strong>');
    const row = html.slice(Math.max(0, rowStart - 250), rowStart + 500);

    expect(rowStart).toBeGreaterThan(-1);
    expect(row).toContain('icon-file');
    expect(row).toContain('DOCX');
    expect(row).toContain('Público');
  });

  it('renders data room access levels for round governance', () => {
    const html = renderInvestorHub(investorHubContent, 'postInvestment');

    expect(html).toContain('Níveis de acesso');
    expect(html).toContain('Interessado qualificado');
    expect(html).toContain('Investidor confirmado');
  });

  // ─── Pitch Deck Tests ──────────────────────────────

  it('renders the pitch deck when presentation tab is active', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    expect(html).toContain('pitch-deck');
    expect(html).toContain('pitch-slides-track');
    expect(html).toContain('data-pitch-prev');
    expect(html).toContain('data-pitch-next');
  });

  it('renders all 8 pitch slides', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    expect(html).toContain('Tá Chegando');
    expect(html).toContain('O problema');
    expect(html).toContain('A solução');
    expect(html).toContain('O mercado');
    expect(html).toContain('Tração');
    expect(html).toContain('Modelo de negócio');
    expect(html).toContain('Rodada Anjo 2026');
    expect(html).toContain('Vamos conversar?');
  });

  it('renders slide navigation dots matching the number of slides', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    const dotCount = (html.match(/data-pitch-goto/g) ?? []).length;
    expect(dotCount).toBe(investorHubContent.pitchDeck.length);
  });

  it('renders real metrics inside traction slide', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    expect(html).toContain('+18 mil');
    expect(html).toContain('+2,6 mi');
    expect(html).toContain('+410 mil');
    expect(html).toContain('4,8');
  });

  it('does not render regular panels when in presentation mode', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    expect(html).not.toContain('Gestão da rodada');
    expect(html).not.toContain('Pipeline da rodada');
    expect(html).not.toContain('document-table');
  });

  it('renders CTA button in the last slide', () => {
    const html = renderInvestorHub(investorHubContent, 'presentation');

    expect(html).toContain('pitch-slide-cta');
    expect(html).toContain('Tenho interesse');
    expect(html).toContain('mailto:fundadores@tachegando.app');
  });
});
