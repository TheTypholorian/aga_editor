package net.typho.aga_editor.window.file_tree

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.util.*
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeNode
import kotlin.io.path.isDirectory
import kotlin.io.path.name

class FileTreeNode(
    @JvmField
    val tree: FileTree,
    @JvmField
    val file: Path,
    @JvmField
    var parent: FileTreeNode? = null
) : MutableTreeNode, AutoCloseable {
    @JvmField
    var watchKey: WatchKey? = null
    @JvmField
    var children = if (file.isDirectory()) Files.list(file).map { FileTreeNode(tree, it, this) }.toList().toMutableList() else null

    fun initWatch() {
        if (watchKey == null) {
            watchKey = file.register(tree.watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE)
        }

        children?.forEach { it.initWatch() }
    }

    override fun close() {
        watchKey?.cancel()
        watchKey = null

        children?.forEach { it.close() }
    }

    override fun toString() = file.name

    override fun children() = Collections.enumeration(children ?: listOf())

    override fun insert(child: MutableTreeNode, index: Int) {
        children?.let {
            it.add(index, child as FileTreeNode)
            child.setParent(this)
        }
    }

    override fun remove(index: Int) {
        children?.removeAt(index)?.removeFromParent()
    }

    override fun remove(node: MutableTreeNode) {
        children?.let {
            if (it.remove(node)) {
                node.removeFromParent()
            }
        }
    }

    override fun setUserObject(`object`: Any?) {
    }

    override fun removeFromParent() {
        parent = null
        close()
    }

    override fun setParent(parent: MutableTreeNode) {
        this.parent = parent as FileTreeNode
        initWatch()
    }

    override fun getChildAt(childIndex: Int) = children?.getOrNull(childIndex)

    override fun getChildCount() = children?.size ?: 0

    override fun getParent() = parent

    override fun getIndex(node: TreeNode?) = children?.indexOf(node) ?: -1

    override fun getAllowsChildren() = children != null

    override fun isLeaf() = children == null
}
