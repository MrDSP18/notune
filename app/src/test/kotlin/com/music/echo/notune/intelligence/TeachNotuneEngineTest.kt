package com.music.echo.notune.intelligence

import com.music.echo.notune.intelligence.personalization.RuleScope
import com.music.echo.notune.intelligence.personalization.RuleType
import com.music.echo.notune.intelligence.personalization.TeachNotuneEngine
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TeachNotuneEngineTest {

    private lateinit var teachEngine: TeachNotuneEngine

    @Before
    fun setUp() {
        teachEngine = TeachNotuneEngine()
    }

    @Test
    fun `test parse exclude sad songs directive`() {
        val directive = "Don't play sad songs when I'm working"
        val rule = teachEngine.parseDirective(directive)

        assertEquals(RuleType.EXCLUDE_GENRE, rule.ruleType)
        assertEquals(RuleScope.TEMPORARY_WORKOUT, rule.scope)
    }

    @Test
    fun `test parse novelty boost directive`() {
        val directive = "I want 30% discovery"
        val rule = teachEngine.parseDirective(directive)

        assertEquals(RuleType.BOOST_NOVELTY, rule.ruleType)
        assertEquals(RuleScope.CURRENT_SESSION, rule.scope)
    }
}
