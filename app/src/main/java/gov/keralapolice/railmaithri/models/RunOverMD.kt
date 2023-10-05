package gov.keralapolice.railmaithri.models

data class RunOverMD(var id: Int,
                     val date_time_of_occurance: String,
                     val place_of_occurance: String,
                     val between_station_1_label: String,
                     val between_station_2_label: String,
                     val source_of_information: String,
                     var category: String,
                     var cause_label: String,
                     var age: Int,
                     var gender: String,
                     var is_identified: Boolean,
                     var name: String,
                     var address: String,
                     var contact_number: String,
                     var identification_details: String,
                     var victim_details: String,
                     var crime_number: String,
                     val case_registered_in: String,
                     val railway_police_station_label: String,
                     val local_police_station: String,
                     val district_label: String,
                     val remarks: String,
                     var photo: String)