# === Build Stage ===
FROM node:22-alpine AS build

WORKDIR /app

# 의존성 캐시 활용을 위해 package 파일 먼저 복사
COPY package.json package-lock.json ./

RUN npm ci

# 소스 코드 복사 후 빌드
COPY . .
RUN npm run build

# === Runtime Stage ===
FROM nginx:alpine

# 커스텀 Nginx 설정 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 빌드 결과물을 Nginx 서빙 디렉토리로 복사
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
