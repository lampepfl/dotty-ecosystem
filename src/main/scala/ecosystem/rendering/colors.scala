package ecosystem.rendering

def red(str: String): String = s"\u001b[31m${str}\u001b[0m"
def green(str: String): String = s"\u001b[32m${str}\u001b[0m"
def stripColor(str: String): String = str.replaceAll("\u001b\\[.*?m", "")
