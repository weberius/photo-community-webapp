import { Injectable } from '@angular/core';

export interface AnalysisResult {
  title: string;
  content: string;
}

@Injectable({
  providedIn: 'root'
})
export class MarkdownService {

  generateReport(results: AnalysisResult[]): string {
    let markdown = '# Bildbesprechung\n\n';

    // Add a timestamp or other metadata if needed
    const date = new Date().toLocaleDateString('de-DE');
    markdown += `*Erstellt am ${date}*\n\n---\n\n`;

    results.forEach(section => {
      markdown += `## ${section.title}\n\n${section.content}\n\n`;
    });

    return markdown;
  }
}
