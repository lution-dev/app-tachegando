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
};
