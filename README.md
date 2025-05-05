# FairSplit-Backend

## Описание

Это Backend часть FairSplit.<br>
Backend представляет из себя сервер на Spring.<br>
Данные хранятся в PostgreSQL.

## Запуск

Переименовать файл `.env.example` в `.env` и заменить все значения в нем на свои.<br>
Запустить postgres контейнер командой
`docker compose --env-file ../.env -f docker/docker-compose.yml -p docker up -d --build postgres`.<br>
Запустить сервер командой
`docker compose --env-file ../.env -f docker/docker-compose.yml -p docker up -d --build fair_split`.
