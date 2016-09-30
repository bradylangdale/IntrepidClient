package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.Client.enemies
import org.abendigo.csgo.Engine.clientState
import java.lang.Math.*

object AutoAimPlugin : InGamePlugin(name = "Auto Aim", duration = 16) {	

	override val author = "Intrepidus"
	override val description = "If Neo Hacked He'd Use This"

	private val aim = Vector(0F, 0F, 0F)

	override fun cycle() {
		val angle = clientState(1024).angle()
		
		try {
			val weapon = (+Me().weapon).type!!
			if (weapon.knife || weapon.grenade) return
		} catch (t: Throwable) {
			if (DEBUG) t.printStackTrace()
		}

		val position = +Me().position

		for ((i,e) in enemies)
		{	
			if(!+e.dead)
			{
				aimAt(position, angle, e)
				
				if (+e.spotted)
				{
					break
				}//else
				//{
					//aimDown(position, angle, position)
				//}
			}
		}
	}
	
	private fun aimDown(position: Vector, angle: Vector, target: Vector) {

		val smoothing = 100F

		calculateAngle(Me(), position, target, aim.reset())
		normalizeAngle(aim)

		normalizeAngle(angle)

		angleSmooth(aim, angle, smoothing)
	}

	private fun aimAt(position: Vector, angle: Vector, target: Player) {

		val enemyPosition = target.bonePosition(Bones.HEAD.id)

		val smoothing = 100F

		compensateVelocity(Me(), target, enemyPosition, smoothing)

		calculateAngle(Me(), position, enemyPosition, aim.reset())
		normalizeAngle(aim)

		normalizeAngle(angle)

		angleSmooth(aim, angle, smoothing)
	}
}