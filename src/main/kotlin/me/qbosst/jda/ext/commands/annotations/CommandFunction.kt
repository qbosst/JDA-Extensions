package me.qbosst.jda.ext.commands.annotations

/**
 * @param priority Priority of function from [Short.MIN_VALUE] being the highest priority
 * and [Short.MAX_VALUE] being the lowest priority
 *
 * @param includeUsage Whether to include the usage that is generated with the usages for the command
 */
@Target(AnnotationTarget.FUNCTION)
annotation class CommandFunction(val examples: Array<String> = [],
                                 val delimiter: Char = ' ',
                                 val priority: Short = Short.MAX_VALUE,
                                 val includeUsage: Boolean = true
)
