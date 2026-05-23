package com.example.data

object DemoData {

    val CITIES = listOf(
        CityData(
            id = "toshkent",
            name = "Tashkent",
            nameUz = "Toshkent",
            region = "Toshkent viloyati",
            aqi = 87,
            pm25 = 32,
            pm10 = 68,
            uvIndex = 5,
            temperature = 28,
            humidity = 45,
            windSpeed = 12,
            dustRisk = 35,
            solarRadiation = 420,
            magneticField = "disturbed",
            conditions = "Qisman bulutli"
        ),
        CityData(
            id = "samarqand",
            name = "Samarkand",
            nameUz = "Samarqand",
            region = "Samarqand viloyati",
            aqi = 42,
            pm25 = 15,
            pm10 = 28,
            uvIndex = 7,
            temperature = 31,
            humidity = 38,
            windSpeed = 8,
            dustRisk = 15,
            solarRadiation = 510,
            magneticField = "normal",
            conditions = "Toza havo"
        ),
        CityData(
            id = "buxoro",
            name = "Bukhara",
            nameUz = "Buxoro",
            region = "Buxoro viloyati",
            aqi = 156,
            pm25 = 78,
            pm10 = 142,
            uvIndex = 9,
            temperature = 36,
            humidity = 22,
            windSpeed = 18,
            dustRisk = 82,
            solarRadiation = 620,
            magneticField = "normal",
            conditions = "Chang bo'roni"
        ),
        CityData(
            id = "namangan",
            name = "Namangan",
            nameUz = "Namangan",
            region = "Namangan viloyati",
            aqi = 65,
            pm25 = 24,
            pm10 = 45,
            uvIndex = 6,
            temperature = 26,
            humidity = 52,
            windSpeed = 7,
            dustRisk = 20,
            solarRadiation = 380,
            magneticField = "normal",
            conditions = "Asosan toza"
        ),
        CityData(
            id = "andijon",
            name = "Andijan",
            nameUz = "Andijon",
            region = "Andijon viloyati",
            aqi = 73,
            pm25 = 28,
            pm10 = 52,
            uvIndex = 6,
            temperature = 27,
            humidity = 50,
            windSpeed = 9,
            dustRisk = 25,
            solarRadiation = 395,
            magneticField = "normal",
            conditions = "Qisman bulutli"
        ),
        CityData(
            id = "qarshi",
            name = "Karshi",
            nameUz = "Qarshi",
            region = "Qashqadaryo viloyati",
            aqi = 118,
            pm25 = 55,
            pm10 = 98,
            uvIndex = 8,
            temperature = 34,
            humidity = 28,
            windSpeed = 15,
            dustRisk = 68,
            solarRadiation = 580,
            magneticField = "normal",
            conditions = "Tumanli"
        ),
        CityData(
            id = "nukus",
            name = "Nukus",
            nameUz = "Nukus",
            region = "Qoraqalpog'iston",
            aqi = 201,
            pm25 = 95,
            pm10 = 185,
            uvIndex = 10,
            temperature = 38,
            humidity = 15,
            windSpeed = 25,
            dustRisk = 96,
            solarRadiation = 680,
            magneticField = "storm",
            conditions = "Og'ir chang"
        ),
        CityData(
            id = "fargona",
            name = "Fergana",
            nameUz = "Farg'ona",
            region = "Farg'ona viloyati",
            aqi = 58,
            pm25 = 22,
            pm10 = 38,
            uvIndex = 5,
            temperature = 25,
            humidity = 55,
            windSpeed = 6,
            dustRisk = 18,
            solarRadiation = 360,
            magneticField = "normal",
            conditions = "Toza havo"
        )
    )

