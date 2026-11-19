package net.typho.aga_editor.window.editor.styling

import net.typho.compilation_util.lexer.JavaLexer
import net.typho.compilation_util.lexer.Keyword
import net.typho.compilation_util.lexer.LexerToken
import net.typho.compilation_util.parser.JavaParser
import net.typho.compilation_util.parser.ParserNode
import net.typho.parsing_util.StringView
import java.text.AttributedCharacterIterator
import java.text.AttributedString

open class JavaCompilerStyling : Styling {
    @JvmField
    protected val lexer = JavaLexer()
    @JvmField
    protected val tokens = mutableListOf<LexerToken>()
    @JvmField
    protected var parsed: ParserNode.File? = null

    override fun update(text: CharSequence, defaultAttributes: Map<AttributedCharacterIterator.Attribute, Any>): List<AttributedCharacterIterator> {
        tokens.clear()
        lexer.process(text, tokens)
        parsed = JavaParser.parse(tokens)

        return buildList {
            var textStart = 0

            var parenthesesInc = 0
            var bracketsInc = 0
            var bracesInc = 0
            var angleBracketsInc = 0

            text.forEachIndexed { textEnd, ch ->
                if (ch == '\n') { // TODO
                    val view = StringView(text, textStart until textEnd)

                    if (view.isEmpty()) {
                        add(AttributedString("").iterator)
                    } else {
                        val attributed = AttributedString(view.toString(), defaultAttributes)

                        fun style(range: IntRange, styling: Map<AttributedCharacterIterator.Attribute, Any?>) {
                            // TODO optimize
                            val min = (range.first - textStart).coerceAtLeast(0)
                            val max = (range.last - textStart).coerceAtMost(view.length)

                            if ((max - min) > 0) {
                                attributed.addAttributes(
                                    styling,
                                    min,
                                    max
                                )
                            }
                        }

                        fun style(anno: ParserNode.Annotation) {
                            anno.type.path.tokens.forEach { style(it.text.range, Styling.annotations) }
                        }

                        tokens.forEach { token ->
                            when (token) {
                                is LexerToken.Comment -> Styling.comments
                                is LexerToken.Error -> Styling.error
                                is LexerToken.Keyword -> Styling.keywords
                                is LexerToken.Number -> Styling.numbers
                                is LexerToken.Punctuation -> when (token.char) {
                                    '(' -> Styling.brackets[(parenthesesInc++) % Styling.brackets.size]
                                    ')' -> Styling.brackets[((parenthesesInc - 1).coerceAtLeast(0).also { parenthesesInc = it }) % Styling.brackets.size]

                                    '[' -> Styling.brackets[(bracketsInc++) % Styling.brackets.size]
                                    ']' -> Styling.brackets[((bracketsInc - 1).coerceAtLeast(0).also { bracketsInc = it }) % Styling.brackets.size]

                                    '{' -> Styling.brackets[(bracesInc++) % Styling.brackets.size]
                                    '}' -> Styling.brackets[((bracesInc - 1).coerceAtLeast(0).also { bracesInc = it }) % Styling.brackets.size]

                                    '<' -> Styling.brackets[(angleBracketsInc++) % Styling.brackets.size]
                                    '>' -> Styling.brackets[((angleBracketsInc - 1).coerceAtLeast(0).also { angleBracketsInc = it }) % Styling.brackets.size]

                                    else -> null
                                }
                                is LexerToken.Text -> Styling.strings
                                else -> null
                            }?.let { style(token.text.range, it) }
                        }

                        parsed?.let { parsed ->
                            for (cls in parsed.classes) {
                                cls.annotations.forEach { style(it) }

                                for (attribute in cls.attributes) {
                                    if (attribute is ParserNode.WithAnnotations) {
                                        attribute.annotations.forEach { style(it) }
                                    }

                                    when (attribute) {
                                        is ParserNode.Field -> {
                                            style(
                                                attribute.name.text.range,
                                                if (attribute.modifiers.any { it.type == Keyword.STATIC }) Styling.staticProperty else Styling.instanceProperty
                                            )
                                        }
                                        is ParserNode.Method -> {
                                            style(attribute.name.text.range, Styling.methodDef)
                                        }
                                    }
                                }
                            }
                        }

                        add(attributed.iterator)
                    }

                    textStart = textEnd + 1
                }
            }
        }
    }

    open class JavaTokenCharacterIterator(
        text: CharSequence,
        tokens: List<LexerToken>,
        @JvmField
        val tokenOffset: Int,
        @JvmField
        val defaultAttributes: Map<AttributedCharacterIterator.Attribute, Any>
    ) : TokenCharacterIterator<LexerToken>(text, tokens) {
        override val LexerToken.range: IntRange
            get() = (text.range.first + tokenOffset)..(text.range.last + tokenOffset)
        override val LexerToken.attributes: Map<AttributedCharacterIterator.Attribute, Any>
            get() = when (this) {
                is LexerToken.Comment -> Styling.comments
                is LexerToken.Error -> Styling.error
                is LexerToken.Identifier -> Styling.methodDef
                is LexerToken.Keyword -> Styling.keywords
                is LexerToken.Number -> Styling.numbers
                is LexerToken.Punctuation -> Styling.brackets[0] // TODO
                is LexerToken.Text -> Styling.strings
            }

        override fun clone() = JavaTokenCharacterIterator(text, tokens.toList(), tokenOffset, defaultAttributes.toMap())

        override fun toString(): String {
            return "JavaTokenCharacterIterator($text)"
        }
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