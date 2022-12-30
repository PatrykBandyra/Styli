import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import ImagesResponse from './dto/images.response';

@Injectable({
    providedIn: 'root',
})
export class ImageService {

    constructor(private http: HttpClient) {
    }

    private static readonly BASE_URL: string = '/api/image';

    uploadImage(image: File, description?: string): Observable<HttpResponse<any>> {
        const form = new FormData();
        form.append('image', image, image.name);
        form.append('imageDetails',
            new Blob([
                JSON.stringify({
                    description: description ? description : '',
                })], {type: 'application/json'},
            ),
        );
        return this.http.post<any>(ImageService.BASE_URL, form);
    }

    getImages(page: number, size: number): Observable<ImagesResponse> {
        return this.http.get<ImagesResponse>(`${ImageService.BASE_URL}?page=${page}&size=${size}`);
    }
}
