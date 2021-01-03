package me.qbosst.jda.ext.commands.annotations

@Target(AnnotationTarget.FUNCTION)
annotation class CommandFunction(val usage: String = "",
                                 val examples: Array<String> = [],
                                 val delimiter: Char = ' ',
                                 val order: Int = -1
)
