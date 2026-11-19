package net.typho.aga_editor.window.editor.styling

import java.text.AttributedCharacterIterator
import java.text.CharacterIterator

abstract class TokenCharacterIterator<V>(
    @JvmField
    val text: CharSequence,
    @JvmField
    val tokens: List<V>
) : AttributedCharacterIterator {
    @JvmField
    protected var pos = 0

    abstract val V.range: IntRange

    abstract val V.attributes: Map<AttributedCharacterIterator.Attribute, Any>

    fun currentToken(): V? {
        return tokens.firstOrNull { pos in it.range }
    }

    override fun getRunStart(): Int {
        for (token in tokens) {
            val range = token.range

            if (pos >= range.first) {
                return if (pos <= range.last) {
                    range.first
                } else {
                    range.last
                }
            } else {
                //break
            }
        }

        return 0
    }

    override fun getRunStart(attribute: AttributedCharacterIterator.Attribute) = getRunStart()

    override fun getRunStart(attributes: Set<AttributedCharacterIterator.Attribute>) = getRunStart()

    override fun getRunLimit(): Int {
        val iterator = tokens.iterator()

        while (iterator.hasNext()) {
            val token = iterator.next()
            val range = token.range

            if (pos >= range.first && pos < range.last) {
                return range.last
            }
        }

        return text.length
    }

    override fun getRunLimit(attribute: AttributedCharacterIterator.Attribute) = getRunLimit()

    override fun getRunLimit(attributes: Set<AttributedCharacterIterator.Attribute>) = getRunLimit()

    override fun getAttributes() = currentToken()?.attributes ?: mapOf()

    override fun getAttribute(attribute: AttributedCharacterIterator.Attribute) = currentToken()?.attributes?.get(attribute)

    override fun getAllAttributeKeys() = currentToken()?.attributes?.keys ?: setOf()

    override fun first(): Char {
        pos = 0
        return current()
    }

    override fun last(): Char {
        pos = if (text.isEmpty()) 0 else text.length - 1
        return current()
    }

    override fun current() = if (pos >= 0 && pos < text.length) text[pos] else CharacterIterator.DONE

    override fun next() = if (pos < text.length - 1) {
        pos++
        current()
    } else {
        pos = text.length
        CharacterIterator.DONE
    }

    override fun previous() = if (pos > 0) {
        pos--
        current()
    } else {
        pos = 0
        CharacterIterator.DONE
    }

    override fun setIndex(position: Int): Char {
        if (position !in text.indices) {
            throw IndexOutOfBoundsException(position)
        }

        pos = position
        return text[pos]
    }

    override fun getBeginIndex() = 0

    override fun getEndIndex() = text.length

    override fun getIndex() = pos
}