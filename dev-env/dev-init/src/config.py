import os


def str2bool(v):
    return v.lower() in ("yes", "true", "t", "1")


def config():
    cfg = {"vault": {"addr": os.getenv('VAULT_ADDR', "http://vault:8200"),
                     "root_token": os.getenv('VAULT_ROOT_TOKEN', "myroot")},
           "postgres": {"skip": str2bool(os.getenv('PG_SKIP', "false")),
                        "host": os.getenv('PG_HOST', "postgres"),
                        "port": int(os.getenv('PG_PORT', "5432")),
                        "username": os.getenv('PG_USERNAME', "postgres"),
                        "password": os.getenv('PG_PASSWORD', "postgres")}}
    return cfg
