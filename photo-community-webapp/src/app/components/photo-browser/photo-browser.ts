import { Component, inject, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ThumbnailStripComponent } from '../thumbnail-strip/thumbnail-strip.component';
import { PhotoService } from '../../services/photo.service';

@Component({
  selector: 'app-photo-browser',
  imports: [CommonModule, ThumbnailStripComponent],
  templateUrl: './photo-browser.html',
  styleUrl: './photo-browser.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PhotoBrowser {
  private photoService = inject(PhotoService);
  photos = this.photoService.photos;
}
