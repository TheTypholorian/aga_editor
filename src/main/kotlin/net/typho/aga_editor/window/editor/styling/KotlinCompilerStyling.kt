package net.typho.aga_editor.window.editor.styling

import com.intellij.openapi.util.Disposer
import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.ERROR_ELEMENT
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.CoreEnvironmentDeprecation
import org.jetbrains.kotlin.KtNodeTypes.FUN
import org.jetbrains.kotlin.KtNodeTypes.PROPERTY
import org.jetbrains.kotlin.cli.create
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.psi.KtExperimentalApi
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.ANNOTATION_ENTRY
import java.awt.Color
import javax.swing.text.StyledDocument

open class KotlinCompilerStyling : Styling, AutoCloseable {
    @JvmField
    val disposable = Disposer.newDisposable()
    @JvmField
    val config = CompilerConfiguration.create()
    @OptIn(CoreEnvironmentDeprecation::class)
    @JvmField
    val environment = KotlinCoreEnvironment.createForProduction(
        disposable,
        config,
        EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
    @JvmField
    val factory = KtPsiFactory(environment.project, false)

    @JvmField
    var keywords = style(color = Color(0xE28C4F))
    @JvmField
    var comments = style(color = Color(0x9BA2B2))
    @JvmField
    var numbers = style(color = Color(0x55B7C6))
    @JvmField
    var strings = style(color = Color(0x6DAA5C))
    @JvmField
    var annotations = style(color = Color(0xD6BC60))
    @JvmField
    var methodDef = style(color = Color(0x58A6F4))
    @JvmField
    var instanceField = style(color = Color(0xCF8EE5))
    @JvmField
    var staticField = style(color = Color(0xCF8EE5), italic = true)
    @JvmField
    var error = style(color = Color(0xE23431), underline = true)
    @JvmField
    val brackets = mutableListOf(
        style(color = Color(0xFFDA60)),
        style(color = Color(0x7BD856)),
        style(color = Color(0x5F8FE2)),
        style(color = Color(0x726EE5))
    )

    @OptIn(KtExperimentalApi::class)
    override fun apply(document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, style(), true)
        val file = factory.createFile(document.getText(range.first, range.last - range.first + 1))
        file.accept(object : KtTreeVisitorVoid() {
            var bracketDepth = 0
            var braceDepth = 0
            var parDepth = 0
            var angleDepth = 0

            override fun visitElement(element: PsiElement) {
                val style = when (element.elementType) {
                    in KEYWORDS -> keywords
                    in SOFT_KEYWORDS -> keywords
                    in COMMENTS -> comments
                    INTEGER_LITERAL, FLOAT_LITERAL -> numbers
                    OPEN_QUOTE, CLOSING_QUOTE, in STRINGS -> strings
                    ANNOTATION_ENTRY -> annotations

                    LBRACKET -> brackets.getOrNull((bracketDepth++) % brackets.size)
                    RBRACKET -> brackets.getOrNull((--bracketDepth) % brackets.size)

                    LBRACE -> brackets.getOrNull((braceDepth++) % brackets.size)
                    RBRACE -> brackets.getOrNull((--braceDepth) % brackets.size)

                    LPAR -> brackets.getOrNull((parDepth++) % brackets.size)
                    RPAR -> brackets.getOrNull((--parDepth) % brackets.size)

                    LT -> brackets.getOrNull((angleDepth++) % brackets.size)
                    GT -> brackets.getOrNull((--angleDepth) % brackets.size)

                    IDENTIFIER -> when (element.parent?.elementType) {
                        FUN -> methodDef
                        PROPERTY -> instanceField
                        else -> null
                    }

                    else -> null
                }

                style?.let {
                    document.setCharacterAttributes(
                        element.textRange.startOffset + range.first,
                        element.textRange.length + range.first,
                        style,
                        true
                    )
                }
                super.visitElement(element)
            }
        })
        file.accept(object : KtTreeVisitorVoid() {
            override fun visitElement(element: PsiElement) {
                if (element.elementType == ERROR_ELEMENT) {
                    document.setCharacterAttributes(
                        element.textRange.startOffset + range.first,
                        element.textRange.length.coerceAtLeast(1) + range.first,
                        error,
                        true
                    )
                }
                super.visitElement(element)
            }
        })
    }

    override fun close() {
        Disposer.dispose(disposable)
    }

    companion object Default : KotlinCompilerStyling()
}