# Stage 1: Build environment using Node.js
FROM node:20-alpine AS build
WORKDIR /app
# Installs project dependencies from package.json
COPY package*.json ./
RUN npm install
# Compiles the React/Vite application into static assets
COPY . .
RUN npm run build

# Stage 2: Production-ready server using Nginx
FROM nginx:alpine
# Copies the static build files from the previous stage to the Nginx serving directory
COPY --from=build /app/dist /usr/share/nginx/html
# Configures Nginx to handle routing for a Single Page Application (SPA) and proxy API calls
RUN echo 'server { \
    listen 80; \
    location /api/ { \
        proxy_pass http://api-gateway:8099/api/; \
        proxy_set_header Host $host; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
    } \
    location / { \
        root /usr/share/nginx/html; \
        index index.html index.htm; \
        try_files $uri $uri/ /index.html; \
    } \
}' > /etc/nginx/conf.d/default.conf
# Exposes the standard HTTP port
EXPOSE 80
# Starts Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
