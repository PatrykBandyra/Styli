import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
    providedIn: 'root',
})
export class ImageService {

    constructor(private http: HttpClient) {
    }

    private static readonly BASE_URL: string = '/api/image';

    uploadImage(image: File, description?: string) {
        const form = new FormData();
        form.append('image', image, image.name);
        form.append('imageDetails',
            new Blob([
                JSON.stringify({
                    description: description ? description : '',
                })], {type: 'application/json'},
            ),
        );
        return this.http.post(ImageService.BASE_URL, form);
    }

    // getImages(page: number, size: number) {
    //     this.http.get<>
    // }
}
