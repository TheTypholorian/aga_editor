package net.typho.aga_editor.window.editor

import com.formdev.flatlaf.util.SystemFileChooser
import net.typho.aga_editor.window.editor.styling.Styling
import net.typho.aga_editor.window.property
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Path
import javax.swing.AbstractAction
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.JTextPane
import javax.swing.KeyStroke
import javax.swing.SwingUtilities
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.SimpleAttributeSet
import javax.swing.text.StyleConstants
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.readText
import kotlin.io.path.writeText

open class FileTextPane(
    file: Path,
    @JvmField
    val styling: Styling? = null
) : JScrollPane(JTextPane()) {
    var file by property(file, ::firePropertyChange)
    val editor: JTextPane
        get() = viewport.view as JTextPane
    var saved by property(true, ::firePropertyChange)

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
}