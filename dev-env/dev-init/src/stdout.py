from colorama import Fore, Style


def print_error(text):
    if len(text.strip()) > 0:
        print("{}{}{}".format(Fore.LIGHTRED_EX, text.strip(), Style.RESET_ALL))


def print_debug(text):
    if len(text.strip()) > 0:
        print("{}{}{}".format(Fore.LIGHTBLACK_EX, text.strip(), Style.RESET_ALL))
