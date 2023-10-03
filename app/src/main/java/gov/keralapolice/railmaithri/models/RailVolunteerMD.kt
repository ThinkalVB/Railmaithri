package gov.keralapolice.railmaithri.models

data  class RailVolunteerMD(var id: Int,
                            var name: String,
                            var rail_volunteer_category_label: String,
                            var age: Int,
                            var email: String,
                            var gender: String,
                            var mobile_number: String,
                            var entrain_station_label: String,
                            var detrain_station_label: String,
                            val nearest_railway_station_label: String,
                            var photo: String)