package io.gitlab.arturbosch.detekt.rules.bugs

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtBreakExpression
import org.jetbrains.kotlin.psi.KtContinueExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtReturnExpression
import org.jetbrains.kotlin.psi.KtThrowExpression

class UnreachableCode(config: Config = Config.empty) : Rule(config) {

	override val issue = Issue("UnreachableCode", Severity.Warning,
			"Unreachable code detected. This code should be removed", Debt.TEN_MINS)

	override fun visitReturnExpression(expression: KtReturnExpression) {
		followedByUnreachableCode(expression)
		super.visitReturnExpression(expression)
	}

	override fun visitThrowExpression(expression: KtThrowExpression) {
		followedByUnreachableCode(expression)
		super.visitThrowExpression(expression)
	}

	override fun visitContinueExpression(expression: KtContinueExpression) {
		followedByUnreachableCode(expression)
	}

	override fun visitBreakExpression(expression: KtBreakExpression) {
		followedByUnreachableCode(expression)
	}

	private fun followedByUnreachableCode(expression: KtExpression) {
		val statements = (expression.parent as? KtBlockExpression)?.statements ?: return
		val indexOfStatement = statements.indexOf(expression)
		if (indexOfStatement < statements.size - 1) {
			report(CodeSmell(issue, Entity.from(expression), message = ""))
		}
	}
}
