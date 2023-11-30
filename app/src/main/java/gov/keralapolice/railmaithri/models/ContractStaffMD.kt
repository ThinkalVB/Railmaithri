package gov.keralapolice.railmaithri.models

class ContractStaffMD(var id: Int,
                      var photo: String,
                      var staff_porter_category_label: String,
                      var name: String,
                      var age: Int,
                      var gender: String,
                      var aadhar_number: String,
                      var job_details: String,
                      var mobile_number: String,
                      val address: String,
                      val native_police_station: String,
                      val railway_station_label: String,
                      val native_state_label: String,
                      val migrant_or_not: Boolean)
