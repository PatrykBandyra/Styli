import {Component} from '@angular/core';
import {FormBuilder, Validators} from "@angular/forms";
import {AuthService} from "../auth.service";
import {RegisterResponse} from "../dto/register.response";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
    selector: 'app-register',
    templateUrl: './register.component.html',
    styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

    constructor(private fb: FormBuilder,
                private authService: AuthService) {
    }

    registerForm = this.fb.nonNullable.group({
        username: ['', [Validators.required]],
        password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(24)]],
        email: ['', [Validators.required, Validators.email]],
        name: ['', [Validators.required]],
        surname: ['', [Validators.required]],
    })

    onRegisterSubmit() {
        const form = this.registerForm.value
        this.authService.register(form.username!!, form.password!!, form.email!!, form.name!!, form.surname!!)
            .subscribe({
                next: (response: RegisterResponse) => {
                    console.log(response);
                },
                error: (err: HttpErrorResponse) => {
                    console.error(err);
                }
            })
    }
}
