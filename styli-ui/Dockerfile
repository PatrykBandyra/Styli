FROM node:latest as build
WORKDIR /app
COPY ./ /app
RUN npm install
RUN npm run build --prod

FROM nginx:latest
COPY --from=build /app/dist/styli-ui /usr/share/nginx/html
EXPOSE 80
