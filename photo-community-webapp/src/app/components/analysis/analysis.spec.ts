import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AnalysisComponent } from './analysis';
import { vi } from 'vitest';

describe('AnalysisComponent', () => {
  let component: AnalysisComponent;
  let fixture: ComponentFixture<AnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnalysisComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AnalysisComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('markdown', '# Test');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should copy to clipboard', async () => {
    const writeTextSpy = vi.fn().mockResolvedValue(undefined);

    // Mock navigator.clipboard
    Object.defineProperty(navigator, 'clipboard', {
      value: {
        writeText: writeTextSpy
      },
      configurable: true
    });

    vi.useFakeTimers();
    await component.copyToClipboard();

    expect(writeTextSpy).toHaveBeenCalledWith('# Test');
    expect(component.copied()).toBe(true);

    vi.advanceTimersByTime(2000);
    expect(component.copied()).toBe(false);
    vi.useRealTimers();
  });

  it('should handle copy error', async () => {
    const consoleSpy = vi.spyOn(console, 'error').mockImplementation(() => { });
    const writeTextSpy = vi.fn().mockRejectedValue(new Error('Failed'));

    Object.defineProperty(navigator, 'clipboard', {
      value: {
        writeText: writeTextSpy
      },
      configurable: true
    });

    await component.copyToClipboard();

    expect(consoleSpy).toHaveBeenCalled();
    expect(component.copied()).toBe(false);
  });

  it('should call copyToClipboard when button is clicked', () => {
    const spy = vi.spyOn(component, 'copyToClipboard');
    const button = fixture.nativeElement.querySelector('button');
    button.click();
    expect(spy).toHaveBeenCalled();
  });
});
