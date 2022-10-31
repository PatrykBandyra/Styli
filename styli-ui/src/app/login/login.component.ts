import {Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {AuthService} from '../auth.service';
import {LoginResponse} from '../dto/login.response';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss'],
})
export class LoginComponent {

    constructor(private router: Router,
                private fb: FormBuilder,
                private authService: AuthService) {

    }

    loginForm = this.fb.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(24)]],
    });
    isLogInError: boolean = false;

    onLogInSubmit(): void {
        const form = this.loginForm.value;
        this.authService.logIn(form.username!!, form.password!!)
            .subscribe({
                next: (_: LoginResponse) => {
                    console.log('Logging in successful');
                    this.router.navigateByUrl('home').then(
                        (_: boolean) => console.log('Navigated to /home'),
                        (_: boolean) => console.error('Could not navigate to /home'));
                },
                error: (_: HttpErrorResponse) => {
                    this.isLogInError = true;
                    this.loginForm.reset();
                    console.error('Logging in failed');
                },
            });
    }
}
