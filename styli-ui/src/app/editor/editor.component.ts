import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {EffectService} from '../effect.service';
import {HttpErrorResponse} from '@angular/common/http';
import {MatSelectChange} from '@angular/material/select';
import {EffectType} from '../effectType';
import {EffectResponse} from '../dto/effect.response';
import {ImageService} from '../image.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss'],
})
export class EditorComponent implements OnInit {

    constructor(private effectService: EffectService,
                private imageService: ImageService,
                private snackBar: MatSnackBar) {
    }

    file?: File;
    imageBase64?: string | ArrayBuffer | null;
    effects: string[] = [];
    selectedEffect?: string;
    readonly EffectType = EffectType;

    @Output() saveSuccessEvent = new EventEmitter<void>();

    ngOnInit(): void {
        this.getAvailableEffects();
    }

    selectFile(event: Event): void {
        const files = (event.target as HTMLInputElement).files;
        if (files && files[0]) {
            this.file = files[0];
            const reader = new FileReader();
            reader.onload = (_: Event) => {
                this.imageBase64 = reader.result;
            };
            reader.readAsDataURL(this.file);
        }
    }

    onSaveClicked(): void {
        this.imageService.uploadImage(
            this.file!!,
        ).subscribe({
            next: () => {
                this.saveSuccessEvent.emit();
                this.snackBar.open('Image saved successfully', 'Close', {duration: 3_000});
            },
            error: () => {
                this.snackBar.open('Error occurred while saving an image', 'Close', {duration: 3_000});
            },
        });
    }

    private getAvailableEffects(): void {
        this.effectService.getAvailableEffects().subscribe({
            next: (effects: string[]) => {
                this.effects = effects;
            },
            error: (_: HttpErrorResponse) => {
                console.error('Could not get available effects');
            },
        });
    }

    onEffectChanged(event: MatSelectChange): void {
        this.selectedEffect = event.value;
    }

    onEffectApplied(effectResponse: EffectResponse) {
        this.imageBase64 = `data:image/jpeg;base64,${effectResponse.image}`;
        this.file = EditorComponent.dataURItoFile(effectResponse.image);
    }

    private static dataURItoFile(dataURI: string): File {
        const byteString: string = window.atob(dataURI);
        const arrayBuffer: ArrayBuffer = new ArrayBuffer(byteString.length);
        const int8Array: Uint8Array = new Uint8Array(arrayBuffer);
        for (let i = 0; i < byteString.length; i++) {
            int8Array[i] = byteString.charCodeAt(i);
        }
        return new File([int8Array], 'image.jpg', {type: 'image/jpeg'});
    }
}
