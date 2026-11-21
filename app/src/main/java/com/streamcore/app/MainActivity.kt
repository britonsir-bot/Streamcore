package com.streamcore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Platform(val label: String, val subtitle: String) {
    YOUTUBE("YouTube", "Video discovery and subscriptions"),
    TIKTOK("TikTok", "Vertical short-video feed"),
    TWITCH("Twitch", "Live streams and categories")
}

data class VideoItem(val title: String, val channel: String, val description: String, val tag: String)

val demoVideos = listOf(
    VideoItem("Building a tiny embedded computer", "Lab Notes", "A project-focused video sample", "Engineering"),
    VideoItem("Why power supplies fail", "Circuit Studio", "Practical electronics troubleshooting", "Education"),
    VideoItem("Linux tools worth learning", "Open Systems", "Software and systems exploration", "Technology"),
    VideoItem("A 90-second science experiment", "Curious Lab", "Short format demo content", "Short")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { StreamCoreApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamCoreApp() {
    var platform by remember { mutableStateOf(Platform.YOUTUBE) }
    var page by remember { mutableStateOf("Feed") }
    var aiFilter by remember { mutableStateOf(true) }
    var shorts by remember { mutableStateOf(false) }
    var keyword by remember { mutableStateOf("") }
    var connected by remember { mutableStateOf(false) }
    val filtered = demoVideos.filter { (shorts || it.tag != "Short") && (keyword.isBlank() || !it.title.contains(keyword, true)) }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Color(0xFFB6C7FF), secondary = Color(0xFFBBC7D8),
        background = Color(0xFF101318), surface = Color(0xFF191D24)
    )) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Column { Text("STREAMCORE", fontWeight = FontWeight.Bold, letterSpacing = 2.sp); Text(platform.label, fontSize = 12.sp) } },
                    actions = { IconButton(onClick = { page = "Settings" }) { Icon(Icons.Default.Settings, "Settings") } },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF191D24))
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf("Feed", "Platforms", "Filters", "Settings").forEach { item ->
                        NavigationBarItem(selected = page == item, onClick = { page = item },
                            icon = { Icon(when(item) { "Feed" -> Icons.Default.PlayArrow; "Platforms" -> Icons.Default.Tv; "Filters" -> Icons.Default.FilterAlt; else -> Icons.Default.Settings }, item) },
                            label = { Text(item) })
                    }
                }
            }
        ) { padding ->
            when(page) {
                "Platforms" -> PlatformPage(platform, { platform = it }, connected, { connected = it }, Modifier.padding(padding))
                "Filters" -> FilterPage(aiFilter, { aiFilter = it }, shorts, { shorts = it }, keyword, { keyword = it }, Modifier.padding(padding))
                "Settings" -> SettingsPage(connected, Modifier.padding(padding))
                else -> FeedPage(platform, filtered, aiFilter, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun FeedPage(platform: Platform, videos: List<VideoItem>, filterOn: Boolean, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Text(if(platform == Platform.TIKTOK) "Vertical feed preview" else "Your feed", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 12.dp))
        if(filterOn) Text("AI content filter enabled · Demo mode", color = Color(0xFF9CC8A8), fontSize = 12.sp)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 18.dp)) {
            items(videos) { video ->
                if(platform == Platform.TIKTOK) VerticalVideoCard(video)
                else StandardVideoCard(video)
            }
            item { Text("Platform feeds are demo content until an adapter is connected.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(8.dp)) }
        }
    }
}

@Composable
fun StandardVideoCard(video: VideoItem) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF202630))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(116.dp).height(80.dp).background(Color(0xFF3C4656), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text(video.title, fontWeight = FontWeight.SemiBold)
                Text(video.channel, color = Color.Gray, fontSize = 12.sp)
                Text(video.description, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun VerticalVideoCard(video: VideoItem) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF202630)), modifier = Modifier.fillMaxWidth().height(360.dp)) {
        Box(Modifier.fillMaxSize().background(Color(0xFF273140)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PlayCircle, null, modifier = Modifier.size(60.dp), tint = Color.White)
                Text(video.title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(12.dp))
                Text(video.channel, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun PlatformPage(selected: Platform, onSelect: (Platform)->Unit, connected: Boolean, setConnected: (Boolean)->Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Platform profiles", style = MaterialTheme.typography.headlineSmall)
        Text("Select an adapter profile. Integrations are currently demonstrators.", color = Color.Gray)
        Platform.entries.forEach { p ->
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF202630))) {
                Row(Modifier.fillMaxWidth().clickable { onSelect(p) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(p.label, fontWeight = FontWeight.Bold); Text(p.subtitle, color = Color.Gray, fontSize = 12.sp) }
                    if(selected == p) Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF9CC8A8))
                }
            }
        }
        Divider()
        Text("Session Vault", style = MaterialTheme.typography.titleMedium)
        Text("Secure session management scaffold. No cookies or credentials are imported in this build.", color = Color.Gray)
        Button(onClick = { setConnected(!connected) }) { Text(if(connected) "Remove demo profile" else "Add demo profile") }
        if(connected) Text("Demo profile active (not authenticated)", color = Color(0xFF9CC8A8))
    }
}

@Composable
fun FilterPage(ai: Boolean, setAi: (Boolean)->Unit, shorts: Boolean, setShorts: (Boolean)->Unit, keyword: String, setKeyword: (String)->Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Content filters", style = MaterialTheme.typography.headlineSmall)
        Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Suspected AI content"); Text("Filter low-effort synthetic content (placeholder)", color = Color.Gray, fontSize = 12.sp) }; Switch(ai, setAi) }
        Row(verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text("Show Shorts / short videos"); Text("Hide short-format sample content", color = Color.Gray, fontSize = 12.sp) }; Switch(shorts, setShorts) }
        OutlinedTextField(keyword, setKeyword, label = { Text("Exclude title keyword") }, modifier = Modifier.fillMaxWidth())
        Text("Rules are applied to demo feed items. AI detection is not yet implemented.", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun SettingsPage(profile: Boolean, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineSmall)
        Text("StreamCore 0.1 · Android native")
        Text("Adapter architecture: modular")
        Text("Session storage: scaffold only")
        Text("Playback engine dependency: Media3 included")
        Text("No external analytics in this starter")
        Text("Platform connectivity and authenticated playback are not enabled.")
    }
}
