package com.vitekkor.compolybot.scenario.command

import com.justai.jaicf.model.scenario.Scenario

abstract class BaseCommand: Scenario {
    abstract val name: String
    abstract val description: String
}
