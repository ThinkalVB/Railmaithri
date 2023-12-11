package gov.keralapolice.railmaithri.models

import com.google.gson.internal.bind.ArrayTypeAdapter

data class BeatDiaryMD(var id: Int,
                       var utc_timestamp: String,
                       var description: String,
                       var beat_label: String,
                       var beat_officer_label: String,
                       var assigned_on: String,
                       var assignment_note: String,
                       var beat_duty_from: String,
                       var beat_duty_to: String,
                       var photo: String,
                       var beatAssignmentToBeatDiaryPid: List<BeatAssignmentToBeatDiaryPid>)
data class BeatAssignmentToBeatDiaryPid(
    val description: String,
    val utc_timestamp: String)