package gov.keralapolice.railmaithri.models

data class RailMaithriMeetingMD(var id: Int,
                                var police_station_label: String,
                                var meeting_type_label: String,
                                var meeting_date: String,
                                var participants: String,
                                var next_meeting_date: String,
                                var gist_of_decisions_taken: String,
                                var photo: String)