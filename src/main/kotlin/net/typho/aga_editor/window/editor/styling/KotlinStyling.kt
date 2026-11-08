package net.typho.aga_editor.window.editor.styling

import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KotlinLexer
import org.jetbrains.kotlin.lexer.KtTokens
import java.awt.Color
import javax.swing.text.AttributeSet
import javax.swing.text.StyledDocument

open class KotlinStyling : Styling {
    @JvmField
    val lexer = KotlinLexer()
    @JvmField
    val keywords = style(color = Color(0xCF8E6d))
    @JvmField
    val tokens = mutableMapOf<IElementType, AttributeSet>()

    init {
        (KtTokens.KEYWORDS.types + KtTokens.SOFT_KEYWORDS.types).forEach { tokens[it] = keywords }
    }

    override fun apply(document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, style(), true)
        lexer.start(document.getText(range.first, range.last - range.first + 1))

        while (lexer.tokenType != null) {
            val style = tokens[lexer.tokenType!!]

            if (style != null) {
                document.setCharacterAttributes(
                    lexer.tokenStart + range.first,
                    lexer.tokenEnd - lexer.tokenStart + range.first,
                    style,
                    true
                )
            }

            lexer.advance()
        }
    }

    companion object Default : KotlinStyling()
}