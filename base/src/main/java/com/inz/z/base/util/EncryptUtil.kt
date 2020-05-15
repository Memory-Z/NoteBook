package com.inz.z.base.util

import android.util.Base64
import com.alibaba.fastjson.util.IOUtils
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.security.*
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher
import kotlin.collections.HashMap
import kotlin.math.max

/**
 * 加密工具类
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2019/05/23 10:43.
 */
object EncryptUtil {
    private const val ENCRYPT_MD5 = "MD5"
    private const val ENCRYPT_SHA256 = "SHA-256"
    private const val ENCRYPT_RSA = "RSA"

    /**
     * SHA-256 编码
     * @param value 需要加密的值
     */
    public fun encryptSHA256(value: String): String {
        val bts: ByteArray = value.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance(ENCRYPT_SHA256)
        md.update(bts)
        return bytes2Hax(bts)
    }

    /**
     * byte 数组 转 16进制 字符串
     * @param bytes byte数组
     * @return 16进制 字符串
     */
    private fun bytes2Hax(bytes: ByteArray): String {
        val sb = StringBuffer()
        var temp: String
        for (b: Byte in bytes) {
            temp = b.toString(16)
            if (bytes.size == 1) {
                sb.append("0")
            }
            sb.append(temp)
        }
        return sb.toString()
    }

    /**
     * MD5 加密
     * @param value 需要加密的值
     * @return MD5 加密后的字符串
     */
    public fun encryptMD5(value: String): String {
        val bytes: ByteArray = value.toByteArray(Charsets.UTF_8)
        val md: MessageDigest = MessageDigest.getInstance(ENCRYPT_MD5)
        md.update(bytes)
        val bts: ByteArray = md.digest()
        val sb = StringBuffer()
        for (b: Byte in bts) {
            val s = b.toString(16)
            sb.append(s)
        }
        return sb.toString().toLowerCase(Locale.CHINA)
    }

    /**
     * Base64 加密
     * @param value 需要加密的值
     * @return 加密后的字符串
     */
    public fun encryptBase64(value: String): String {
        val bytes = Base64.encode(value.toByteArray(Charsets.UTF_8), Base64.DEFAULT)
        return bytes.toString()
    }

    /**
     * Base64 解密
     * @param value 加密后的字符串
     * @return 解密后的字符串
     */
    public fun decodeBase64(value: String): String {
        val bytes = Base64.decode(value, Base64.DEFAULT)
        return bytes.toString()
    }


    /**
     * 生成 - RSA 密钥对
     */
    fun createRsaKeys(keySize: Int): Map<String, String>? {
        var kpg: KeyPairGenerator? = null
        try {
            kpg = KeyPairGenerator.getInstance(ENCRYPT_RSA)
        } catch (e: Exception) {
            throw IllegalArgumentException("No such algorithm [ $ENCRYPT_RSA ] ")
        }
        kpg?.initialize(keySize)
        // 生成密钥对
        val keyPair = kpg?.genKeyPair()
        // 公钥
        val pubKey = keyPair?.public
        // 私钥
        val priKey = keyPair?.private
        val pubKeyStr = this.encryptBase64(pubKey?.encoded.toString())
        val priKeyStr = this.encryptBase64(priKey?.encoded.toString())
        val pairMap = HashMap<String, String>()
        pairMap.put("publicKey", pubKeyStr)
        pairMap.put("privateKey", priKeyStr)
        return pairMap
    }

    /**
     * RSA - 获取公钥
     * @param pubKey "Base64 加密后公钥
     * @throws Exception
     */
    fun getRsaPublicKey(pubKey: String): RSAPublicKey? {
        val keyFactory = KeyFactory.getInstance(ENCRYPT_RSA)
        val x509EncodedKeySpec = X509EncodedKeySpec(decodeBase64(pubKey).toByteArray())
        return keyFactory.generatePublic(x509EncodedKeySpec) as RSAPublicKey
    }

