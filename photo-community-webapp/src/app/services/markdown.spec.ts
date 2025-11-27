import { TestBed } from '@angular/core/testing';
import { MarkdownService } from './markdown';

describe('MarkdownService', () => {
  let service: MarkdownService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MarkdownService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should generate report', () => {
    const results = [
      { title: 'Section 1', content: 'Content 1' },
      { title: 'Section 2', content: 'Content 2' }
    ];

    const report = service.generateReport(results);

    expect(report).toContain('# Bildbesprechung');
    expect(report).toContain('## Section 1');
    expect(report).toContain('Content 1');
    expect(report).toContain('## Section 2');
    expect(report).toContain('Content 2');
  });
});
