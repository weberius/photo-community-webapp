import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UploadComponent } from './upload';
import { By } from '@angular/platform-browser';
import { vi } from 'vitest';

describe('UploadComponent', () => {
  let component: UploadComponent;
  let fixture: ComponentFixture<UploadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(UploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should handle file selection via input', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    const event = { target: { files: [file] } };

    const handleFileSpy = vi.spyOn(component, 'handleFile');
    component.onFileSelected(event);

    expect(handleFileSpy).toHaveBeenCalledWith(file);
    expect(component.selectedFile()).toBe(file);
  });

  it('should handle drag over', () => {
    const event = {
      preventDefault: vi.fn(),
      stopPropagation: vi.fn()
    } as any;
    component.onDragOver(event);
    expect(event.preventDefault).toHaveBeenCalled();
    expect(event.stopPropagation).toHaveBeenCalled();
  });

  it('should handle file drop', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    const event = {
      preventDefault: vi.fn(),
      stopPropagation: vi.fn(),
      dataTransfer: { files: [file] }
    } as any;

    const handleFileSpy = vi.spyOn(component, 'handleFile');
    component.onDrop(event);

    expect(event.preventDefault).toHaveBeenCalled();
    expect(event.stopPropagation).toHaveBeenCalled();
    expect(handleFileSpy).toHaveBeenCalledWith(file);
  });

  it('should emit file on upload', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    component.selectedFile.set(file);

    let emittedFile: File | undefined;
    component.fileSelected.subscribe(f => emittedFile = f);

    component.upload();

    expect(emittedFile).toBe(file);
  });

  it('should remove image', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    component.selectedFile.set(file);
    component.previewUrl.set('data:image/jpeg;base64,test');

    const event = { stopPropagation: vi.fn() } as any;
    component.removeImage(event);

    expect(component.selectedFile()).toBeNull();
    expect(component.previewUrl()).toBeNull();
    expect(event.stopPropagation).toHaveBeenCalled();
  });

  it('should handle file and generate preview', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });

    // Mock FileReader class
    class MockFileReader {
      readAsDataURL = vi.fn();
      onload: any = null;
      result = 'data:image/jpeg;base64,test';

      constructor() {
        mockReader = this;
      }
    }

    let mockReader: any;
    vi.spyOn(window, 'FileReader').mockImplementation(MockFileReader as any);

    component.handleFile(file);

    // Trigger onload manually
    if (mockReader.onload) {
      mockReader.onload({} as any);
    }

    expect(component.previewUrl()).toBe('data:image/jpeg;base64,test');
  });

  it('should not handle non-image files', () => {
    const file = new File([''], 'test.txt', { type: 'text/plain' });
    component.handleFile(file);
    expect(component.selectedFile()).toBeNull();
  });

  it('should call upload when button is clicked', () => {
    const file = new File([''], 'test.jpg', { type: 'image/jpeg' });
    component.selectedFile.set(file);
    component.previewUrl.set('data:image/jpeg;base64,test');
    fixture.detectChanges();

    const spy = vi.spyOn(component, 'upload');
    const button = fixture.nativeElement.querySelector('button.bg-blue-600');
    button.click();
    expect(spy).toHaveBeenCalled();
  });
});
