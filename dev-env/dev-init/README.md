# TLDR;

```
$ docker build -t dev/init .
$ docker run --rm -v $pwd\:/app/src -w /app/src --network=dev-env_default -ti dev/init /bin/sh
```