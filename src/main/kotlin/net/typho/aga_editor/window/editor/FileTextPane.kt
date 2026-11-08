package net.typho.aga_editor.window.editor

import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.nio.file.Path
import javax.swing.AbstractAction
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextPane
import javax.swing.KeyStroke
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

open class FileTextPane(
    @JvmField
    val file: Path
) : JScrollPane(JTextArea().apply {
    if (!file.exists()) {
        file.createParentDirectories()
        file.createFile()
    }

    text = file.readText()
    tabSize = 2
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "save")
    actionMap.put("save", object : AbstractAction("save") {
        override fun actionPerformed(e: ActionEvent) {
            file.writeText(text)
        }
    })
}) {
    val editor: JTextPane
        get() = viewport.view as JTextPane
}