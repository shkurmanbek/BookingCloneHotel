package com.example.medinahotel

class ModelHotel {

    var uid:String = ""
    var id:String = ""
    var article:String = ""
    var description:String = ""
    var location:String = ""
    var city:String = ""
    var category:String = ""
    var categoryId:String = ""
    var url:String = ""
    var timestamp:Long = 0
    var price:Int = 0
    var downloadsCount:Long = 0


    constructor()
    constructor(
        uid: String,
        id: String,
        article: String,
        description: String,
        location: String,
        city: String,
        category: String,
        categoryId: String,
        url: String,
        timestamp: Long,
        price: Int,
        downloadsCount: Long
    ) {
        this.uid = uid
        this.id = id
        this.article = article
        this.description = description
        this.location = location
        this.city = city
        this.category = category
        this.categoryId = categoryId
        this.url = url
        this.timestamp = timestamp
        this.price = price
        this.downloadsCount = downloadsCount
    }


}