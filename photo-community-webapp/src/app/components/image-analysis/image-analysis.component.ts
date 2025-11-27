import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadComponent } from '../upload/upload';
import { AnalysisComponent } from '../analysis/analysis';
import { GeminiService } from '../../services/gemini';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-image-analysis',
  standalone: true,
  imports: [CommonModule, UploadComponent, AnalysisComponent, ModalComponent],
  template: `
    <div class="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-4xl mx-auto">
        <div class="text-center mb-12">
          <h1 class="text-4xl font-bold text-gray-900 mb-4">Bildbesprechung</h1>
          <p class="text-xl text-gray-600">Lade ein Foto hoch und erhalte eine detaillierte Analyse.</p>
        </div>

        <app-upload (fileSelected)="onFileSelected($event)" *ngIf="!analyzing() && !analysisResult()"></app-upload>

        <div *ngIf="analyzing()" class="flex flex-col items-center justify-center mt-12">
          <div class="animate-spin rounded-full h-16 w-16 border-t-2 border-b-2 border-blue-500 mb-4"></div>
          <p class="text-lg text-gray-700">Bild wird analysiert...</p>
          <p class="text-sm text-gray-500 mt-2">Dies kann einen Moment dauern.</p>
        </div>

        <app-analysis *ngIf="analysisResult()" [markdown]="analysisResult()!"></app-analysis>
        
        <div *ngIf="analysisResult()" class="mt-8 text-center">
          <button (click)="reset()" class="text-blue-600 hover:text-blue-800 font-medium">
            Neues Bild analysieren
          </button>
        </div>
      </div>

      <app-modal
        [isOpen]="showErrorModal()"
        title="Fehler"
        [message]="errorMessage()"
        (close)="closeErrorModal()"
      ></app-modal>
    </div>
  `,
  styles: []
})
export class ImageAnalysisComponent {
  private geminiService = inject(GeminiService);

  analyzing = signal(false);
  analysisResult = signal<string | null>(null);
  showErrorModal = signal(false);
  errorMessage = signal('');

  async onFileSelected(file: File) {
    this.analyzing.set(true);
    try {
      const result = await this.geminiService.analyzeImage(file);
      this.analysisResult.set(result);
    } catch (error: any) {
      console.error('Analysis failed', error);

      let message = 'Ein unerwarteter Fehler ist aufgetreten.';

      if (error.message?.includes('API_KEY_INVALID') ||
        JSON.stringify(error).includes('API_KEY_INVALID')) {
        message = 'Das Bild konnte aus technischen Gründen nicht verabeitet werden.\nKein zulässiger API_KEY gefunden.';
      }

      this.errorMessage.set(message);
      this.showErrorModal.set(true);
    } finally {
      this.analyzing.set(false);
    }
  }

  closeErrorModal() {
    this.showErrorModal.set(false);
  }

  reset() {
    this.analysisResult.set(null);
    this.analyzing.set(false);
  }
}
