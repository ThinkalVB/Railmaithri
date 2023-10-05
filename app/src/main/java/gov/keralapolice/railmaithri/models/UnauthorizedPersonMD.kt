package gov.keralapolice.railmaithri.models

data class UnauthorizedPersonMD(var id: Int,
                                var category_label: String,
                                var description: String,
                                var police_station_label: String,
                                var place_of_check: String,
                                var photo: String,
                                var latitude: Float,
                                var longitude: Float)
