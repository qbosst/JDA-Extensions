package me.qbosst.jda.ext.commands.exceptions

class ParserNotRegistered(val type: Class<*>): Exception("No parsers registered for `${type.simpleName}`")