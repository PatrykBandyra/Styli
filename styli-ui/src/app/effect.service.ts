import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {EffectRequest} from './dto/effect.request';
import {EffectResponse} from './dto/effect.response';

@Injectable({
    providedIn: 'root',
})
export class EffectService {

    constructor(private http: HttpClient) {
    }

    private static readonly BASE_URL: string = '/api/effect';

    getAvailableEffects(): Observable<string[]> {
        return this.http.get<string[]>(EffectService.BASE_URL);
    }

    applyEffect(effectRequest: EffectRequest): Observable<EffectResponse> {
        const form = new FormData();
        form.append('effectRequest',
            new Blob([
                JSON.stringify({
                    effectName: effectRequest.effectName,
                    effectParams: effectRequest.effectParams,
                })], {type: 'application/json'},
            ),
        );
        form.append('image', effectRequest.image, effectRequest.image.name);
        if (effectRequest.image2) {
            form.append('image2', effectRequest.image2, effectRequest.image2.name);
        }
        return this.http.post<EffectResponse>(EffectService.BASE_URL, form);
    }
}
