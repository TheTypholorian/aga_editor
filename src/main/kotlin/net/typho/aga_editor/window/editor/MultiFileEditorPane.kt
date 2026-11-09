package net.typho.aga_editor.window.editor

import com.formdev.flatlaf.FlatClientProperties
import net.typho.aga_editor.window.editor.styling.Styling
import java.nio.file.Path
import java.util.function.IntConsumer
import javax.swing.JTabbedPane
import kotlin.io.path.name

open class MultiFileEditorPane : JTabbedPane() {
    init {
        putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true)
        putClientProperty(
            FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
            IntConsumer { index ->
                val tab = getTabComponentAt(index)
                (tab as? FileTextPane)?.save()
                removeTabAt(index)
            }
        )
    }

    fun open(file: Path, styling: Styling? = null): FileTextPane {
        repeat(tabCount) { i ->
            val tab = getTabComponentAt(i)

            if ((tab as? FileTextPane)?.file == file) {
                selectedIndex = i
                return tab
            }
        }

        return FileTextPane(file, styling).also {
            addTab(file.name, it)
            selectedIndex = tabCount - 1
        }
    }
}