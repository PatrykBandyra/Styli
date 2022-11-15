#FROM node:latest as build
#WORKDIR /app
#COPY ./ /app
#RUN npm install
#RUN npm run build-prod
#
#FROM nginx:latest
#COPY --from=build /app/dist/styli-ui /usr/share/nginx/html
#COPY --from=build /app/nginx.conf /etc/nginx/conf.d/default.conf
#EXPOSE 80


#FROM node:latest as build
#WORKDIR /app
#COPY ./ /app
#RUN npm install
#RUN npm run start-prod
#
#EXPOSE 4200

FROM node:alpine AS build
WORKDIR /app
COPY . .
RUN npm install && \
    npm run build

FROM nginx:alpine
COPY --from=build /app/dist/* /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
