import { Component, input, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-analysis',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="bg-white shadow-lg rounded-lg p-8 mt-8 relative group">
      <button (click)="copyToClipboard()" 
              class="absolute top-4 right-4 p-2 text-gray-500 hover:text-blue-600 bg-gray-100 hover:bg-blue-50 rounded-full transition-all opacity-0 group-hover:opacity-100 focus:opacity-100"
              title="Markdown kopieren">
        <span *ngIf="!copied()">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z"></path>
          </svg>
        </span>
        <span *ngIf="copied()" class="text-green-600 flex items-center">
          <svg class="w-5 h-5 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path>
          </svg>
        </span>
      </button>
      
      <div class="prose max-w-none whitespace-pre-wrap">
        {{ markdown() }}
      </div>
    </div>
  `,
  styles: []
})
export class AnalysisComponent {
  markdown = input.required<string>();
  copied = signal(false);

  async copyToClipboard() {
    try {
      await navigator.clipboard.writeText(this.markdown());
      this.copied.set(true);
      setTimeout(() => this.copied.set(false), 2000);
    } catch (err) {
      console.error('Failed to copy text: ', err);
    }
  }
}
