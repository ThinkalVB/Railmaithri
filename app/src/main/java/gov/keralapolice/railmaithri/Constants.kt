package gov.keralapolice.railmaithri

class URL {
    companion object{
        private const val DEVELOPMENT_URL     = "http://103.10.168.42:8000"
        private const val LOCAL_URL           = "http://192.168.4.63:8000"
        private const val DEPLOYMENT_URL      = "https://railmaithri.keralapolice.gov.in:8000"
        private const val BASE_URL            = DEVELOPMENT_URL

        const val RAILWAY_STATIONS_LIST       = "$BASE_URL/railmaithri/dropdown/railway_station_list/"
        const val TRAINS_LIST                 = "$BASE_URL/railmaithri/dropdown/train_list/"
        const val INTELLIGENCE_SEVERITY_TYPES = "$BASE_URL/railmaithri/dropdown/severity_type_list/"
        const val INTELLIGENCE_TYPES          = "$BASE_URL/railmaithri/dropdown/intelligence_type_list/"
        const val COMPARTMENT_TYPES           = "$BASE_URL/railmaithri/dropdown/compartment_type_list/"
        const val DENSITY_TYPES               = "$BASE_URL/railmaithri/dropdown/density_category_list/"
        const val MEETING_TYPES               = "$BASE_URL/railmaithri/dropdown/janamaithri_meeting_type_list/"
        const val POI_TYPES                   = "$BASE_URL/railmaithri/dropdown/poi_category_list/"
        const val POLICE_STATIONS_LIST        = "$BASE_URL/accounts/dropdown/police_station_list/"
        const val DISTRICTS_LIST              = "$BASE_URL/accounts/dropdown/district_list/"
        const val STATES_LIST                 = "$BASE_URL/accounts/dropdown/states/"
        const val ABANDONED_PROPERTY_TYPES    = "$BASE_URL/railmaithri/dropdown/abandoned_property_category_list/"
        const val RAIL_VOLUNTEER_TYPES        = "$BASE_URL/railmaithri/dropdown/rail_volunteer_category_list/"
        const val GENDER_TYPES                = "$BASE_URL/railmaithri/dropdown/gender_type_list/"
        const val CONTACT_TYPES               = "$BASE_URL/railmaithri/dropdown/contacts_category_list/"
        const val WATCH_ZONE_TYPES            = "$BASE_URL/railmaithri/dropdown/watchzone_category_list/"
        const val VENDOR_TYPES                = "$BASE_URL/railmaithri/dropdown/ua_vendor_beggar_list/"
        const val LOST_PROPERTY_TYPES         = "$BASE_URL/railmaithri/dropdown/lost_property_category_list/"
        const val FOUND_IN_TYPES              = "$BASE_URL/railmaithri/dropdown/found_in_type_list/"
        const val SURAKSHA_SAMITHI_LIST       = "$BASE_URL/railmaithri/dropdown/suraksha_samithi_list/"
        const val SHOP_TYPES                  = "$BASE_URL/railmaithri/dropdown/shop_category_list/"
        const val CRIME_MEMO_TYPES            = "$BASE_URL/railmaithri/dropdown/crime_memo_category_list/"
        const val RUN_OVER_TYPES              = "$BASE_URL/railmaithri/dropdown/run_over_category_list/"
        const val RUN_OVER_CAUSE_TYPES        = "$BASE_URL/railmaithri/dropdown/run_over_cause_category_list/"
        const val COUNTRY_LIST                = "$BASE_URL/accounts/dropdown/country_list/"
        const val CONTRACT_STAFF_TYPES        = "$BASE_URL/railmaithri/dropdown/contract_staff_porter_category_list/"
        const val CRIME_MEMO_STATION_TYPES    = "$BASE_URL/railmaithri/dropdown/crime_memo_station_type_list/"

        const val MOBILE_LOGIN                = "$BASE_URL/accounts/mobile_login/"
        const val MOBILE_LOGOUT               = "$BASE_URL/accounts/logout/"
        const val PASSWORD_CHANGE             = "$BASE_URL/accounts/password_change/"
        const val INCIDENT_REPORT             = "$BASE_URL/api/v1/incident_report/"
        const val PASSENGER_STATISTICS        = "$BASE_URL/api/v1/passenger_statistics/"
        const val STRANGER_CHECK              = "$BASE_URL/api/v1/stranger_check/"
        const val BEAT_DIARY                  = "$BASE_URL/api/v1/beat_diary/"
        const val BEAT_ASSIGNMENT_DIARY       = "$BASE_URL/api/v1/beat_assignment_diary/"
        const val POI                         = "$BASE_URL/api/v1/poi/"
        const val EMERGENCY_CONTACTS          = "$BASE_URL/api/v1/contacts/"
        const val LOST_PROPERTY               = "$BASE_URL/api/v1/lost_property/"
        const val ABANDONED_PROPERTY          = "$BASE_URL/api/v1/abandoned_property/"
        const val RELIABLE_PERSON             = "$BASE_URL/api/v1/reliable_person/"
        const val INTELLIGENCE_INFORMATION    = "$BASE_URL/api/v1/intelligence_report/"
        const val SURAKSHA_SAMITHI_MEMBERS    = "$BASE_URL/api/v1/suraksha_samithi_members/"
        const val UNAUTHORIZED_PEOPLE         = "$BASE_URL/api/v1/ua_vendor_beggar_mental_patient/"
        const val CRIME_MEMO                  = "$BASE_URL/api/v1/crime_memo/"
        const val RAIL_VOLUNTEER              = "$BASE_URL/api/v1/rail_volunteer/"
        const val RAILMAITHRI_MEETING         = "$BASE_URL/api/v1/janamaithri_meeting/"
        const val SHOPS                       = "$BASE_URL/api/v1/shop/"
        const val LABOURS                     = "$BASE_URL/api/v1/shop_labour/"
        const val INTRUDER_ALERT              = "$BASE_URL/api/v1/intruder_report/"
        const val LONELY_PASSENGER            = "$BASE_URL/api/v1/lonely_passenger/"
        const val SOS                         = "$BASE_URL/api/v1/sos_message/"
        const val RUN_OVER                    = "$BASE_URL/api/v1/run_over_report/"
        const val CONTRACT_STAFF              = "$BASE_URL/api/v1/contract_staff_porter/"
        const val WEB_LINKS                   = "$BASE_URL/api/v1/weblink/"

        const val OFFICERS_IN_PS              = "$BASE_URL/accounts/dropdown/beat_officers_in_ps/"
        const val COMMUNICATION_RECEIVER      = "$BASE_URL/api/v1/close_communication_receiver/"
        const val CLOSE_COMMUNICATION         = "$BASE_URL/api/v1/close_communication/"
        const val TASK_LIST                   = "$BASE_URL/api/v1/beat_officer_notifications/"
        const val WATCH_ZONE                  = "$BASE_URL/api/v1/watch_zone/?mobile=true"
        const val LOCATION_UPDATE             = "$BASE_URL/api/v1/tracking_history/"
    }
}

