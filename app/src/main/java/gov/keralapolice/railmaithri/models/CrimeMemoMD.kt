package gov.keralapolice.railmaithri.models

data class CrimeMemoMD(var id: Int,
                       var crime_memo_category_label: String,
                       var crime_details: String,
                       var memo_details: String,
                       var police_station_label: String,
                       var case_registered_in: String,
                       var local_police_station: String,
                       var other_police_station: String,
                       var photo: String,
                       var pdf_file: String)
