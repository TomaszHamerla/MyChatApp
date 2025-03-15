FROM node:18 AS build

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build -- --configuration=production

FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/chatApp/browser/ /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]

EXPOSE 80
