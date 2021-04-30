package com.nullpt.rpc.encryption

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec


/**
 * test
 */
fun main() {
    val key = AESUtil.generateKey()
    println(key.contentToString())
    val keyPair = RSAUtil.generateKey()
    val secretBody = RSAUtil.encryptByPublicKey(key, keyPair.second)
    val secretBody1 = RSAUtil.encryptByPublicKey(key, keyPair.second)

    println(String(secretBody))
    println(String(secretBody1))
}

/**
 * aes util
 */
object AESUtil {

    private const val transformation = "AES"
    private const val strKey = "ABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789"

    fun generateKey(): ByteArray {
        //创建密钥生成器
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(transformation)
        //初始化密钥
        keyGenerator.init(SecureRandom(strKey.toByteArray()))
        //生成密钥
        val getKey: SecretKey = keyGenerator.generateKey()
        return getKey.encoded
    }

    /**
     * encrypt
     */
    @Suppress("GetInstance")
    fun encrypt(byteArray: ByteArray, password: ByteArray): ByteArray {
        //创建cipher对象
        val cipher = Cipher.getInstance(transformation)
        //初始化:加密/解密
        val keySpec = SecretKeySpec(password, transformation)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        //加密
        return cipher.doFinal(byteArray)
    }

    /**
     * decrypt
     */
    @Suppress("GetInstance")
    fun decrypt(byteArray: ByteArray, password: ByteArray): ByteArray {
        //创建cipher对象
        val cipher = Cipher.getInstance(transformation)
        //初始化:加密/解密
        val keySpec = SecretKeySpec(password, transformation)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        return cipher.doFinal(byteArray)
    }

}