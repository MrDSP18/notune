package com.music.echo.notune.intelligence.personalization

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * "Teach NØTUNE" Engine
 *
 * Converts natural language directives or direct UI controls into explicit, priority-ranked
 * preference rules (`PreferenceRule`).
 */
@Singleton
class TeachNotuneEngine @Inject constructor() {

    fun parseDirective(rawDirective: String): PreferenceRule {
        val directive = rawDirective.trim()
        val lower = directive.lowercase()

        val ruleType: RuleType
        val scope: RuleScope
        val actionTarget: String

        when {
            lower.contains("don't play") || lower.contains("never play") || lower.contains("no more") -> {
                ruleType = if (lower.contains("sad") || lower.contains("melancholic")) {
                    RuleType.EXCLUDE_GENRE
                } else if (lower.contains("remix")) {
                    RuleType.EXCLUDE_GENRE
                } else {
                    RuleType.EXCLUDE_ARTIST
                }
                scope = if (lower.contains("working") || lower.contains("work") || lower.contains("studying")) {
                    RuleScope.TEMPORARY_WORKOUT
                } else {
                    RuleScope.PERMANENT
                }
                actionTarget = extractTarget(directive)
            }

            lower.contains("more") || lower.contains("give me") || lower.contains("prefer") -> {
                ruleType = when {
                    lower.contains("tamil") || lower.contains("hindi") || lower.contains("english") || lower.contains("korean") -> RuleType.PREFER_LANGUAGE
                    lower.contains("2000s") || lower.contains("90s") || lower.contains("80s") -> RuleType.PREFER_GENRE
                    else -> RuleType.PREFER_GENRE
                }
                scope = RuleScope.PERMANENT
                actionTarget = extractTarget(directive)
            }

            lower.contains("discovery") || lower.contains("new music") || lower.contains("something new") -> {
                ruleType = RuleType.BOOST_NOVELTY
                scope = RuleScope.CURRENT_SESSION
                actionTarget = "novelty_boost_0.8"
            }

            lower.contains("workout music") && lower.contains("don't use") -> {
                ruleType = RuleType.CUSTOM_NATURAL_LANGUAGE
                scope = RuleScope.TEMPORARY_WORKOUT
                actionTarget = "EXCLUDE_SESSION_TASTE"
            }

            else -> {
                ruleType = RuleType.CUSTOM_NATURAL_LANGUAGE
                scope = RuleScope.PERMANENT
                actionTarget = directive
            }
        }

        return PreferenceRule(
            id = UUID.randomUUID().toString(),
            ruleType = ruleType,
            scope = scope,
            condition = directive,
            actionTarget = actionTarget,
            priority = if (scope == RuleScope.CURRENT_SESSION) 9 else 7,
            confidence = 1.0f
        )
    }

    private fun extractTarget(directive: String): String {
        val words = directive.split(" ")
        return if (words.size > 2) words.drop(2).joinToString(" ") else directive
    }
}
