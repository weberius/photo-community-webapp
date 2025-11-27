import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-modal',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div *ngIf="isOpen" class="fixed inset-0 z-50 flex items-center justify-center overflow-x-hidden overflow-y-auto outline-none focus:outline-none">
      <div class="fixed inset-0 bg-gray-500 bg-opacity-75 transition-opacity" (click)="closeModal()"></div>
      
      <div class="relative w-auto max-w-lg mx-auto my-6 z-50">
        <div class="relative flex flex-col w-full bg-white border-0 rounded-lg shadow-lg outline-none focus:outline-none">
          <!-- Header -->
          <div class="flex items-start justify-between p-5 border-b border-solid border-gray-200 rounded-t">
            <h3 class="text-2xl font-semibold text-gray-900">
              {{ title }}
            </h3>
            <button
              class="p-1 ml-auto bg-transparent border-0 text-black opacity-5 float-right text-3xl leading-none font-semibold outline-none focus:outline-none"
              (click)="closeModal()"
            >
              <span class="bg-transparent text-black opacity-5 h-6 w-6 text-2xl block outline-none focus:outline-none">
                ×
              </span>
            </button>
          </div>
          
          <!-- Body -->
          <div class="relative p-6 flex-auto">
            <p class="my-4 text-gray-600 text-lg leading-relaxed whitespace-pre-line">
              {{ message }}
            </p>
          </div>
          
          <!-- Footer -->
          <div class="flex items-center justify-end p-6 border-t border-solid border-gray-200 rounded-b">
            <button
              class="text-red-500 background-transparent font-bold uppercase px-6 py-2 text-sm outline-none focus:outline-none mr-1 mb-1 ease-linear transition-all duration-150 hover:bg-red-50 rounded"
              type="button"
              (click)="closeModal()"
            >
              Schließen
            </button>
          </div>
        </div>
      </div>
    </div>
  `,
    styles: []
})
export class ModalComponent {
    @Input() title: string = '';
    @Input() message: string = '';
    @Input() isOpen: boolean = false;
    @Output() close = new EventEmitter<void>();

    closeModal() {
        this.close.emit();
    }
}
