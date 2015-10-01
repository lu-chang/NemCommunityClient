from ply import lex
from ply import yacc
import sys

reserved = { 'define' : 'DEFINE' }
tokens = [
		"LBRACE",
		"RBRACE",
		"LPAREN",
		"RPAREN",
		"LBRACKET",
		"RBRACKET",
		"SYMBOL",
		"COLON",
		"COMA",
		"VALUE",
		"END",
		"COMMENT"
		] + list(reserved.values())

def t_COMMENT(t):
    r'//.*'
    pass

t_ignore = ' \t\n'
t_LBRACE = (r"{")
t_RBRACE = (r"}")
t_LPAREN = (r"\(")
t_RPAREN = (r"\)")
t_LBRACKET = (r"\[")
t_RBRACKET = (r"]")
def t_SYMBOL(t):
	r"[0-9a-z_A-Z]+"
	t.type = reserved.get(t.value, 'SYMBOL')
	return t

t_COLON = (r':')
t_COMA = (r',')
t_VALUE = (r'("((\\.)|[^"])*?"|'
		r"'((\\.)|[^'])*?')")

t_END = (r';')

def t_error(t):
	raise TypeError("Unknown text '%.40s'" % (t.value,))

lex.lex()

#lex.input(r'xxxx"te\"mp\"ura"yyy')
#for tok in iter(lex.token, None):
#		print repr(tok.type), repr(tok.value)

# fun = { d(ecorator), e(lement), c(lose), v(alue) }

def indent(fun, level):
	fun.d("\t"*level)

debugMode = False

class Field:
	def __init__(self, name, value):
		self.name = name
		self.value = value
	
	def __repr__(self):
		if self.name is None:
			return "NONE"
		return "F["+self.name+"]: "+str(self.value)

	def pretty(self, fun, lev):
		if self.name is None:
			return
		indent(fun, lev)
		fun.e(self.name)
		fun.d(": ")
		self.value.pretty(fun, lev)
		fun.c(self.name)

class Fields:
	def __init__(self, field):
		self.fields = [field]
	
	def add(self, field):
		self.fields.append(field)

	def __repr__(self):
		return "(" + ",".join([repr(i) for i in self.fields]) + ")"

	def pretty(self, fun, lev):
		count = len(self.fields)
		for i in xrange(count):
			f = self.fields[i]
			f.pretty(fun, lev)
			if i != count-1:
				fun.d(",\n")
			else:
				fun.d("\n")

class Value:
	def __init__(self, value):
		self.value = value

	def __repr__(self):
		if self.value is None:
			return "NONE"
		return repr(self.value)

	def pretty(self, fun, lev):
		if self.value is None:
			return
		fun.v(self.value)

class Values:
	def __init__(self, value):
		self.values = [value]
	
	def add(self, value):
		self.values.append(value)

	def __repr__(self):
		return "[" + ",".join([repr(i) for i in self.values]) + "]"

	def pretty(self, fun, lev):
		fun.d("[\n")
		count = len(self.values)
		for i in xrange(count):
			fun.e("["+str(i)+"]")
			v = self.values[i]
			indent(fun, lev+1)
			v.pretty(fun, lev+1)
			if i != count-1:
				fun.d(",\n")
			else:
				fun.d("\n")
			fun.c("["+str(i)+"]")
		indent(fun, lev)
		fun.d("]")

class Array:
	def __init__(self, values):
		self.data = values
	
	def __repr_(self):
		return repr(self.data)

	def pretty(self, fun, lev):
		self.data.pretty(fun, lev)

class JsObject:
	def __init__(self, fields):
		self.data = fields

	def __repr__(self):
		return repr(self.data)

	def pretty(self, fun, lev=0):
		fun.d("{\n")
		self.data.pretty(fun, lev + 1)
		indent(fun, lev)
		fun.d("}")

def p_translation(p):
	'translation : DEFINE LPAREN object RPAREN END'
	p[0] = p[3]

def p_object_empty(p):
	'object : LBRACE RBRACE'

def p_object(p):
	'object : LBRACE fields RBRACE'
	if debugMode:
		print "OBJECT END:", p[2], p[2].fields[0].name
	p[0] = JsObject(p[2])

def p_object_value(p):
	'object : VALUE'
	if debugMode:
		print "value", p[1]
	p[0] = Value(p[1])

