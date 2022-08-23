package com.bowencraft.blzeemachines

import xyz.xenondevs.nova.material.FoodOptions
import xyz.xenondevs.nova.material.NovaMaterialRegistry

object Items {
    
    // we will register items here later
    val RUBY = NovaMaterialRegistry.registerFood(
        blzeemachines,
        "ruby",
        FoodOptions(
            consumeTime = 100, // 40 ticks = 2 second
            nutrition = 4,
            saturationModifier = 0.3f
        )
    )
    fun init() = Unit
    
}