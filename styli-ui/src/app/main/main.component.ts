import {Component, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {AuthService} from "../auth.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-main',
    templateUrl: './main.component.html',
    styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

    constructor(private http: HttpClient,
                private router: Router,
                private authService: AuthService) {
    }

    text: string = '';
    isLoggedIn?: boolean;

    ngOnInit(): void {
        this.isLoggedIn = this.authService.isLoggedIn();
    }

    onLogInBtnClick() {
        this.router.navigateByUrl('/login')
            .then(
                (_: boolean) => console.debug('Navigated to /login'),
                (_: boolean) => console.error('Could not navigate to /login'));
    }

    onLogOutBtnClick() {
        console.log('Log out');
        this.authService.logOut();
        this.isLoggedIn = this.authService.isLoggedIn();
    }
}
