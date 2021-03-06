package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.lexer.KtTokens.ABSTRACT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ANNOTATION_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.COMPANION_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.CONST_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.DATA_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.ENUM_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.EXTERNAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.FINAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INFIX_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INLINE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INNER_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.INTERNAL_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.LATEINIT_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OPEN_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OPERATOR_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.OVERRIDE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PRIVATE_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PROTECTED_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.PUBLIC_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.SEALED_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.SUSPEND_KEYWORD
import org.jetbrains.kotlin.lexer.KtTokens.TAILREC_KEYWORD
import org.jetbrains.kotlin.psi.KtModifierList
import org.jetbrains.kotlin.psi.psiUtil.allChildren
import java.util.Arrays

/**
 * Modifier order array taken from ktlint: https://github.com/shyiko/ktlint
 */
class ModifierOrder(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue(javaClass.simpleName,
			Severity.Style,
			debt = Debt(mins = 1))

	// subset of KtTokens.MODIFIER_KEYWORDS_ARRAY
	private val order = arrayOf(
			PUBLIC_KEYWORD, PROTECTED_KEYWORD, PRIVATE_KEYWORD, INTERNAL_KEYWORD,
			FINAL_KEYWORD, OPEN_KEYWORD, ABSTRACT_KEYWORD,
			SUSPEND_KEYWORD, TAILREC_KEYWORD,
			OVERRIDE_KEYWORD,
			CONST_KEYWORD, LATEINIT_KEYWORD,
			INNER_KEYWORD, EXTERNAL_KEYWORD,
			ENUM_KEYWORD, ANNOTATION_KEYWORD, SEALED_KEYWORD, DATA_KEYWORD,
			COMPANION_KEYWORD,
			INLINE_KEYWORD,
			// NOINLINE_KEYWORD, CROSSINLINE_KEYWORD, OUT_KEYWORD, IN_KEYWORD, VARARG_KEYWORD, REIFIED_KEYWORD
			INFIX_KEYWORD,
			OPERATOR_KEYWORD
			// HEADER_KEYWORD, IMPL_KEYWORD
	)

	override fun visitModifierList(list: KtModifierList) {
		super.visitModifierList(list)

		val modifiers = list.allChildren
				.toList()
				.filter { it !is PsiWhiteSpace }
				.toTypedArray()

		val sortedModifiers = modifiers.copyOf()
				.apply { sortWith(compareBy { order.indexOf(it.node.elementType) }) }

		if (!Arrays.equals(modifiers, sortedModifiers)) {
			val modifierString = sortedModifiers.joinToString(" ") { it.text }

			report(CodeSmell(Issue(javaClass.simpleName, Severity.Style,
					"Modifier order should be: $modifierString", Debt(mins = 1)), Entity.from(list), message = ""))
		}
	}
}
