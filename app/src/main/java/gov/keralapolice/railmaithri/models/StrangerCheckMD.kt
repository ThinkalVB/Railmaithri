package gov.keralapolice.railmaithri.models
data  class StrangerCheckMD(var id: Int,
                            var name: String,
                            var purpose_of_visit: String,
                            var identification_marks_details: String,
                            var age: Int,
                            var email: String,
                            var languages_known: String,
                            var mobile_number: String,
                            var photo: String,
                            val checking_date_time: String,
                            val place_of_check: String,
                            val native_address: String,
                            val native_state_label: String,
                            val native_police_station: String,
                            val remarks: String,
                            val police_station_label: String,
                            val is_foreigner: Boolean,
                            val country_name: String,
                            val land_phone_number: String,
                            val id_card_details: String,
                            var latitude: Float,
                            var longitude: Float)

