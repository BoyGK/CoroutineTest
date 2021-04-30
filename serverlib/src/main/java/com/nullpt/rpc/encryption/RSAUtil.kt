package com.nullpt.rpc.encryption

import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher

/**
 * test
 */
fun main() {
    val keyPair = RSAUtil.generateKey()
    val secretBody = RSAUtil.encryptByPublicKey("123456".toByteArray(), keyPair.second)
    println(String(secretBody))
    println(String(RSAUtil.decryptByPrivateKey(secretBody, keyPair.first)))
}

/**
 * rsa util
 */
object RSAUtil {

    private const val transformation = "RSA"
    private const val ENCRYPT_MAX_SIZE = 117//加密每次最大加密字节
    private const val DECRYPT_MAX_SIZE = 256//解密每次最大加密字节

    /**
     * create key pair
     */
    fun generateKey(): Pair<PrivateKey, PublicKey> {
        val generator = KeyPairGenerator.getInstance(transformation)
        val keyPair = generator.genKeyPair()
        val publicKey = keyPair.public
        val privateKey = keyPair.private
        return privateKey to publicKey
    }

    /**
     * 公钥分段加密
     * @param byteArray 原文
     * @param publicKey 公钥
     */
    fun encryptByPublicKey(byteArray: ByteArray, publicKey: PublicKey): ByteArray {
        try {
            //创建cipher对象
            val cipher = Cipher.getInstance(transformation)
            //初始化cipher
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            //加密
            var temp: ByteArray
            var offset = 0 //当前偏移的位置
            val outputStream = ByteArrayOutputStream()
            //拆分input
            while (byteArray.size - offset > 0) {
                //每次最大加密117个字节
                if (byteArray.size - offset >= ENCRYPT_MAX_SIZE) {
                    //剩余部分大于117
                    //加密完整117
                    temp = cipher.doFinal(byteArray, offset, ENCRYPT_MAX_SIZE)
                    //重新计算偏移位置
                    offset += ENCRYPT_MAX_SIZE
                } else {
                    //加密最后一块
                    temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                    //重新计算偏移位置
                    offset = byteArray.size
                }
                //存储到临时的缓冲区
                outputStream.write(temp)
            }
            outputStream.close()
            return Base64.getEncoder().encode(outputStream.toByteArray())
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }

    /**
     * 私钥分段解密
     * @param bytes 秘文
     * @param privateKey 私钥
     */
    fun decryptByPrivateKey(bytes: ByteArray, privateKey: PrivateKey): ByteArray {
        try {
            val byteArray = Base64.getDecoder().decode(bytes)
            //创建cipher对象
            val cipher = Cipher.getInstance(transformation)
            //初始化cipher
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            //分段解密
            var temp: ByteArray
            var offset = 0 //当前偏移的位置
            val outputStream = ByteArrayOutputStream()
            //拆分input
            while (byteArray.size - offset > 0) {
                //每次最大解密256个字节
                if (byteArray.size - offset >= DECRYPT_MAX_SIZE) {
                    temp = cipher.doFinal(byteArray, offset, DECRYPT_MAX_SIZE)
                    //重新计算偏移位置
                    offset += DECRYPT_MAX_SIZE
                } else {
                    //加密最后一块
                    temp = cipher.doFinal(byteArray, offset, byteArray.size - offset)
                    //重新计算偏移位置
                    offset = byteArray.size
                }
                //存储到临时的缓冲区
                outputStream.write(temp)
            }
            outputStream.close()
            return outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            return ByteArray(0)
        }
    }

}