import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {BehaviorSubject, Observable, Subscription, take, tap} from 'rxjs';
import ImagesResponse, {Content} from '../dto/images.response';
import {ImageService} from '../image.service';
import * as _ from 'lodash';

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

    constructor(private imageService: ImageService) {
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
}
