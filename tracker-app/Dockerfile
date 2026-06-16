# Build stage
FROM node:22-alpine AS builder

WORKDIR /app

RUN npm install -g pnpm@10

ENV CI=true

COPY pnpm-lock.yaml ./

RUN pnpm fetch

COPY . .

RUN pnpm install --offline

RUN pnpm run build

# Production stage
FROM node:22-alpine

WORKDIR /app

RUN npm install -g serve

COPY --from=builder /app/dist ./dist

EXPOSE 5173

CMD ["serve", "-s", "dist", "-l", "5173"]