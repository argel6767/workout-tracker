# Build stage
FROM node:21-alpine AS builder

WORKDIR /app

RUN npm install -g pnpm

ENV CI=true

COPY pnpm-lock.yaml ./

RUN pnpm fetch

COPY . .

RUN pnpm install

RUN pnpm run build

# Production stage
FROM node:21-alpine

WORKDIR /app

RUN npm install -g serve

COPY --from=builder /app/dist ./dist

EXPOSE 5173

CMD ["serve", "-s", "dist", "-l", "5173"]