class Storage {
    companion object{
        const val PROFILE                     = "PROFILE"
        const val TOKEN                       = "TOKEN"

        const val RAILWAY_STATIONS_LIST       = "RAILWAY_STATIONS_LIST"
        const val TRAINS_LIST                 = "TRAINS_LIST"
        const val INTELLIGENCE_SEVERITY_TYPES = "INTELLIGENCE_SEVERITY_TYPES"
        const val INTELLIGENCE_TYPES          = "INTELLIGENCE_TYPES"
        const val COMPARTMENT_TYPES           = "COMPARTMENT_TYPES"
        const val DENSITY_TYPES               = "DENSITY_TYPES"
        const val MEETING_TYPES               = "MEETING_TYPES"
        const val POI_TYPES                   = "POI_TYPES"
        const val POLICE_STATIONS_LIST        = "POLICE_STATIONS_LIST"
        const val DISTRICTS_LIST              = "DISTRICTS_LIST"
        const val STATES_LIST                 = "STATES_LIST"
        const val ABANDONED_PROPERTY_TYPES    = "ABANDONED_PROPERTY_TYPES"
        const val RAIL_VOLUNTEER_TYPES        = "RAIL_VOLUNTEER_TYPES"
        const val GENDER_TYPES                = "GENDER_TYPES"
        const val CONTACT_TYPES               = "CONTACT_TYPES"
        const val WATCH_ZONE_TYPES            = "WATCH_ZONE_TYPES"
        const val VENDOR_TYPES                = "VENDOR_TYPES"
        const val LOST_PROPERTY_TYPES         = "LOST_PROPERTY_TYPES"
        const val FOUND_IN_TYPES              = "FOUND_IN_TYPES"
        const val SURAKSHA_SAMITHI_LIST       = "SURAKSHA_SAMITHI_LIST"
        const val SHOP_TYPES                  = "SHOP_TYPES"
        const val CRIME_MEMO_TYPES            = "CRIME_MEMO_TYPES"
        const val RUN_OVER_TYPES              = "RUN_OVER_TYPES"
        const val RUN_OVER_CAUSE_TYPES        = "RUN_OVER_CAUSE_TYPES"
        const val INCIDENT_TYPES              = "INCIDENT_TYPES"
        const val BOOLEAN_ANSWERS             = "BOOLEAN_ANSWERS"
        const val STATION_TYPE                = "STATION_TYPE"
        const val COUNTRY_LIST                = "COUNTRY_LIST"
        const val CONTRACT_STAFF_TYPES        = "CONTRACT_STAFF_TYPES"
        const val CRIME_MEMO_STATION_TYPES    ="CRIME_MEMO_STATION_TYPES"

        const val INCIDENT_REPORT             = "INCIDENT_REPORT"
        const val PASSENGER_STATISTICS        = "PASSENGER_STATISTICS"
        const val STRANGER_CHECK              = "STRANGER_CHECK"
        const val BEAT_DIARY                  = "BEAT_DIARY"
        const val POI                         = "POI"
        const val EMERGENCY_CONTACTS          = "EMERGENCY_CONTACTS"
        const val LOST_PROPERTY               = "LOST_PROPERTY"
        const val ABANDONED_PROPERTY          = "ABANDONED_PROPERTY"
        const val RELIABLE_PERSON             = "RELIABLE_PERSON"
        const val SURAKSHA_SAMITHI_MEMBERS    = "SURAKSHA_SAMITHI_MEMBERS"
        const val UNAUTHORIZED_PEOPLE         = "UNAUTHORIZED_PEOPLE"
        const val CRIME_MEMO                  = "CRIME_MEMO"
        const val RAIL_VOLUNTEER              = "RAIL_VOLUNTEER"
        const val RAILMAITHRI_MEETING         = "RAILMAITHRI_MEETING"
        const val INTELLIGENCE_INFORMATION    = "INTELLIGENCE_INFORMATION"
        const val SHOPS                       = "SHOPS"
        const val LABOURS                     = "LABOURS"
        const val WATCH_ZONE                  = "WATCH_ZONE"
        const val RUN_OVER                    = "RUN_OVER"
        const val CONTRACT_STAFF              = "CONTRACT_STAFF"
        const val WEB_LINKS                   = "WEB_LINKS"
    }
}

class Mode {
    companion object{
        const val SEARCH_FORM              = "SEARCH_FORM"
        const val NEW_FORM                 = "NEW_FORM"
        const val UPDATE_FORM              = "UPDATE_FORM"
        const val VIEW_FORM                = "VIEW_FORM"
    }
}

class App {
    companion object{
        const val APP_VERSION               = "3.0.0"
        const val API_VERSION               = "1.0.0"
    }
}

class FormState {
    companion object{
        const val FORM_TO_UPLOAD            = "FORM_TO_UPLOAD"
        const val CACHED_FORM               = "CACHED_FORM"
        const val FORM_TO_UPDATE            = "FORM_TO_UPDATE"
    }
}

class ResponseType {
    companion object{
        const val SUCCESS                   = 0
        const val NETWORK_ERROR             = 1
        const val API_ERROR                 = 2
    }
}