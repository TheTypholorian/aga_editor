package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.lexer.JavaLexer
import net.typho.compilation_util.lexer.LexerToken
import java.nio.file.Path
import javax.swing.event.DocumentEvent
import javax.swing.text.StyledDocument

open class JavaCompilerStyling : Styling {
    @JvmField
    val lexer = JavaLexer()

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
        if (change == null) {
            val tokens = lexer.process(document.getText(0, document.length))

            return {
                val start = System.currentTimeMillis()
                document.setCharacterAttributes(0, document.length, Styling.clear, true)

                for (token in tokens) {
                    style(token, document)
                }
                println("${System.currentTimeMillis() - start} ms apply")
            }
        } else {
            val update = lexer.update(change) ?: return { }
            val tokens = update.newTokensInRange.toList()

            return {
                val start = System.currentTimeMillis()
                document.setCharacterAttributes(update.range.first, update.range.last - update.range.first, Styling.clear, true)

                for (token in tokens) {
                    style(token, document)
                }
                println("${System.currentTimeMillis() - start} ms apply")
            }
        }
    }
}