package com.example.ezdecor
import android.content.Context
import android.graphics.Bitmap
import android.opengl.*
import android.opengl.Matrix
import android.os.SystemClock
import java.nio.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.*
class MyRenderer(private val context: Context, private val room3D: Room3D) : GLSurfaceView.Renderer {
    private var newWP: Boolean = false
    private var resourceID: Int = R.drawable.wallpapers
    private lateinit var bitmapFile: Bitmap
    private var lengthA: Float = room3D.getSideA()*0.1f
    private var lengthB: Float = room3D.getSideB()*0.1f
    private var height: Float = room3D.getHeightRoom()*0.1f
    private val positionCount = 3
    private val textureCount = 2
    private val stride = (positionCount + textureCount) * 4
    private var vertexData: FloatBuffer? = null
    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uTextureUnitLocationBottom = 0
    private var uMatrixLocation = 0
    private var programId = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private var texture = 0
    private var texture2 = 0
    private var texture3 = 0
    private var aTextureLocationBottom = 0
    private val time: Long = 10000
    fun getResourceID(): Any {
        return resourceID
    }
    override fun onSurfaceCreated(arg0: GL10, arg1: EGLConfig) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        createAndUseProgram()
        locations()
        prepareData(newWP)
        bindData()
        createViewMatrix()
    }
    override fun onSurfaceChanged(arg0: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
    }
    private fun prepareData(bitmap: Boolean) {
        val vertices: FloatArray
        if (room3D.getTriangleForm()){
            val heightTriangle: Float = room3D.getHeightTriangle()*0.1f
            val rightPartA: Float = room3D.getRightPartA()*2*0.1f
            vertices = floatArrayOf( // Координаты вершин и текстурных координат боковых граней
                -lengthA, height, heightTriangle, 0f, 0f, //передний квадрат стены
                -lengthA, -height, heightTriangle, 0f, 1f,
                lengthA, height, heightTriangle, 1f, 0f,
                lengthA, -height, heightTriangle, 1f, 1f,
                lengthA, height, heightTriangle, 0f, 0f, //правый квадрат стены
                lengthA, -height, heightTriangle, 0f, 1f,
                lengthA-rightPartA, height, -heightTriangle, 1f, 0f,
                lengthA-rightPartA, -height, -heightTriangle, 1f, 1f,
                lengthA-rightPartA, height, -heightTriangle, 0f, 0f, //левый квадрат стены
                lengthA-rightPartA, -height, -heightTriangle, 0f, 1f,
                -lengthA, height, heightTriangle, 1f, 0f,
                -lengthA, -height, heightTriangle, 1f, 1f,
                -lengthA*0.99f, height*0.99f, heightTriangle*0.99f, 0f, 0f, //передний квадрат
                -lengthA*0.99f, -height*0.99f, heightTriangle*0.99f, 0f, 1f,
                lengthA*0.99f, height*0.99f, heightTriangle*0.99f, 1f, 0f,
                lengthA*0.99f, -height*0.99f, heightTriangle*0.99f, 1f, 1f,
                lengthA*0.99f, height*0.99f, heightTriangle*0.99f, 0f, 0f, //правый квадрат
                lengthA*0.99f, -height*0.99f, heightTriangle*0.99f, 0f, 1f,
                (lengthA-rightPartA)*0.99f, height*0.99f, -heightTriangle*0.99f, 1f, 0f,
                (lengthA-rightPartA)*0.99f, -height*0.99f, -heightTriangle*0.99f, 1f, 1f,
                (lengthA-rightPartA)*0.99f, height*0.99f, -heightTriangle*0.99f, 0f, 0f, //левый квадрат
                (lengthA-rightPartA)*0.99f, -height*0.99f, -heightTriangle*0.99f, 0f, 1f,
                -lengthA*0.99f, height*0.99f, heightTriangle*0.99f, 1f, 0f,
                -lengthA*0.99f, -height*0.99f, heightTriangle*0.99f, 1f, 1f,
                // Координаты вершин и текстурных координат нижней грани
                -lengthA, -height, heightTriangle, 0f, 1f,
                lengthA, -height, heightTriangle, 1f, 0f,
                lengthA-rightPartA, -height, -heightTriangle, 0f, 0f)
        }
        else{
            vertices = floatArrayOf( // Координаты вершин и текстурных координат боковых граней
                -lengthA, height, lengthB, 0f, 0f, //передний квадрат стены
                -lengthA, -height, lengthB, 0f, 1f,
                lengthA, height, lengthB, 1f, 0f,
                lengthA, -height, lengthB, 1f, 1f,
                lengthA, height, lengthB, 0f, 0f, //правый квадрат стены
                lengthA, -height, lengthB, 0f, 1f,
                lengthA, height, -lengthB, 1f, 0f,
                lengthA, -height, -lengthB, 1f, 1f,
                -lengthA, height, -lengthB, 0f, 0f, //задний квадрат стены
                -lengthA, -height, -lengthB, 0f, 1f,
                lengthA, height, -lengthB, 1f, 0f,
                lengthA, -height, -lengthB, 1f, 1f,
                -lengthA, height, -lengthB, 0f, 0f, //левый квадрат стены
                -lengthA, -height, -lengthB, 0f, 1f,
                -lengthA, height, lengthB, 1f, 0f,
                -lengthA, -height, lengthB, 1f, 1f,
                -lengthA*0.99f, height*0.99f, lengthB*0.99f, 0f, 0f, //передний квадрат
                -lengthA*0.99f, -height*0.99f, lengthB*0.99f, 0f, 1f,
                lengthA*0.99f, height*0.99f, lengthB*0.99f, 1f, 0f,
                lengthA*0.99f, -height*0.99f, lengthB*0.99f, 1f, 1f,
                lengthA*0.99f, height*0.99f, lengthB*0.99f, 0f, 0f, //правый квадрат
                lengthA*0.99f, -height*0.99f, lengthB*0.99f, 0f, 1f,
                lengthA*0.99f, height*0.99f, -lengthB*0.99f, 1f, 0f,
                lengthA*0.99f, -height*0.99f, -lengthB*0.99f, 1f, 1f,
                -lengthA*0.99f, height*0.99f, -lengthB*0.99f, 0f, 0f, //задний квадрат
                -lengthA*0.99f, -height*0.99f, -lengthB*0.99f, 0f, 1f,
                lengthA*0.99f, height*0.99f, -lengthB*0.99f, 1f, 0f,
                lengthA*0.99f, -height*0.99f, -lengthB*0.99f, 1f, 1f,
                -lengthA*0.99f, height*0.99f, -lengthB*0.99f, 0f, 0f, //левый квадрат
                -lengthA*0.99f, -height*0.99f, -lengthB*0.99f, 0f, 1f,
                -lengthA*0.99f, height*0.99f, lengthB*0.99f, 1f, 0f,
                -lengthA*0.99f, -height*0.99f, lengthB*0.99f, 1f, 1f,
                // Координаты вершин и текстурных координат нижней грани
                -lengthA, -height, lengthB, 0f, 1f,
                lengthA, -height, lengthB, 1f, 1f,
                -lengthA, -height, -lengthB, 0f, 0f,
                lengthA, -height, -lengthB, 1f, 0f)
        }
        vertexData = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData?.put(vertices)
        texture = TextureUtils.loadTexture(context, R.drawable.wall)
        texture2 = if (bitmap){
            TextureUtils.loadTexture(bitmapFile)
        } else{
            TextureUtils.loadTexture(context, resourceID)
        }
        texture3 = TextureUtils.loadTexture(context, R.drawable.floor)
    }
    private fun createAndUseProgram() {
        val vertexShaderId: Int =
            ShaderUtils.createShader(context, GLES20.GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId: Int =
            ShaderUtils.createShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId)
        GLES20.glUseProgram(programId)
    }
    private fun locations() {
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
        aTextureLocation = GLES20.glGetAttribLocation(programId, "a_Texture")
        aTextureLocationBottom = GLES20.glGetAttribLocation(programId, "a_TextureBottom")
        uTextureUnitLocation = GLES20.glGetUniformLocation(programId, "u_TextureUnit")
        uTextureUnitLocationBottom = GLES20.glGetUniformLocation(programId, "u_TextureUnitBottom")
        uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix")
    }
    private fun bindData() {
        vertexData?.position(0)
        GLES20.glVertexAttribPointer(aPositionLocation, positionCount, GLES20.GL_FLOAT, false, stride, vertexData)
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        vertexData?.position(positionCount)
        GLES20.glVertexAttribPointer(aTextureLocation, textureCount, GLES20.GL_FLOAT, false, stride, vertexData)
        GLES20.glEnableVertexAttribArray(aTextureLocation)
    }
    private fun createProjectionMatrix(width: Int, height: Int) {
        val ratio: Float
        var left = -1f
        var right = 1f
        var bottom = -1f
        var top = 1f
        val near = 2f
        val far = 12f
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
    }
    private fun createViewMatrix() {
        val timeF: Float = (SystemClock.uptimeMillis() % time).toFloat() / time
        val angle = timeF * 2 * PI.toFloat()
        // точка положения камеры
        val eyeX = cos(angle) * 1.3f
        val eyeY = 1.2f
        var eyeZ = sin(angle) * 2.5f
        eyeZ *= if (lengthA>height) lengthA else height
        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f
        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ,
            upX, upY, upZ)
    }
    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }
    override fun onDrawFrame(arg0: GL10) {
        createViewMatrix()
        bindMatrix()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        if (room3D.getTriangleForm()){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4)
        }else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 4, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 8, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2)
        if (room3D.getTriangleForm()){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 12, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4)
        }else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 16, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 20, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 24, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 28, 4)
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture3)
        if (room3D.getTriangleForm()){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 24, 3)
        }else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 32, 4)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 36, 4)
        }
    }
    fun updateWallTexture(textureId: Int) {
        resourceID = textureId
        newWP = false
    }
    fun updateWallTextureBitmap(bitmap: Bitmap) {
        bitmapFile = bitmap
        newWP = true
    }
}