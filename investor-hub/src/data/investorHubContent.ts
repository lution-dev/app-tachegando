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
        'Pitch, tese, mercado, tração, uso de capital e chamada clara para investidores interessados.',
      requiresAuth: false,
      sections: [
        {
          eyebrow: 'Oportunidade',
          title: 'A rua tem oferta, demanda e pouca inteligência de rota.',
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
      title: 'Business Plan revisado',
      type: 'DOCX',
      description: 'Plano de negócio completo com premissas comerciais, expansão e proposta da rodada.',
      requiresAuth: true,
    },
    {
      title: 'Uso do capital',
      type: 'XLSX',
      description: 'Plano de alocação por frente de crescimento.',
      requiresAuth: true,
    },
    {
      title: 'Premissas financeiras do lançamento',
      type: 'XLSX',
      description: 'Base, assinatura, receita projetada e sensibilidade de conversão.',
      requiresAuth: true,
    },
    {
      title: 'Dados societários e contrato',
      type: 'PDF',
      description: 'Dados da empresa, contrato de investidor-anjo e documentos de diligência legal.',
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
  businessPlan: {
    title: 'Business Plan revisado',
    summary:
      'O plano foi reorganizado para leitura de investidores: tese, validação, lançamento, expansão, proposta da rodada e diligência jurídica ficam em uma trilha única.',
    publicHighlights: [
      {
        label: 'Tese',
        title: 'Marketplace de proximidade + SaaS',
        detail:
          'O Tá Chegando organiza a economia itinerante conectando vendedores em rota a compradores próximos com geolocalização, alertas e gestão operacional.',
      },
      {
        label: 'Validação',
        title: 'Itinerância dinâmica e vendas diretas',
        detail:
          'O Business Plan aponta validação com canais B2B e operação de campo; nomes, acordos e termos comerciais ficam restritos ao data room.',
      },
      {
        label: 'Lançamento',
        title: 'Produto pronto para ativação comercial',
        detail:
          'A próxima etapa é iniciar campo, suporte aos vendedores, playbook de expansão e gestão de frotas com dados em tempo real.',
      },
    ],
    protectedHighlights: [
      {
        label: 'Interessado qualificado',
        title: 'Base e receita de lançamento',
        detail:
          'Premissas de base pré-cadastrada, assinatura, conversão e receita mensal ficam protegidas por convite.',
      },
      {
        label: 'Negociação',
        title: 'Proposta de aporte',
        detail:
          'Cotas, retorno esperado e condições econômicas devem ser liberados somente no estágio de negociação.',
      },
      {
        label: 'Diligência legal',
        title: 'Dados financeiros e jurídicos protegidos',
        detail:
          'Dados societários, contrato e documentos da empresa não ficam públicos na área aberta do hub.',
      },
    ],
    milestones: [
      {
        period: 'Imediato',
        title: 'Lançamento em São Paulo',
        description: 'Ativação comercial, operação de campo e suporte aos primeiros vendedores.',
      },
      {
        period: '6 meses',
        title: 'Licenciamento LATAM e frotas',
        description: 'Abrir conversas regionais e consolidar gestão de múltiplos vendedores.',
      },
      {
        period: '1 ano',
        title: 'Expansão nacional',
        description: 'Replicar o playbook operacional em outros estados brasileiros.',
      },
      {
        period: '2-5 anos',
        title: 'Conversão e liderança regional',
        description: 'Preparar maturidade operacional, eventos contratuais e possível saída estratégica.',
      },
    ],
  },
  roundManager: {
    currentRound: {
      name: 'Rodada Anjo 2026',
      status: 'Preparação final',
      targetAmount: 'R$ 375 mil',
      committedAmount: 'Em qualificação',
      softCommitments: '25 cotas',
      instrument: 'Contrato de investidor-anjo',
      valuationModel: 'LC 182/2021; termos econômicos no data room',
      closingTarget: 'Lançamento imediato',
      owner: 'Founding team',
    },
    stages: [
      {
        label: 'Novo interesse',
        count: 18,
        amount: 'R$ 270 mil',
        status: 'done',
      },
      {
        label: 'Reunião marcada',
        count: 9,
        amount: 'R$ 135 mil',
        status: 'current',
      },
      {
        label: 'Due diligence',
        count: 5,
        amount: 'R$ 75 mil',
        status: 'current',
      },
      {
        label: 'Term sheet',
        count: 2,
        amount: 'R$ 30 mil',
        status: 'next',
      },
      {
        label: 'Comprometido',
        count: 0,
        amount: 'R$ 0',
        status: 'next',
      },
    ],
    trackedInvestors: [
      {
        name: 'C. T.',
        profile: 'Anjo operador, marketplace local',
        stage: 'Term sheet',
        ticket: 'R$ 30 mil',
        lastTouch: 'Revisão de cap enviada',
        nextStep: 'Validar cláusulas de informação',
      },
      {
        name: 'B. M.',
        profile: 'Founder investidor, SaaS B2B',
        stage: 'Due diligence',
        ticket: 'R$ 15 mil',
        lastTouch: 'Acesso ao data room liberado',
        nextStep: 'Responder perguntas sobre CAC',
      },
      {
        name: 'A. P. N.',
        profile: 'Smart money, operação urbana',
        stage: 'Reunião marcada',
        ticket: 'R$ 15 mil',
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
        documents: ['Business Plan revisado', 'Uso do capital', 'Premissas financeiras'],
      },
      {
        label: 'Investidor confirmado',
        audience: 'Investidores com compromisso assinado',
        documents: ['Dados societários e contrato', 'Term sheet', 'Updates mensais'],
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
