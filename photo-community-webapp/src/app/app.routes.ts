import { Routes } from '@angular/router';
import { ImageAnalysisComponent } from './components/image-analysis/image-analysis.component';
import { PhotoBrowser } from './components/photo-browser/photo-browser';
import { Review } from './components/review/review';
import { Discussion } from './components/discussion/discussion';
import { Research } from './components/research/research';

export const routes: Routes = [
    { path: '', redirectTo: 'browser', pathMatch: 'full' },
    { path: 'bildbesprechung', component: ImageAnalysisComponent },
    { path: 'browser', component: PhotoBrowser },
    { path: 'review', component: Review },
    { path: 'discussion', component: Discussion },
    { path: 'research', component: Research }
];
