package gov.keralapolice.railmaithri.models

data class PoiMD(var id: Int,
                 var added_by_label: String,
                 var poi_category_label: String,
                 var name: String,
                 var police_station_label: String,
                 var district_label: String,
                 var latitude: Float,
                 var longitude: Float,
                 var photo: String)
