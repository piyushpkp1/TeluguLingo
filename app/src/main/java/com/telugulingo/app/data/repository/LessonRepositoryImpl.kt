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
        // Build 120 lessons: base 60 + advanced 60
        val baseLessons = buildLessons()
        val allLessons = buildList {
            addAll(baseLessons)
            for (i in 61..120) {
                val baseLesson = baseLessons[(i - 1) % 60]
                add(
                    LessonEntity(
                        id = i.toLong(),
                        title = "${baseLesson.title} (Advanced)",
                        titleTelugu = baseLesson.titleTelugu,
                        titleRomanized = baseLesson.titleRomanized,
                        description = "${baseLesson.description} - Advanced exercises",
                        dayNumber = i,
                        xpReward = baseLesson.xpReward * 2,
                        phase = ((i - 1) / 10) + 1
                    )
                )
            }
        }
        lessonDao.insertLessons(allLessons)
    }

    private fun buildLessons(): List<LessonEntity> = listOf(
        LessonEntity(1, "Hello & Goodbye", "హలో & వీడ్కోలు", "Hello & Vedukolu", "Learn hello and goodbye", 1, 20, 1),
        LessonEntity(2, "Please & Thank You", "దయచేసి & ధన్యవాదాలు", "Dayachesi & Dhanyavadalalu", "Polite expressions", 2, 20, 1),
        LessonEntity(3, "Yes & No", "అవును & కాదు", "Avunu & Kadu", "Affirmative and negative", 3, 20, 1),
        LessonEntity(4, "Self Introduction", "స్వయం పరిచయం", "Svayam Parichayam", "Introduce yourself", 4, 20, 1),
        LessonEntity(5, "How Are You?", "మీరు ఎలా ఉన్నారు?", "Meeru ela unnaru?", "Casual greetings", 5, 20, 1),
        LessonEntity(6, "Nice to Meet You", "మిమ్మల్ని కలిసినందుకు ఆనందించాను", "Mimmalni kalisinaduku anandinchanu", "Pleasantries", 6, 20, 1),
        LessonEntity(7, "More Greetings", "మరిన్ని గుర్తులు", "Marinni gurtulu", "Extended greetings", 7, 20, 1),
        LessonEntity(8, " Farewells", "వీడ్కోలు & నివాసం", "Vedukolu & Nivasam", "Ways to say goodbye", 8, 20, 1),
        LessonEntity(9, "Greetings Review", "గుర్తుల సమీక్ష", "Gurtula samiksha", "Practice all greetings", 9, 30, 1),
        LessonEntity(10, "Phase 1 Assessment", "దశ 1 అంచనా", "Dasha 1 anchnaa", "Test greetings knowledge", 10, 30, 1),
        LessonEntity(11, "Numbers 1-5", "సంఖ్యలు 1-5", "Sankhyalu 1-5", "Count from one to five", 11, 20, 2),
        LessonEntity(12, "Numbers 6-10", "సంఖ్యలు 6-10", "Sankhyalu 6-10", "Count from six to ten", 12, 20, 2),
        LessonEntity(13, "Numbers 11-20", "సంఖ్యలు 11-20", "Sankhyalu 11-20", "Count from eleven to twenty", 13, 20, 2),
        LessonEntity(14, "Numbers 21-50", "సంఖ్యలు 21-50", "Sankhyalu 21-50", "Twenty-one to fifty", 14, 20, 2),
        LessonEntity(15, "Numbers 51-100", "సంఖ్యలు 51-100", "Sankhyalu 51-100", "Fifty-one to one hundred", 15, 20, 2),
        LessonEntity(16, "Asking Age", "వయస్సు అడగడం", "Vayasu adagadam", "Ask and tell age", 16, 20, 2),
        LessonEntity(17, "Counting Practice", "లెక్కణం అభ్యాసం", "Lekkanam abhyasam", "Practice counting", 17, 20, 2),
        LessonEntity(18, "Ordinal Numbers", "క్రమాంకాలు", "Kramaamkalu", "First, second, third...", 18, 20, 2),
        LessonEntity(19, "Numbers Review", "సంఖ్యల సమీక్ష", "Sankhyala samiksha", "Review all numbers", 19, 30, 2),
        LessonEntity(20, "Phase 2 Assessment", "దశ 2 అంచనా", "Dasha 2 anchnaa", "Numbers knowledge test", 20, 30, 2),
        LessonEntity(21, "Family Members", "కుటుంబ సభ్యులు", "Kutumba sabhyulu", "Basic family words", 21, 20, 3),
        LessonEntity(22, "Parents & Siblings", "తల్లి & తండ్రి", "Thalli & Thandri", "Mother, father, brothers, sisters", 22, 20, 3),
        LessonEntity(23, "Extended Family", "విస్తరిత కుటుంబం", "Vistarita kutumbam", "Uncles, aunts, grandparents", 23, 20, 3),
        LessonEntity(24, "Pronouns", "సర్వనామాలు", "Sarvanamaalu", "I, you, he, she, we, they", 24, 20, 3),
        LessonEntity(25, "Possessive Pronouns", "ఆధీనపు", "Aadheena pronom", "My, your, his, her, our", 25, 20, 3),
        LessonEntity(26, "This / That / These", "ఇది / అది / వీను", "Idhi / Adhi / Veenu", "Demonstratives", 26, 20, 3),
        LessonEntity(27, "Family Practice", "కుటుంబ అభ్యాసం", "Kutumba abhyasam", "Practice family vocabulary", 27, 20, 3),
        LessonEntity(28, "Introduction Dialogue", "పరిచయ సంభాషణ", "Parichayam sambhaashanam", "Self introduction conversation", 28, 20, 3),
        LessonEntity(29, "Family Review", "కుటుంబ సమీక్ష", "Kutumba samiksha", "Review family words", 29, 30, 3),
        LessonEntity(30, "Phase 3 Assessment", "దశ 3 అంచనా", "Dasha 3 anchnaa", "Family & pronouns test", 30, 30, 3),
        LessonEntity(31, "Basic Verbs", "ప్రాథమిక క్రియలు", "Praathmika kriyaalu", "Come, go, eat, drink", 31, 20, 4),
        LessonEntity(32, "More Verbs", "ఇంకన్ని క్రియలు", "Inkani kriyaalu", "See, hear, speak, walk", 32, 20, 4),
        LessonEntity(33, "Want & Need", "కావాలి & అవసరం", "Kaavali & Avasaram", "Want, need, like, dislike", 33, 20, 4),
        LessonEntity(34, "Can & Should", "పక్ష & లేక", "Pakshha & leka", "Ability and obligation", 34, 20, 4),
        LessonEntity(35, "Present Tense", "వర్తమాన కాలం", "Vartamaan kaalam", "I go, you eat, he sees", 35, 20, 4),
        LessonEntity(36, "Everyday Actions", "తప్పని కార్యలు", "Thappana kaaryalu", "Daily routine verbs", 36, 20, 4),
        LessonEntity(37, "Verbs Practice", "క్రియల అభ్యాసం", "Kriyaala abhyasam", "Practice verb conjugation", 37, 20, 4),
        LessonEntity(38, "At the Market", "మార్కెట్లో", "Marketloo", "Shopping and bargaining verbs", 38, 20, 4),
        LessonEntity(39, "Verbs Review", "క్రియల సమీక్ష", "Kriyaala samiksha", "Review all verbs", 39, 30, 4),
        LessonEntity(40, "Phase 4 Assessment", "దశ 4 అంచనా", "Dasha 4 anchnaa", "Verbs and actions test", 40, 30, 4),
        LessonEntity(41, "Food Vocabulary", "ఆహార పదజ్ఞానం", "Ahaara padagnaanam", "Rice, bread, vegetables", 41, 20, 5),
        LessonEntity(42, "Fruits & Drinks", "పండ్లు & పానీయాలు", "Pandlu & Paaneeyalu", "Fruits and beverages", 42, 20, 5),
        LessonEntity(43, "Spices & Flavors", "మసాలాలు & రుచులు", "Masaalalu & Ruchulu", "Spicy, salty, sweet, sour", 43, 20, 5),
        LessonEntity(44, "At the Restaurant", "హోటల్లో", "Hotalloo", "Ordering food", 44, 20, 5),
        LessonEntity(45, "Cooking Verbs", "వంట క్రియలు", "Vanta kriyaalu", "Cook, fry, boil, roast", 45, 20, 5),
        LessonEntity(46, "Hungry & Thirsty", "ఆకలి & దప్ప", "Aakali & dhappa", "Expressions of hunger and thirst", 46, 20, 5),
        LessonEntity(47, "Restaurant Dialogue", "హోటల్ సంభాషణ", "Hotel sambhaashanam", "Practice ordering", 47, 20, 5),
        LessonEntity(48, "Food Review", "ఆహార సమీక్ష", "Ahaara samiksha", "Review food vocabulary", 48, 30, 5),
        LessonEntity(49, "Taste & Quality", "రుచి & నాణ్యత", "Ruchi & Naanitya", "Good, bad, tasty, fresh", 49, 20, 5),
        LessonEntity(50, "Phase 5 Assessment", "దశ 5 అంచనా", "Dasha 5 anchnaa", "Food and eating test", 50, 30, 5),
        LessonEntity(51, "Time of Day", "రోజు సమయాలు", "Roju sama yaalu", "Morning, afternoon, evening, night", 51, 20, 6),
        LessonEntity(52, "Days & Months", "రోజులు & నెలలు", "Rojuulu & Nelalu", "Days of week, months of year", 52, 20, 6),
        LessonEntity(53, "Seasons & Weather", "ఋతువులు & వాతావరణం", "Ruthuvulu & Vaathavaaranam", "Rainy, sunny, cold, hot", 53, 20, 6),
        LessonEntity(54, "Directions", "దిశలు", "Dishalu", "North, south, east, west, left, right", 54, 20, 6),
        LessonEntity(55, "Places in Town", "పట్టణంలో ప్రాంతాలు", "Pattanaamlo praantalu", "Bank, hospital, market, school", 55, 20, 6),
        LessonEntity(56, "Asking Directions", "దిశలు అడగడం", "Dishalu adagadam", "Where is...? How do I get to...?", 56, 20, 6),
        LessonEntity(57, "Travel & Transport", "ప్రయాణం & రవాణా", "Pravaahanam & Ravaana", "Bus, train, car, airplane", 57, 20, 6),
        LessonEntity(58, "Time & Place Review", "సమయ & ప్రాంత సమీక్ష", "Samaya & praanta samiksha", "Review time and places", 58, 30, 6),
        LessonEntity(59, "Weather & Nature", "వాతావరణ & ప్రకృతి", "Vaathavaarani & prakrithi", "Nature vocabulary", 59, 20, 6),
        LessonEntity(60, "Phase 6 Assessment", "దశ 6 అంచనా", "Dasha 6 anchnaa", "Time, place & weather test", 60, 50, 6),
        LessonEntity(61, "Hello & Goodbye (Advanced)", "హలో & వీడ్కోలు", "Hello & Vedukolu", "Learn hello and goodbye with more complexity", 61, 40, 7),
        LessonEntity(62, "Please & Thank You (Advanced)", "దయచేసి & ధన్యవాదాలు", "Dayachesi & Dhanyavadalalu", "Polite expressions with more complexity", 62, 40, 7),
        LessonEntity(63, "Yes & No (Advanced)", "అవును & కాదు", "Avunu & Kadu", "Affirmative and negative with more complexity", 63, 40, 7),
        LessonEntity(64, "Self Introduction (Advanced)", "స్వయం పరిచయం", "Svayam Parichayam", "Introduce yourself with more complexity", 64, 40, 7),
        LessonEntity(65, "How Are You? (Advanced)", "మీరు ఎలా ఉన్నారు?", "Meeru ela unnaru?", "Casual greetings with more complexity", 65, 40, 7),
        LessonEntity(66, "Nice to Meet You (Advanced)", "మిమ్మల్ని కలిసినందుకు ఆనందించాను", "Mimmalni kalisinaduku anandinchanu", "Pleasantries with more complexity", 66, 40, 7),
        LessonEntity(67, "More Greetings (Advanced)", "మరిన్ని గుర్తులు", "Marinni gurtulu", "Extended greetings with more complexity", 67, 40, 7),
        LessonEntity(68, " Farewells (Advanced)", "వీడ్కోలు & నివాసం", "Vedukolu & Nivasam", "Ways to say goodbye with more complexity", 68, 40, 7),
        LessonEntity(69, "Greetings Review (Advanced)", "గుర్తుల సమీక్ష", "Gurtula samiksha", "Practice all greetings with more complexity", 69, 60, 7),
        LessonEntity(70, "Phase 1 Assessment (Advanced)", "దశ 1 అంచనా", "Dasha 1 anchnaa", "Test greetings knowledge with more complexity", 70, 60, 7),
        LessonEntity(71, "Numbers 1-5 (Advanced)", "సంఖ్యలు 1-5", "Sankhyalu 1-5", "Count from one to five with more complexity", 71, 40, 8),
        LessonEntity(72, "Numbers 6-10 (Advanced)", "సంఖ్యలు 6-10", "Sankhyalu 6-10", "Count from six to ten with more complexity", 72, 40, 8),
        LessonEntity(73, "Numbers 11-20 (Advanced)", "సంఖ్యలు 11-20", "Sankhyalu 11-20", "Count from eleven to twenty with more complexity", 73, 40, 8),
        LessonEntity(74, "Numbers 21-50 (Advanced)", "సంఖ్యలు 21-50", "Sankhyalu 21-50", "Twenty-one to fifty with more complexity", 74, 40, 8),
        LessonEntity(75, "Numbers 51-100 (Advanced)", "సంఖ్యలు 51-100", "Sankhyalu 51-100", "Fifty-one to one hundred with more complexity", 75, 40, 8),
        LessonEntity(76, "Asking Age (Advanced)", "వయస్సు అడగడం", "Vayasu adagadam", "Ask and tell age with more complexity", 76, 40, 8),
        LessonEntity(77, "Counting Practice (Advanced)", "లెక్కణం అభ్యాసం", "Lekkanam abhyasam", "Practice counting with more complexity", 77, 40, 8),
        LessonEntity(78, "Ordinal Numbers (Advanced)", "క్రమాంకాలు", "Kramaamkalu", "First, second, third... with more complexity", 78, 40, 8),
        LessonEntity(79, "Numbers Review (Advanced)", "సంఖ్యల సమీక్ష", "Sankhyala samiksha", "Review all numbers with more complexity", 79, 60, 8),
        LessonEntity(80, "Phase 2 Assessment (Advanced)", "దశ 2 అంచనా", "Dasha 2 anchnaa", "Numbers knowledge test with more complexity", 80, 60, 8),
        LessonEntity(81, "Family Members (Advanced)", "కుటుంబ సభ్యులు", "Kutumba sabhyulu", "Basic family words with more complexity", 81, 40, 9),
        LessonEntity(82, "Parents & Siblings (Advanced)", "తల్లి & తండ్రి", "Thalli & Thandri", "Mother, father, brothers, sisters with more complexity", 82, 40, 9),
        LessonEntity(83, "Extended Family (Advanced)", "విస్తరిత కుటుంబం", "Vistarita kutumbam", "Uncles, aunts, grandparents with more complexity", 83, 40, 9),
        LessonEntity(84, "Pronouns (Advanced)", "సర్వనామాలు", "Sarvanamaalu", "I, you, he, she, we, they with more complexity", 84, 40, 9),
        LessonEntity(85, "Possessive Pronouns (Advanced)", "ఆధీనపు", "Aadheena pronom", "My, your, his, her, our with more complexity", 85, 40, 9),
        LessonEntity(86, "This / That / These (Advanced)", "ఇది / అది / వీను", "Idhi / Adhi / Veenu", "Demonstratives with more complexity", 86, 40, 9),
        LessonEntity(87, "Family Practice (Advanced)", "కుటుంబ అభ్యాసం", "Kutumba abhyasam", "Practice family vocabulary with more complexity", 87, 40, 9),
        LessonEntity(88, "Introduction Dialogue (Advanced)", "పరిచయ సంభాషణ", "Parichayam sambhaashanam", "Self introduction conversation with more complexity", 88, 40, 9),
        LessonEntity(89, "Family Review (Advanced)", "కుటుంబ సమీక్ష", "Kutumba samiksha", "Review family words with more complexity", 89, 60, 9),
        LessonEntity(90, "Phase 3 Assessment (Advanced)", "దశ 3 అంచనా", "Dasha 3 anchnaa", "Family & pronouns test with more complexity", 90, 60, 9),
        LessonEntity(91, "Basic Verbs (Advanced)", "ప్రాథమిక క్రియలు", "Praathmika kriyaalu", "Come, go, eat, drink with more complexity", 91, 40, 10),
        LessonEntity(92, "More Verbs (Advanced)", "ఇంకన్ని క్రియలు", "Inkani kriyaalu", "See, hear, speak, walk with more complexity", 92, 40, 10),
        LessonEntity(93, "Want & Need (Advanced)", "కావాలి & అవసరం", "Kaavali & Avasaram", "Want, need, like, dislike with more complexity", 93, 40, 10),
        LessonEntity(94, "Can & Should (Advanced)", "పక్ష & లేక", "Pakshha & leka", "Ability and obligation with more complexity", 94, 40, 10),
        LessonEntity(95, "Present Tense (Advanced)", "వర్తమాన కాలం", "Vartamaan kaalam", "I go, you eat, he sees with more complexity", 95, 40, 10),
        LessonEntity(96, "Everyday Actions (Advanced)", "తప్పని కార్యలు", "Thappana kaaryalu", "Daily routine verbs with more complexity", 96, 40, 10),
        LessonEntity(97, "Verbs Practice (Advanced)", "క్రియల అభ్యాసం", "Kriyaala abhyasam", "Practice verb conjugation with more complexity", 97, 40, 10),
        LessonEntity(98, "At the Market (Advanced)", "మార్కెట్లో", "Marketloo", "Shopping and bargaining verbs with more complexity", 98, 40, 10),
        LessonEntity(99, "Verbs Review (Advanced)", "క్రియల సమీక్ష", "Kriyaala samiksha", "Review all verbs with more complexity", 99, 60, 10),
        LessonEntity(100, "Phase 4 Assessment (Advanced)", "దశ 4 అంచనా", "Dasha 4 anchnaa", "Verbs and actions test with more complexity", 100, 60, 10),
        LessonEntity(101, "Food Vocabulary (Advanced)", "ఆహార పదజ్ఞానం", "Ahaara padagnaanam", "Rice, bread, vegetables with more complexity", 101, 40, 11),
        LessonEntity(102, "Fruits & Drinks (Advanced)", "పండ్లు & పానీయాలు", "Pandlu & Paaneeyalu", "Fruits and beverages with more complexity", 102, 40, 11),
        LessonEntity(103, "Spices & Flavors (Advanced)", "మసాలాలు & రుచులు", "Masaalalu & Ruchulu", "Spicy, salty, sweet, sour with more complexity", 103, 40, 11),
        LessonEntity(104, "At the Restaurant (Advanced)", "హోటల్లో", "Hotalloo", "Ordering food with more complexity", 104, 40, 11),
        LessonEntity(105, "Cooking Verbs (Advanced)", "వంట క్రియలు", "Vanta kriyaalu", "Cook, fry, boil, roast with more complexity", 105, 40, 11),
        LessonEntity(106, "Hungry & Thirsty (Advanced)", "ఆకలి & దప్ప", "Aakali & dhappa", "Expressions of hunger and thirst with more complexity", 106, 40, 11),
        LessonEntity(107, "Restaurant Dialogue (Advanced)", "హోటల్ సంభాషణ", "Hotel sambhaashanam", "Practice ordering with more complexity", 107, 40, 11),
        LessonEntity(108, "Food Review (Advanced)", "ఆహార సమీక్ష", "Ahaara samiksha", "Review food vocabulary with more complexity", 108, 60, 11),
        LessonEntity(109, "Taste & Quality (Advanced)", "రుచి & నాణ్యత", "Ruchi & Naanitya", "Good, bad, tasty, fresh with more complexity", 109, 40, 11),
        LessonEntity(110, "Phase 5 Assessment (Advanced)", "దశ 5 అంచనా", "Dasha 5 anchnaa", "Food and eating test with more complexity", 110, 60, 11),
        LessonEntity(111, "Time of Day (Advanced)", "రోజు సమయాలు", "Roju sama yaalu", "Morning, afternoon, evening, night with more complexity", 111, 40, 12),
        LessonEntity(112, "Days & Months (Advanced)", "రోజులు & నెలలు", "Rojuulu & Nelalu", "Days of week, months of year with more complexity", 112, 40, 12),
        LessonEntity(113, "Seasons & Weather (Advanced)", "ఋతువులు & వాతావరణం", "Ruthuvulu & Vaathavaaranam", "Rainy, sunny, cold, hot with more complexity", 113, 40, 12),
        LessonEntity(114, "Directions (Advanced)", "దిశలు", "Dishalu", "North, south, east, west, left, right with more complexity", 114, 40, 12),
        LessonEntity(115, "Places in Town (Advanced)", "పట్టణంలో ప్రాంతాలు", "Pattanaamlo praantalu", "Bank, hospital, market, school with more complexity", 115, 40, 12),
        LessonEntity(116, "Asking Directions (Advanced)", "దిశలు అడగడం", "Dishalu adagadam", "Where is...? How do I get to...? with more complexity", 116, 40, 12),
        LessonEntity(117, "Travel & Transport (Advanced)", "ప్రయాణం & రవాణా", "Pravaahanam & Ravaana", "Bus, train, car, airplane with more complexity", 117, 40, 12),
        LessonEntity(118, "Time & Place Review (Advanced)", "సమయ & ప్రాంత సమీక్ష", "Samaya & praanta samiksha", "Review time and places with more complexity", 118, 60, 12),
        LessonEntity(119, "Weather & Nature (Advanced)", "వాతావరణ & ప్రకృతి", "Vaathavaarani & prakrithi", "Nature vocabulary with more complexity", 119, 40, 12),
        LessonEntity(120, "Phase 6 Assessment (Advanced)", "దశ 6 అంచనా", "Dasha 6 anchnaa", "Time, place & weather test with more complexity", 120, 100, 12)
    )
}