    val ALERTS = listOf(
        AlertData(
            id = "a1",
            type = "dust",
            title = "Kuchi chang bo'roni",
            titleUz = "Kuchli chang bo'roni",
            description = "Qoraqum cho'lidan kuchli chang bo'roni Buxoro va Qashqadaryo viloyatlarini qamrab olmoqqa. Ko'rinish 500m dan kam. PM10 me'yordan 14 marta yuqori. Barcha tashqi ishlarni to'xtating.",
            regions = listOf("Buxoro", "Qarshi", "Qoraqalpog'iston"),
            severity = "critical",
            status = "active",
            timestamp = "2026-05-21T08:30:00",
            expiresAt = "2026-05-22T18:00:00"
        ),
        AlertData(
            id = "a2",
            type = "magnetic",
            title = "Geomagnetik bo'ron G2",
            titleUz = "Geomagnetik bo'ron G2",
            description = "G2 darajasidagi geomagnetik bo'ron davom etmoqda. GPS ishlashida buzilishlar kuzatilishi mumkin. Radio aloqaga ta'sir ko'rsatadi. Sezgir odamlarda bosh og'rig'i va charchoq kuzatilishi mumkin.",
            regions = listOf("Butun O'rta Osiyo"),
            severity = "medium",
            status = "active",
            timestamp = "2026-05-21T06:00:00",
            expiresAt = "2026-05-22T06:00:00"
        ),
        AlertData(
            id = "a3",
            type = "uv",
            title = "Haddan tashqari UV nurlanish",
            titleUz = "Haddan tashqari UV nurlanish",
            description = "Nukus va Qoraqalpog'istonda UV indeksi 10-11 (Ekstremal) ga yetmoqda. Maksimal himoya talab qilinadi. Soat 10:00-16:00 da tashqarida qolmang.",
            regions = listOf("Qoraqalpog'iston", "Xorazm"),
            severity = "high",
            status = "active",
            timestamp = "2026-05-21T09:00:00",
            expiresAt = "2026-05-21T18:00:00"
        ),
        AlertData(
            id = "a4",
            type = "smog",
            title = "Shahar tutuni ogohlantirishi",
            titleUz = "Shahar tutuni ogohlantirishi",
            description = "Havo aylanishining kamayishi sababli Toshkentda PM2.5 darajasi ko'tarilgan. Sezgir guruhlar tashqi faoliyatni cheklashi kerak. Eng yuqori daraja soat 07:00-10:00 da kutilmoqda.",
            regions = listOf("Toshkent"),
            severity = "low",
            status = "monitoring",
            timestamp = "2026-05-21T07:00:00",
            expiresAt = "2026-05-21T22:00:00"
        ),
        AlertData(
            id = "a5",
            type = "heat",
            title = "Issiqlik to'lqini",
            titleUz = "Issiqlik to'lqini",
            description = "Keyingi 3 kun davomida O'zbekistonning janubida harorat 40°C dan oshishi kutilmoqda. Tashqi ishchilar uchun issiqlik urishi xavfi yuqori. Majburiy suv ichish tanaffuslari.",
            regions = listOf("Surxondaryo", "Qarshi", "Buxoro"),
            severity = "medium",
            status = "monitoring",
            timestamp = "2026-05-21T05:00:00",
            expiresAt = "2026-05-24T20:00:00"
        ),
        AlertData(
            id = "a6",
            type = "chemical",
            title = "Orol dengizi zaharli changi",
            titleUz = "Orol dengizi zaharli changi",
            description = "Qoraqalpog'istonda qurib qolgan Orol dengizi tubidan pestitsid qoldiqlari o'z ichiga olgan zaharli chang aniqlandi. Nafas olish salomatligi uchun juda xavfli. Tashqarida N95 niqob majburiy.",
            regions = listOf("Qoraqalpog'iston"),
            severity = "critical",
            status = "active",
            timestamp = "2026-05-20T22:00:00",
            expiresAt = "2026-05-23T22:00:00"
        )
    )

