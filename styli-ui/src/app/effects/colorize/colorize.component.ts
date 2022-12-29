import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {EffectService} from '../../effect.service';
import {EffectResponse} from '../../dto/effect.response';
import {EffectType} from '../../effectType';

@Component({
  selector: 'app-colorize',
  templateUrl: './colorize.component.html',
  styleUrls: ['./colorize.component.scss']
})
export class ColorizeComponent {

    constructor(private effectService: EffectService) {
    }

    @Input() file?: File;
    @Output() effectAppliedEvent = new EventEmitter<EffectResponse>();

    onColorizeBtnClicked(): void {
        this.effectService.applyEffect({
            effectName: EffectType.COLORIZE.toString(),
            effectParams: [],
            image: this.file!!,
        }).subscribe(
            (response: EffectResponse) => this.effectAppliedEvent.emit(response),
        );
    }

}
