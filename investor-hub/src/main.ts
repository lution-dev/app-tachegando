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

function render(): void {
  appRoot.innerHTML = renderInvestorHub(investorHubContent, activeTabId);
  bindTabButtons();
}

function bindTabButtons(): void {
  const buttons = appRoot.querySelectorAll<HTMLButtonElement>('[data-tab-id]');

  buttons.forEach((button) => {
    button.addEventListener('click', () => {
      const nextTabId = button.dataset.tabId as InvestorHubTabId | undefined;

      if (!nextTabId || nextTabId === activeTabId) {
        return;
      }

      activeTabId = nextTabId;
      render();
    });
  });
}

render();
