package me.qbosst.jda.ext.commands.argument

import me.qbosst.jda.ext.commands.annotations.Greedy
import me.qbosst.jda.ext.commands.annotations.Tentative
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.jvmErasure

class Argument(val name: String,
               val type: Class<*>,
               val isGreedy: Boolean,
               val isOptional: Boolean,
               val isNullable: Boolean,
               val isTentative: Boolean,
               val index: Int,
               internal val kParameter: KParameter
)
{
    constructor(kParameter: KParameter): this(
        name = kParameter.name ?: kParameter.index.toString(),
        type = kParameter.type.jvmErasure.javaObjectType,
        isGreedy = kParameter.hasAnnotation<Greedy>(),
        isOptional = kParameter.isOptional,
        isNullable = kParameter.type.isMarkedNullable,
        isTentative = kParameter.hasAnnotation<Tentative>(),
        index = kParameter.index,
        kParameter = kParameter
    )

    init
    {
        if (isTentative && !(isNullable || isOptional))
            throw IllegalStateException("$name is marked as tentative, but does not have a default value and is not marked nullable!")
    }

    fun format(withType: Boolean) = buildString {
        if(isOptional || isNullable) append('[') else append('<')

        append(name)
        if(withType)
            append(": ").append(type.simpleName)

        if(isOptional || isNullable) append(']') else append('>')
    }
}