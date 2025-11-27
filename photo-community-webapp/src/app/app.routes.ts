import { Routes } from '@angular/router';
import { ImageAnalysisComponent } from './components/image-analysis/image-analysis.component';

export const routes: Routes = [
    { path: '', redirectTo: 'bildbesprechung', pathMatch: 'full' },
    { path: 'bildbesprechung', component: ImageAnalysisComponent }
];
