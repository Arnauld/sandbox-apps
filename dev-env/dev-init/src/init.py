#!/usr/bin/env python3

from colorama import init, Fore, Style
from config import config
from postgres import wait_for_postgres

init()

conf = config()
print("---")
print("{}{}{}".format(Fore.RED, conf, Style.RESET_ALL))
print("---")

wait_for_postgres(conf["postgres"])
