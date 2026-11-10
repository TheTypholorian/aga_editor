package net.typho.aga_editor.window.editor.styling

import java.awt.Color
import java.nio.file.Path
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

interface Styling {
    companion object {
        @JvmField // TODO
        val byFileExtension = mutableMapOf<String, Styling>(
            "kt" to KotlinCompilerStyling
        )
    }

    fun style(
        color: Color? = null,
        bold: Boolean = false,
        italic: Boolean = false,
        underline: Boolean = false,
        strikethrough: Boolean = false
    ) = SimpleAttributeSet().also { attributes ->
        color?.let { StyleConstants.setForeground(attributes, it) }
        StyleConstants.setBold(attributes, bold)
        StyleConstants.setItalic(attributes, italic)
        StyleConstants.setUnderline(attributes, underline)
        StyleConstants.setStrikeThrough(attributes, strikethrough)
    }

    fun apply(file: Path, document: StyledDocument, range: IntRange = 0 until document.length)
}