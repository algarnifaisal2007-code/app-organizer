package com.faisal.apporganizer.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Brightness3
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * فئات التطبيقات. ترتيب القيم هنا هو ترتيب أولوية المطابقة بالكلمات المفتاحية،
 * لذا الفئات الأكثر تحديدًا تأتي أولًا.
 */
enum class Category(
    val title: String,
    val icon: ImageVector,
    val start: Color,
    val end: Color,
    val keywords: List<String> = emptyList(),
) {
    ISLAMIC(
        "إسلامي", Icons.Rounded.Brightness3, Color(0xFF1B8A5A), Color(0xFF0E5E3F),
        listOf(
            "quran", "koran", "prayer", "azan", "adhan", "athan", "muslim", "islam", "salat",
            "salah", "qibla", "hisn", "athkar", "azkar", "adhkar", "hadith",
            "قرآن", "القران", "أذان", "اذان", "صلاة", "أذكار", "اذكار",
        ),
    ),
    GOVERNMENT(
        "خدمات حكومية", Icons.Rounded.AccountBalance, Color(0xFF2E7D6B), Color(0xFF1D4E89),
        listOf(
            "absher", "tawakkalna", "nafath", "sehhaty", "mawid", "balady", "gosi", "qiwa",
            "najiz", "ejar", "musaned", "gov.sa", "sa.gov",
            "أبشر", "توكلنا", "نفاذ", "صحتي",
        ),
    ),
    FINANCE(
        "مالية وبنوك", Icons.Rounded.AccountBalanceWallet, Color(0xFFF2A93B), Color(0xFFD9534F),
        listOf(
            "bank", "rajhi", "alinma", "snb", "alahli", "albilad", "aljazira", "sabb", "stcpay",
            "stc.pay", "urpay", "paypal", "wallet", "tamara", "tabby", "finance", "crypto",
            "binance", "tadawul", "stock", "invest", "مصرف", "بنك",
        ),
    ),
    FOOD(
        "طعام وتوصيل", Icons.Rounded.Fastfood, Color(0xFFFF7A45), Color(0xFFE0405A),
        listOf(
            "talabat", "hungerstation", "jahez", "mrsool", "toyou", "keeta", "ubereats",
            "deliveroo", "mcdonald", "kfc", "starbucks", "dominos", "pizza", "burger", "food",
            "restaurant", "coffee",
        ),
    ),
    SHOPPING(
        "تسوق", Icons.Rounded.ShoppingCart, Color(0xFFEC4899), Color(0xFF9D4EDD),
        listOf(
            "amazon", "com.noon", "shein", "aliexpress", "temu", "ebay", "jarir", "namshi",
            "nahdi", "ikea", "shopping", "souq", "market",
        ),
    ),
    COMMUNICATION(
        "مراسلة واتصال", Icons.Rounded.Call, Color(0xFF25D366), Color(0xFF128C7E),
        listOf(
            "whatsapp", "telegram", "messenger", "orca", "signal", "viber", "com.imo", "skype",
            "zoom", "teams", "discord", "botim", "dialer", "contacts", "messaging", "mms", "sms",
            "gmail", "email", "mail", "outlook", "slack", "رسائل", "هاتف",
        ),
    ),
    SOCIAL(
        "تواصل اجتماعي", Icons.Rounded.People, Color(0xFF4F7CFF), Color(0xFFB14FFF),
        listOf(
            "facebook", "instagram", "twitter", "snapchat", "tiktok", "musically", "trill",
            "linkedin", "reddit", "pinterest", "threads", "tumblr", "social",
        ),
    ),
    MUSIC(
        "موسيقى وصوتيات", Icons.Rounded.MusicNote, Color(0xFFFF4FA3), Color(0xFF7A3CFF),
        listOf(
            "spotify", "anghami", "music", "soundcloud", "deezer", "podcast", "audio", "radio",
            "recorder", "mp3", "صوت",
        ),
    ),
    VIDEO(
        "فيديو وترفيه", Icons.Rounded.Movie, Color(0xFFE53935), Color(0xFF8E24AA),
        listOf(
            "youtube", "netflix", "shahid", "osn", "disney", "primevideo", "avod", "video",
            "player", "vlc", "twitch", "starz", "movie", "cinema", "فيديو",
        ),
    ),
    PHOTO(
        "صور وكاميرا", Icons.Rounded.PhotoCamera, Color(0xFFFFB347), Color(0xFFFF6F61),
        listOf(
            "camera", "gallery", "photo", "snapseed", "lightroom", "picsart", "canva", "capcut",
            "inshot", "vsco", "remini", "facetune", "صور", "كاميرا",
        ),
    ),
    GAMES(
        "ألعاب", Icons.Rounded.SportsEsports, Color(0xFF7C4DFF), Color(0xFF00BCD4),
        listOf(
            "game", "pubg", "tencent.ig", "supercell", "clash", "roblox", "minecraft", "mojang",
            "com.king.", "freefire", "garena", "activision", "gameloft", "miniclip", "rovio",
            "playrix", "zynga", "puzzle", "chess", "ludo", "لعبة", "العاب",
        ),
    ),
    TRAVEL(
        "خرائط وسفر", Icons.Rounded.Map, Color(0xFF00A884), Color(0xFF3F8CFF),
        listOf(
            "maps", "waze", "uber", "careem", "bolt", "booking", "airbnb", "flynas", "saudia",
            "flyadeal", "travel", "trip", "expedia", "almosafer", "hotel", "flight", "taxi",
            "navigation", "خرائط",
        ),
    ),
    NEWS(
        "أخبار", Icons.Rounded.Public, Color(0xFF607D8B), Color(0xFF263238),
        listOf("news", "sabq", "alarabiya", "aljazeera", "okaz", "cnn", "bbc", "أخبار", "اخبار"),
    ),
    HEALTH(
        "صحة ولياقة", Icons.Rounded.FitnessCenter, Color(0xFF43A047), Color(0xFF00897B),
        listOf(
            "health", "fitness", "fitbit", "workout", "gym", "steps", "calorie", "sleep",
            "meditat", "yoga", "diet", "صحة",
        ),
    ),
    EDUCATION(
        "تعليم وقراءة", Icons.Rounded.School, Color(0xFF3949AB), Color(0xFF1E88E5),
        listOf(
            "duolingo", "classroom", "madrasati", "noor", "learn", "school", "education",
            "kindle", "book", "dictionary", "translate", "quizlet", "coursera", "udemy", "khan",
            "blackboard", "university", "تعليم", "مدرستي", "قاموس",
        ),
    ),
    PRODUCTIVITY(
        "إنتاجية وعمل", Icons.Rounded.Work, Color(0xFF5C6BC0), Color(0xFF26A69A),
        listOf(
            "docs", "sheets", "slides", "drive", "office", "word", "excel", "powerpoint", "notion",
            "calendar", "keep", "notes", "todo", "task", "evernote", "pdf", "scanner", "dropbox",
            "onedrive", "trello", "asana", "jira", "ملاحظات", "تقويم",
        ),
    ),
    TOOLS(
        "أدوات", Icons.Rounded.Build, Color(0xFF78909C), Color(0xFF455A64),
        listOf(
            "calculator", "clock", "files", "filemanager", "cleaner", "vpn", "browser", "chrome",
            "firefox", "opera", "keyboard", "launcher", "flashlight", "torch", "weather",
            "compass", "barcode", "backup", "battery", "حاسبة", "ساعة", "ملفات", "طقس",
        ),
    ),
    SYSTEM(
        "النظام", Icons.Rounded.Settings, Color(0xFF546E7A), Color(0xFF37474F),
        listOf(
            "settings", "com.android.", "com.samsung.android.", "com.sec.", "com.miui.",
            "com.huawei.", "com.oppo.", "com.coloros.", "الإعدادات",
        ),
    ),
    OTHER("أخرى", Icons.Rounded.Apps, Color(0xFF8D6E63), Color(0xFF5D4037)),
}