def p_object_value_empty(p):
	'object : '
	p[0] = Value(None)

def p_fields_multiple(p):
	'fields : fields COMA field'
	p[0] = p[1]
	p[0].add(p[3])

def p_fields_single(p):
	'fields : field'
	p[0] = Fields(p[1])

def p_field_empty(p):
	'field : '
	p[0] = Field(None, None)

def p_field_obj(p):
	'field : SYMBOL COLON object'
	p[0] = Field(p[1], p[3])

def p_field_array(p):
	'field : SYMBOL COLON array'
	p[0] = Field(p[1], p[3])

def p_field(p):
	'field : SYMBOL COLON VALUE'
	p[0] = Field(p[1], p[3])

def p_array(p):
	'array : LBRACKET values RBRACKET'
	p[0] = Array(p[2])

def p_values_single(p):
	'values : object'
	p[0] = Values(p[1])

def p_values(p):
	'values : values COMA object'
	p[0] = p[1]
	p[0].add(p[3])

class Printer:
	def __init__(self, ast):
		self.ast = ast
		self.elements = []
		self.data = {}
		self.order = []
		self.translations = None
		self.outputFile = None

		self.d = self.collectDecorate
		self.e = self.collectElement
		self.c = self.collectClose
		self.v = self.collectValue

	def process(self):
		self.ast.pretty(self)
	
	def collectDecorate(self, decoration):
		pass

	def collectElement(self, element):
		self.elements.append(element)
	
	def collectClose(self, element):
		self.elements.pop()
	
	def collectValue(self, value):
		self.order.append('.'.join(self.elements))
		self.data['.'.join(self.elements)] = value
	
	def generateDecorate(self, decoration):
		self.outputFile.write(decoration)
	
	def generateElement(self, element):
		self.elements.append(element)
		if not element.startswith('['):
			self.outputFile.write(element)

	def generateClose(self, element):
		self.elements.pop()
	
	def generateValue(self, value):
		key = '.'.join(self.elements)
		self.outputFile.write(self.translations[key][0])

	def generate(self, translations, outputFile):
		self.translations = translations
		self.outputFile = outputFile
		self.d = self.generateDecorate
		self.e = self.generateElement
		self.c = self.generateClose
		self.v = self.generateValue
		self.outputFile.write("define(")
		self.ast.pretty(self)
		self.outputFile.write(");\n")

baseIn = open('english_base.js').read()
baseParser = yacc.yacc()
baseAst = baseParser.parse(baseIn)
baseData = Printer(baseAst)
baseData.process()

debugMode=True
currIn = open('english.js').read()
currParser = yacc.yacc()
currAst = currParser.parse(currIn)
currData = Printer(currAst)
currData.process()

dstIn = open(sys.argv[1]).read()
dstParser = yacc.yacc()
dstAst = dstParser.parse(dstIn)
dstData = Printer(dstAst)
dstData.process()

# first let's create a english-to-X dictionary

dictionary = {}
for e in baseData.order:
	vb = baseData.data.get(e)
	vx = dstData.data.get(e)
	if vx != None:
		dictionary[vb] = [vx, e]

changes=[]
translated = []
outData={}
for e in currData.order:
	vc = currData.data.get(e)
	vb = baseData.data.get(e)
	vx = dstData.data.get(e)

	if vb != vc:
		if vb is not None:
			changes.append("changed " + e +
					"\n\tfrom: " + vb +
					"\n\t  to: " + vc +
					"\n\t was: " + vx + "\n")

			outData[e] = [vc, vx]
		else:
			found = dictionary.get(vc)
			if found:
				translated.append("translated " + e +
						"\n\t    from: " + vc +
						"\n\t      to: " + found[0] +
						"\n\tbased on: " + found[1] + "\n")
				outData[e] = [found[0], vc]
			else:
				outData[e] = [vc, None]
	else:
		if vx == None:
			outData[e] = [vc, None]
		else:
			outData[e] = [vx]


with open(sys.argv[1], 'w') as outputFile:
	currData.generate(outData, outputFile)

with open('logs/' + sys.argv[1] + '.txt', 'a') as logFile:
	for i in changes:
		logFile.write(i + "\n")
	for i in translated:
		logFile.write(i + "\n")

	logFile.write("-----")
