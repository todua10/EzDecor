package com.example.ezdecor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils


object TextureUtils {
    //    fun loadTexture(context: Context, resourceId: Any): Int {
//        // создание объекта текстуры
//        val textureIds = IntArray(1)
//        GLES20.glGenTextures(1, textureIds, 0)
//        if (textureIds[0] == 0) {
//            return 0
//        }
//        // получение Bitmap
//        val options = BitmapFactory.Options()
//        options.inScaled = false
//        if (resourceId is Int) {
//            val bitmap = BitmapFactory.decodeResource(
//                context.resources, resourceId, options
//            )
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
//            bitmap.recycle()
//        }
//        else if (resourceId is String){
//            val bitmap = BitmapFactory.decodeFile(resourceId)
//            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
//            bitmap.recycle()
//        }
//        // настройка объекта текстуры
//
//
//        // сброс target
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
//        return textureIds[0]
//    }
    fun loadTexture(context: Context, resourceId: Int): Int {
        // создание объекта текстуры
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return 0
        }
        // получение Bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            context.resources, resourceId, options
        )
        // настройка объекта текстуры
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()


        // сброс target
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
    fun loadTexture(bitmapFile: Bitmap): Int {
        // создание объекта текстуры
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return 0
        }
        // настройка объекта текстуры
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapFile, 0)
        bitmapFile.recycle()


        // сброс target
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
}