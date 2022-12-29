import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EffectResponse} from '../../dto/effect.response';
import {EffectType} from '../../effectType';
import {EffectService} from '../../effect.service';

@Component({
    selector: 'app-background-swap',
    templateUrl: './background-swap.component.html',
    styleUrls: ['./background-swap.component.scss'],
})
export class BackgroundSwapComponent {

    constructor(private effectService: EffectService) {
    }

    @Input() file?: File;
    @Output() effectAppliedEvent = new EventEmitter<EffectResponse>();

    backImageFile?: File;
    backColor: string = '#FFFFFF';

    onBackgroundSwapBtnClick(): void {
        this.effectService.applyEffect({
            effectName: EffectType.BACKGROUND_SWAP.toString(),
            effectParams: [{
                name: 'bg_color',
                value: this.backColor
            }],
            image: this.file!!,
            image2: this.backImageFile
        }).subscribe(
            (response: EffectResponse) => this.effectAppliedEvent.emit(response),
        );
    }

    selectBackgroundFile(event: Event) {
        const files = (event.target as HTMLInputElement).files;
        if (files && files[0]) {
            this.backImageFile = files[0];
        }
    }
}
