# TLDR;

```
$ docker build -t dev-env/vault-init .
$ docker run --rm -v $pwd\:/app/src -w /app/src --network=dev-env_default -ti dev-env/vault-init /bin/sh
```