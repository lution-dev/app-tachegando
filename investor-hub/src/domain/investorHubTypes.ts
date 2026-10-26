export type InvestorDashboardStat = {
  label: string;
  value: string;
  detail: string;
};

export type InvestorMonthlyStatement = {
  period: string;
  title: string;
  summary: string;
  href: string;
  publishedAt: string;
  type: 'PDF';
};

export type InvestorOwnership = {
  name: string;
  ownership: string;
  profile: string;
};

export type InvestorDashboard = {
  eyebrow: string;
  title: string;
  summary: string;
  updatedAt: string;
  stats: InvestorDashboardStat[];
  statements: InvestorMonthlyStatement[];
  ownership: InvestorOwnership[];
};

export type InvestorHubContent = {
  hubStatus: string;
  contactEmail: string;
  hero: {
    eyebrow: string;
    title: string;
    subtitle: string;
  };
  investorDashboard: InvestorDashboard;
};
