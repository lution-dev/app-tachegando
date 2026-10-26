import { investorHubContent } from './data/investorHubContent';
import './styles.css';
import { renderInvestorHub } from './ui/renderInvestorHub';

const root = document.querySelector<HTMLDivElement>('#app');

if (!root) {
  throw new Error('Investor Hub root element #app was not found.');
}

root.innerHTML = renderInvestorHub(investorHubContent);
