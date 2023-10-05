package gov.keralapolice.railmaithri.models

data class AbandonedPropertyMD(var id: Int,
                               var abandoned_property_category_label: String,
                               var crime_case_details: String,
                               var seized_or_not: Boolean,
                               var phone_no: String,
                               var remarks: String,
                               var action_remarks: String,
                               var police_station_label: String,
                               var photo: String)
