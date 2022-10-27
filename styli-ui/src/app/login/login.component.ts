import {Component} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {AuthService} from "../auth.service";
import {LoginResponse} from "../dto/login.response";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.scss']
})
export class LoginComponent {

    constructor(private fb: FormBuilder, private authService: AuthService) {

    }

    loginForm = this.fb.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(24)]],
    })

    onLogInSubmit(): void {
        const form = this.loginForm.value
        this.authService.logIn(form.username!!, form.password!!)
            .subscribe({
                next: (response: LoginResponse) => {
                    console.log('Logging successful');
                    console.log(response.token);
                    console.log(response.expiresAt);
                },
                error: (err: HttpErrorResponse) => {
                    console.error('Logging failed')
                }
            });
    }
}
