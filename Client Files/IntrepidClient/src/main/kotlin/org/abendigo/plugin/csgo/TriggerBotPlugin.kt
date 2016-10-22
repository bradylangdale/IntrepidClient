package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.offsets.m_dwForceAttack
import org.abendigo.csgo.Client.clientDLL
import org.abendigo.csgo.Client.enemies
import org.abendigo.plugin.sleep

object TriggerBotPlugin : InGamePlugin("Trigger Bot", duration = 1) {

	override fun cycle() {
		
		for ((i, e) in enemies) {
			
			try {
				val weapon = (+Me().weapon).type!!
				if (weapon.knife || weapon.grenade) return
			} catch (t: Throwable) {
				if (DEBUG) t.printStackTrace()
			}
			
			if (e.address == +Me.targetAddress) {
				clientDLL[m_dwForceAttack] = 5.toByte()
				sleep(10)
				clientDLL[m_dwForceAttack] = 4.toByte()
			}
			
		}
	}

}