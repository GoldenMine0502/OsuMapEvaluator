package kr.goldenmine.downloader

import kotlinx.coroutines.delay
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.DesiredCapabilities
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

/**
 * Created by ehe12 on 2018-12-12.
 */
class OsuMapCrawler(private val id: String, private val pw: String, private val saveFolder: File, private val show: Boolean = true) {
    private lateinit var driver: WebDriver

    private val downloadingList = ArrayList<Int>()
    private val cannotDownloadList = ArrayList<Int>()
    private val queue = Executors.newSingleThreadExecutor()

    private val savePath = saveFolder.toPath()

    init {
        val prefs: MutableMap<String, Any> = HashMap()
        prefs["download.default_directory"] = saveFolder.absolutePath
        val caps = DesiredCapabilities.chrome()
        val options = ChromeOptions()
        if (!show) options.addArguments("--headless")
        options.setExperimentalOption("prefs", prefs)
        caps.setCapability(ChromeOptions.CAPABILITY, options)

        driver = ChromeDriver(caps)
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                try {
                    driver.quit()
                } catch (e: Exception) {
                }
            }
        })
        driver.manage().window().size = Dimension(1920, 1000)
        driver.get("https://osu.ppy.sh/home")

        /* login */
        val loginButtonElement = driver.findElement(By.className("js-user-login--menu"))
        Thread.sleep(1000L)
        loginButtonElement.click()
        var form_loginId: WebElement? = null
        var form_loginPw: WebElement? = null
        var form_loginSubmit: WebElement? = null
        for (el in driver.findElements(By.className("login-box__form-input"))) {
            val placeholder = el.getAttribute("placeholder")
            if (placeholder != null) {
                if (placeholder == "이메일 주소") {
                    form_loginId = el
                }
                if (placeholder == "비밀번호") {
                    form_loginPw = el
                }
            }
        }
        for (el in driver.findElements(By.className("btn-osu-big__content"))) {
            val text = el.text
            if (text.contains("로그인")) {
                form_loginSubmit = el
            }
        }
        Thread.sleep(1000L)
        sendKeys(form_loginId, id, 50, 10)
        Thread.sleep(1000L)
        sendKeys(form_loginPw, pw, 50, 10)
        Thread.sleep(1000L)
        form_loginSubmit!!.click()
    }

    fun waitAllFileDownloaded() {
        while(!queue.awaitTermination(1, TimeUnit.SECONDS))
            Thread.sleep(1000L)
        while(saveFolder.listFiles()?.first { it.name.endsWith("crdownload") } != null)
            Thread.sleep(1000L)
    }

    fun addMap(mapNumber: Int) {
        downloadingList.add(mapNumber)
        queue.execute {
            if(checkBlockedDownloadingMap()) {
                println("요청 거부, 2분후 재시도 - 이러한 실패 발생시 재시도해야 모든 맵이 다운로드 됨")
                Thread.sleep(120000L)
            }
            downloadMap(mapNumber)
        }
    }

    fun checkBlockedDownloadingMap(): Boolean = driver.findElements(By.tagName("h1"))?.first { it.text.contains("여기 계시면 안됩니다.")} != null

    fun downloadMap(mapNumber: Int) {
        val link = "https://osu.ppy.sh/beatmapsets/$mapNumber"
        println(mapNumber)
        driver[link]

        var retryCount = 0
        while (retryCount < 5) {
            val downloadElement = driver.findElements(By.className("btn-osu-big__text-top")).first { it.text == "다운로드" }
            if (downloadElement != null) {
                Thread.sleep(3000L)
                downloadElement.click()

                break
            } else {
                println("$mapNumber 에러, 2초후 재시도")
                retryCount++
                Thread.sleep(2000L)
            }
        }
        if (retryCount == 5) {
            println("$mapNumber 실패")
            driver.findElements(By.className("beatmapset-header__availability-info"))
                ?.first { it.text.contains("다운로드 할 수 없") }
                ?.also {
                    println("다운로드 불가능: $mapNumber")
                    cannotDownloadList.add(mapNumber)
                }
        }
    }

}

private val r = Random()
fun getRandom(start: Int, finish: Int): Int {
    return r.nextInt(finish - start + 1) + start
}

fun sendKeys(element: WebElement?, str: String, sleep: Int, updown: Int) {
    for (i in str.indices) {
        element!!.sendKeys(str[i].toString())
        Thread.sleep(
            sleep + (if (updown > 0) getRandom(
                -updown,
                updown
            ) else 0).toLong()
        )
    }
}