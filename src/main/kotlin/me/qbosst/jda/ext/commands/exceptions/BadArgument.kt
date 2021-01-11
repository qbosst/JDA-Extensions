package me.qbosst.jda.ext.commands.exceptions

import me.qbosst.jda.ext.commands.argument.Argument

class BadArgument(
    val expected: Argument,
    val provided: List<String>,
    message: String = "${provided} is not a valid type of ${expected.type.simpleName}",
    cause: Throwable? = null
): Exception(message, cause) {

    constructor(
        expected: Argument,
        provided: String,
        message: String = "${provided} is not a valid type of ${expected.type.simpleName}",
        cause: Throwable? = null
    ): this(expected, listOf(provided), message, cause)
}