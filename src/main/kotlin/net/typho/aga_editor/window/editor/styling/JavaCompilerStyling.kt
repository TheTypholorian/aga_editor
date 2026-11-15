package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.lexer.JavaLexer
import net.typho.compilation_util.lexer.LexerToken
import java.awt.Color
import java.awt.Font
import java.nio.file.Path
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.event.UndoableEditListener
import javax.swing.text.AttributeSet
import javax.swing.text.Element
import javax.swing.text.Position
import javax.swing.text.Segment
import javax.swing.text.Style
import javax.swing.text.StyledDocument

open class JavaCompilerStyling : StyledDocument {
    @JvmField
    val lexer = JavaLexer()

    override fun addStyle(nm: String?, parent: Style?): Style? {
        TODO("Not yet implemented")
    }

    override fun removeStyle(nm: String?) {
        TODO("Not yet implemented")
    }

    override fun getStyle(nm: String?): Style? {
        TODO("Not yet implemented")
    }

    override fun setCharacterAttributes(
        offset: Int,
        length: Int,
        s: AttributeSet?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun setParagraphAttributes(
        offset: Int,
        length: Int,
        s: AttributeSet?,
        replace: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun setLogicalStyle(pos: Int, s: Style?) {
        TODO("Not yet implemented")
    }

    override fun getLogicalStyle(p: Int): Style? {
        TODO("Not yet implemented")
    }

    override fun getParagraphElement(pos: Int): Element? {
        TODO("Not yet implemented")
    }

    override fun getCharacterElement(pos: Int): Element? {
        TODO("Not yet implemented")
    }

    override fun getForeground(attr: AttributeSet?): Color? {
        TODO("Not yet implemented")
    }

    override fun getBackground(attr: AttributeSet?): Color? {
        TODO("Not yet implemented")
    }

    override fun getFont(attr: AttributeSet?): Font? {
        TODO("Not yet implemented")
    }

    override fun getLength(): Int {
        TODO("Not yet implemented")
    }

    override fun addDocumentListener(listener: DocumentListener?) {
        TODO("Not yet implemented")
    }

    override fun removeDocumentListener(listener: DocumentListener?) {
        TODO("Not yet implemented")
    }

    override fun addUndoableEditListener(listener: UndoableEditListener?) {
        TODO("Not yet implemented")
    }

    override fun removeUndoableEditListener(listener: UndoableEditListener?) {
        TODO("Not yet implemented")
    }

    override fun getProperty(key: Any?): Any? {
        TODO("Not yet implemented")
    }

    override fun putProperty(key: Any?, value: Any?) {
        TODO("Not yet implemented")
    }

    override fun remove(offs: Int, len: Int) {
        TODO("Not yet implemented")
    }

    override fun insertString(offset: Int, str: String?, a: AttributeSet?) {
        TODO("Not yet implemented")
    }

    override fun getText(offset: Int, length: Int): String? {
        TODO("Not yet implemented")
    }

    override fun getText(offset: Int, length: Int, txt: Segment?) {
        TODO("Not yet implemented")
    }

    override fun getStartPosition(): Position? {
        TODO("Not yet implemented")
    }

    override fun getEndPosition(): Position? {
        TODO("Not yet implemented")
    }

    override fun createPosition(offs: Int): Position? {
        TODO("Not yet implemented")
    }

    override fun getRootElements(): Array<out Element?>? {
        TODO("Not yet implemented")
    }

    override fun getDefaultRootElement(): Element? {
        TODO("Not yet implemented")
    }

    override fun render(r: Runnable?) {
        TODO("Not yet implemented")
    }

    open fun style(token: LexerToken, document: StyledDocument) {
        val style = when (token) {
            is LexerToken.Comment -> Styling.comments
            is LexerToken.Error -> Styling.error
            is LexerToken.Identifier -> Styling.methodDef // TODO
            is LexerToken.Keyword -> Styling.keywords
            is LexerToken.Number -> Styling.numbers
            is LexerToken.Punctuation -> Styling.brackets[0]
            is LexerToken.Text -> Styling.strings
        }

        document.setCharacterAttributes(
            token.text.range.first,
            token.text.range.last - token.text.range.first,
            style,
            false
        )
    }

    override fun apply(file: Path, document: StyledDocument, change: DocumentEvent?): Runnable {
        val tokens = lexer.process(document.getText(0, document.length))

        return {
            val start = System.currentTimeMillis()
            document.setCharacterAttributes(0, document.length, Styling.clear, true)

            for (token in tokens) {
                style(token, document)
            }
            println("${System.currentTimeMillis() - start} ms apply")
        }
    }
}