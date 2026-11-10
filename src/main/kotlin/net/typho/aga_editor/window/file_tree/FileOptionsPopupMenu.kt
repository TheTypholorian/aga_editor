package net.typho.aga_editor.window.file_tree

import java.awt.event.ActionEvent
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.AbstractAction
import javax.swing.JMenu
import javax.swing.JPopupMenu
import javax.swing.JTextField
import kotlin.io.path.isDirectory

class FileOptionsPopupMenu(
    @JvmField
    val file: Path,
    @JvmField
    val tree: FileTree? = null
) : JPopupMenu() {
    init {
        add(JMenu("New").also { menu ->
            if (file.isDirectory()) {
                menu.add(object : AbstractAction("File") {
                    override fun actionPerformed(e: ActionEvent) {
                        val popup = JPopupMenu()
                        val field = JTextField(30)
                        popup.add(field)
                        field.addActionListener { e ->
                            val name = field.text.trim()

                            if (name.isNotEmpty()) {
                                val file = file.resolve(name)
                                Files.createFile(file)
                                popup.isVisible = false

                                if (tree != null) {
                                    tree.open(file)
                                }
                            }
                        }

                        popup.show(this@FileOptionsPopupMenu.invoker, 0, 0)
                        field.requestFocusInWindow()
                    }
                })
                menu.add(object : AbstractAction("Folder") {
                    override fun actionPerformed(e: ActionEvent) {
                    }
                })
            }
        })
    }
}