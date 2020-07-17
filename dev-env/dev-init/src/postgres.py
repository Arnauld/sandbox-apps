import subprocess
import time

from colorama import Fore, Style
from stdout import print_error, print_debug


def flush_process(process, log):
    while True:
        if log:
            output = process.stdout.readline()
            print_debug(output.strip())
            output = process.stderr.readline()
            print_error(output.strip())

        # Do something else
        rc = process.poll()
        if rc is not None:
            if rc != 0 and log:
                print_error('RETURN CODE {}'.format(rc))
                # Process has finished, read rest of the output
                for output in process.stdout.readlines():
                    print_debug(output.strip())
                for output in process.stderr.readlines():
                    print_error(output.strip())
            return rc


def wait_for_postgres(conf, retries=4, retry_delay_in_sec=3):
    for attempt in range(retries):
        last = attempt == (retries - 1)
        print_debug("wait for postgres - attempt {}{}{}/{}".format(Fore.GREEN, attempt + 1, Style.RESET_ALL, retries))

        process = subprocess.Popen(["/usr/bin/psql", "-h", conf["host"], "-U", conf["username"], "-c", "select 1"],
                                   stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE,
                                   universal_newlines=True,
                                   env={"PGPASSWORD": conf["password"]})
        rc = flush_process(process, last)
        if rc == 0:
            return

        time.sleep(retry_delay_in_sec)
    raise Exception("Unable to reach postgres")


def init_database(pg_conf, db_conf):
    db_name = db_conf["name"]
    db_username = db_conf["username"]
    db_password = db_conf["password"]

    sql = f"""SET client_min_messages = warning;
CREATE USER {db_username};
ALTER USER {db_username} WITH PASSWORD '{db_password}';
ALTER ROLE {db_username} WITH NOSUPERUSER INHERIT CREATEROLE NOCREATEDB LOGIN NOREPLICATION NOBYPASSRLS;

CREATE DATABASE {db_name}
   WITH TEMPLATE = template0
   ENCODING = 'UTF8'
   TABLESPACE = pg_default
   LC_COLLATE = 'en_US.utf8'
   LC_CTYPE = 'en_US.utf8'
   CONNECTION LIMIT = 255;
ALTER DATABASE {db_name} OWNER TO {db_username};
GRANT CONNECT ON DATABASE {db_name} TO {db_username};
"""
    with open('tmp.sql', 'w') as f:
        f.write(sql)

    process = subprocess.Popen(f"/usr/bin/psql -h {pg_conf['host']} -U {pg_conf['username']} < tmp.sql",
                               shell=True,
                               stdout=subprocess.PIPE,
                               stderr=subprocess.PIPE,
                               universal_newlines=True,
                               env={"PGPASSWORD": pg_conf["password"]})
    rc = flush_process(process, True)
