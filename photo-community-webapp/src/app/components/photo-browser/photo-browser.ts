import { Component, signal, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Photo {
  id: number;
  title: string;
  photographer: string;
  date: string;
  imageUrl: string;
  likes: number;
  comments: number;
}

@Component({
  selector: 'app-photo-browser',
  imports: [CommonModule],
  templateUrl: './photo-browser.html',
  styleUrl: './photo-browser.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PhotoBrowser {
  photos = signal<Photo[]>([
    {
      id: 1,
      title: 'Sonnenuntergang am Meer',
      photographer: 'Anna Schmidt',
      date: '2025-11-20',
      imageUrl: '/picture1.jpg',
      likes: 142,
      comments: 23
    },
    {
      id: 2,
      title: 'Berglandschaft im Nebel',
      photographer: 'Max Müller',
      date: '2025-11-18',
      imageUrl: '/picture2.jpg',
      likes: 98,
      comments: 15
    },
    {
      id: 3,
      title: 'Stadtleben bei Nacht',
      photographer: 'Lisa Weber',
      date: '2025-11-15',
      imageUrl: '/picture3.jpg',
      likes: 215,
      comments: 34
    },
    {
      id: 4,
      title: 'Herbstwald',
      photographer: 'Tom Fischer',
      date: '2025-11-12',
      imageUrl: '/picture4.jpg',
      likes: 167,
      comments: 28
    }
  ]);
}
