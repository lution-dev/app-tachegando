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
      id: 'presentation',
      index: '00',
      label: 'Apresentação',
      title: 'Pitch deck interativo',
      summary:
        'A narrativa completa do Tá Chegando em slides visuais: problema, solução, mercado, tração, modelo, rodada e próximos passos.',
      requiresAuth: false,
      sections: [],
    },
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
      requiresAuth: false,
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
      requiresAuth: false,
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
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Uso do capital',
      type: 'XLSX',
      description: 'Plano de alocação por frente de crescimento.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Premissas financeiras do lançamento',
      type: 'XLSX',
      description: 'Base, assinatura, receita projetada e sensibilidade de conversão.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Dados societários e contrato',
      type: 'PDF',
      description: 'Dados da empresa, contrato de investidor-anjo e documentos de diligência legal.',
      href: '#',
      requiresAuth: false,
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
  pitchDeck: [
    {
      id: 'cover',
      theme: 'cover',
      cssBackground: 'linear-gradient(135deg, #070d2d 0%, #1B2A4A 40%, #ff4f12 100%)',
      title: 'Tá Chegando',
      subtitle: 'O mapa vivo da economia itinerante.',
      bullets: [
        'Rodada Anjo 2026 — R$ 375 mil',
        'Contrato de investidor-anjo · LC 182/2021',
        'Produto pronto · Lançamento imediato',
      ],
    },
    {
      id: 'problem',
      theme: 'photo',
      cssBackground: 'linear-gradient(180deg, rgba(7,13,45,.82) 0%, rgba(7,13,45,.68) 100%), url(/slides/slide-02-problem.png) center/cover',
      title: 'O problema',
      subtitle: 'A rua tem oferta, demanda e pouca inteligência de rota.',
      bullets: [
        '+18 mil vendedores itinerantes mapeados, sem ferramentas digitais',
        'Consumidores não sabem quando o vendedor passará na sua rua',
        'Sem dados de demanda, vendedores perdem tempo e receita',
        'Mercado fragmentado, analógico e sem solução dominante',
      ],
    },
    {
      id: 'solution',
      theme: 'dark',
      cssBackground: 'linear-gradient(135deg, #070d2d 0%, #0f1a3d 50%, #1a2847 100%)',
      title: 'A solução',
      subtitle: 'Conectamos vendedores itinerantes a consumidores em tempo real.',
      bullets: [
        'Mapa em tempo real com geolocalização e alertas de proximidade',
        'Heatmap de demanda para otimizar rotas e aumentar vendas',
        'Notificações push para consumidores quando o vendedor se aproxima',
        'Gestão de frotas para donos de múltiplos veículos',
      ],
    },
    {
      id: 'market',
      theme: 'photo',
      cssBackground: 'linear-gradient(180deg, rgba(7,13,45,.78) 0%, rgba(7,13,45,.65) 100%), url(/slides/slide-04-market.png) center/cover',
      title: 'O mercado',
      subtitle: 'Mercado grande, fragmentado e em transformação digital.',
      bullets: [
        'Brasil: +6 milhões de vendedores ambulantes e itinerantes',
        'Segmento ignorado por apps de delivery e e-commerce',
        'Economia de rua movimenta R$ 18 bi/ano no comércio informal urbano',
        'Expansão natural para LATAM e vendas porta a porta',
      ],
      metrics: [
        { label: 'TAM Brasil', value: 'R$ 18 bi' },
        { label: 'Vendedores ambulantes', value: '6 mi+' },
        { label: 'Penetração digital', value: '<2%' },
      ],
    },
    {
      id: 'traction',
      theme: 'dark',
      cssBackground: 'linear-gradient(135deg, #070d2d 0%, #0d1633 60%, #1a1f3d 100%)',
      title: 'Tração',
      subtitle: 'Crescimento consistente em vendedores, consumidores e engajamento.',
      bullets: [
        'Base potencial mapeada para validação comercial',
        'Alcance urbano inicial em praças prioritárias',
        'Potencial de recorrência via alertas de proximidade',
        'Experiência validada nos primeiros ciclos de uso',
      ],
      metrics: [
        { label: 'Vendedores ativos', value: '+18 mil' },
        { label: 'Consumidores únicos', value: '+2,6 mi' },
        { label: 'Notificações/mês', value: '+410 mil' },
        { label: 'Avaliação média', value: '4,8' },
      ],
    },
    {
      id: 'model',
      theme: 'dark',
      cssBackground: 'linear-gradient(135deg, #070d2d 0%, #111b3a 50%, #0d1633 100%)',
      title: 'Modelo de negócio',
      subtitle: 'Assinatura para vendedores + marketplace de alcance.',
      bullets: [
        'SaaS: assinatura mensal para vendedores com rotas otimizadas',
        'Marketplace: alcance premium e destaque por região',
        'Frotas: gestão B2B para donos de múltiplos veículos',
        'Dados: inteligência de demanda por bairro e horário',
      ],
      metrics: [
        { label: 'Receita principal', value: 'SaaS + Marketplace' },
        { label: 'Ticket vendedor', value: 'R$ 49–149/mês' },
        { label: 'Margem projetada', value: '72%' },
      ],
    },
    {
      id: 'round',
      theme: 'accent',
      cssBackground: 'linear-gradient(135deg, #ff4f12 0%, #df3d00 40%, #1B2A4A 100%)',
      title: 'Rodada Anjo 2026',
      subtitle: 'Capital para acelerar produto e validação comercial.',
      bullets: [
        'Produto e geolocalização — refinamento do app e dados de demanda',
        'Aquisição local — funil de vendedores e playbook de campo',
        'Operação e suporte inicial — primeiras frotas e atendimento',
      ],
      metrics: [
        { label: 'Meta', value: 'R$ 375 mil' },
        { label: 'Instrumento', value: 'Anjo · LC 182' },
        { label: 'Soft commits', value: '25 cotas' },
        { label: 'Fechamento', value: 'Imediato' },
      ],
    },
    {
      id: 'cta',
      theme: 'cover',
      cssBackground: 'linear-gradient(135deg, #1B2A4A 0%, #070d2d 50%, #ff4f12 100%)',
      title: 'Vamos conversar?',
      subtitle: 'Entre em contato para agendar uma reunião com os fundadores.',
      bullets: [
        'Deck completo e resumo executivo disponíveis',
        'Data room com documentos de diligência',
        'Reunião de apresentação em 30 minutos',
      ],
      ctaLabel: 'Tenho interesse',
      ctaHref: 'mailto:fundadores@tachegando.app?subject=Interesse%20em%20investir%20no%20Ta%20Chegando',
    },
  ],
};
