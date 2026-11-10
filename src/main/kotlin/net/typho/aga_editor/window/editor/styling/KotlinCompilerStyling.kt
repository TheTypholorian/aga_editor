package net.typho.aga_editor.window.editor.styling

import com.intellij.psi.PsiElement
import com.intellij.psi.TokenType.ERROR_ELEMENT
import com.intellij.psi.util.elementType
import org.jetbrains.kotlin.KtNodeTypes.*
import org.jetbrains.kotlin.analysis.api.KaExperimentalApi
import org.jetbrains.kotlin.analysis.api.resolution.resolveSuccessfulSymbol
import org.jetbrains.kotlin.analysis.api.session.analyze
import org.jetbrains.kotlin.analysis.api.standalone.buildStandaloneAnalysisAPISession
import org.jetbrains.kotlin.analysis.api.symbols.KaKotlinPropertySymbol
import org.jetbrains.kotlin.analysis.project.structure.builder.buildKtSourceModule
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.lexer.KtTokens.*
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import java.awt.Color
import javax.swing.text.StyledDocument
import kotlin.io.path.Path

open class KotlinCompilerStyling : Styling {
    @JvmField
    val session = buildStandaloneAnalysisAPISession {
        buildKtModuleProvider {
            val platform = JvmPlatforms.jvmPlatformByTargetVersion(JvmTarget.DEFAULT)
            this.platform = platform

            addModule(
                buildKtSourceModule {
                    moduleName = "editor"
                    this.platform = platform
                    addSourceRoot(Path("sandbox")) // TODO
                }
            )
        }
    }
    @JvmField
    val factory = KtPsiFactory(session.project, false)

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
    var property = style(color = Color(0xCF8EE5))
    @JvmField
    var error = style(color = Color(0xE23431), underline = true)
    @JvmField
    val brackets = mutableListOf(
        style(color = Color(0xFFDA60)),
        style(color = Color(0x7BD856)),
        style(color = Color(0x5F8FE2)),
        style(color = Color(0x726EE5))
    )

    @OptIn(KaExperimentalApi::class)
    override fun apply(document: StyledDocument, range: IntRange) {
        document.setCharacterAttributes(range.first, range.last - range.first + 1, style(), true)
        val file = factory.createFile(document.getText(range.first, range.last - range.first + 1))

        analyze(file) {
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

                        IDENTIFIER -> when (element.parent?.elementType) {
                            FUN -> methodDef
                            PROPERTY -> property
                            else -> null
                        }
                        REFERENCE_EXPRESSION -> {
                            (element as KtNameReferenceExpression).resolveSuccessfulSymbol()?.let { symbol ->
                                when (symbol) {
                                    is KaKotlinPropertySymbol -> property
                                    else -> null
                                }
                            }
                        }

                        LBRACKET -> brackets.getOrNull((bracketDepth++) % brackets.size)
                        RBRACKET -> brackets.getOrNull((--bracketDepth) % brackets.size)

                        LBRACE -> brackets.getOrNull((braceDepth++) % brackets.size)
                        RBRACE -> brackets.getOrNull((--braceDepth) % brackets.size)

                        LPAR -> brackets.getOrNull((parDepth++) % brackets.size)
                        RPAR -> brackets.getOrNull((--parDepth) % brackets.size)

                        LT -> brackets.getOrNull((angleDepth++) % brackets.size)
                        GT -> brackets.getOrNull((--angleDepth) % brackets.size)

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
    }

    companion object Default : KotlinCompilerStyling()
}