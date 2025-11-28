import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UploadComponent } from '../upload/upload';
import { PhotoService } from '../../services/photo.service';

@Component({
  selector: 'app-image-analysis',
  standalone: true,
  imports: [CommonModule, UploadComponent],
  template: `
    <div class="min-h-screen bg-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-4xl mx-auto">
        <div class="text-center mb-12">
          <h1 class="text-4xl font-bold text-gray-900 mb-4">Foto Upload</h1>
          <p class="text-xl text-gray-600">Lade ein Foto hoch, um es mit der Community zu teilen.</p>
        </div>

        <app-upload (fileSelected)="onFileSelected($event)"></app-upload>
      </div>
    </div>
  `,
  styles: []
})
export class ImageAnalysisComponent {
  private photoService = inject(PhotoService);
  private router = inject(Router);

  onFileSelected(file: File) {
    // Create a URL for the uploaded file to display it locally
    const imageUrl = URL.createObjectURL(file);

    // Create a new photo object
    const newPhoto = {
      id: Date.now(), // Simple ID generation
      title: file.name.split('.')[0] || 'Neues Foto',
      photographer: 'Du', // Hardcoded for now
      date: new Date().toISOString().split('T')[0],
      imageUrl: imageUrl,
      likes: 0,
      comments: 0
    };

    // Add to service
    this.photoService.addPhoto(newPhoto);

    // Navigate back to browser
    this.router.navigate(['/browser']);
  }
}
