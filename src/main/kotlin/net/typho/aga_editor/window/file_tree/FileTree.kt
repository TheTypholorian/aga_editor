package net.typho.aga_editor.window.file_tree

import net.typho.aga_editor.window.property
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.file.Path
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultTreeModel

open class FileTree(
    folder: Path,
    @JvmField
    protected val open: (file: Path) -> Unit
) : JTree(FileTreeNode(folder)) {
    var folder by property(folder, ::firePropertyChange) { old, new ->
        model = DefaultTreeModel(FileTreeNode(new), false)
    }

    init {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    val path = getPathForLocation(e.x, e.y) ?: return
                    val node = path.lastPathComponent as FileTreeNode
                    open(node.file)
                }
            }
        })
    }
}