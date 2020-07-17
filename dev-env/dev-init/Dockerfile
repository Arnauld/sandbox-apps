## base image
FROM python:3.7.4-alpine

## install dependencies
RUN apk update && \
    apk add --no-cache --update curl tar bash ncurses ca-certificates openssl openssh-client && \
    apk add --virtual build-deps gcc python3-dev libffi-dev openssl-dev musl-dev && \
    apk add postgresql-dev postgresql-client && \
    apk add netcat-openbsd && \
    pip3 install --no-cache --upgrade pip cffi && \
    rm -rf /var/cache/apk/* && \
    rm -rf /root/.cache && \
    ln -s /usr/bin/python3 /usr/bin/python

WORKDIR /app
COPY . /app
RUN pip install -r requirements.txt

CMD ["/app/src/init.py"]
