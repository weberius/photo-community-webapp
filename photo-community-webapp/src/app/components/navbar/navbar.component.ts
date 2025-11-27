import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex-shrink-0 flex items-center">
              <span class="text-xl font-bold text-gray-800">ZfpF</span>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <a routerLink="/bildbesprechung" 
                 routerLinkActive="border-blue-500 text-gray-900" 
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Bild+
              </a>
              <a routerLink="/browser" 
                 routerLinkActive="border-blue-500 text-gray-900" 
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Browser
              </a>
              <a routerLink="/review" 
                 routerLinkActive="border-blue-500 text-gray-900" 
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Review
              </a>
              <a routerLink="/discussion" 
                 routerLinkActive="border-blue-500 text-gray-900" 
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Discussion
              </a>
              <a routerLink="/research" 
                 routerLinkActive="border-blue-500 text-gray-900" 
                 class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
                Research
              </a>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `,
  styles: []
})
export class NavbarComponent { }
