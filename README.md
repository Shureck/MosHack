# MosHack
## Описание прокта
Проект направлен на решение задачи улучшения способов поиска интересных мероприятий, создания централизованной системы агрегации событий, а так же разработка платформы для поддержания и развития сообществ людей по интересам

## Постановка задачи, проработка идеи
Основная задача – разработать функциональный прототип для подбора и планированию мероприятий. В рамках сервиса пользователю должна быть предоставлена возможность подобрать одно или несколько мероприятий, проходящих в городе Москве, а также спланировать досуг для самых разных категорий пользователей (например, отдых всей семьи, ребёнка или одного взрослого).

## MVP
Перед началом разработки командой был проведён опрос большого числа респондентов на предмет того, какими сервисами для подбора досуговых мероприятий пользуются люди, что им в них нравится и какие недостатки имеются. Это помогло сформулировать требования к проекту.

В ходе обсуждений, был принят итоговый формат - мобильное приложение, позволяющее отслеживать все мероприятия, публикуемые на сайте [Mos.ru](https://www.mos.ru/afisha/). Приложение должно обладать возможностью фильтрации мероприятий, отображения ближайшие как по времени, так и по растоянию, А так же выдавать персонализированные рекомендации на основании интересов и подписок.

Также было решено разработать систему авторских каналов. Благодаря ней, люди смогут делиться своими впечатлениями от посещённых мероприятий, выпускать подборки интересных событий на ближайшее время, а также общаться и расширять свой круг знакомств.
## Реализация
Полученный продукт включает в себя сервер и клиентское приложение для Android смартфонов. Сервер упакован в Docker и развернуть разработанную систему можно с помощью одной команды
Документация API сервера доступна по следующей ссылке http://45.157.140.16:23200/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/main-controller/putPreview или в файле openAPI.yml

В начале использования мобильного приложения необходимо пройти регистрацию или авторизацию в системе. После чего будет доступен полный функционал приложения.

Видео демонстрация проекта и информация по нему доступна по следующей ссылке https://drive.google.com/drive/folders/11CUEOgTFMpvN3YWV37iVoAQXatTTq-Zx 

Карта активноти (состояний) приложения доступна по следующей ссылке https://www.figma.com/file/O6clZQInbMvYb4L9B5EWIQ/Untitled?node-id=113%3A35

### Стек технологий
- Java Spring Boot
    + высокая скорость разработки
    + большая поддержка комьюнити
    + множество полезных инструментов
- PostgreSQL
    + фиксированная разметка данных
    + часть работы (например, расчет текущего рейтинга) можно при необходимости переложить на БД
- Android SDK
    + стандартный инструмент для разработки мобильных приложений
    + обеспечивает большой охват аудитории

### Архитектура
Решение построено на клиет-серверной архитектуре, где основную вычислительную нагрузку и хранение данных на себя берёт сервер. 
