import { describe, expect, it } from 'vitest';
import { escapeHtml, textOrFallback } from './formatters';

describe('textOrFallback', () => {
  it('returns fallback for empty values', () => {
    expect(textOrFallback('', 'Indisponivel')).toBe('Indisponivel');
    expect(textOrFallback('   ', 'Indisponivel')).toBe('Indisponivel');
  });

  it('returns trimmed text when present', () => {
    expect(textOrFallback('  Rodada aberta  ', 'Indisponivel')).toBe('Rodada aberta');
  });
});

describe('escapeHtml', () => {
  it('escapes unsafe HTML characters', () => {
    expect(escapeHtml('<script>alert("x")</script>')).toBe(
      '&lt;script&gt;alert(&quot;x&quot;)&lt;/script&gt;',
    );
  });
});
