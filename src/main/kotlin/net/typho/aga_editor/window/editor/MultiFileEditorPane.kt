package net.typho.aga_editor.window.editor

import com.formdev.flatlaf.FlatClientProperties
import net.typho.aga_editor.window.editor.styling.Styling
import java.nio.file.Path
import java.util.function.IntConsumer
import javax.swing.JTabbedPane
import kotlin.io.path.extension
import kotlin.io.path.name

open class MultiFileEditorPane : JTabbedPane() {
    init {
        putClientProperty(FlatClientProperties.TABBED_PANE_TAB_CLOSABLE, true)
        putClientProperty(
            FlatClientProperties.TABBED_PANE_TAB_CLOSE_CALLBACK,
            IntConsumer { index ->
                val tab = getComponentAt(index)
                (tab as? FileTextPane)?.save()
                removeTabAt(index)
            }
        )
    }

    fun open(file: Path, styling: (() -> Styling)? = Styling.byFileExtension[file.extension]): FileTextPane {
        repeat(tabCount) { i ->
            val tab = getComponentAt(i)

            if ((tab as? FileTextPane)?.file == file) {
                selectedIndex = i
                return tab
            }
        }

        return FileTextPane(file, styling?.invoke()).also {
            addTab(file.name, it)
            selectedIndex = tabCount - 1
        }
    }
}