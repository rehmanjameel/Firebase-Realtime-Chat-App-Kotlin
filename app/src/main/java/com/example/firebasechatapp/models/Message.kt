package com.example.firebasechatapp.models

class Message {

    var text: String? = null
    var name: String? = null
    var image: String? = null
//    val sender: String? = null
//    val receiver: String? = null

    var timeStamp: Long = System.currentTimeMillis()
    constructor()  //empty for firebase

    constructor(messageText: String, nameText: String, imageUrl: String) {
        text = messageText
        name = nameText
        image = imageUrl
    }
}