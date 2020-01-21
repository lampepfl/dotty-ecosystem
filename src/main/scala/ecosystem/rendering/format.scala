package ecosystem.rendering

def asciiEscape(escString: String)(str: String): String =
  s"\u001b[${escString}m${str}\u001b[0m"

// Colors
def black   = asciiEscape("30")
def red     = asciiEscape("31")
def green   = asciiEscape("32")
def yellow  = asciiEscape("33")
def blue    = asciiEscape("34")
def magenta = asciiEscape("35")
def cyan    = asciiEscape("36")
def white   = asciiEscape("37")

// Style
def underline = asciiEscape("4")
def bold = asciiEscape("1")

// Combinations
def url = underline compose cyan

def stripDecorations(str: String): String = str.replaceAll("\u001b\\[.*?m", "")
