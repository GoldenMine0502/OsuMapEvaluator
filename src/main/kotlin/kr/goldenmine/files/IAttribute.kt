package kr.goldenmine.files

interface IAttribute {
    fun getAttributes(): HashMap<String, Any>
    fun addAttribute(key: String, value: Any)
    fun getAttribute(key: String): Any?
}