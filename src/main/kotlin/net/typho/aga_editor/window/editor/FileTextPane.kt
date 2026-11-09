package net.typho.aga_editor.window.editor

import com.formdev.flatlaf.util.SystemFileChooser
import net.typho.aga_editor.window.editor.styling.Styling
import net.typho.aga_editor.window.property
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Path
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.*
import javax.swing.undo.UndoManager
import kotlin.io.path.*

open class FileTextPane(
    file: Path,
    @JvmField
    val styling: Styling? = null
) : JScrollPane(JTextPane()) {
    var file by property(file, ::firePropertyChange)
    val editor: JTextPane
        get() = viewport.view as JTextPane
    var saved by property(true, ::firePropertyChange)
    @JvmField
    val brackets = mutableMapOf(
        '(' to ')',
        '{' to '}',
        '[' to ']',
        '<' to '>',
        '"' to '"',
        '\'' to '\''
    )
    @JvmField
    val undoManager = UndoManager()

    init {
        if (!file.exists()) {
            file.createParentDirectories()
            file.createFile()
        }

        editor.text = file.readText()
        editor.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save")
        editor.actionMap.put("save", object : AbstractAction("save") {
            override fun actionPerformed(e: ActionEvent) {
                save()
            }
        })

        editor.document.addUndoableEditListener {
            val edit = it.edit

            if (edit is AbstractDocument.DefaultDocumentEvent) {
                if (edit.type == DocumentEvent.EventType.INSERT ||
                    edit.type == DocumentEvent.EventType.REMOVE
                ) {
                    undoManager.addEdit(edit)
                }
            }
        }

        editor.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), "undo")
        editor.actionMap.put("undo", object : AbstractAction("undo") {
            override fun actionPerformed(e: ActionEvent) {
                undo()
            }
        })
        editor.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), "redo")
        editor.inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK or InputEvent.SHIFT_DOWN_MASK), "redo")
        editor.actionMap.put("redo", object : AbstractAction("redo") {
            override fun actionPerformed(e: ActionEvent) {
                redo()
            }
        })

        (editor.document as AbstractDocument).documentFilter = object : DocumentFilter() {
            override fun remove(
                fb: FilterBypass,
                offset: Int,
                length: Int
            ) {
                if (length == 1) {
                    val text = fb.document.getText(offset, length)
                    val other = brackets[text[0]]

                    if (other != null && fb.document.getText(offset + 1, 1)[0] == other) {
                        fb.remove(offset + 1, length)
                    }
                }

                super.remove(fb, offset, length)
            }

            override fun replace(
                fb: FilterBypass,
                offset: Int,
                length: Int,
                text: String?,
                attrs: AttributeSet?
            ) {
                super.replace(fb, offset, length, text, attrs)

                if (length == 0 && text != null && text.length == 1) {
                    if (text[0] == '\n') {
                        val fullText = fb.document.getText(0, offset)
                        val lineIndex = fullText.substringAfterLast('\n')
                        val indentIndex = lineIndex.indexOfFirst { !it.isWhitespace() }
                        val indent = if (indentIndex == -1) lineIndex else lineIndex.substring(0, indentIndex)
                        fb.insertString(offset + 1, indent, null)
                    } else {
                        val other = brackets[text[0]]

                        if (other != null) {
                            val caret = editor.caretPosition
                            fb.insertString(caret, other.toString(), null)
                            editor.caretPosition = caret
                        }
                    }
                }
            }
        }
        editor.styledDocument.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                saved = false
                SwingUtilities.invokeLater { applyStyle() }
            }

            override fun removeUpdate(e: DocumentEvent) {
                saved = false
                SwingUtilities.invokeLater { applyStyle() }
            }

            override fun changedUpdate(e: DocumentEvent) {
            }
        })
        applyStyle()
    }

    override fun addNotify() {
        super.addNotify()
        (parent as? JTabbedPane)?.let { pane ->
            fun updateTitle() {
                val index = pane.indexOfComponent(this)
                pane.setTitleAt(index, if (saved) file.name else file.name + "*")
            }

            addPropertyChangeListener("file") {
                updateTitle()
            }
            addPropertyChangeListener("saved") {
                updateTitle()
            }
        }
    }

    open fun applyStyle() {
        editor.styledDocument.setParagraphAttributes(
            0,
            editor.styledDocument.length,
            SimpleAttributeSet().also {
                StyleConstants.setTabSet(it, BetterTabSet(editor, 4f))
                StyleConstants.setFontSize(it, 14)
            },
            false
        )
        styling?.apply(editor.styledDocument)
    }

    fun save() {
        if (!file.exists()) {
            val chooser = SystemFileChooser()
            val result = chooser.showOpenDialog(this)

            if (result == SystemFileChooser.APPROVE_OPTION) {
                file = chooser.selectedFile.toPath()
            } else {
                return
            }
        }

        file.writeText(editor.text)
        saved = true
    }

    fun undo() {
        if (undoManager.canUndo()) {
            undoManager.undo()
        }
    }

    fun redo() {
        if (undoManager.canRedo()) {
            undoManager.redo()
        }
    }
}