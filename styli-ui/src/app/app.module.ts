import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './main/main.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {LoginComponent} from './login/login.component';
import {ReactiveFormsModule} from '@angular/forms';
import {RegisterComponent} from './register/register.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {CommonModule} from '@angular/common';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {GalleryComponent} from './gallery/gallery.component';
import {EditorComponent} from './editor/editor.component';
import {MatSelectModule} from '@angular/material/select';
import {AuthInterceptor} from './auth.interceptor';
import {CartoonizeComponent} from './effects/cartoonize/cartoonize.component';
import {BackgroundSwapComponent} from './effects/background-swap/background-swap.component';
import {ColorPickerModule} from 'ngx-color-picker';
import {ColorizeComponent} from './effects/colorize/colorize.component';
import {MatSidenavModule} from '@angular/material/sidenav';
import {InfiniteScrollModule} from 'ngx-infinite-scroll';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

@NgModule({
    declarations: [
        AppComponent,
        MainComponent,
        LoginComponent,
        RegisterComponent,
        GalleryComponent,
        EditorComponent,
        CartoonizeComponent,
        BackgroundSwapComponent,
        ColorizeComponent,
    ],
    imports: [
        CommonModule,
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        MatFormFieldModule,
        MatInputModule,
        MatCardModule,
        MatButtonModule,
        MatToolbarModule,
        MatIconModule,
        MatSnackBarModule,
        MatSelectModule,
        ColorPickerModule,
        MatSidenavModule,
        InfiniteScrollModule,
        MatProgressSpinnerModule,
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true},
    ],
    bootstrap: [AppComponent],
})
export class AppModule {
}
