package data

data class UserData(
    var userId: String?="",
    var name: String?="",
    var number: String?="",
    var imageurl: String?=""
){
    fun toMap() = mapOf(
        "usedId" to userId,
        "name" to name,
        "number" to number,
        "imageurl" to imageurl
    )
}