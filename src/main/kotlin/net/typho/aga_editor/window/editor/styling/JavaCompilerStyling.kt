package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.lexer.JavaLexer
import net.typho.compilation_util.lexer.LexerToken
import java.nio.file.Path
import javax.swing.event.DocumentEvent
import javax.swing.text.StyledDocument

open class JavaCompilerStyling : Styling {
    @JvmField
    val lexer = JavaLexer()

    override fun apply(file: Path, document: StyledDocument, change: DocumentEvent?) {
        val text = document.getText(0, document.length)
        val tokens = if (change == null) {
            document.setCharacterAttributes(0, document.length, Styling.clear, true)
            lexer.process(text)
        } else {
            val update = lexer.update(change) ?: return
            document.setCharacterAttributes(0, document.length, Styling.clear, true)
            lexer.tokens
            //document.setCharacterAttributes(update.range.first, update.range.last - update.range.first, Styling.clear, true)
            //update.newTokensInRange
        }

        for (token in tokens) {
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
                token.range.first,
                token.range.last - token.range.first,
                style,
                false
            )
        }
    }
}