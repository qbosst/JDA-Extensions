package me.qbosst.jda.ext.commands.exceptions

import me.qbosst.jda.ext.commands.argument.Argument

class BadArgument(val expected: Argument,
                  val provided: String,
                  cause: Throwable? = null
): Exception("`${expected.name}` must be a `${expected.type.simpleName}`", cause)