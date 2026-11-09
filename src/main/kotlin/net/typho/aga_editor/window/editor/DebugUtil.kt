package net.typho.aga_editor.window.editor

import com.intellij.psi.PsiElement

fun PsiElement.debugPrint(indent: Int = 0) {
    println("\t".repeat(indent) + this)
    children.forEach { it.debugPrint(indent + 1) }
}