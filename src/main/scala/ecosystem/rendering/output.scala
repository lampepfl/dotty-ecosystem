package ecosystem.rendering

def error(str: String) = println(s"[${red("error")}] $str")
def warning(str: String) = println(s"[${yellow("warn")}] $str")
def info(str: String) = println(s"[info] $str")