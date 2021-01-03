package me.qbosst.jda.ext.commands.argument

import kotlin.reflect.KParameter

class Argument(val name: String,
               val type: Class<*>,
               val greedy: Boolean,
               val optional: Boolean,
               val nullable: Boolean,
               val isTentative: Boolean,
               val index: Int,
               internal val kParameter: KParameter
)
{
    fun format(withType: Boolean) = buildString {
        if(optional || nullable) append('[') else append('<')

        append(name)
        if(withType)
            append(": ").append(type.simpleName)

        if(greedy)
            append("...")

        if(optional || nullable) append(']') else append('>')
    }
}