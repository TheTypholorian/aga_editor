package net.typho.aga_editor.window.editor.styling

import net.typho.parsing_util.StringView
import java.awt.Color
import java.awt.Paint
import java.awt.font.TextAttribute
import java.text.AttributedCharacterIterator

fun interface Styling {
    companion object {
        @JvmField // TODO
        val byFileExtension = mutableMapOf<String, () -> Styling>(
            "java" to { JavaCompilerStyling() }
        )
        @JvmField
        val keywords = style(color = Color(0xE28C4F))
        @JvmField
        val comments = style(color = Color(0x9BA2B2))
        @JvmField
        val numbers = style(color = Color(0x55B7C6))
        @JvmField
        val strings = style(color = Color(0x6DAA5C))
        @JvmField
        val annotations = style(color = Color(0xD6BC60))
        @JvmField
        val methodDef = style(color = Color(0x58A6F4))
        @JvmField
        val instanceProperty = style(color = Color(0xCF8EE5))
        @JvmField
        val staticProperty = style(color = Color(0xCF8EE5), italic = true)
        @JvmField
        val error = style(color = Color(0xE23431), underline = TextAttribute.UNDERLINE_ON)
        @JvmField
        val brackets = mutableListOf(
            style(color = Color(0xFFDA60)),
            style(color = Color(0x7BD856)),
            style(color = Color(0x5F8FE2)),
            style(color = Color(0x726EE5))
        )

        fun style(
            color: Paint? = null,
            bold: Float? = null,
            italic: Boolean? = null,
            underline: Int? = null,
            strikethrough: Boolean? = null
        ) = buildMap<AttributedCharacterIterator.Attribute, Any> {
            color?.let { put(TextAttribute.FOREGROUND, it) }
            bold?.let { put(TextAttribute.WEIGHT, it) }
            italic?.let { put(TextAttribute.POSTURE, if (it) TextAttribute.POSTURE_OBLIQUE else TextAttribute.POSTURE_REGULAR) }
            underline?.let { put(TextAttribute.UNDERLINE, it) }
            strikethrough?.let { put(TextAttribute.STRIKETHROUGH, it) }
        }
    }

    fun update(text: CharSequence, defaultAttributes: Map<AttributedCharacterIterator.Attribute, Any>): List<AttributedCharacterIterator>

    object None : Styling {
        override fun update(
            text: CharSequence,
            defaultAttributes: Map<AttributedCharacterIterator.Attribute, Any>
        ): List<AttributedCharacterIterator> {
            return buildList {
                var start = 0

                text.forEachIndexed { index, ch ->
                    if (ch == '\n') {
                        add(StaticCharacterIterator(StringView(text, start until index), defaultAttributes))
                        start = index + 1
                    }
                }
            }
        }
    }
}