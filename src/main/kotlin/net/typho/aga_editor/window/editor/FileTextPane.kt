package net.typho.aga_editor.window.editor

import net.typho.aga_editor.window.editor.styling.Styling
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Path
import javax.swing.AbstractAction
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument
import javax.swing.text.TabSet
import javax.swing.text.TabStop
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.math.ceil

open class FileTextPane(
    @JvmField
    val file: Path,
    @JvmField
    val styling: Styling? = null
) : JScrollPane(JTextPane()) {
    val editor: JTextPane
        get() = viewport.view as JTextPane

    init {
        if (!file.exists()) {
            file.createParentDirectories()
            file.createFile()
        }

        editor.text = file.readText()
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save")
        actionMap.put("save", object : AbstractAction("save") {
            override fun actionPerformed(e: ActionEvent) {
                file.writeText(editor.text)
            }
        })

        editor.styledDocument.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                SwingUtilities.invokeLater { applyStyle() }
            }

            override fun removeUpdate(e: DocumentEvent) {
                SwingUtilities.invokeLater { applyStyle() }
            }

            override fun changedUpdate(e: DocumentEvent) {
            }
        })
        applyStyle()
    }

    open fun applyStyle() {
        editor.styledDocument.setParagraphAttributes(
            0,
            editor.styledDocument.length,
            SimpleAttributeSet().also {
                StyleConstants.setTabSet(it, BetterTabSet(editor, 4f))
            },
            false
        )
        styling?.apply(editor.styledDocument)
    }
}