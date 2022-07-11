package com.example.medinahotel

class ModelReserve {
    var category:String = ""
    var city:String = ""
    var days:Int = 0
    var hotelName:String = ""
    var location:String = ""
    var name:String = ""
    var price:Int = 0
    var uid:String = ""

    constructor()

    constructor(
        category: String,
        city: String,
        days: Int,
        hotelName: String,
        location: String,
        name: String,
        price: Int,
        uid: String
    ) {
        this.category = category
        this.city = city
        this.days = days
        this.hotelName = hotelName
        this.location = location
        this.name = name
        this.price = price
        this.uid = uid
    }
}