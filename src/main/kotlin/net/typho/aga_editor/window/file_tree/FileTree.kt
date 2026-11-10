package net.typho.aga_editor.window.file_tree

import net.typho.aga_editor.window.property
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.TreeCellRenderer
import kotlin.io.path.isDirectory
import kotlin.io.path.name

open class FileTree(
    folder: Path,
    @JvmField
    val open: (file: Path) -> Unit
) : JTree(), AutoCloseable {
    var folder by property(folder, ::firePropertyChange) { old, new ->
        model = FileTreeModel(folder)
    }

    init {
        model = FileTreeModel(folder)
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val file = (getPathForLocation(e.x, e.y) ?: return).lastPathComponent as Path

                if (SwingUtilities.isRightMouseButton(e)) {
                    FileOptionsPopupMenu(file, this@FileTree).show(this@FileTree, e.x, e.y)
                } else if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e) && !file.isDirectory()) {
                    open(file)
                }
            }
        })
    }

    override fun close() {
        (model as? AutoCloseable)?.close()
    }

    override fun convertValueToText(
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): String? {
        return (value as? Path)?.name
    }

    override fun removeNotify() {
        super.removeNotify()
        close()
    }
}