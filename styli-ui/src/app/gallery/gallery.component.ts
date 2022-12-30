import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, Subscription, take, tap} from 'rxjs';
import ImagesResponse, {Content} from '../dto/images.response';
import {ImageService} from '../image.service';
import * as _ from 'lodash';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
    selector: 'app-gallery',
    templateUrl: './gallery.component.html',
    styleUrls: ['./gallery.component.scss'],
})
export class GalleryComponent implements OnInit, OnDestroy {

    images$ = new BehaviorSubject<Content[]>([]);
    finished: boolean = true;
    isMoreContent: boolean = true;
    lastTotalElements: number = 0;
    private static readonly PAGE_SIZE: number = 6;
    private currentPage: number = 0;

    @Input() saveSuccess?: Observable<void>;
    private saveSuccessSubscription?: Subscription;

    constructor(private imageService: ImageService,
                private snackBar: MatSnackBar) {
    }

    ngOnInit(): void {
        this.saveSuccessSubscription = this.saveSuccess?.subscribe(() => {
            this.isMoreContent = true;
            this.getImages(this.lastTotalElements, 1);
        });
        this.getImages();
    }

    ngOnDestroy(): void {
        this.saveSuccessSubscription?.unsubscribe();
    }

    onScroll(): void {
        this.getImages();
    }

    private getImages(page: number = this.currentPage,
                      pageSize: number = GalleryComponent.PAGE_SIZE): void {
        if (!this.finished || !this.isMoreContent) return;
        this.finished = false;
        this.imageService.getImages(page, pageSize).pipe(
            tap((response: ImagesResponse) => {
                if (response.content !== undefined) {
                    const currentImages: Content[] = this.images$.getValue();
                    let newImages = response.content.map((content: Content) => ({
                        ...content,
                        image: `data:image/jpeg;base64,${content.image}`,
                    }));
                    newImages = _.reverse(newImages);
                    this.images$.next(_.concat(newImages, currentImages));
                }
            }),
            take(1),
            tap(() => this.currentPage += 1),
            tap((response: ImagesResponse) => this.isMoreContent = !response.last),
            tap((response: ImagesResponse) => {
                if (response.totalElements !== undefined) {
                    this.lastTotalElements = response.totalElements;
                }
            }),
            tap(() => this.finished = true),
        ).subscribe();
    }

    deleteImage(index: number, id: number): void {
        this.imageService.deleteImage(id).subscribe({
            next: (_: HttpResponse<void>) => {
                const currentImages: Content[] = this.images$.getValue();
                currentImages.splice(index, 1);
                this.images$.next(currentImages);
                this.snackBar.open('Image deleted successfully', 'Close', {duration: 3_000});
            },
            error: (_: HttpErrorResponse) => {
                this.snackBar.open('Error occurred while deleting an image', 'Close', {duration: 3_000});
            },
        });

    }
}
