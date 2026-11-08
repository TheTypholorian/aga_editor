package net.typho.aga_editor.window.editor.styling

import com.intellij.psi.tree.IElementType
import org.jetbrains.kotlin.lexer.KotlinLexer
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.lexer.KtTokens
import java.awt.Color
import javax.swing.text.AttributeSet
import javax.swing.text.StyledDocument

open class KotlinStyling : Styling {
    @JvmField
    val lexer = KotlinLexer()
    @JvmField
    protected val tokenStyles = mutableMapOf<IElementType, AttributeSet>()

    @JvmField
    val keywords = style(color = Color(0xCF8E6d))
    @JvmField
    val comments = style(color = Color(0x7A7E85))
    @JvmField
    val numbers = style(color = Color(0x2AACB8))
    @JvmField
    val templates = style(color = Color(0xCF8E6d))
    @JvmField
    val strings = style(color = Color(0x6AAB73))

    init {
        (KtTokens.KEYWORDS.types + KtTokens.SOFT_KEYWORDS.types).forEach { tokenStyles[it] = keywords }
        KtTokens.COMMENTS.types.forEach { tokenStyles[it] = comments }
        tokenStyles[KtTokens.INTEGER_LITERAL] = numbers
        tokenStyles[KtTokens.FLOAT_LITERAL] = numbers
        tokenStyles[KtTokens.INTERPOLATION_PREFIX] = templates
        tokenStyles[KtTokens.ESCAPE_SEQUENCE] = templates
        KtTokens.STRINGS.types.forEach { tokenStyles[it] = strings }
    }

    fun getStyle(token: IElementType) = tokenStyles[token]

    override fun apply(document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, style(), true)
        lexer.start(document.getText(range.first, range.last - range.first + 1))

        while (lexer.tokenType != null) {
            val style = getStyle(lexer.tokenType!!)

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