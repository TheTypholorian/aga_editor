package net.typho.aga_editor.window.file_tree

import java.nio.file.Files
import java.nio.file.Path
import java.util.Collections
import javax.swing.tree.TreeNode
import kotlin.io.path.isDirectory
import kotlin.io.path.name

data class FileTreeNode(
    @JvmField
    val file: Path,
    @JvmField
    val parent: FileTreeNode? = null
) : TreeNode {
    @JvmField
    val children = if (file.isDirectory()) Files.list(file).map { FileTreeNode(it, this) }.toList() else null

    override fun children() = Collections.enumeration(children ?: listOf())

    override fun getChildAt(childIndex: Int) = children?.getOrNull(childIndex)

    override fun getChildCount() = children?.size ?: 0

    override fun getParent() = parent

    override fun getIndex(node: TreeNode) = children?.indexOf(node) ?: -1

    override fun getAllowsChildren() = children != null

    override fun isLeaf() = children == null

    override fun toString() = file.name
}
