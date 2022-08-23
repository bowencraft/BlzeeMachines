package com.bowencraft.blzeemachines

import xyz.xenondevs.nova.addon.Addon

object blzeemachines : Addon() {
    
    override fun init() {
        // Called when the addon is initialized.
        // Register NovaMaterials, RecipeTypes, etc. here
    
        Items.init()
        Blocks.init()
        
    }
    
    override fun onEnable() {
        // Called when the addon is enabled.
    }
    
    override fun onDisable() {
        // Called when the addon is disabled.
    }
    
}