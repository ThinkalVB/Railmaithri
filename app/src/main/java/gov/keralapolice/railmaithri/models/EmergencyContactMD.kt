package gov.keralapolice.railmaithri.models

data class EmergencyContactMD(var id: Int,
                              var name: String,
                              var police_station_label: String,
                              var district_label: String,
                              var railway_station_label: String,
                              var contacts_category_label: String,
                              var remarks: String,
                              var contact_number: String,
                              var email: String,
                              var latitude: Float,
                              var longitude: Float,
                              var photo: String)
