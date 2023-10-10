package com.example.ezdecor
import kotlin.math.*
class Room3D(private val sideA: Float,
             private val sideB: Float,
             sideC: Float,
             private val heightRoom: Float,
             private val triangleForm: Boolean) {
    private var area = 0f
    private var angleA = 0f
    private var angleB = 0f
    private var angleC = 0f
    private var heightTriangle = 0f
    private var rightPartA = 0f
    private var areaWalls = 0f
    init {
        if(triangleForm){
            val semiperimeter = (sideA + sideB + sideC) / 2
            area = sqrt(semiperimeter.toDouble() * (semiperimeter - sideA)
                    * (semiperimeter - sideB) * (semiperimeter - sideC)).toFloat()
            angleA = (acos((sideA.pow(2)+sideB.pow(2)-sideC.pow(2))/(2*sideA*sideB))*180/ PI).toFloat()
            angleB = (acos((sideA.pow(2)+sideC.pow(2)-sideB.pow(2))/(2*sideA*sideC))*180/ PI).toFloat()
            angleC = (acos((sideB.pow(2)+sideC.pow(2)-sideA.pow(2))/(2*sideB*sideC))*180/ PI).toFloat()
            heightTriangle = (2*area)/sideA
            rightPartA = sqrt((sideB.pow(2)-heightTriangle.pow(2)))
        }
        else{
            area = sideA * sideB
        }
        areaWalls = area * heightRoom
    }
    fun getHeightTriangle(): Float {
        return heightTriangle
    }
    fun getRightPartA(): Float {
        return rightPartA
    }
    fun getSideA(): Float {
        return sideA
    }
    fun getSideB(): Float {
        return sideB
    }
    fun getHeightRoom(): Float {
        return heightRoom
    }
    fun getTriangleForm(): Boolean {
        return triangleForm
    }
    fun getAreaWalls(): Float {
        return areaWalls
    }
    fun setAreaWalls(length: Float, width: Float){
        areaWalls -= (length*width)
    }
}