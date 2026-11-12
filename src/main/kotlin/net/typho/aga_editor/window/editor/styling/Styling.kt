package net.typho.aga_editor.window.editor.styling

import java.awt.Color
import java.nio.file.Path
import javax.swing.event.DocumentEvent
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

interface Styling {
    companion object {
        @JvmField // TODO
        val byFileExtension = mutableMapOf<String, () -> Styling>(
            "java" to { JavaCompilerStyling() },
            "kt" to { KotlinCompilerStyling },
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
        val property = style(color = Color(0xCF8EE5))
        @JvmField
        val error = style(color = Color(0xE23431), underline = true)
        @JvmField
        val brackets = mutableListOf(
            style(color = Color(0xFFDA60)),
            style(color = Color(0x7BD856)),
            style(color = Color(0x5F8FE2)),
            style(color = Color(0x726EE5))
        )
        @JvmField
        val clear = SimpleAttributeSet()

        fun style(
            color: Color? = null,
            bold: Boolean? = null,
            italic: Boolean? = null,
            underline: Boolean? = null,
            strikethrough: Boolean? = null
        ) = SimpleAttributeSet().also { attributes ->
            color?.let { StyleConstants.setForeground(attributes, it) }
            bold?.let { StyleConstants.setBold(attributes, it) }
            italic?.let { StyleConstants.setItalic(attributes, it) }
            underline?.let { StyleConstants.setUnderline(attributes, it) }
            strikethrough?.let { StyleConstants.setStrikeThrough(attributes, it) }
        }
    }

    fun apply(
        file: Path,
        document: StyledDocument,
        change: DocumentEvent? = null
    )
}