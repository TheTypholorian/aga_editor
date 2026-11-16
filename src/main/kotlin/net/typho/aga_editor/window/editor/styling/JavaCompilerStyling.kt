package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.lexer.JavaLexer
import net.typho.compilation_util.lexer.LexerToken
import net.typho.compilation_util.parser.JavaParser
import net.typho.compilation_util.parser.ParserNode
import java.awt.Color
import java.awt.Font
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import javax.swing.Action
import javax.swing.JComponent
import javax.swing.event.DocumentListener
import javax.swing.event.UndoableEditListener
import javax.swing.text.AttributeSet
import javax.swing.text.Caret
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.Document
import javax.swing.text.EditorKit
import javax.swing.text.Element
import javax.swing.text.GapContent
import javax.swing.text.PlainDocument
import javax.swing.text.Position
import javax.swing.text.Segment
import javax.swing.text.Style
import javax.swing.text.ViewFactory

open class JavaCompilerStyling(
    @JvmField
    val editor: JComponent
) : EditorKit() {
    @JvmField
    protected val lexer = JavaLexer()
    @JvmField
    protected var tokens = listOf<LexerToken>()
    @JvmField
    protected var parsed: ParserNode.File? = null

    /*
    protected fun reload() {
        val tokens = //lexer.process(getText(0, length))
        val parsed = JavaParser.parse(tokens)

        // TODO

        this.tokens = tokens
        this.parsed = parsed
    }
     */

    override fun getContentType() = "text/x-java-source"

    override fun getViewFactory() = null

    override fun getActions() = arrayOf<Action>() // TODO

    override fun createCaret() = null

    override fun createDefaultDocument() = DefaultStyledDocument()

    override fun read(`in`: InputStream, doc: Document, pos: Int) {
        read(`in`.reader(), doc, pos)
    }

    override fun read(`in`: Reader, doc: Document, pos: Int) {
    }

    override fun write(out: OutputStream, doc: Document, pos: Int, len: Int) {
        write(out.writer(), doc, pos, len)
    }

    override fun write(out: Writer, doc: Document, pos: Int, len: Int) {
    }

    /*
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
     */
}