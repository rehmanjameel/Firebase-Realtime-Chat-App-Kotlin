package com.example.firebasechatapp.models

class Message {

    var text: String? = null

    var timeStamp: Long = System.currentTimeMillis()
    constructor()  //empty for firebase

    constructor(messageText: String) {
        text = messageText
    }


}