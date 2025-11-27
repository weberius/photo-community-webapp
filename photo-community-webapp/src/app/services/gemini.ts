import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { GoogleGenerativeAI, GenerativeModel } from '@google/generative-ai';
import { environment } from '../../environments/environment';
import { MarkdownService } from './markdown';
import { firstValueFrom } from 'rxjs';

interface PromptConfig {
  title: string;
  file: string;
}

@Injectable({
  providedIn: 'root'
})
export class GeminiService {
  private genAI: GoogleGenerativeAI;
  private model: GenerativeModel;
  private markdownService = inject(MarkdownService);
  private http = inject(HttpClient);

  constructor() {
    this.genAI = new GoogleGenerativeAI(environment.geminiApiKey);
    this.model = this.genAI.getGenerativeModel({ model: 'gemini-3-pro-preview' });
  }

  async analyzeImage(file: File): Promise<string> {
    const base64Image = await this.fileToGenerativePart(file);
    const prompts = await this.loadPrompts();

    const results = await Promise.all(prompts.map(async (item) => {
      const result = await this.model.generateContent([item.prompt, base64Image]);
      const response = await result.response;
      return { title: item.title, content: response.text() };
    }));

    return this.markdownService.generateReport(results);
  }

  private async loadPrompts(): Promise<{ title: string, prompt: string }[]> {
    const config = await firstValueFrom(this.http.get<PromptConfig[]>('assets/prompts.json'));

    return Promise.all(config.map(async (item) => {
      const prompt = await firstValueFrom(this.http.get(`assets/prompts/${item.file}`, { responseType: 'text' }));
      return { title: item.title, prompt };
    }));
  }

  private async fileToGenerativePart(file: File): Promise<{ inlineData: { data: string, mimeType: string } }> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onloadend = () => {
        const base64String = reader.result as string;
        // Remove data URL prefix (e.g., "data:image/jpeg;base64,")
        const base64Data = base64String.split(',')[1];
        resolve({
          inlineData: {
            data: base64Data,
            mimeType: file.type
          }
        });
      };
      reader.onerror = reject;
      reader.readAsDataURL(file);
    });
  }
}
