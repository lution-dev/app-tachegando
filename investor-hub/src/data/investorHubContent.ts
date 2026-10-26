import type { InvestorHubContent } from '../domain/investorHubTypes';

export const investorHubContent: InvestorHubContent = {
  hubStatus: 'Acompanhamento do investidor',
  contactEmail: 'investidores@tachegando.app',
  hero: {
    eyebrow: 'Portal do investidor',
    title: 'Acompanhamento do investidor',
    subtitle:
      'Visão direta para quem já investiu no Tá Chegando: evolução operacional, participação, base legal e extratos mensais em um só lugar.',
  },
  investorDashboard: {
    eyebrow: 'Extratos dos últimos meses',
    title: 'Extratos dos últimos meses',
    summary: 'Visão consolidada para quem já acompanha a empresa.',
    updatedAt: 'Dados de acompanhamento atualizados em 19/05/2026',
    stats: [
      {
        label: 'Ativos hoje (DAU)',
        value: 'A validar',
        detail: 'O schema atual ainda não registra atividade diária de login de forma confiável.',
      },
      {
        label: 'Online agora',
        value: '32.454',
        detail: 'Número informado para apresentação da rodada.',
      },
      {
        label: 'Vendedores cadastrados',
        value: '56.325',
        detail: 'Base de vendedores informada para apresentação da rodada.',
      },
      {
        label: 'Participações',
        value: '5% / 2% / 1%',
        detail: 'Exibidas por iniciais, sem abrir valores individuais em reais.',
      },
      {
        label: 'Contrato',
        value: 'Investidor-anjo',
        detail: 'Instrumento jurídico de acompanhamento da participação.',
      },
      {
        label: 'Base legal',
        value: 'LC 182/2021',
        detail: 'Referência jurídica usada na estrutura da rodada.',
      },
    ],
    statements: [
      {
        period: 'Maio 2026',
        title: 'Extrato operacional - Maio 2026',
        summary: 'Resumo de operação, uso do capital e próximos pontos de atenção.',
        href: '/statements/extrato-maio-2026.pdf',
        publishedAt: '19/05/2026',
        type: 'PDF',
      },
      {
        period: 'Abril 2026',
        title: 'Extrato operacional - Abril 2026',
        summary: 'Evolução comercial, base operacional e decisões acompanhadas no mês.',
        href: '/statements/extrato-abril-2026.pdf',
        publishedAt: '30/04/2026',
        type: 'PDF',
      },
      {
        period: 'Março 2026',
        title: 'Extrato operacional - Março 2026',
        summary: 'Movimentação da empresa e leitura de tração para investidores.',
        href: '/statements/extrato-marco-2026.pdf',
        publishedAt: '31/03/2026',
        type: 'PDF',
      },
      {
        period: 'Fevereiro 2026',
        title: 'Extrato operacional - Fevereiro 2026',
        summary: 'Atualização mensal com indicadores, aprendizados e governança.',
        href: '/statements/extrato-fevereiro-2026.pdf',
        publishedAt: '28/02/2026',
        type: 'PDF',
      },
      {
        period: 'Janeiro 2026',
        title: 'Extrato operacional - Janeiro 2026',
        summary: 'Registro de evolução do início do ciclo e prioridades operacionais.',
        href: '/statements/extrato-janeiro-2026.pdf',
        publishedAt: '31/01/2026',
        type: 'PDF',
      },
      {
        period: 'Dezembro 2025',
        title: 'Extrato operacional - Dezembro 2025',
        summary: 'Fechamento do ciclo anterior com contexto para acompanhamento atual.',
        href: '/statements/extrato-dezembro-2025.pdf',
        publishedAt: '31/12/2025',
        type: 'PDF',
      },
    ],
    ownership: [
      {
        name: 'C. T.',
        ownership: '5%',
        profile: 'Investidor-anjo',
      },
      {
        name: 'B. M.',
        ownership: '2%',
        profile: 'Investidor-anjo',
      },
      {
        name: 'A. P. N.',
        ownership: '1%',
        profile: 'Investidor-anjo',
      },
    ],
  },
};
