import { Component, input, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Photo } from '../../services/photo.service';

@Component({
  selector: 'app-thumbnail-strip',
  imports: [CommonModule],
  template: `
    <div class="w-full overflow-x-auto py-4 bg-white/50 backdrop-blur-sm border-b border-gray-200">
      <div class="flex space-x-6 px-4 min-w-max">
        @for (photo of photos(); track photo.id) {
          <div class="flex items-center space-x-3 group cursor-pointer hover:bg-white/80 p-2 rounded-lg transition-all duration-200">
            <div class="relative w-16 h-16 rounded-md overflow-hidden shadow-sm group-hover:shadow-md transition-shadow">
              <img [src]="photo.imageUrl" [alt]="photo.title" class="w-full h-full object-cover" />
            </div>
            <div class="flex flex-col">
              <span class="text-sm font-medium text-gray-900 group-hover:text-blue-600 transition-colors">{{ photo.title }}</span>
              <span class="text-xs text-gray-500">{{ photo.photographer }}</span>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ThumbnailStripComponent {
  photos = input.required<Photo[]>();
}
