package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
import com.example.BuildConfig

// --- Retrofit Data Classes ---
@JsonClass(generateAdapter = true)
data class GeminiPart(val text: String)

@JsonClass(generateAdapter = true)
data class GeminiContent(val parts: List<GeminiPart>)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

class IqlimoyViewModel : ViewModel() {

    private val _selectedCity = MutableStateFlow(DemoData.CITIES.first())
    val selectedCity: StateFlow<CityData> = _selectedCity.asStateFlow()

    private val _healthProfile = MutableStateFlow(HealthProfile())
    val healthProfile: StateFlow<HealthProfile> = _healthProfile.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Active alert filters
    private val _alertFilter = MutableStateFlow("all") // "all", "active", "monitoring", "resolved"
    val alertFilter: StateFlow<String> = _alertFilter.asStateFlow()

    // Retrofit client lazy initialization
    private val geminiService: GeminiApiService by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        retrofit.create(GeminiApiService::class.java)
    }

    init {
        // Clear chat to seed starting welcome message
        clearChat()
    }

    fun setCityById(id: String) {
        val city = DemoData.CITIES.find { it.id == id }
        if (city != null) {
            _selectedCity.value = city
        }
    }

    fun setAlertFilter(filter: String) {
        _alertFilter.value = filter
    }

    fun updateHealthProfile(profile: HealthProfile) {
        _healthProfile.value = profile
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
        _error.value = null
    }

    fun toggleHealthCondition(conditionId: String) {
        val current = _healthProfile.value
        val list = current.conditions.toMutableList()
        if (list.contains(conditionId)) {
            list.remove(conditionId)
        } else {
            list.add(conditionId)
        }
        _healthProfile.value = current.copy(conditions = list)
    }

    fun setProfileName(name: String) {
        _healthProfile.value = _healthProfile.value.copy(name = name)
    }

    fun setProfileAgeGroup(group: String) {
        _healthProfile.value = _healthProfile.value.copy(ageGroup = group)
    }

    fun sendMessage(text: String) {
        if (text.trim().isEmpty() || _isTyping.value) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text.trim(),
            isUser = true
        )

        val updatedList = _chatMessages.value + userMessage
        _chatMessages.value = updatedList
        _isTyping.value = true
        _error.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Check if message corresponds to keywords for instantaneous grounded answers
                val groundedMatch = findGroundedAnswer(text)
                if (groundedMatch != null) {
                    kotlinx.coroutines.delay(800) // Realistic typing simulation
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        id = (System.currentTimeMillis() + 1).toString(),
                        text = groundedMatch,
                        isUser = false
                    )
                } else {
                    // Call the secure live Gemini API!
                    val responseText = callGeminiLive(updatedList)
                    _chatMessages.value = _chatMessages.value + ChatMessage(
                        id = (System.currentTimeMillis() + 1).toString(),
                        text = responseText,
                        isUser = false
                    )
                }
            } catch (e: Exception) {
                _error.value = "Gemini AI javob bera olmadi. Barchasi joyidami? Iltimos, qayta urinib ko'ring."
                // Fallback to grounded pre-seeded response in case of API failure
                val fallbackAnswer = DemoData.AI_RESPONSES["default"] ?: "Iltimos, so'rovingizga mos ma'lumotlar bilan qayta urinib ko'ring."
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    id = (System.currentTimeMillis() + 1).toString(),
                    text = fallbackAnswer,
                    isUser = false
                )
            } finally {
                _isTyping.value = false
            }
        }
    }

    private fun findGroundedAnswer(query: String): String? {
        val q = query.lowercase().trim()
        
        // Match specific phrases or words
        if (q.contains("jorig'i aqi") || q.contains("joriy aqi") || q.contains("toshkent aqi") || q.contains("havo sifati")) {
            return DemoData.AI_RESPONSES["aqi"]
        }
        if (q.contains("eng yaxshi vaqt") || q.contains("tashqi vaqt") || q.contains("tashqarida bo'lish")) {
            return DemoData.AI_RESPONSES["outdoor"]
        }
        if (q.contains("magnit") || q.contains("geomagnit") || q.contains("bo'ron")) {
            return DemoData.AI_RESPONSES["magnetic"]
        }
        if (q.contains("chang bo'roni") || q.contains("buxoro chang")) {
            return DemoData.AI_RESPONSES["dust"]
        }
        if (q.contains("prognoz") || q.contains("7 kunlik")) {
            return DemoData.AI_RESPONSES["forecast"]
        }
        if (q.contains("b2b") || q.contains("api")) {
            return DemoData.AI_RESPONSES["b2b"]
        }
        if (q.contains("qurilish") || q.contains("xavfi")) {
            return DemoData.AI_RESPONSES["construction"]
        }
        if (q.contains("sog'liq") || q.contains("maslahat") || q.contains("kasal")) {
            return DemoData.AI_RESPONSES["health"]
        }
        if (q.contains("orol") || q.contains("orol dengizi") || q.contains("zaharli")) {
            return DemoData.AI_RESPONSES["aral"]
        }
        return null
    }

    private suspend fun callGeminiLive(history: List<ChatMessage>): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw Exception("Keystore placeholder exists. Falling back to pre-seeded dataset.")
        }

        val city = _selectedCity.value
        val profile = _healthProfile.value

        // Construct System Instructions
        val systemInstructionText = """
            Siz 'Iqlimoy' ekologik monitoring platformasining aqlli AI yordamchisisiz. Siz foydalanuvchilarning havo sifati, UV nuri, chang darajasi, geomagnit bo'ronlar va ekologik sog'liq so'rovlariga o'zbek tilida qisqa, aniq va hayotiy javoblar berasiz.
            Foydalanuvchining sog'liq profili: Ismi: ${profile.name.ifEmpty { "Do'stim" }}, Yosh guruhi: ${profile.ageGroup}, Kasalliklari: ${profile.conditions.joinToString()}.
            Foydalanuvchi hozirda joylashgan shahar: ${city.nameUz} (AQI: ${city.aqi}, PM2.5: ${city.pm25} mcg/m3, PM10: ${city.pm10} mcg/m3, UV Indeksi: ${city.uvIndex}, Harorat: ${city.temperature}°C, Namlik: ${city.humidity}%, Chang xavfi: ${city.dustRisk}/100, Magnit maydoni: ${city.magneticField}).
            Siz doimo do'stona, tushunarli, aniq maslahatlar bering. Agar foydalanuvchi joriy shahar ekologiyasini so'rasa, shu ma'lumotlar bilan javob bering. O'zbek tilida yozing.
        """.trimIndent()

        val contents = history.map { message ->
            GeminiContent(
                parts = listOf(GeminiPart(text = message.text))
            )
        }

        val request = GeminiRequest(
            contents = contents,
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemInstructionText)))
        )

        val response = geminiService.generateContent(apiKey, request)
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
            ?: throw Exception("API responded with an empty body.")
    }
}
