package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.Client.enemies
import org.abendigo.csgo.Engine.clientState
import org.abendigo.csgo.Vector
import java.lang.Math.*
import java.util.HashMap
import kotlin.collections.MutableMap
import kotlin.collections.Set
import kotlin.collections.MutableSet
import org.abendigo.plugin.sleep

object AutoAimPlugin : InGamePlugin(name = "Auto Aim", duration = 8) {	

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
		var distanced_e: MutableMap<Double, Int?> = HashMap()
		
		for ((i,e) in enemies)
		{	
			if(!+e.dead)
			{	
				distanced_e.put((Distance(position, e.bonePosition(Bones.UPPER_CHEST.id))), i)
			}
		}
		
		var dist: MutableSet<Double> = (distanced_e.keys).toMutableSet()
		dist = (dist.sorted()).toMutableSet()
		var _e: MutableSet<Int?> = mutableSetOf()
		
		for (d in dist)
		{
			_e.add(distanced_e.get(d))
		}
		
		for (_i in _e)
		{
			var tar = enemies.get(_i)
			
			aimAt(position, angle, tar!!)
				
			if (+tar.spotted)
			{
				aimAt(position, angle, tar)
				sleep(2)
				break
			}
		}
		
		distanced_e.clear()
		dist.clear()
		_e.clear()
	}
	
	private fun Distance(p1: Vector, p2: Vector) : Double
	{
		return Math.sqrt((((p1.x*p1.x)+(p2.x*p2.x))+((p1.y*p1.y)+(p2.y*p2.y))+((p1.z*p1.z)+(p2.z*p2.z))).toDouble())
	}
	
	private fun aimSilent(position: Vector, angle: Vector, target: Player) {

		val enemyPosition = target.bonePosition(Bones.HEAD.id)

		compensateVelocityRage(Me(), target, enemyPosition)

		calculateAngleRage(Me(), position, enemyPosition, aim.reset())
		normalizeAngle(aim)

		normalizeAngle(angle)

		angleInstantSilent(aim, angle)
	}

	private fun aimAt(position: Vector, angle: Vector, target: Player) {

		val enemyPosition = target.bonePosition(Bones.HEAD.id)

		compensateVelocityRage(Me(), target, enemyPosition)

		calculateAngleRage(Me(), position, enemyPosition, aim.reset())
		normalizeAngle(aim)

		normalizeAngle(angle)

		angleInstant(aim, angle)
	}
}