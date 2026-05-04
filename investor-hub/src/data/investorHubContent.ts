import type { InvestorHubContent } from '../domain/investorHubTypes';

export const investorHubContent: InvestorHubContent = {
  hubStatus: 'Rodada em preparacao',
  hero: {
    eyebrow: 'Hub para investidores anjo',
    title: 'O mapa vivo da economia itinerante.',
    subtitle:
      'Uma area apresentavel para investidores acompanharem tese, documentos, negociacao e resultados do Ta Chegando em um unico lugar.',
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
      label: 'Mercado',
      value: 'local-first',
      detail: 'Vendedores itinerantes conectados a consumidores proximos.',
      tone: 'navy',
    },
    {
      label: 'Modelo',
      value: 'SaaS + B2B',
      detail: 'Assinatura para vendedores e planos de frota.',
      tone: 'blue',
    },
    {
      label: 'Uso do capital',
      value: 'produto',
      detail: 'Produto, geolocalizacao, aquisicao e operacao inicial.',
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
            'O Ta Chegando conecta vendedores itinerantes a consumidores proximos com mapa, alertas, demanda por regiao e ferramentas para vender melhor.',
          bullets: [
            'Consumidor encontra vendedores proximos',
            'Vendedor otimiza rota e recorrencia',
            'Frotas acompanham operacao em tempo real',
          ],
        },
        {
          eyebrow: 'Rodada',
          title: 'Capital para acelerar produto e validacao comercial.',
          body:
            'A rodada anjo financia refinamento do app, dados de demanda, funil de vendedores e preparacao das primeiras frotas.',
          bullets: ['Produto e geolocalizacao', 'Aquisicao local', 'Operacao e suporte inicial'],
        },
      ],
    },
    {
      id: 'negotiation',
      index: '02',
      label: 'Negociacao',
      title: 'Data room e due diligence',
      summary:
        'Documentos, termos, perguntas, agenda e status de negociacao em uma area preparada para convite.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Data room',
          title: 'Documentos sensiveis ficam em area protegida.',
          body:
            'Na V1, mostramos o preview do fluxo. Na evolucao, investidores convidados acessam arquivos e status por permissao.',
          bullets: ['Deck completo', 'Termos da rodada', 'Perguntas de diligencia'],
        },
      ],
    },
    {
      id: 'postInvestment',
      index: '03',
      label: 'Pos-aporte',
      title: 'Prestacao de contas e proximos marcos',
      summary:
        'Updates mensais, indicadores, uso do dinheiro, riscos e decisoes que investidores precisam acompanhar.',
      requiresAuth: true,
      sections: [
        {
          eyebrow: 'Governanca',
          title: 'Depois do aporte, o Hub vira cadencia de acompanhamento.',
          body:
            'A area pos-aporte organiza resultados, aprendizados, proximos marcos e uso do capital investido.',
          bullets: ['Updates mensais', 'Indicadores de crescimento', 'Uso do capital por frente'],
        },
      ],
    },
  ],
  timeline: [
    {
      label: 'Pitch e tese',
      description: 'Problema, solucao, mercado, tracao e pedido.',
      status: 'current',
    },
    {
      label: 'Due diligence',
      description: 'Documentos, perguntas, termos e proximas reunioes.',
      status: 'locked',
    },
    {
      label: 'Prestacao de contas',
      description: 'Metricas, marcos, uso do capital e updates mensais.',
      status: 'locked',
    },
  ],
  documents: [
    {
      title: 'Deck da rodada',
      type: 'PDF',
      description: 'Narrativa completa para reunioes de captacao.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Resumo executivo',
      type: '1P',
      description: 'One-pager para compartilhamento rapido.',
      href: '#',
      requiresAuth: false,
    },
    {
      title: 'Uso do capital',
      type: '$',
      description: 'Plano de alocacao por frente de crescimento.',
      requiresAuth: true,
    },
    {
      title: 'FAQ do investidor',
      type: 'Q',
      description: 'Perguntas comuns sobre modelo, risco e mercado.',
      href: '#',
      requiresAuth: false,
    },
  ],
};
