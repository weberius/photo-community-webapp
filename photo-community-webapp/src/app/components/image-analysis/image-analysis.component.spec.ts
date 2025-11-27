import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ImageAnalysisComponent } from './image-analysis.component';
import { GeminiService } from '../../services/gemini';
import { vi } from 'vitest';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('ImageAnalysisComponent', () => {
    let component: ImageAnalysisComponent;
    let fixture: ComponentFixture<ImageAnalysisComponent>;
    let geminiServiceMock: any;

    beforeEach(async () => {
        geminiServiceMock = {
            analyzeImage: vi.fn()
        };

        await TestBed.configureTestingModule({
            imports: [ImageAnalysisComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                { provide: GeminiService, useValue: geminiServiceMock }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ImageAnalysisComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should call analyzeImage on file selection', async () => {
        const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
        geminiServiceMock.analyzeImage.mockResolvedValue('Markdown Result');

        await component.onFileSelected(file);

        expect(component.analyzing()).toBe(false);
        expect(component.analysisResult()).toBe('Markdown Result');
        expect(geminiServiceMock.analyzeImage).toHaveBeenCalledWith(file);
    });

    it('should handle analysis error', async () => {
        const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
        geminiServiceMock.analyzeImage.mockRejectedValue(new Error('Error'));
        const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => { });

        await component.onFileSelected(file);

        expect(component.analyzing()).toBe(false);
        expect(component.showErrorModal()).toBe(true);
        expect(component.errorMessage()).toContain('Ein unerwarteter Fehler ist aufgetreten.');
        expect(consoleSpy).toHaveBeenCalled();
    });

    it('should handle API_KEY_INVALID error', async () => {
        const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
        geminiServiceMock.analyzeImage.mockRejectedValue(new Error('API_KEY_INVALID'));
        const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => { });

        await component.onFileSelected(file);

        expect(component.analyzing()).toBe(false);
        expect(component.showErrorModal()).toBe(true);
        expect(component.errorMessage()).toContain('Kein zulässiger API_KEY gefunden');
        expect(consoleSpy).toHaveBeenCalled();
    });

    it('should reset', () => {
        component.analysisResult.set('Result');
        component.reset();
        expect(component.analysisResult()).toBeNull();
        expect(component.analyzing()).toBe(false);
    });

    it('should close error modal', () => {
        component.showErrorModal.set(true);
        component.closeErrorModal();
        expect(component.showErrorModal()).toBe(false);
    });
});