    val FORECAST_7DAY = listOf(
        ForecastDay("2026-05-21", "Bugun", 18, 30, 87, 5, 10, "Qisman bulutli", 35),
        ForecastDay("2026-05-22", "Psh", 17, 29, 72, 4, 25, "Asosan bulutli", 28),
        ForecastDay("2026-05-23", "Jum", 16, 28, 55, 6, 5, "Toza havo", 20),
        ForecastDay("2026-05-24", "Sha", 19, 32, 95, 7, 0, "Tumanli", 45),
        ForecastDay("2026-05-25", "Yak", 21, 35, 110, 8, 0, "Tumanli", 62),
        ForecastDay("2026-05-26", "Du", 22, 34, 88, 7, 5, "Qisman bulutli", 38),
        ForecastDay("2026-05-27", "Se", 20, 31, 65, 5, 15, "Asosan bulutli", 25)
    )

    val HOURLY_FORECAST = listOf(
        HourlyForecast("07:00", 65, 21, "sun"),
        HourlyForecast("09:00", 78, 24, "cloud"),
        HourlyForecast("11:00", 87, 27, "cloud"),
        HourlyForecast("13:00", 92, 29, "sun"),
        HourlyForecast("15:00", 88, 30, "sun"),
        HourlyForecast("17:00", 76, 28, "cloud"),
        HourlyForecast("19:00", 60, 25, "cloud"),
        HourlyForecast("21:00", 52, 22, "moon")
    )

    val PARTNERS = listOf(
        Partner(
            id = "p1",
            name = "Oq Suv Kafe",
            category = "Kafe va Restoran",
            description = "Toshkent markazida HEPA filtrli toza havoli ichki muhit",
            discount = "Iqlimoy foydalanuvchilari uchun 15% chegirma",
            cleanAirScore = 95
        ),
        Partner(
            id = "p2",
            name = "Ekopark Chimyon",
            category = "Tabiat va Dam olish",
            description = "1600m balandlikdagi tog' kurortida doimo toza havo",
            discount = "Yaxshi havo sifati kunlarida kirish bepul",
            cleanAirScore = 98
        ),
        Partner(
            id = "p3",
            name = "Wellness Hub Uz",
            category = "Sog'liq va Fitness",
            description = "Yunusobodda HEPA filtrlangon havoli premium sport zali",
            discount = "Kunlik abonement: 20,000 vs 35,000 so'm",
            cleanAirScore = 92
        ),
        Partner(
            id = "p4",
            name = "Registon Tours",
            category = "Turizm va Meros",
            description = "Samarqandda havo eng toza paytda ertalabki meros sayohatlari",
            discount = "Ertalabki sessiyalarda 10% chegirma",
            cleanAirScore = 85
        ),
        Partner(
            id = "p5",
            name = "Ipak Yo'li Dorixonasi",
            category = "Sog'liq va Farovonlik",
            description = "Ifloslantiruvchi moddalardan himoya kosmetikasi va nafas olish mahsulotlari",
            discount = "Bu hafta N95 niqobda 25% chegirma",
            cleanAirScore = 90
        )
    )

    val REGIONS_GRID = listOf(
        RegionGridData("karakalpakstan", "Qoraqalpog'iston", 201, 0, 0, 2),
        RegionGridData("xorazm", "Xorazm", 145, 0, 2, 1),
        RegionGridData("buxoro", "Buxoro", 156, 1, 0, 2),
        RegionGridData("navoiy", "Navoiy", 98, 1, 2, 1),
        RegionGridData("samarqand", "Samarqand", 42, 2, 0, 1),
        RegionGridData("jizzax", "Jizzax", 68, 2, 1, 1),
        RegionGridData("toshkent", "Toshkent", 87, 2, 2, 1),
        RegionGridData("qarshi", "Qashqadaryo", 118, 3, 0, 1),
        RegionGridData("surxon", "Surxondaryo", 85, 3, 1, 1),
        RegionGridData("sirdaryo", "Sirdaryo", 72, 3, 2, 1),
        RegionGridData("fargona", "Farg'ona", 58, 4, 0, 1),
        RegionGridData("namangan", "Namangan", 65, 4, 1, 1),
        RegionGridData("andijon", "Andijon", 73, 4, 2, 1)
    )

