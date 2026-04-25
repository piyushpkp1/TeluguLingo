package com.telugulingo.app.data.repository

import com.telugulingo.app.data.local.db.dao.LessonDao
import com.telugulingo.app.data.local.db.entity.LessonEntity
import com.telugulingo.app.data.mapper.toDomain
import com.telugulingo.app.domain.model.Lesson
import com.telugulingo.app.domain.repository.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LessonRepositoryImpl @Inject constructor(
    private val lessonDao: LessonDao
) : LessonRepository {

    override fun getAllLessons(): Flow<List<Lesson>> =
        lessonDao.getAllLessons().map { list -> list.map { it.toDomain() } }

    override suspend fun getLessonById(id: Long): Lesson? =
        lessonDao.getLessonById(id)?.toDomain()

    override suspend fun getLessonByDay(dayNumber: Int): Lesson? =
        lessonDao.getLessonByDay(dayNumber)?.toDomain()

    override suspend fun initializeLessons() {
        // Only insert if the DB is empty — avoids redundant writes on every launch
        val count = lessonDao.getLessonCount()
        if (count > 0) return
        lessonDao.insertLessons(buildLessons())
    }

    private fun buildLessons(): List<LessonEntity> = listOf(
        // Phase 1 – Greetings (9 lessons, no review)
        LessonEntity(1, "Hello & Goodbye", "హలో & వీడ్కోలు", "Hello & Vedukolu", "Learn hello and goodbye", 1, 20, 1),
        LessonEntity(2, "Please & Thank You", "దయచేసి & ధన్యవాదాలు", "Dayachesi & Dhanyavadalalu", "Polite expressions", 2, 20, 1),
        LessonEntity(3, "Yes & No", "అవును & కాదు", "Avunu & Kadu", "Affirmative and negative", 3, 20, 1),
        LessonEntity(4, "Self Introduction", "స్వయం పరిచయం", "Svayam Parichayam", "Introduce yourself", 4, 20, 1),
        LessonEntity(5, "How Are You?", "మీరు ఎలా ఉన్నారు?", "Meeru ela unnaru?", "Casual greetings", 5, 20, 1),
        LessonEntity(6, "Nice to Meet You", "మిమ్మల్ని కలిసినందుకు ఆనందించాను", "Mimmalni kalisinaduku anandinchanu", "Pleasantries", 6, 20, 1),
        LessonEntity(7, "More Greetings", "మరిన్ని గుర్తులు", "Marinni gurtulu", "Extended greetings", 7, 20, 1),
        LessonEntity(8, "Farewells", "వీడ్కోలు", "Vedukolu", "Ways to say goodbye", 8, 20, 1),
        LessonEntity(10, "Phase 1 Assessment", "దశ 1 అంచనా", "Dasha 1 anchnaa", "Test greetings knowledge", 10, 30, 1),
        // Phase 2 – Numbers (9 lessons, no review)
        LessonEntity(11, "Numbers 1-5", "సంఖ్యలు 1-5", "Sankhyalu 1-5", "Count from one to five", 11, 20, 2),
        LessonEntity(12, "Numbers 6-10", "సంఖ్యలు 6-10", "Sankhyalu 6-10", "Count from six to ten", 12, 20, 2),
        LessonEntity(13, "Numbers 11-20", "సంఖ్యలు 11-20", "Sankhyalu 11-20", "Count from eleven to twenty", 13, 20, 2),
        LessonEntity(14, "Numbers 21-50", "సంఖ్యలు 21-50", "Sankhyalu 21-50", "Twenty-one to fifty", 14, 20, 2),
        LessonEntity(15, "Numbers 51-100", "సంఖ్యలు 51-100", "Sankhyalu 51-100", "Fifty-one to one hundred", 15, 20, 2),
        LessonEntity(16, "Asking Age", "వయస్సు అడగడం", "Vayasu adagadam", "Ask and tell age", 16, 20, 2),
        LessonEntity(17, "Counting Practice", "లెక్కణం అభ్యాసం", "Lekkanam abhyasam", "Practice counting", 17, 20, 2),
        LessonEntity(18, "Ordinal Numbers", "క్రమాంకాలు", "Kramaamkalu", "First, second, third...", 18, 20, 2),
        LessonEntity(20, "Phase 2 Assessment", "దశ 2 అంచనా", "Dasha 2 anchnaa", "Numbers knowledge test", 20, 30, 2),
        // Phase 3 – Family & Pronouns (9 lessons, no review)
        LessonEntity(21, "Family Members", "కుటుంబ సభ్యులు", "Kutumba sabhyulu", "Basic family words", 21, 20, 3),
        LessonEntity(22, "Parents & Siblings", "తల్లి & తండ్రి", "Thalli & Thandri", "Mother, father, brothers, sisters", 22, 20, 3),
        LessonEntity(23, "Extended Family", "విస్తరిత కుటుంబం", "Vistarita kutumbam", "Uncles, aunts, grandparents", 23, 20, 3),
        LessonEntity(24, "Pronouns", "సర్వనామాలు", "Sarvanamaalu", "I, you, he, she, we, they", 24, 20, 3),
        LessonEntity(25, "Possessive Pronouns", "స్వంత సర్వనామాలు", "Svanta sarvanamaalu", "My, your, his, her, our", 25, 20, 3),
        LessonEntity(26, "This / That / These", "ఇది / అది / ఇవి", "Idhi / Adhi / Ivi", "Demonstratives", 26, 20, 3),
        LessonEntity(27, "Family Practice", "కుటుంబ అభ్యాసం", "Kutumba abhyasam", "Practice family vocabulary", 27, 20, 3),
        LessonEntity(28, "Introduction Dialogue", "పరిచయ సంభాషణ", "Parichayam sambhaashanam", "Self introduction conversation", 28, 20, 3),
        LessonEntity(30, "Phase 3 Assessment", "దశ 3 అంచనా", "Dasha 3 anchnaa", "Family & pronouns test", 30, 30, 3),
        // Phase 4 – Verbs (9 lessons, no review)
        LessonEntity(31, "Basic Verbs", "ప్రాథమిక క్రియలు", "Praathmika kriyaalu", "Come, go, eat, drink", 31, 20, 4),
        LessonEntity(32, "More Verbs", "ఇంకన్ని క్రియలు", "Inkani kriyaalu", "See, hear, speak, walk", 32, 20, 4),
        LessonEntity(33, "Want & Need", "కావాలి & అవసరం", "Kaavali & Avasaram", "Want, need, like, dislike", 33, 20, 4),
        LessonEntity(34, "Can & Should", "చేయగలను & చేయాలి", "Cheyagalanu & cheyyaali", "Ability and obligation", 34, 20, 4),
        LessonEntity(35, "Present Tense", "వర్తమాన కాలం", "Vartamaan kaalam", "I go, you eat, he sees", 35, 20, 4),
        LessonEntity(36, "Everyday Actions", "రోజువారీ చర్యలు", "Rojuvaari charyalu", "Daily routine verbs", 36, 20, 4),
        LessonEntity(37, "Verbs Practice", "క్రియల అభ్యాసం", "Kriyaala abhyasam", "Practice verb conjugation", 37, 20, 4),
        LessonEntity(38, "At the Market", "మార్కెట్లో", "Marketloo", "Shopping and bargaining verbs", 38, 20, 4),
        LessonEntity(40, "Phase 4 Assessment", "దశ 4 అంచనా", "Dasha 4 anchnaa", "Verbs and actions test", 40, 30, 4),
        // Phase 5 – Food (9 lessons, no review)
        LessonEntity(41, "Food Vocabulary", "ఆహార పదజ్ఞానం", "Ahaara padagnaanam", "Rice, bread, vegetables", 41, 20, 5),
        LessonEntity(42, "Fruits & Drinks", "పండ్లు & పానీయాలు", "Pandlu & Paaneeyalu", "Fruits and beverages", 42, 20, 5),
        LessonEntity(43, "Spices & Flavors", "మసాలాలు & రుచులు", "Masaalalu & Ruchulu", "Spicy, salty, sweet, sour", 43, 20, 5),
        LessonEntity(44, "At the Restaurant", "హోటల్లో", "Hotalloo", "Ordering food", 44, 20, 5),
        LessonEntity(45, "Cooking Verbs", "వంట క్రియలు", "Vanta kriyaalu", "Cook, fry, boil, roast", 45, 20, 5),
        LessonEntity(46, "Hungry & Thirsty", "ఆకలి & దప్పిక", "Aakali & dhappika", "Expressions of hunger and thirst", 46, 20, 5),
        LessonEntity(47, "Restaurant Dialogue", "హోటల్ సంభాషణ", "Hotel sambhaashanam", "Practice ordering", 47, 20, 5),
        LessonEntity(49, "Taste & Quality", "రుచి & నాణ్యత", "Ruchi & Naanitya", "Good, bad, tasty, fresh", 49, 20, 5),
        LessonEntity(50, "Phase 5 Assessment", "దశ 5 అంచనా", "Dasha 5 anchnaa", "Food and eating test", 50, 30, 5),
        // Phase 6 – Time, Place & Weather (9 lessons, no review)
        LessonEntity(51, "Time of Day", "రోజు సమయాలు", "Roju samayaalu", "Morning, afternoon, evening, night", 51, 20, 6),
        LessonEntity(52, "Days & Months", "రోజులు & నెలలు", "Rojuulu & Nelalu", "Days of week, months of year", 52, 20, 6),
        LessonEntity(53, "Seasons & Weather", "ఋతువులు & వాతావరణం", "Ruthuvulu & Vaathavaaranam", "Rainy, sunny, cold, hot", 53, 20, 6),
        LessonEntity(54, "Directions", "దిశలు", "Dishalu", "North, south, east, west, left, right", 54, 20, 6),
        LessonEntity(55, "Places in Town", "పట్టణంలో ప్రాంతాలు", "Pattanaamlo praantalu", "Bank, hospital, market, school", 55, 20, 6),
        LessonEntity(56, "Asking Directions", "దిశలు అడగడం", "Dishalu adagadam", "Where is...? How do I get to...?", 56, 20, 6),
        LessonEntity(57, "Travel & Transport", "ప్రయాణం & రవాణా", "Pravaahanam & Ravaana", "Bus, train, car, airplane", 57, 20, 6),
        LessonEntity(59, "Weather & Nature", "వాతావరణం & ప్రకృతి", "Vaathavaaranam & prakrithi", "Nature vocabulary", 59, 20, 6),
        LessonEntity(60, "Phase 6 Assessment", "దశ 6 అంచనా", "Dasha 6 anchnaa", "Time, place & weather test", 60, 50, 6),
        // Advanced Phase 1 – Greetings (9 lessons, no review)
        LessonEntity(61, "Hello & Goodbye (Advanced)", "హలో & వీడ్కోలు", "Hello & Vedukolu", "Advanced hello and goodbye conversations", 61, 40, 7),
        LessonEntity(62, "Please & Thank You (Advanced)", "దయచేసి & ధన్యవాదాలు", "Dayachesi & Dhanyavadalalu", "Advanced polite expressions", 62, 40, 7),
        LessonEntity(63, "Yes & No (Advanced)", "అవును & కాదు", "Avunu & Kadu", "Advanced affirmative and negative", 63, 40, 7),
        LessonEntity(64, "Self Introduction (Advanced)", "స్వయం పరిచయం", "Svayam Parichayam", "Advanced self introduction", 64, 40, 7),
        LessonEntity(65, "How Are You? (Advanced)", "మీరు ఎలా ఉన్నారు?", "Meeru ela unnaru?", "Advanced casual greetings", 65, 40, 7),
        LessonEntity(66, "Nice to Meet You (Advanced)", "మిమ్మల్ని కలిసినందుకు ఆనందించాను", "Mimmalni kalisinaduku anandinchanu", "Advanced pleasantries", 66, 40, 7),
        LessonEntity(67, "More Greetings (Advanced)", "మరిన్ని గుర్తులు", "Marinni gurtulu", "Advanced extended greetings", 67, 40, 7),
        LessonEntity(68, "Farewells (Advanced)", "వీడ్కోలు", "Vedukolu", "Advanced ways to say goodbye", 68, 40, 7),
        LessonEntity(70, "Phase 1 Assessment (Advanced)", "దశ 1 అంచనా", "Dasha 1 anchnaa", "Advanced greetings test", 70, 60, 7),
        // Advanced Phase 2 – Numbers (9 lessons, no review)
        LessonEntity(71, "Numbers 1-5 (Advanced)", "సంఖ్యలు 1-5", "Sankhyalu 1-5", "Advanced counting one to five", 71, 40, 8),
        LessonEntity(72, "Numbers 6-10 (Advanced)", "సంఖ్యలు 6-10", "Sankhyalu 6-10", "Advanced counting six to ten", 72, 40, 8),
        LessonEntity(73, "Numbers 11-20 (Advanced)", "సంఖ్యలు 11-20", "Sankhyalu 11-20", "Advanced eleven to twenty", 73, 40, 8),
        LessonEntity(74, "Numbers 21-50 (Advanced)", "సంఖ్యలు 21-50", "Sankhyalu 21-50", "Advanced twenty-one to fifty", 74, 40, 8),
        LessonEntity(75, "Numbers 51-100 (Advanced)", "సంఖ్యలు 51-100", "Sankhyalu 51-100", "Advanced fifty-one to one hundred", 75, 40, 8),
        LessonEntity(76, "Asking Age (Advanced)", "వయస్సు అడగడం", "Vayasu adagadam", "Advanced age conversations", 76, 40, 8),
        LessonEntity(77, "Counting Practice (Advanced)", "లెక్కణం అభ్యాసం", "Lekkanam abhyasam", "Advanced counting practice", 77, 40, 8),
        LessonEntity(78, "Ordinal Numbers (Advanced)", "క్రమాంకాలు", "Kramaamkalu", "Advanced ordinal numbers", 78, 40, 8),
        LessonEntity(80, "Phase 2 Assessment (Advanced)", "దశ 2 అంచనా", "Dasha 2 anchnaa", "Advanced numbers test", 80, 60, 8),
        // Advanced Phase 3 – Family & Pronouns (9 lessons, no review)
        LessonEntity(81, "Family Members (Advanced)", "కుటుంబ సభ్యులు", "Kutumba sabhyulu", "Advanced family vocabulary", 81, 40, 9),
        LessonEntity(82, "Parents & Siblings (Advanced)", "తల్లి & తండ్రి", "Thalli & Thandri", "Advanced parents and siblings", 82, 40, 9),
        LessonEntity(83, "Extended Family (Advanced)", "విస్తరిత కుటుంబం", "Vistarita kutumbam", "Advanced extended family", 83, 40, 9),
        LessonEntity(84, "Pronouns (Advanced)", "సర్వనామాలు", "Sarvanamaalu", "Advanced pronouns", 84, 40, 9),
        LessonEntity(85, "Possessive Pronouns (Advanced)", "స్వంత సర్వనామాలు", "Svanta sarvanamaalu", "Advanced possessive pronouns", 85, 40, 9),
        LessonEntity(86, "This / That / These (Advanced)", "ఇది / అది / ఇవి", "Idhi / Adhi / Ivi", "Advanced demonstratives", 86, 40, 9),
        LessonEntity(87, "Family Practice (Advanced)", "కుటుంబ అభ్యాసం", "Kutumba abhyasam", "Advanced family conversations", 87, 40, 9),
        LessonEntity(88, "Introduction Dialogue (Advanced)", "పరిచయ సంభాషణ", "Parichayam sambhaashanam", "Advanced introduction dialogue", 88, 40, 9),
        LessonEntity(90, "Phase 3 Assessment (Advanced)", "దశ 3 అంచనా", "Dasha 3 anchnaa", "Advanced family & pronouns test", 90, 60, 9),
        // Advanced Phase 4 – Verbs (9 lessons, no review)
        LessonEntity(91, "Basic Verbs (Advanced)", "ప్రాథమిక క్రియలు", "Praathmika kriyaalu", "Advanced basic verbs", 91, 40, 10),
        LessonEntity(92, "More Verbs (Advanced)", "ఇంకన్ని క్రియలు", "Inkani kriyaalu", "Advanced extended verbs", 92, 40, 10),
        LessonEntity(93, "Want & Need (Advanced)", "కావాలి & అవసరం", "Kaavali & Avasaram", "Advanced want and need", 93, 40, 10),
        LessonEntity(94, "Can & Should (Advanced)", "చేయగలను & చేయాలి", "Cheyagalanu & cheyyaali", "Advanced ability and obligation", 94, 40, 10),
        LessonEntity(95, "Present Tense (Advanced)", "వర్తమాన కాలం", "Vartamaan kaalam", "Advanced present tense", 95, 40, 10),
        LessonEntity(96, "Everyday Actions (Advanced)", "రోజువారీ చర్యలు", "Rojuvaari charyalu", "Advanced daily routine verbs", 96, 40, 10),
        LessonEntity(97, "Verbs Practice (Advanced)", "క్రియల అభ్యాసం", "Kriyaala abhyasam", "Advanced verb conjugation", 97, 40, 10),
        LessonEntity(98, "At the Market (Advanced)", "మార్కెట్లో", "Marketloo", "Advanced shopping verbs", 98, 40, 10),
        LessonEntity(100, "Phase 4 Assessment (Advanced)", "దశ 4 అంచనా", "Dasha 4 anchnaa", "Advanced verbs test", 100, 60, 10),
        // Advanced Phase 5 – Food (9 lessons, no review)
        LessonEntity(101, "Food Vocabulary (Advanced)", "ఆహార పదజ్ఞానం", "Ahaara padagnaanam", "Advanced food vocabulary", 101, 40, 11),
        LessonEntity(102, "Fruits & Drinks (Advanced)", "పండ్లు & పానీయాలు", "Pandlu & Paaneeyalu", "Advanced fruits and beverages", 102, 40, 11),
        LessonEntity(103, "Spices & Flavors (Advanced)", "మసాలాలు & రుచులు", "Masaalalu & Ruchulu", "Advanced spices and flavors", 103, 40, 11),
        LessonEntity(104, "At the Restaurant (Advanced)", "హోటల్లో", "Hotalloo", "Advanced food ordering", 104, 40, 11),
        LessonEntity(105, "Cooking Verbs (Advanced)", "వంట క్రియలు", "Vanta kriyaalu", "Advanced cooking verbs", 105, 40, 11),
        LessonEntity(106, "Hungry & Thirsty (Advanced)", "ఆకలి & దప్పిక", "Aakali & dhappika", "Advanced hunger expressions", 106, 40, 11),
        LessonEntity(107, "Restaurant Dialogue (Advanced)", "హోటల్ సంభాషణ", "Hotel sambhaashanam", "Advanced ordering practice", 107, 40, 11),
        LessonEntity(109, "Taste & Quality (Advanced)", "రుచి & నాణ్యత", "Ruchi & Naanitya", "Advanced taste vocabulary", 109, 40, 11),
        LessonEntity(110, "Phase 5 Assessment (Advanced)", "దశ 5 అంచనా", "Dasha 5 anchnaa", "Advanced food test", 110, 60, 11),
        // Advanced Phase 6 – Time, Place & Weather (9 lessons, no review)
        LessonEntity(111, "Time of Day (Advanced)", "రోజు సమయాలు", "Roju samayaalu", "Advanced time expressions", 111, 40, 12),
        LessonEntity(112, "Days & Months (Advanced)", "రోజులు & నెలలు", "Rojuulu & Nelalu", "Advanced days and months", 112, 40, 12),
        LessonEntity(113, "Seasons & Weather (Advanced)", "ఋతువులు & వాతావరణం", "Ruthuvulu & Vaathavaaranam", "Advanced weather vocabulary", 113, 40, 12),
        LessonEntity(114, "Directions (Advanced)", "దిశలు", "Dishalu", "Advanced directions", 114, 40, 12),
        LessonEntity(115, "Places in Town (Advanced)", "పట్టణంలో ప్రాంతాలు", "Pattanaamlo praantalu", "Advanced places vocabulary", 115, 40, 12),
        LessonEntity(116, "Asking Directions (Advanced)", "దిశలు అడగడం", "Dishalu adagadam", "Advanced direction-asking", 116, 40, 12),
        LessonEntity(117, "Travel & Transport (Advanced)", "ప్రయాణం & రవాణా", "Pravaahanam & Ravaana", "Advanced travel vocabulary", 117, 40, 12),
        LessonEntity(119, "Weather & Nature (Advanced)", "వాతావరణం & ప్రకృతి", "Vaathavaaranam & prakrithi", "Advanced nature vocabulary", 119, 40, 12),
        LessonEntity(120, "Phase 6 Assessment (Advanced)", "దశ 6 అంచనా", "Dasha 6 anchnaa", "Advanced time, place & weather test", 120, 100, 12)
    )
}
