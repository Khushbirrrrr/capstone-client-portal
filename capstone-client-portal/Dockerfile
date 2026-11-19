FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:1.27-alpine
ENV PORT=80 BACKEND_HOST=backend NGINX_ENVSUBST_FILTER="^(PORT|BACKEND_HOST)$"
COPY nginx.conf /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist/capstone-client-portal/browser /usr/share/nginx/html
EXPOSE 80
