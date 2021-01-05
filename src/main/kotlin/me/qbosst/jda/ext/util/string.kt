package me.qbosst.jda.ext.util

import net.dv8tion.jda.api.entities.Message

fun String.withSpoiler(): String = "||$this||"

fun String.withItalics(): String = "*$this*"

fun String.withBold(): String = "**$this**"

fun String.withUnderline(): String = "__${this}__"

fun String.withStrikethrough(): String = "~~$this~~"

fun String.withSingleLineCode(): String = "`$this`"

fun String.withMultilineCode(): String = "```$this```"

fun String.withMultilineCode(language: String): String = "```$language\n$this```"

fun String.maxLength(length: Int = Message.MAX_CONTENT_LENGTH, ending: String = "..."): String =
    if(this.length > length) substring(0, length-ending.length)+ending else this




