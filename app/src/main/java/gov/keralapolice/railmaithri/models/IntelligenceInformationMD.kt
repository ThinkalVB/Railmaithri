package gov.keralapolice.railmaithri.models

data class IntelligenceInformationMD(var id: Int,
                                     var intelligence_type: String,
                                     var severity: String,
                                     var mobile_number: String,
                                     var information: String,
                                     var remarks: String,
                                     var photo: String,
                                     var latitude: Float,
                                     var longitude: Float)
