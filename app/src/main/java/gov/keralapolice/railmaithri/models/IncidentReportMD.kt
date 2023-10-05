package gov.keralapolice.railmaithri.models

data class IncidentReportMD(var id: Int,
                            var status_label: String,
                            var incident_type: String,
                            var name: String,
                            var age: Int,
                            var mobile_number: String,
                            var train_name: String,
                            var coach: String,
                            var seat: String,
                            var platform_number: String,
                            val railway_station_label: String,
                            var photo: String,
                            val incident_details: String,
                            val track_location: String,
                            val assignment_remarks: String,
                            var latitude: Float,
                            var longitude: Float)