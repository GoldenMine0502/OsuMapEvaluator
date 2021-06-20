
fun main() {
    val termPer = 80.0 // 대충한 180bpm 연타?
    val od = 60.0 // OD 10 100점판정

    val count = 5
    println(((termPer * count) + od) / count)

    val count2 = 10
    println(((termPer * count2) + od) / count2)
}