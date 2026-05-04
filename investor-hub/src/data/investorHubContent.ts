import type { InvestorHubContent } from '../domain/investorHubTypes';

export const investorHubContent: InvestorHubContent = {
  hubStatus: 'Rodada em preparação',
  hero: {
    eyebrow: 'Hub para investidores anjo',
    title: 'O mapa vivo da economia itinerante.',
    subtitle:
      'Uma área apresentável para investidores acompanharem tese, documentos, negociação e resultados do Tá Chegando em um único lugar.',
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
      label: 'Vendedores ativos',
      value: '+18 mil',
      detail: 'Base potencial mapeada para validação comercial.',
      tone: 'navy',
    },
    {
      label: 'Consumidores únicos',
      value: '+2,6 mi',
      detail: 'Alcance urbano inicial em praças prioritárias.',
      tone: 'blue',
    },
    {
      label: 'Notificações/mês',
      value: '+410 mil',
      detail: 'Potencial de recorrência via alertas de proximidade.',
      tone: 'green',
    },
    {
      label: 'Avaliação média',
      value: '4,8',
      detail: 'Experiência validada nos primeiros ciclos de uso.',
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
            'O Tá Chegando conecta vendedores itinerantes a consumidores próximos com mapa, alertas, demanda por região e ferramentas para vender melhor.',
          bullets: [
            'Conectamos vendedores itinerantes a consumidores em tempo real.',
            'Modelo de assinatura para vendedores e marketplace de alcance.',
            'Mercado grande, fragmentado e em transformação digital.',
            'Time com execução comprovada e produto validado.',
          ],
        },
        {
          eyebrow: 'Rodada',
          title: 'Capital para acelerar produto e validação comercial.',
          body:
            'A rodada anjo financia refinamento do app, dados de demanda, funil de vendedores e preparação das primeiras frotas.',
          bullets: ['Produto e geolocalização', 'Aquisição local', 'Operação e suporte inicial'],
        },
      ],
    },
    {
      id: 'negotiation',
      index: '02',
      label: 'Negociação',
      title: 'Data room e due diligence',
      summary:
        'Documentos, termos, perguntas, agenda e status de negociação em uma área preparada para convite.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Data room',
          title: 'Documentos sensíveis ficam em área protegida.',
          body:
            'Na V1, mostramos o preview do fluxo. Na evolução, investidores convidados acessam arquivos e status por permissão.',
          bullets: ['Deck completo', 'Termos da rodada', 'Perguntas de diligência'],
        },
      ],
    },
    {
      id: 'postInvestment',
      index: '03',
      label: 'Pós-aporte',
      title: 'Prestação de contas e próximos marcos',
      summary:
        'Updates mensais, indicadores, uso do dinheiro, riscos e decisões que investidores precisam acompanhar.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Governança',
          title: 'Depois do aporte, o Hub vira cadência de acompanhamento.',
          body:
            'A área pós-aporte organiza resultados, aprendizados, próximos marcos e uso do capital investido.',
          bullets: ['Updates mensais', 'Indicadores de crescimento', 'Uso do capital por frente'],
        },
      ],
    },
  ],
  timeline: [
    {
      label: 'Pitch e tese',
      description: 'Problema, solução, mercado, tração e pedido.',
      status: 'current',
    },
    {
      label: 'Due diligence',
      description: 'Documentos, perguntas, termos e próximas reuniões.',
      status: 'locked',
    },
    {
      label: 'Prestação de contas',
      description: 'Métricas, marcos, uso do capital e updates mensais.',
      status: 'locked',
    },
  ],
  documents: [
    {
      title: 'Deck da rodada',
      type: 'PDF',
      description: 'Narrativa completa para reuniões de captação.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Resumo executivo',
      type: '1P',
      description: 'One-pager para compartilhamento rápido.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Uso do capital',
      type: 'XLSX',
      description: 'Plano de alocação por frente de crescimento.',
      requiresAuth: true,
    },
    {
      title: 'FAQ do investidor',
      type: 'PDF',
      description: 'Perguntas comuns sobre modelo, risco e mercado.',
      href: '#',
      requiresAuth: false,
    },
  ],
  roundManager: {
    currentRound: {
      name: 'Rodada Anjo 2026',
      status: 'Preparação final',
      targetAmount: 'R$ 1,2 mi',
      committedAmount: 'R$ 420 mil',
      softCommitments: 'R$ 310 mil',
      instrument: 'SAFE / mútuo conversível',
      valuationModel: 'Cap negociável por tranche',
      closingTarget: '31/07/2026',
      owner: 'Gabriel e founding team',
    },
    stages: [
      {
        label: 'Novo interesse',
        count: 18,
        amount: 'R$ 1,8 mi',
        status: 'done',
      },
      {
        label: 'Reunião marcada',
        count: 9,
        amount: 'R$ 920 mil',
        status: 'current',
      },
      {
        label: 'Due diligence',
        count: 5,
        amount: 'R$ 640 mil',
        status: 'current',
      },
      {
        label: 'Term sheet',
        count: 2,
        amount: 'R$ 310 mil',
        status: 'next',
      },
      {
        label: 'Comprometido',
        count: 3,
        amount: 'R$ 420 mil',
        status: 'done',
      },
    ],
    trackedInvestors: [
      {
        name: 'Camila Torres',
        profile: 'Anjo operador, marketplace local',
        stage: 'Term sheet',
        ticket: 'R$ 180 mil',
        lastTouch: 'Revisão de cap enviada',
        nextStep: 'Validar cláusulas de informação',
      },
      {
        name: 'Bruno Martins',
        profile: 'Founder investidor, SaaS B2B',
        stage: 'Due diligence',
        ticket: 'R$ 120 mil',
        lastTouch: 'Acesso ao data room liberado',
        nextStep: 'Responder perguntas sobre CAC',
      },
      {
        name: 'Ana Paula Neri',
        profile: 'Smart money, operação urbana',
        stage: 'Reunião marcada',
        ticket: 'R$ 90 mil',
        lastTouch: 'Convite enviado',
        nextStep: 'Demo do app vendedor',
      },
    ],
    accessLevels: [
      {
        label: 'Público',
        audience: 'Visitantes e indicações iniciais',
        documents: ['Resumo executivo', 'FAQ do investidor'],
      },
      {
        label: 'Interessado qualificado',
        audience: 'Investidores com reunião agendada',
        documents: ['Deck completo', 'Uso do capital', 'Perguntas de diligência'],
      },
      {
        label: 'Investidor confirmado',
        audience: 'Investidores com compromisso assinado',
        documents: ['Term sheet', 'Cap table', 'Updates mensais'],
      },
    ],
    governance: [
      'Atualizar status da rodada toda segunda-feira.',
      'Registrar próxima ação para cada investidor ativo.',
      'Liberar documentos por estágio, não por link público.',
      'Consolidar compromissos assinados antes de comunicar fechamento.',
    ],
  },
};
