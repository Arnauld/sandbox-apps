# TLDR;

```
$ docker build -t dev-env/vault-init .
$ docker run --rm -v $pwd\:/app/src -w /app/src --network=dev-env_default -ti dev/vault-init /bin/sh
```