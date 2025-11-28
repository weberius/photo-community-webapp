import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <nav class="bg-white shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Row 1: Brand -->
        <div class="flex items-center py-4">
          <div class="flex-shrink-0">
            <span class="text-xl font-bold text-gray-800">ZfpF</span>
          </div>
        </div>
        
        <!-- Row 2: Navigation Links -->
        <div class="hidden sm:flex sm:space-x-8 pb-4">
          <a routerLink="/upload" 
               routerLinkActive="border-blue-500 text-gray-900" 
               class="border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700 inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium">
              Upload
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
    </nav>
  `,
  styles: []
})
export class NavbarComponent { }
