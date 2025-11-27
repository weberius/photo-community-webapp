import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModalComponent } from './modal.component';

describe('ModalComponent', () => {
    let component: ModalComponent;
    let fixture: ComponentFixture<ModalComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ModalComponent]
        }).compileComponents();

        fixture = TestBed.createComponent(ModalComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should emit close event when closeModal is called', () => {
        const spy = vi.spyOn(component.close, 'emit');
        component.closeModal();
        expect(spy).toHaveBeenCalled();
    });

    it('should display title and message', () => {
        component.title = 'Test Title';
        component.message = 'Test Message';
        component.isOpen = true;
        fixture.detectChanges();

        const compiled = fixture.nativeElement as HTMLElement;
        expect(compiled.textContent).toContain('Test Title');
        expect(compiled.textContent).toContain('Test Message');
    });

    it('should not be visible when isOpen is false', () => {
        component.isOpen = false;
        fixture.detectChanges();

        const compiled = fixture.nativeElement as HTMLElement;
        expect(compiled.querySelector('.fixed')).toBeNull();
    });

    it('should close when clicking the close button', () => {
        component.isOpen = true;
        fixture.detectChanges();

        const spy = vi.spyOn(component.close, 'emit');
        const button = fixture.nativeElement.querySelector('button');
        button.click();

        expect(spy).toHaveBeenCalled();
    });

    it('should close when clicking the backdrop', () => {
        component.isOpen = true;
        fixture.detectChanges();

        const spy = vi.spyOn(component.close, 'emit');
        const backdrop = fixture.nativeElement.querySelector('.bg-gray-500');
        backdrop.click();

        expect(spy).toHaveBeenCalled();
    });
});