    /**
     * RSA - 获取私钥
     * @param priKey Base64 加密后私钥
     * @throws Exception
     */
    fun getRsaPrivateKey(priKey: String): RSAPrivateKey? {
        val keyFactory = KeyFactory.getInstance(ENCRYPT_RSA)
        val x509EncodedKeySpec = X509EncodedKeySpec(decodeBase64(priKey).toByteArray())
        return keyFactory.generatePublic(x509EncodedKeySpec) as RSAPrivateKey
    }


    /**
     * RSA - 公钥加密
     */
    fun encryptRsaPublicKey(data: String, publicKey: RSAPublicKey): String? {
        try {
            val cipher = Cipher.getInstance(ENCRYPT_RSA)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            return encryptBase64(
                rsaSplitCodec(
                    cipher,
                    Cipher.ENCRYPT_MODE,
                    data.toByteArray(Charsets.UTF_8),
                    publicKey.modulus.bitLength()
                ).toString()
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Encrypt public key > [ $data ] have error ", e)
        }
    }

    /**
     * RSA - 私钥机密
     */
    fun encryptRsaPrivateKey(data: String, privateKey: RSAPrivateKey): String? {
        try {
            val cipher = Cipher.getInstance(ENCRYPT_RSA)
            cipher.init(Cipher.ENCRYPT_MODE, privateKey)
            return encryptBase64(
                rsaSplitCodec(
                    cipher,
                    Cipher.ENCRYPT_MODE,
                    data.toByteArray(Charsets.UTF_8),
                    privateKey.modulus.bitLength()
                ).toString()
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Encrypt private key > [ $data ] have error ", e)
        }

    }

    /**
     * RSA - 公钥解码
     */
    fun decodeRsaPublicKey(data: String, publicKey: RSAPublicKey): String? {
        try {
            val cipher = Cipher.getInstance(ENCRYPT_RSA)
            cipher.init(Cipher.DECRYPT_MODE, publicKey)
            return String(
                rsaSplitCodec(
                    cipher,
                    Cipher.DECRYPT_MODE,
                    data.toByteArray(Charsets.UTF_8),
                    publicKey.modulus.bitLength()
                ),
                Charsets.UTF_8
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Encrypt public key > [ $data ] have error ", e)
        }
    }

    /**
     * RSA - 私钥解码
     */
    fun decodeRsaPrivateKey(data: String, privateKey: RSAPrivateKey): String? {
        try {
            val cipher = Cipher.getInstance(ENCRYPT_RSA)
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            return String(
                rsaSplitCodec(
                    cipher,
                    Cipher.DECRYPT_MODE,
                    data.toByteArray(Charsets.UTF_8),
                    privateKey.modulus.bitLength()
                ),
                Charsets.UTF_8
            )
        } catch (e: Exception) {
            throw IllegalArgumentException("Encrypt public key > [ $data ] have error ", e)
        }
    }


    /**
     * RSA 解析
     */
    private fun rsaSplitCodec(
        cipher: Cipher,
        opmode: Int,
        datas: ByteArray,
        keySize: Int
    ): ByteArray {
        var maxBlock = 0
        if (opmode == Cipher.ENCRYPT_MODE) {
            maxBlock = keySize / 8
        } else {
            maxBlock = keySize / 8 - 11
        }
        val out = ByteArrayOutputStream()
        var offset = 0
        var buff: ByteArray
        var i = 0
        try {
            while (datas.size > offset) {
                if (datas.size - offset > maxBlock) {
                    buff = cipher.doFinal(datas, offset, maxBlock)
                } else {
                    buff = cipher.doFinal(datas, offset, datas.size - offset)
                }
                out.write(buff, offset, maxBlock)
                i++
                offset = i * maxBlock
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("加密阈值为 [ $maxBlock ] 发生异常. ", e)
        }
        val resultData = out.toByteArray()
        IOUtils.close(out)
        return resultData
    }
}