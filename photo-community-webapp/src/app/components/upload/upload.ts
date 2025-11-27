import { Component, output, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="flex flex-col items-center justify-center p-8 border-2 border-dashed border-gray-300 rounded-lg hover:border-blue-500 transition-colors cursor-pointer bg-gray-50"
         (dragover)="onDragOver($event)" (drop)="onDrop($event)" (click)="fileInput.click()">
      
      <input #fileInput type="file" class="hidden" (change)="onFileSelected($event)" accept="image/*">
      
      <div *ngIf="!previewUrl()" class="text-center">
        <svg class="mx-auto h-12 w-12 text-gray-400" stroke="currentColor" fill="none" viewBox="0 0 48 48" aria-hidden="true">
          <path d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
        </svg>
        <p class="mt-1 text-sm text-gray-600">Klicken oder Bild hierher ziehen</p>
        <p class="mt-1 text-xs text-gray-500">PNG, JPG, GIF bis zu 10MB</p>
      </div>

      <div *ngIf="previewUrl()" class="relative">
        <img [src]="previewUrl()" class="max-h-64 rounded-lg shadow-md object-contain">
        <button (click)="removeImage($event)" class="absolute -top-2 -right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600 focus:outline-none">
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>
    </div>
    
    <div *ngIf="previewUrl()" class="mt-4 flex justify-center">
      <button (click)="upload()" class="px-6 py-2 bg-blue-600 text-white font-semibold rounded-lg shadow-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-75 transition-all disabled:opacity-50 disabled:cursor-not-allowed">
        Bild analysieren
      </button>
    </div>
  `,
  styles: []
})
export class UploadComponent {
  fileSelected = output<File>();
  previewUrl = signal<string | null>(null);
  selectedFile = signal<File | null>(null);

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.handleFile(file);
    }
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.handleFile(file);
    }
  }

  handleFile(file: File) {
    if (file.type.startsWith('image/')) {
      this.selectedFile.set(file);
      const reader = new FileReader();
      reader.onload = () => {
        this.previewUrl.set(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(event: Event) {
    event.stopPropagation();
    this.previewUrl.set(null);
    this.selectedFile.set(null);
  }

  upload() {
    const file = this.selectedFile();
    if (file) {
      this.fileSelected.emit(file);
    }
  }
}
