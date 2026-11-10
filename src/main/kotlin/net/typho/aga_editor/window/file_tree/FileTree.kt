package net.typho.aga_editor.window.file_tree

import net.typho.aga_editor.window.property
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultTreeModel

open class FileTree(
    folder: Path,
    @JvmField
    val open: (file: Path) -> Unit
) : JTree(), AutoCloseable {
    @JvmField
    val watchService = FileSystems.getDefault().newWatchService()
    @JvmField
    val watchThread = Thread {
        try {
            while (true) {
                val key = watchService.take() ?: break
                key.pollEvents().forEach { event ->
                }
                key.reset()
            }
        } catch (e: ClosedWatchServiceException) {
        }
    }
    var folder by property(folder, ::firePropertyChange) { old, new ->
        model = DefaultTreeModel(FileTreeNode(this, folder), false)
    }

    init {
        model = DefaultTreeModel(FileTreeNode(this, folder), false)
        watchThread.start()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val node = ((getPathForLocation(e.x, e.y) ?: return).lastPathComponent as FileTreeNode)

                if (SwingUtilities.isRightMouseButton(e)) {
                    FileOptionsPopupMenu(node.file, this@FileTree).show(this@FileTree, e.x, e.y)
                } else if (e.clickCount == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    open(node.file)
                }
            }
        })
    }

    override fun close() {
        watchService.close()
    }

    override fun removeNotify() {
        super.removeNotify()
        close()
    }
}