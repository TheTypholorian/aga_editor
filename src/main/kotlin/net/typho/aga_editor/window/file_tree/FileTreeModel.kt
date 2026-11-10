package net.typho.aga_editor.window.file_tree

import java.nio.file.ClosedWatchServiceException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import javax.swing.SwingUtilities
import javax.swing.event.TreeModelEvent
import javax.swing.event.TreeModelListener
import javax.swing.tree.TreeModel
import javax.swing.tree.TreePath
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

open class FileTreeModel(
    @JvmField
    val root: Path
) : TreeModel, AutoCloseable {
    @JvmField
    val watchService = FileSystems.getDefault().newWatchService()
    @JvmField
    val watchThread = Thread {
        try {
            while (true) {
                val key = watchService.take() ?: break
                key.pollEvents().forEach { event ->
                    when (event.kind()) {
                        StandardWatchEventKinds.ENTRY_CREATE -> {
                            val path = (key.watchable() as Path).resolve(event.context() as Path)
                            SwingUtilities.invokeLater {
                                val list = map.computeIfAbsent(path.parent) { mutableListOf() }
                                val index = list.size
                                list.add(path)

                                if (path.isDirectory()) {
                                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
                                }

                                val event = TreeModelEvent(this, path.parent.toTreePath(), intArrayOf(index), arrayOf(path))
                                listeners.forEach { it.treeNodesInserted(event) }
                            }
                        }
                        StandardWatchEventKinds.ENTRY_DELETE -> {
                            val path = (key.watchable() as Path).resolve(event.context() as Path)
                            SwingUtilities.invokeLater {
                                val list = map.computeIfAbsent(path.parent) { mutableListOf() }
                                val index = list.indexOf(path)

                                map.remove(path)
                                list.remove(path)

                                val event = TreeModelEvent(this, path.parent.toTreePath(), intArrayOf(index), arrayOf(path))
                                listeners.forEach { it.treeNodesRemoved(event) }
                            }
                        }
                    }
                }
                key.reset()
            }
        } catch (e: ClosedWatchServiceException) {
        }
    }
    @JvmField
    protected val map = mutableMapOf<Path, MutableList<Path>>()
    @JvmField
    val listeners = mutableListOf<TreeModelListener>()

    init {
        fun scan(path: Path) {
            if (path.isDirectory()) {
                val entries = path.listDirectoryEntries()
                map[path] = entries.toMutableList()
                entries.forEach { scan(it) }
            }
        }

        scan(root)
        map.keys.forEach { it.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE) }
        watchThread.start()
    }

    override fun close() {
        watchService.close()
    }

    protected open fun Path.toTreePath(): TreePath {
        return if (this == this@FileTreeModel.root) TreePath(this) else parent.toTreePath().pathByAddingChild(this)
    }

    override fun getRoot() = root

    override fun getChild(parent: Any?, index: Int) = map[parent]?.get(index)

    override fun getChildCount(parent: Any?) = map[parent]?.size ?: 0

    override fun isLeaf(node: Any?) = !(node as Path).isDirectory()

    override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
        throw UnsupportedOperationException()
    }

    override fun getIndexOfChild(parent: Any?, child: Any?) = map[parent]?.indexOf(child) ?: -1

    override fun addTreeModelListener(l: TreeModelListener) {
        listeners.add(l)
    }

    override fun removeTreeModelListener(l: TreeModelListener) {
        listeners.remove(l)
    }
}