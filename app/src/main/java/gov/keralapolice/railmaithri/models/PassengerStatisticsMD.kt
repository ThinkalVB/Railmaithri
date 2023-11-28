package gov.keralapolice.railmaithri.models

data class PassengerStatisticsMD(var id: Int,
                                 var photo: String,
                                 var train_label: String,
                                 var density_label: String,
                                 var compartment_type_label: String,
                                 var coach: String,
                                 var action_status: Boolean,
                                 var action_remarks: String)

