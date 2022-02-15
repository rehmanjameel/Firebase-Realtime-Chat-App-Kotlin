package com.example.firebasechatapp.models

class Message {
    var uid: String? = null
    var text: String? = null
    var name: String? = null
    var image: String? = null
//    val sender: String? = null
//    val receiver: String? = null

    var timeStamp: Long = System.currentTimeMillis()
    constructor()  //empty for firebase

    constructor(uUid: String, messageText: String, nameText: String, imageUrl: String) {
        uid = uUid
        text = messageText
        name = nameText
        image = imageUrl
    }
}