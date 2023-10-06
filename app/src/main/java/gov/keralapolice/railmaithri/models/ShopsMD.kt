package gov.keralapolice.railmaithri.models

data class ShopsMD(var id: Int,
                   val shop_category_label: String,
                   val name: String,
                   val owner_name: String,
                   val aadhar_number: String,
                   val contact_number: String,
                   var licence_number: String,
                   var railway_station_label: String,
                   var platform_number: Int,
                   var photo: String,
                   var latitude: Float,
                   var longitude: Float)
