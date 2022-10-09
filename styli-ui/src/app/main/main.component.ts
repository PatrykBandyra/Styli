import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

    constructor(private http: HttpClient) {
    }

    text: string = '';

    ngOnInit(): void {
    }

    onClick(): void {
        this.http.get<HealthCheckDto>('/api/health').subscribe({
            next: (response: HealthCheckDto) => {
                this.text = response.message;
            }
        });
    }

}

interface HealthCheckDto {
    message: string
}
