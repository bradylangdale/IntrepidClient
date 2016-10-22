package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.Client.clientDLL
import org.abendigo.csgo.Engine.clientState
import org.abendigo.csgo.offsets.*
import org.abendigo.util.random
import org.jire.arrowhead.get
import org.jire.arrowhead.keyPressed
import java.awt.event.KeyEvent

// Huge credits and thanks to "double v"'s skin changer source!
// Also credits to LegitPlayer1337 for his further support and information!
// Find skin IDs here: http://www.unknowncheats.me/forum/counterstrike-global-offensive/148322-skin-ids.html

object SkinChangerPlugin : InGamePlugin("Skin Changer", duration = 1) {

	private const val APPLY_KEY = KeyEvent.VK_F1

	private const val DEFAULT_SKIN_SEED = 0
	private const val DEFAULT_STATTRAK = 1337 // -1 for no StatTrak, 0+ for StatTrak amount
	private const val DEFAULT_WEAR = 0.0001F // lower = less wear, higher = more wear
	private const val DEFAULT_QUALITY = 0

	// state so that Weapons.invoke can infer
	private var weaponAddress = 0
	private lateinit var weapon: Weapons

	private fun skins() {
		Weapons.AK47(418, 0, 100)
		Weapons.AUG(418)
		Weapons.AWP(418, 0, 268)
		Weapons.CZ75A(418)
		Weapons.DESERT_EAGLE(418)
		Weapons.FAMAS(418)
		Weapons.FIVE_SEVEN(418)
		Weapons.G3SG1(418)
		Weapons.GALIL(418)
		Weapons.GLOCK(418)
		Weapons.M249(418)
		Weapons.M4A1_SILENCER(418, 0, 1057)
		Weapons.M4A4(418, 0, 104)
		Weapons.MAC10(418)
		Weapons.MAG7(418)
		Weapons.MP7(418)
		Weapons.MP9(418)
		Weapons.NEGEV(418)
		Weapons.NOVA(418, 0, 2049)
		Weapons.P2000(418)
		Weapons.P250(418)
		Weapons.P90(418)
		Weapons.PP_BIZON(418)
		Weapons.R8_REVOLVER(418)
		Weapons.SAWED_OFF(418)
		Weapons.SCAR20(418)
		Weapons.SSG08(418)
		Weapons.SG556(418)
		Weapons.TEC9(418)
		Weapons.UMP45(418)
		Weapons.USP_SILENCER(418, 0, 2679)
		Weapons.XM1014(418)
		Weapons.KNIFE_T(418)
		Weapons.KNIFE(418)
	}

	override fun cycle() {
		for (i in 1..3) try {
			var currentWeaponIndex: Int = csgo[Me().address + m_hMyWeapons + ((i - 1) * 0x4)]
			currentWeaponIndex = currentWeaponIndex and 0xFFF
			weaponAddress = clientDLL[m_dwEntityList + (currentWeaponIndex - 1) * 0x10]
			if (weaponAddress <= 0) continue
			val weaponID: Int = csgo[weaponAddress + m_iItemDefinitionIndex]
			val xuid: Int = csgo[weaponAddress + m_OriginalOwnerXuidLow]

			csgo[weaponAddress + m_iItemIDHigh] = 1 // patch to make the skins stay
			csgo[weaponAddress + m_iAccountID] = xuid // patch to make StatTrak stay

			weapon = Weapons.byID(weaponID)!!

			skins()
		} catch (t: Throwable) {
			if (DEBUG) t.printStackTrace()
		}

		if (keyPressed(APPLY_KEY)) engineDLL[clientState(1024).address + m_dwForceFullUpdate] = -1
	}

	private fun skin(skinID: Int, skinSeed: Int, statTrak: Int, wear: Float, quality: Int) {
		csgo[weaponAddress + m_nFallbackPaintKit] = skinID
		csgo[weaponAddress + m_nFallbackSeed] = skinSeed
		csgo[weaponAddress + m_nFallbackStatTrak] = statTrak
		csgo[weaponAddress + m_iEntityQuality] = quality
		csgo[weaponAddress + m_flFallbackWear] = wear
	}

	private operator fun Weapons.invoke(skinID: Int, skinSeed: Int = DEFAULT_SKIN_SEED,
	                                    statTrak: Int = DEFAULT_STATTRAK, wear: Float = DEFAULT_WEAR,
	                                    quality: Int = DEFAULT_QUALITY) {
		if (this == weapon) skin(skinID, skinSeed, statTrak, wear, quality)
	}

}
