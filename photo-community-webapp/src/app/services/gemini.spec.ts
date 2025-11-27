import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { GeminiService } from './gemini';
import { MarkdownService } from './markdown';
import { vi } from 'vitest';

// Mock the GoogleGenerativeAI class
vi.mock('@google/generative-ai', () => {
  const generateContentMock = vi.fn().mockResolvedValue({
    response: Promise.resolve({
      text: () => 'AI Response'
    })
  });

  class GoogleGenerativeAIMock {
    getGenerativeModel = vi.fn().mockReturnValue({
      generateContent: generateContentMock
    });
  }

  return {
    GoogleGenerativeAI: GoogleGenerativeAIMock
  };
});

describe('GeminiService', () => {
  let service: GeminiService;
  let httpMock: HttpTestingController;
  let markdownServiceMock: any;

  beforeEach(() => {
    markdownServiceMock = {
      generateReport: vi.fn().mockReturnValue('Markdown Report')
    };

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: MarkdownService, useValue: markdownServiceMock }
      ]
    });
    service = TestBed.inject(GeminiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    vi.restoreAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should analyze image', async () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });

    // Mock fileToGenerativePart to avoid FileReader async issues
    vi.spyOn(service as any, 'fileToGenerativePart').mockResolvedValue({
      inlineData: { data: 'base64', mimeType: 'image/jpeg' }
    });

    const promise = service.analyzeImage(file);

    // Wait for async code to reach http call
    await new Promise(resolve => setTimeout(resolve, 0));

    // Expect prompts.json request
    const req1 = httpMock.expectOne('assets/prompts.json');
    req1.flush([{ title: 'Test', file: 'test.txt' }]);

    // Wait for promise resolution after first flush
    await new Promise(resolve => setTimeout(resolve, 0));

    // Expect prompt text request
    const req2 = httpMock.expectOne('assets/prompts/test.txt');
    req2.flush('Prompt content');

    const result = await promise;

    expect(result).toBe('Markdown Report');
    expect(markdownServiceMock.generateReport).toHaveBeenCalled();
  });

  it('should handle file reading', async () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });

    // Mock FileReader class
    class MockFileReader {
      readAsDataURL = vi.fn();
      onloadend: any = null;
      result = 'data:image/jpeg;base64,test';

      constructor() {
        mockReader = this;
      }
    }

    let mockReader: any;
    vi.spyOn(window, 'FileReader').mockImplementation(MockFileReader as any);

    // We need to mock loadPrompts to avoid HTTP calls or just let it fail?
    // We only want to test fileToGenerativePart.
    // We can call fileToGenerativePart directly via casting.

    const promise = (service as any).fileToGenerativePart(file);

    if (mockReader.onloadend) {
      mockReader.onloadend();
    }

    const result = await promise;

    expect(result).toEqual({
      inlineData: {
        data: 'test',
        mimeType: 'image/jpeg'
      }
    });
  });
});
