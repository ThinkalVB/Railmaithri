package gov.keralapolice.railmaithri.models

data class CrimeMemoMD(var id: Int,
                       var crime_memo_category_label: String,
                       var memo_details: String,
                       var police_station_label: String,
                       var photo: String)
