import {Component, EventEmitter, Input, Output} from '@angular/core';
import {EffectService} from '../../effect.service';
import {EffectType} from '../../effectType';
import {EffectResponse} from '../../dto/effect.response';

@Component({
    selector: 'app-cartoonize',
    templateUrl: './cartoonize.component.html',
    styleUrls: ['./cartoonize.component.scss'],
})
export class CartoonizeComponent {

    constructor(private effectService: EffectService) {
    }

    @Input() file?: File;
    @Output() effectAppliedEvent = new EventEmitter<EffectResponse>();

    onCartoonizeBtnClick(): void {
        this.effectService.applyEffect({
            effectName: EffectType.CARTOONIZE.toString(),
            effectParams: [],
            image: this.file!!,
        }).subscribe(
            (response: EffectResponse) => this.effectAppliedEvent.emit(response),
        );
    }
}
