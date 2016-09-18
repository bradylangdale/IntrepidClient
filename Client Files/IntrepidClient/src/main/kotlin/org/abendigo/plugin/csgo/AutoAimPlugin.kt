package org.abendigo.plugin.csgo

import org.abendigo.DEBUG
import org.abendigo.csgo.*
import org.abendigo.csgo.Client.enemies
import org.abendigo.csgo.Engine.clientState
import org.abendigo.plugin.sleep
import org.abendigo.util.random
import org.abendigo.util.randomFloat
import org.jire.arrowhead.keyPressed
import org.jire.arrowhead.keyReleased
import java.lang.Math.abs

object AutoAimPlugin : InGamePlugin(name = "Auto Aim", duration = 2) {	

	override val author = "Intrepidus"
	override val description = "If Neo Hacked He'd Use This"

	private const val AIM_KEY = 1 /* left click */

	private const val LOCK_FOV = 360
	private const val UNLOCK_FOV = 360

	private const val JUMP_REDUCTION = 0

	private val TARGET_BONES = arrayOf(Bones.HEAD)
	private const val CHANGE_BONE_CHANCE = 0

	private var target: Player? = null
	private var targetBone = newTargetBone()
	private val aim = Vector(0F, 0F, 0F)

	override fun cycle() {
		val forceAim = true
		val angle = clientState(1024).angle()
		
		if (!forceAim && keyReleased(AIM_KEY)) return

		val lockFOV = LOCK_FOV * 100f
		val unlockFOV = UNLOCK_FOV * 100f
		
		try {
			val weapon = (+Me().weapon).type!!
			if (weapon.knife) return
		} catch (t: Throwable) {
			if (DEBUG) t.printStackTrace()
		}

		val position = +Me().position

		if (target == null) if (!findTarget(position, angle, lockFOV)) return

		if (+Me().dead || +target!!.dead || !+target!!.spotted || +target!!.dormant) {
			target = null
			return
		}

		aimAt(position, angle, target!!, unlockFOV)
		
		if (+target!!.address != +Me.targetAddress){target = null}
	}

	private fun newTargetBone() = TARGET_BONES[random(TARGET_BONES.size)]

	private fun findTarget(position: Vector<Float>, angle: Vector<Float>, lockFOV: Float): Boolean {
		var closestDelta = Int.MAX_VALUE
		var closetPlayer: Player? = null
		for ((i, e) in enemies) {
			if (+Me().dead) return false
			if (+e.dead || !+e.spotted || +e.dormant) continue

			val ePos = e.bonePosition(Bones.HEAD.id)
			val distance = distance(position, ePos)

			calculateAngle(Me(), position, ePos, aim.reset())
			normalizeAngle(aim)

			val yawDiff = abs(angle.y - aim.y)
			val delta = abs(Math.sin(Math.toRadians(yawDiff.toDouble())) * distance)

			if (delta <= lockFOV && delta < closestDelta) {
				closestDelta = delta.toInt()
				closetPlayer = e
			}
		}

		if (closetPlayer != null) {
			target = closetPlayer
			return true
		}

		return false
	}

	private fun aimAt(position: Vector<Float>, angle: Vector<Float>, target: Player, unlockFOV: Float) {

		val enemyPosition = target.bonePosition(targetBone.id)

		val smoothing = 100F

		compensateVelocity(Me(), target, enemyPosition, smoothing)

		calculateAngle(Me(), position, enemyPosition, aim.reset())
		normalizeAngle(aim)

		normalizeAngle(angle)

		val distance = distance(position, enemyPosition)
		val yawDelta = abs(angle.y - aim.y)
		val deltaFOV = abs(Math.sin(Math.toRadians(yawDelta.toDouble())) * distance)

		if (deltaFOV >= unlockFOV) AutoAimPlugin.target = null
		else angleSmooth(aim, angle, smoothing)
	}
}