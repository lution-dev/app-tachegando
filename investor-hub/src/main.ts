import { investorHubContent } from './data/investorHubContent';
import type { InvestorHubTabId } from './domain/investorHubTypes';
import './styles.css';
import { renderInvestorHub } from './ui/renderInvestorHub';

const root = document.querySelector<HTMLDivElement>('#app');

if (!root) {
  throw new Error('Investor Hub root element #app was not found.');
}

const appRoot = root;
let activeTabId: InvestorHubTabId = 'preInvestment';
const stageContentId = 'stage-content';
const tabObjectiveTargets: Record<InvestorHubTabId, string> = {
  preInvestment: stageContentId,
  negotiation: 'data-room',
  postInvestment: 'investor-journey',
};

function render(): void {
  appRoot.innerHTML = renderInvestorHub(investorHubContent, activeTabId);
  bindTabButtons();
}

function bindTabButtons(): void {
  const buttons = appRoot.querySelectorAll<HTMLButtonElement>('[data-tab-id]');

  buttons.forEach((button) => {
    button.addEventListener('click', () => {
      const nextTabId = button.dataset.tabId as InvestorHubTabId | undefined;

      if (!nextTabId) {
        return;
      }

      const targetId = button.dataset.targetId ?? tabObjectiveTargets[nextTabId] ?? stageContentId;

      activeTabId = nextTabId;
      render();
      focusObjective(targetId);
    });
  });
}

function focusObjective(targetId: string): void {
  const target = document.getElementById(targetId);

  if (!target) {
    return;
  }

  target.scrollIntoView({ block: 'start', behavior: 'auto' });
  target.focus({ preventScroll: true });
}

render();
