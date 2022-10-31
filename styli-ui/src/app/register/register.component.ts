import {Component} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {AuthService} from '../auth.service';
import {RegisterResponse} from '../dto/register.response';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {delay, tap} from 'rxjs';

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss'],
})
export class RegisterComponent {

    constructor(private router: Router,
                private fb: FormBuilder,
                private authService: AuthService,
                public snackBar: MatSnackBar) {
    }

    registerForm = this.fb.nonNullable.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(24)]],
        email: ['', [Validators.required, Validators.email]],
        name: ['', [Validators.required]],
        surname: ['', [Validators.required]],
    });
    isRegisterError: boolean = false;

    onRegisterSubmit() {
        const form = this.registerForm.value;
        this.authService.register(form.username!!, form.password!!, form.email!!, form.name!!, form.surname!!)
            .pipe(
                tap({
                    next: (_: RegisterResponse) => {
                        this.snackBar.open('Registration successful. Pleas log in', 'Close', {duration: 5000});
                    },
                    error: (_: HttpErrorResponse) => {
                        this.snackBar.open('Registration failed', 'Close', {duration: 5000});
                    },
                }),
            )
            .subscribe({
                next: (_: RegisterResponse) => {
                    console.log('Registration successful');
                    this.router.navigateByUrl('login').then(
                        (_: boolean) => console.log('Navigated to /login'),
                        (_: boolean) => console.error('Could not navigate to /login'));
                },
                error: (_: HttpErrorResponse) => {
                    this.isRegisterError = true;
                    this.registerForm.reset();
                    console.error('Registration failed');
                },
            });
    }
}
