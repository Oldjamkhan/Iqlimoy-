# 🌿 Iqlimoy — Havo va Ekologiya Monitoringi

**Iqlimoy** — O'zbekiston bo'ylab havoning sifati (AQI), meteostantsiya ko'rsatkichlari, ekologik vaziyat va sun'iy yo'ldosh tahlillarini real vaqt rejimida monitoring qilish va tahlil etish uchun mo'ljallangan, zamonaviy **Jetpack Compose** arxitekturasidagi oqlangan mobil ilova.

Dastur ekologik muammolarga befarq bo'lmagan, sog'lig'ini himoya qilishni istagan va hududdagi havo sifatiga qarab kunlik faoliyatini rejalashtirmoqchi bo'lgan har bir shaxs va mutaxassislar uchun juda qulaydir.

---

## 🎨 Vizual va Dizayn Mukammalligi (Sleek Interface Concept)

Ilova dastlabki to'q fondagi dizayndan butunlay voz kechib, foydalanuvchiga tetiklik va havoning musaffoligini his ettiruvchi zamonaviy **Sleek Interface** yorug'lik mavzusiga (Light Theme) o'tkazildi:
- **Asosiy fonlar (`BgColor`)**: Yumshoq pastel tusdagi ochiq kulrang-ko'k (`#F3F4F9`) yuzalar.
- **Karta va panellar (`CardColor`)**: Oq rangli material 3 uslubidagi baland egilgan qirralarga ega (24dp rounded corners) zamonaviy bloklar.
- **Asosiy urg'u berilgan ranglar (`PrimaryColor` / `IndicatorColor`)**: Ochiq va to'q firuza-ko'k rang juftliklari (`#0061A4` va `#D1E4FF`).
- **Elementlarning bog'liqligi**: Chegara chiziqlari va kontrastlar Material Design 3 andozalariga fully-aligned qilib moslashtirilgan.

---

## 🚀 Ilovaning Asosiy Modullari va Imkoniyatlari

Ilova 5 ta yetakchi tarmoqdan tashkil topgan bo'lib, ular navigatsiya paneli (Bottom Navigation) orqali juda qulay va tezkor tarzda boshqariladi:

### 1. 📊 Monitoring Plitasi (HomeScreen)
- **AQI markaziy datchigi**: Nafas olish uslubidagi pulsasiyalanuvchi (pulsating breathing animation) vizual datchik ko'rsatkichi bilan havoning bugungi holatini namoyish qiladi.
- **Atrof-muhit detali**: O'zbekistonning barcha viloyat va yirik hududlarini tanlash imkonini beruvchi hudud datchigi ("Shahar tanlash" dialogi bilan).
- **Atrof-muhit parametrlari kridi**:
  - Nozik mikroskopik chang zarralari (PM2.5 va PM10)
  - Zaharli va zararli gaz datchiklari (Azot dioksidi NO₂, Is gazi CO, Ozon O₃)
  - Er yuzasining magnit maydoni nurlanishi (Earth Magnetic activity)
  - Kislotali yomg'ir (Acid Rain Risk) va Chang bo'roni xavflari (Dust risk)
- **Salomatlik maslahatnomasi (Advisory Card)**: Havoning sifat darajasiga qarab astma va nafas olish organlari kasalligi borlar uchun maxsus tavsiyalar ("Sog'liqni saqlash bo'yicha tavsiyanoma" - Health Advisory).

### 2. 🗺️ Interaktiv Geoxarita (MapScreen)
- **Leaflet.js asosi**: Veb-aloqa (WebView) orqali yuklanuvchi mustahkam geo-axborot xaritasi.
- **Hududiy AQI nuqtalari**: Har bir viloyat va shaharning geo-koordinatalarida havo sifatining rang kodlari (Yashil, Sariq, Siyoh rang) va harorati markers shaklida joylangan.
- **Sleek Light Tiles**: Xarita asosi `cartocdn.com/light_all` zamonaviy yorug'lik tilslariga o'tkazilib, u ilovaning umumiy dizayn ko'rinishi bilan mukammal integratsiyalashgan.
- **Android-JS ko'prigi**: Xaritadagi ko'rsatgich ochilganda "Batafsil ko'rish" tugmasini bosish orqali Java ko'prigi orqali to'g'ridan-to'g'ri Kotlin ilovasining tegishli viloyat ma'lumotlariga yo'naltiriladi.

### 3. 💬 Iqlimiy AI Assistant (AssistantScreen)
- **Intellektual maslahat**: Havoning ifloslanishi sharoitlaridan qanday saqlanish, changga qarshi profilaktika masalalari kabi savollarga Gemini AI yordamida tezkor javoblar.
- **Oqlangan Chat Dizayni**: Chat xabarlari chiroyli ajratilgan (foydalanuvchi xabarlari `Color(0xFFD1E4FF)` va burchak silliqlashlari o'ngga/chapga maxsus taqsimlangan).
- **Tezkor shablonli savollar**: Masalan, 'astma va allergiya profilaktikasi', 'havoni tozalash', va 'niqob taqish' bo'yicha tezkor savol berish tugmalari.

### 4. ⚠️ Favqulodda Ogohlantirishlar (AlertsScreen)
- **Dinamik ogohlantirishlar**: Atrof-muhitdagi qizil zonalarni tezda filtrlab, "Faol", "Kuzatuv ostida" va "Yangi e'lonlar" kabi bo'limlar bilan saralash.
- **Xavf ko'rsatgichi**: Alerts darajalari `Critical`, `High`, `Medium` kabi maxsus ogohlantirish piktogrammalari bilan belgilangan.

### 5. 🌟 Qo'shimcha Resurslar (ExploreScreen)
- **Kunlik faollik reytingi (Activity Score Card)**: Bugungi kunda havo ochiq havoda sayr qilish yoki sport mashg'ulotlariga qanchalik mos kelishining tahlili.
- **Ekologik bilim tarmog'i**: Viloyatlarda daraxt ekish, ekologik tadbirlar, mas'ul hamkor tashkilotlar va platforma maqsadi to'g'risida static materiallar.

---

## 🛠️ Loyiha Strukturasi va Ishlatilgan Texnologiyalar

- **Jetpack Compose**: Deklarativ UI yaratish vositasi.
- **Jetpack Navigation / Compose Navigation**: Sahifalararo o'tish marshrutlari.
- **Coroutines & Vector Graphics**: Tezkor grafik chizmalar va asinxron jarayonlarni boshqarish.
- **Kotlin Standard Library & M3 components**: Dasturdagi barcha asosiy interaktiv elementlar M3 andozalariga mos.

---

*Iqlimoy — toza havo va yashil kelajak sari muhim qadamdir!* 🌿💚