    val AI_RESPONSES = mapOf(
        "aqi" to "Toshkentning joriy AQI ko'rsatkichi 87 (O'rtacha). PM2.5 32 µg/m³ — JSSning 25 µg/m³ ko'rsatkichidan biroz yuqori. Bugun kuchli jismoniy mashq qilishni cheklashni tavsiya etaman. Hozirgi eng toza shahar — Samarqand, AQI 42 (Yaxshi).",
        "outdoor" to "Toshkentda bugungi eng yaxshi tashqi faoliyat vaqti: 06:00-09:00. Ertalab havo ifloslanishi odatda 15-20% pastroq bo'ladi. UV indeksi o'rtacha (5), SPF 30+ tavsiya etiladi. Buxoro va Nukusdan bugun butunlay saqlaning — faol chang bo'roni va ekstremal UV ogohlantirishlari kuchda.",
        "magnetic" to "G2 darajasidagi geomagnetik bo'ron butun Markaziy Osiyoda faol. K-indeksi 6 ga yetgan. Ta'siri: GPS signali buzilishi, qisqa to'lqinli radio interferensiyasi, kichik elektr tarmog'i tebranishlari. Sezgir odamlarda bosh og'rig'i yoki charchoq bo'lishi mumkin. Tugash vaqti: 18-24 soat ichida.",
        "dust" to "Buxoro chang bo'roni bugun taxminan 03:45 UTC da Qoraqum cho'lidan boshlandi. PM10 142 µg/m³ — me'yordan 14 marta yuqori. Soatiga 18 km tezlik bilan shimoli-sharq tomon harakat qilmoqda. Kechqurun Qashqadaryo viloyatiga yetib borishi kutilmoqda. Ta'sirlangan hududlardagi barcha qurilish maydonlari darhol ishni to'xtatishi kerak.",
        "forecast" to "Toshkent uchun 7 kunlik prognoz: AQI juma kuni Yaxshi darajaga yaxshilanadi, keyin janubdan keluvchi yuqori bosimli tizim ifloslantiruvchi moddalarni ushlab qolishi sababli dam olish kunlarida Zararli darajaga ko'tariladi. Harorat yakshanba kuni 35°C ga yaqinlashadi. Tashqarida bo'lish uchun eng yaxshi kunlar: juma va shanba ertalabi.",
        "b2b" to "Iqlimoy B2B API qurilish kompaniyalari, qishloq xo'jaligi va tadbirlar tashkilotchilariga real vaqt va prognoz ekologik ma'lumotlarini beradi. Pro tarif: kuniga 10,000 API so'rovi, 6 soatlik prognoz. Enterprise: maxsus sun'iy yo'ldosh, shaxsiy menejer, SLA 99.9%. Hozirda beta bosqichida — 12 ta korporativ mijoz.",
        "construction" to "Toshkent uchun bugungi qurilish xavfi: PAST-O'RTA. Shamol: 12 km/soat (80m gacha kranlarga xavfsiz). UV: 5 (standart himoya). Chang xavfi: 35/100 (standart ehtiyot choralari). Harorat: 28°C (har 2 soatda suv ichish tanaffuslari tavsiya etiladi). Ob-havo to'xtashi talab etilmaydi. Shu hafta beton quyish uchun optimal vaqt — juma.",
        "health" to "Joriy ma'lumotlarga asoslangan sog'liq bo'yicha maslahat: Astma/COPD bemorlari bugun Buxoro, Qarshi va Nukusda tashqarida qolmasligi kerak. Sog'lom odamlar Samarqand va Farg'onada tashqarida mashq qila oladi. Chang ta'sirlangan hududlardagi barcha tashqi ishchilar: N95 niqob majburiy. Magnit bo'roni nafas olish simptomlarini kuchaytirishi mumkin."
    )

    fun getAQILabel(aqi: Int): String {
        return when {
            aqi <= 50 -> "Yaxshi"
            aqi <= 100 -> "O'rtacha"
            aqi <= 150 -> "Zararli"
            aqi <= 200 -> "Juda Zararli"
            else -> "Xavfli"
        }
    }
}
