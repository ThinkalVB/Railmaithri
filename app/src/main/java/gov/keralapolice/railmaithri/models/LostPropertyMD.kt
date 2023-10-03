package gov.keralapolice.railmaithri.models

data class LostPropertyMD(var id: Int,
                          var police_station_label: String,
                          var lost_property_category_label: String,
                          var description: String,
                          var found_in: String,
                          var found_on: String,
                          var police_station_number: String,
                          var return_remarks: String,
                          var photo: String)
