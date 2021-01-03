package me.qbosst.jda.ext.commands.annotations

/**
 * @param order Priority of function from [Short.MIN_VALUE] being the biggest priority
 * and [Short.MAX_VALUE] being the lowest priority
 */
@Target(AnnotationTarget.FUNCTION)
annotation class CommandFunction(val examples: Array<String> = [],
                                 val delimiter: Char = ' ',
                                 val order: Short = Short.MAX_VALUE
)
