export type InvestorHubTabId = 'preInvestment' | 'negotiation' | 'postInvestment';

export type InvestorHubCta = {
  label: string;
  href: string;
  variant: 'primary' | 'secondary';
};

export type InvestorHubMetric = {
  label: string;
  value: string;
  detail: string;
  tone: 'navy' | 'blue' | 'gold' | 'green';
};

export type InvestorHubDocument = {
  title: string;
  type: string;
  description: string;
  href?: string;
  requiresAuth: boolean;
};

export type InvestorHubTimelineItem = {
  label: string;
  description: string;
  status: 'done' | 'current' | 'locked';
};

export type InvestorRoundStageStatus = 'done' | 'current' | 'next' | 'blocked';

export type InvestorRound = {
  name: string;
  status: string;
  targetAmount: string;
  committedAmount: string;
  softCommitments: string;
  instrument: string;
  valuationModel: string;
  closingTarget: string;
  owner: string;
};

export type InvestorRoundStage = {
  label: string;
  count: number;
  amount: string;
  status: InvestorRoundStageStatus;
};

export type InvestorRoundTrackedInvestor = {
  name: string;
  profile: string;
  stage: string;
  ticket: string;
  lastTouch: string;
  nextStep: string;
};

export type InvestorRoundAccessLevel = {
  label: string;
  audience: string;
  documents: string[];
};

export type InvestorRoundManager = {
  currentRound: InvestorRound;
  stages: InvestorRoundStage[];
  trackedInvestors: InvestorRoundTrackedInvestor[];
  accessLevels: InvestorRoundAccessLevel[];
  governance: string[];
};

export type InvestorHubSection = {
  eyebrow: string;
  title: string;
  body: string;
  bullets: string[];
};

export type InvestorHubTab = {
  id: InvestorHubTabId;
  index: string;
  label: string;
  title: string;
  summary: string;
  requiresAuth: boolean;
  sections: InvestorHubSection[];
};

export type InvestorHubContent = {
  hubStatus: string;
  hero: {
    eyebrow: string;
    title: string;
    subtitle: string;
    ctas: InvestorHubCta[];
  };
  metrics: InvestorHubMetric[];
  tabs: InvestorHubTab[];
  documents: InvestorHubDocument[];
  timeline: InvestorHubTimelineItem[];
  roundManager: InvestorRoundManager;
};
