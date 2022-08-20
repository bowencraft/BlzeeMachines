package com.bowencraft

import xyz.xenondevs.nova.addon.Addon
import xyz.xenondevs.nova.material.FoodOptions
import xyz.xenondevs.nova.material.NovaMaterialRegistry

object blzeemachines : Addon() {
    
    override fun init() {
        // Called when the addon is initialized.
        // Register NovaMaterials, RecipeTypes, etc. here
        
        Items.init()
        
    }
    
    override fun onEnable() {
        // Called when the addon is enabled.
    }
    
    override fun onDisable() {
        // Called when the addon is disabled.
    }
    
}