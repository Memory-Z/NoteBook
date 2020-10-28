package com.inz.z.note_book.bean.response.base

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * 用户登录信息
 *
 * @author Zhenglj
 * @version 1.0.0
 * Create by inz in 2020/10/28 09:54.
 */
class LoginInfo() : Serializable, Parcelable {

    var userId: String? = null
    var userName: String? = ""
    var userToken: String? = ""
    var userPhotoSrc: String? = ""
    var userPhotoIndex: Int = 0
    var userSex: Int = 0
    var userContact: String = ""
    var userAddress: String = ""
    var userDetail: String = ""

    constructor(parcel: Parcel) : this() {
        userId = parcel.readString()
        userName = parcel.readString()
        userToken = parcel.readString()
        userPhotoSrc = parcel.readString()
        userPhotoIndex = parcel.readInt()
        userSex = parcel.readInt()
        userContact = parcel.readString() ?: ""
        userAddress = parcel.readString() ?: ""
        userDetail = parcel.readString() ?: ""
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(userName)
        parcel.writeString(userToken)
        parcel.writeString(userPhotoSrc)
        parcel.writeInt(userPhotoIndex)
        parcel.writeInt(userSex)
        parcel.writeString(userContact)
        parcel.writeString(userAddress)
        parcel.writeString(userDetail)
    }

    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<LoginInfo> {
        override fun createFromParcel(parcel: Parcel): LoginInfo {
            return LoginInfo(parcel)
        }

        override fun newArray(size: Int): Array<LoginInfo?> {
            return arrayOfNulls(size)
        }
    }


    override fun toString(): String {
        return "LoginInfo(userId=$userId, userName=$userName, userToken=$userToken, userPhotoSrc=$userPhotoSrc, userPhotoIndex=$userPhotoIndex, userSex=$userSex, userContact='$userContact', userAddress='$userAddress', userDetail='$userDetail')"
    }

}