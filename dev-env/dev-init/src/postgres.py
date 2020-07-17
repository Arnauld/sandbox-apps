import subprocess
import time

from colorama import Fore, Style
from stdout import print_error, print_debug


def wait_for_postgres(conf, retries=4, retry_delay_in_sec=3):
    for attempt in range(retries):
        last = attempt == (retries - 1)
        print_debug("wait for postgres - attempt {}{}{}/{}".format(Fore.GREEN, attempt + 1, Style.RESET_ALL, retries))

        process = subprocess.Popen(["/usr/bin/psql", "-h", conf["host"], "-U", conf["username"], "-c", "select 1"],
                                   stdout=subprocess.PIPE,
                                   stderr=subprocess.PIPE,
                                   universal_newlines=True,
                                   env={"PGPASSWORD": conf["password"]})

        while True:
            if last:
                output = process.stdout.readline()
                print_debug(output.strip())
                output = process.stderr.readline()
                print_error(output.strip())

            # Do something else
            rc = process.poll()
            if rc is not None:
                if rc == 0:
                    return
                else:
                    if last:
                        print_error('RETURN CODE {}'.format(rc))
                        # Process has finished, read rest of the output
                        for output in process.stdout.readlines():
                            print_debug(output.strip())
                        for output in process.stderr.readlines():
                            print_error(output.strip())

                break

        time.sleep(retry_delay_in_sec)
    raise Exception("Unable to reach postgres")
