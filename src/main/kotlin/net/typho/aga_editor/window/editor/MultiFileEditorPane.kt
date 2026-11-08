package net.typho.aga_editor.window.editor

import java.nio.file.Path
import javax.swing.JTabbedPane
import kotlin.io.path.name

open class MultiFileEditorPane : JTabbedPane() {
    fun open(file: Path): FileTextPane {
        repeat(tabCount) { i ->
            val tab = getTabComponentAt(i)

            if ((tab as? FileTextPane)?.file == file) {
                selectedIndex = i
                return tab
            }
        }

        return FileTextPane(file).also {
            addTab(file.name, FileTextPane(file))
        }
    }
}