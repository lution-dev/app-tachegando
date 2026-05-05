import { investorHubContent } from './data/investorHubContent';
import type { InvestorHubTabId } from './domain/investorHubTypes';
import './styles.css';
import { renderInvestorHub } from './ui/renderInvestorHub';

const root = document.querySelector<HTMLDivElement>('#app');

if (!root) {
  throw new Error('Investor Hub root element #app was not found.');
}

const appRoot = root;
let activeTabId: InvestorHubTabId = 'presentation';
let currentSlide = 0;
const totalSlides = investorHubContent.pitchDeck.length;

const stageContentId = 'stage-content';
const tabObjectiveTargets: Record<InvestorHubTabId, string> = {
  presentation: 'pitch-deck',
  preInvestment: stageContentId,
  negotiation: 'data-room',
  postInvestment: 'investor-journey',
};

function render(): void {
  appRoot.innerHTML = renderInvestorHub(investorHubContent, activeTabId);
  bindTabButtons();

  if (activeTabId === 'presentation') {
    bindPitchNav();
    updatePitchDeck();
  }
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
      currentSlide = 0;
      render();
      focusObjective(targetId);
    });
  });
}

function bindPitchNav(): void {
  const prevBtn = appRoot.querySelector<HTMLButtonElement>('[data-pitch-prev]');
  const nextBtn = appRoot.querySelector<HTMLButtonElement>('[data-pitch-next]');
  const dots = appRoot.querySelectorAll<HTMLButtonElement>('[data-pitch-goto]');

  prevBtn?.addEventListener('click', () => {
    if (currentSlide > 0) {
      currentSlide--;
      updatePitchDeck();
    }
  });

  nextBtn?.addEventListener('click', () => {
    if (currentSlide < totalSlides - 1) {
      currentSlide++;
      updatePitchDeck();
    }
  });

  dots.forEach((dot) => {
    dot.addEventListener('click', () => {
      const index = parseInt(dot.dataset.pitchGoto ?? '0', 10);
      currentSlide = index;
      updatePitchDeck();
    });
  });

  // Keyboard navigation
  const deck = appRoot.querySelector<HTMLElement>('[data-pitch-track]');
  deck?.parentElement?.addEventListener('keydown', (e: KeyboardEvent) => {
    if (e.key === 'ArrowLeft' && currentSlide > 0) {
      currentSlide--;
      updatePitchDeck();
    } else if (e.key === 'ArrowRight' && currentSlide < totalSlides - 1) {
      currentSlide++;
      updatePitchDeck();
    }
  });
}

function updatePitchDeck(): void {
  const track = appRoot.querySelector<HTMLElement>('[data-pitch-track]');
  const dots = appRoot.querySelectorAll<HTMLButtonElement>('[data-pitch-goto]');
  const counter = appRoot.querySelector<HTMLElement>('[data-pitch-current]');
  const prevBtn = appRoot.querySelector<HTMLButtonElement>('[data-pitch-prev]');
  const nextBtn = appRoot.querySelector<HTMLButtonElement>('[data-pitch-next]');

  if (track) {
    track.style.transform = `translateX(-${currentSlide * 100}%)`;
  }

  dots.forEach((dot, i) => {
    dot.classList.toggle('active', i === currentSlide);
  });

  if (counter) {
    counter.textContent = String(currentSlide + 1);
  }

  if (prevBtn) {
    prevBtn.disabled = currentSlide === 0;
    prevBtn.style.transform = 'rotate(180deg)';
  }

  if (nextBtn) {
    nextBtn.disabled = currentSlide === totalSlides - 1;
  }
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
