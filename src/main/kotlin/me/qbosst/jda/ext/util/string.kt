package me.qbosst.jda.ext.util

import net.dv8tion.jda.api.entities.Message

fun String.spoiler(): String = "||$this||"

fun String.italics(): String = "*$this*"

fun String.bold(): String = "**$this**"

fun String.underline(): String = "__${this}__"

fun String.strikethrough(): String = "~~$this~~"

fun String.singleLineCode(): String = "`$this`"

fun String.multilineCode(): String = "```$this```"

fun String.multilineCode(language: String): String = "```$language\n$this```"

fun String.maxLength(length: Int = Message.MAX_CONTENT_LENGTH, ending: String = "..."): String =
    if(this.length > length) substring(0, length-ending.length)+ending else this




