import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {LoginResponse} from "./dto/login.response";
import {Observable, shareReplay, tap} from "rxjs";
import {DateTime} from "luxon";
import {RegisterResponse} from "./dto/register.response";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private static readonly BASE_URL: string = '/api/auth';

    constructor(private http: HttpClient) {
    }

    register(username: string, password: string, email: string, name: string, surname: string) {
        return this.http.post<RegisterResponse>(`${AuthService.BASE_URL}/register`, {
            username: username, password: password, email: email, name: name, surname: surname
        });
    }

    logIn(username: string, password: string): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${AuthService.BASE_URL}/login`, {username: username, password: password})
            .pipe(
                tap((res: LoginResponse) => AuthService.setSession(res)),
                shareReplay()
            );
    }

    logOut(): void {
        localStorage.removeItem("token");
        localStorage.removeItem("expires_at");
    }

    isLoggedIn(): boolean {
        return DateTime.now().toUnixInteger() < AuthService.getExpiration();
    }

    private static getExpiration(): number {
        const expiresAt: string | null = localStorage.getItem('expires_at');
        return Number(expiresAt);
    }

    private static setSession(loginResponse: LoginResponse): void {
        localStorage.setItem('token', loginResponse.token);
        localStorage.setItem('expires_at', loginResponse.expiresAt.toString());
    }
}
