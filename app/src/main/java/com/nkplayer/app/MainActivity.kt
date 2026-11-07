package com.nkplayer.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF070B14)
private val SurfaceDeep = Color(0xFF101827)
private val SurfaceGlass = Color(0xCC17243A)
private val Accent = Color(0xFF8AB8FF)
private val AccentViolet = Color(0xFFB5A1FF)
private val TextPrimary = Color(0xFFF4F7FF)
private val TextMuted = Color(0xFF8D9AB0)
private val Hairline = Color.White.copy(alpha = 0.10f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NKPlayerApp() }
    }
}

private enum class PlayerTab { HOME, AUDIO, VIDEO, LIBRARY }

@Composable
private fun NKPlayerApp() {
    val context = LocalContext.current
    var accessRefresh by remember { mutableIntStateOf(0) }
    val audioAllowed = remember(accessRefresh) { hasAudioPermission(context) }
    val videoAllowed = remember(accessRefresh) { hasVideoPermission(context) }
    var audioItems by remember { mutableStateOf<List<DeviceMedia>>(emptyList()) }
    var videoItems by remember { mutableStateOf<List<DeviceMedia>>(emptyList()) }
    var selectedTab by remember { mutableStateOf(PlayerTab.HOME) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { accessRefresh++ }

    fun requestMediaAccess() {
        val permissions = mediaPermissionsToRequest()
        permissionLauncher.launch(permissions)
    }

    LaunchedEffect(audioAllowed, videoAllowed) {
        if (audioAllowed) audioItems = MediaStoreRepository.loadAudio(context)
        if (videoAllowed) videoItems = MediaStoreRepository.loadVideos(context)
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Background) {
            Box(modifier = Modifier.fillMaxSize()) {
                AmbientBackdrop()
                Column(
                    modifier = Modifier.fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 18.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (selectedTab) {
                            PlayerTab.HOME -> HomeScreen(onTabSelected = { selectedTab = it })
                            PlayerTab.AUDIO -> AudioLibraryScreen(
                                items = audioItems,
                                hasAccess = audioAllowed,
                                onRequestAccess = ::requestMediaAccess
                            )
                            PlayerTab.VIDEO -> VideoLibraryScreen(
                                items = videoItems,
                                hasAccess = videoAllowed,
                                onRequestAccess = ::requestMediaAccess
                            )
                            PlayerTab.LIBRARY -> LibraryPlaceholder("Your Library", "Favorites, playlists and listening history will live here.", Icons.Rounded.LibraryMusic)
                        }
                    }
                    BottomNavigation(selectedTab, onSelect = { selectedTab = it })
                }
            }
        }
    }
}

private fun mediaPermissionsToRequest(): Array<String> = when {
    Build.VERSION.SDK_INT >= 34 -> arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO,
        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
    )
    Build.VERSION.SDK_INT >= 33 -> arrayOf(
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )
    else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
}

private fun hasAudioPermission(context: android.content.Context): Boolean {
    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_AUDIO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

private fun hasVideoPermission(context: android.content.Context): Boolean {
    if (Build.VERSION.SDK_INT >= 34) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED
    }
    val permission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.READ_MEDIA_VIDEO
        else Manifest.permission.READ_EXTERNAL_STORAGE
    return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun AmbientBackdrop() {
    Box(Modifier.fillMaxSize().background(Background)) {
        Box(
            Modifier.align(Alignment.TopEnd).offset(x = 110.dp, y = (-90).dp)
                .size(280.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0x443D78D8), Color.Transparent)))
        )
        Box(
            Modifier.align(Alignment.BottomStart).offset(x = (-130).dp, y = 80.dp)
                .size(300.dp).clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0x332F5E9E), Color.Transparent)))
        )
    }
}

@Composable
private fun HomeScreen(onTabSelected: (PlayerTab) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(top = 20.dp, bottom = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("NK PLAYER", color = Accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.8.sp)
                Spacer(Modifier.height(8.dp))
                Text("Welcome back.", color = TextPrimary, fontSize = 29.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.7).sp)
                Text("Your entertainment, one place.", color = TextMuted, fontSize = 13.sp)
            }
            GlassIconButton(Icons.Rounded.Search, "Search") { }
            Spacer(Modifier.width(9.dp))
            GlassIconButton(Icons.Rounded.Settings, "Settings") { }
        }

        Spacer(Modifier.height(27.dp))
        SectionHeading("CONTINUE", "Pick up where you left off")
        Spacer(Modifier.height(12.dp))
        HazeCard(
            modifier = Modifier.fillMaxWidth(),
            brush = Brush.linearGradient(listOf(Color(0xFF263F69), Color(0xFF172A46), Color(0xCC172033))),
            radius = 26.dp
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(62.dp).clip(RoundedCornerShape(19.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF638FD1), Color(0xFF293F69)))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Column(Modifier.weight(1f).padding(start = 15.dp)) {
                    Text("Nothing playing yet", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    Spacer(Modifier.height(5.dp))
                    Text("Choose a track or video to begin", color = Color(0xFFB1BED2), fontSize = 11.sp)
                }
                Box(
                    Modifier.size(38.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.Rounded.PlayArrow, null, tint = Color.White) }
            }
            Spacer(Modifier.height(18.dp))
            Box(Modifier.fillMaxWidth().height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.16f)))
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("READY WHEN YOU ARE", color = Color(0xFFB1BED2), fontSize = 9.sp, letterSpacing = 1.1.sp)
                Text("--:--", color = Color(0xFFB1BED2), fontSize = 9.sp)
            }
        }

        Spacer(Modifier.height(27.dp))
        SectionHeading("YOUR ACTIVITY", "Your personal media space")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActivityTile("Favorites", Icons.Rounded.FavoriteBorder, Modifier.weight(1f))
            ActivityTile("Playlists", Icons.Rounded.QueueMusic, Modifier.weight(1f))
            ActivityTile("History", Icons.Rounded.History, Modifier.weight(1f))
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActivityTile("Recently added", Icons.Rounded.Schedule, Modifier.weight(1f))
            ActivityTile("Continue", Icons.Rounded.PlayCircleOutline, Modifier.weight(1f))
            ActivityTile("Folders", Icons.Rounded.FolderOpen, Modifier.weight(1f))
        }

        Spacer(Modifier.height(27.dp))
        SectionHeading("QUICK SETTINGS", "Your player, your way")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickTile("Equalizer", Icons.Rounded.Equalizer, Modifier.weight(1f))
            QuickTile("Sleep timer", Icons.Rounded.Bedtime, Modifier.weight(1f))
            QuickTile("Speed", Icons.Rounded.Speed, Modifier.weight(1f))
        }

        Spacer(Modifier.height(27.dp))
        SectionHeading("EXPLORE", "Jump into your collection")
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExploreTile("Audio library", "Songs, albums & artists", Icons.Rounded.Headphones, Modifier.weight(1f)) { onTabSelected(PlayerTab.AUDIO) }
            ExploreTile("Video library", "Movies & local clips", Icons.Rounded.Movie, Modifier.weight(1f)) { onTabSelected(PlayerTab.VIDEO) }
        }
        Spacer(Modifier.height(16.dp))
        Text("Media access and playback are being built in the next stages.", color = TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun SectionHeading(kicker: String, subtitle: String) {
    Column {
        Text(kicker, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.7.sp)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = TextPrimary.copy(alpha = 0.92f), fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun HazeCard(
    modifier: Modifier = Modifier,
    brush: Brush = Brush.linearGradient(listOf(SurfaceGlass, SurfaceDeep)),
    radius: androidx.compose.ui.unit.Dp = 22.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(radius))
            .background(brush)
            .border(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.035f))), RoundedCornerShape(radius))
            .padding(17.dp),
        content = content
    )
}

@Composable
private fun GlassIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    Box(
        Modifier.size(43.dp).clip(RoundedCornerShape(15.dp))
            .background(SurfaceGlass)
            .border(1.dp, Hairline, RoundedCornerShape(15.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Icon(icon, contentDescription = description, tint = TextPrimary, modifier = Modifier.size(19.dp)) }
}

@Composable
private fun ActivityTile(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    HazeCard(modifier = modifier, radius = 18.dp) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(21.dp))
        Spacer(Modifier.height(12.dp))
        Text(label, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun QuickTile(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Color(0xCC1B2B43), Color(0xAA111B2B))))
            .border(1.dp, Hairline, RoundedCornerShape(18.dp))
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = AccentViolet, modifier = Modifier.size(21.dp))
        Spacer(Modifier.height(8.dp))
        Text(label, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ExploreTile(title: String, subtitle: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    HazeCard(modifier = modifier.clickable(onClick = onClick), radius = 19.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(39.dp).clip(RoundedCornerShape(13.dp))
                    .background(Color(0x333F75C5)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Accent, modifier = Modifier.size(21.dp)) }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(3.dp))
                Text(subtitle, color = TextMuted, fontSize = 9.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun LibraryPlaceholder(title: String, description: String, icon: ImageVector) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 42.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            Modifier.size(58.dp).clip(RoundedCornerShape(19.dp))
                .background(Color(0x332D5A9B)).border(1.dp, Hairline, RoundedCornerShape(19.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = Accent, modifier = Modifier.size(28.dp)) }
        Spacer(Modifier.height(20.dp))
        Text(title, color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = TextMuted, fontSize = 14.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(22.dp))
        HazeCard(Modifier.fillMaxWidth()) {
            Icon(Icons.Rounded.FolderOpen, null, tint = Accent, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(12.dp))
            Text("Your library is not scanned yet", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(5.dp))
            Text("Device scanning and real media loading will be connected in the library implementation stage.", color = TextMuted, fontSize = 11.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun BottomNavigation(selected: PlayerTab, onSelect: (PlayerTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(23.dp))
            .background(Color(0xD9162235))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(23.dp))
            .padding(horizontal = 5.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem("Home", Icons.Rounded.Home, selected == PlayerTab.HOME) { onSelect(PlayerTab.HOME) }
        NavItem("Audio", Icons.Rounded.Headphones, selected == PlayerTab.AUDIO) { onSelect(PlayerTab.AUDIO) }
        NavItem("Video", Icons.Rounded.VideoLibrary, selected == PlayerTab.VIDEO) { onSelect(PlayerTab.VIDEO) }
        NavItem("Library", Icons.Rounded.LibraryMusic, selected == PlayerTab.LIBRARY) { onSelect(PlayerTab.LIBRARY) }
    }
}

@Composable
private fun NavItem(label: String, icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
            .background(if (active) Color(0x334F83D1) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (active) Accent else TextMuted, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(3.dp))
        Text(label, color = if (active) Accent else TextMuted, fontSize = 9.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
    }
}


@Composable
private fun AudioLibraryScreen(
    items: List<DeviceMedia>,
    hasAccess: Boolean,
    onRequestAccess: () -> Unit
) {
    var selected by remember { mutableStateOf("Songs") }
    var query by remember { mutableStateOf("") }
    var sort by remember { mutableStateOf("Name") }
    val visible = remember(items, query, selected, sort) {
        val searched = items.filter {
            it.title.contains(query, true) || it.artist.contains(query, true) || it.album.contains(query, true)
        }
        when (selected) {
            "Albums" -> searched.distinctBy { it.album }
            "Artists" -> searched.distinctBy { it.artist }
            else -> searched
        }.let { list ->
            if (sort == "Name") list.sortedBy { if (selected == "Artists") it.artist else if (selected == "Albums") it.album else it.title }
            else list.sortedByDescending { it.durationMs }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(top = 22.dp, bottom = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("AUDIO COLLECTION", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(7.dp))
                Text("Your music.", color = TextPrimary, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text(if (hasAccess) "${items.size} tracks on this device" else "Scan your device music", color = TextMuted, fontSize = 12.sp)
            }
            GlassIconButton(Icons.Rounded.Sort, "Change sort") { sort = if (sort == "Name") "Duration" else "Name" }
        }
        Spacer(Modifier.height(22.dp))
        SearchSurface(hint = "Search songs, artists, albums", value = query, onValueChange = { query = it })
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Songs", "Albums", "Artists").forEach { item ->
                FilterPill(item, selected == item) { selected = item }
            }
        }
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(selected, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("${visible.size} items · $sort", color = TextMuted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(13.dp))
        when {
            !hasAccess -> PermissionCard("Allow audio access", "NK PLAYER needs audio permission to list music stored on your device.", onRequestAccess)
            visible.isEmpty() -> EmptyLibraryCard(
                icon = Icons.Rounded.MusicNote,
                title = if (query.isBlank()) "No audio found" else "No matching tracks",
                body = "Try another search, or add supported audio files to your device."
            )
            else -> visible.forEach { item ->
                val title = when (selected) {
                    "Albums" -> item.album
                    "Artists" -> item.artist
                    else -> item.title
                }
                val subtitle = when (selected) {
                    "Albums" -> item.artist
                    "Artists" -> "${items.count { it.artist == item.artist }} tracks"
                    else -> item.artist
                }
                MediaRow(title.ifBlank { "Unknown" }, subtitle, item.durationMs, Icons.Rounded.MusicNote)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun VideoLibraryScreen(
    items: List<DeviceMedia>,
    hasAccess: Boolean,
    onRequestAccess: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(items, query) {
        items.filter { it.title.contains(query, true) }.sortedBy { it.title.lowercase() }
    }
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(top = 22.dp, bottom = 18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("VIDEO COLLECTION", color = Accent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(7.dp))
                Text("Your videos.", color = TextPrimary, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text(if (hasAccess) "${items.size} videos on this device" else "Scan your device videos", color = TextMuted, fontSize = 12.sp)
            }
            GlassIconButton(Icons.Rounded.Movie, "Video library") { }
        }
        Spacer(Modifier.height(22.dp))
        SearchSurface(hint = "Search videos", value = query, onValueChange = { query = it })
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("All videos", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Text("${visible.size} items", color = TextMuted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(13.dp))
        when {
            !hasAccess -> PermissionCard("Allow video access", "NK PLAYER needs video permission to show local videos. Android may let you select only specific videos.", onRequestAccess)
            visible.isEmpty() -> EmptyLibraryCard(
                icon = Icons.Rounded.Movie,
                title = if (query.isBlank()) "No videos found" else "No matching videos",
                body = "Supported local video files will appear here after a device scan."
            )
            else -> visible.forEach { item ->
                MediaRow(item.title, "Local video", item.durationMs, Icons.Rounded.PlayCircleOutline)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun PermissionCard(title: String, body: String, onRequestAccess: () -> Unit) {
    HazeCard(Modifier.fillMaxWidth(), radius = 22.dp) {
        Icon(Icons.Rounded.FolderOpen, null, tint = Accent, modifier = Modifier.size(27.dp))
        Spacer(Modifier.height(12.dp))
        Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(body, color = TextMuted, fontSize = 11.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF31578D))
                .clickable(onClick = onRequestAccess)
                .padding(horizontal = 17.dp, vertical = 11.dp)
        ) { Text("Grant permission", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
    }
}

@Composable
private fun MediaRow(title: String, subtitle: String, durationMs: Long, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp))
            .background(Color(0xAA17243A))
            .border(1.dp, Hairline, RoundedCornerShape(17.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(43.dp).clip(RoundedCornerShape(13.dp))
                .background(Color(0x333F75C5)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = Accent, modifier = Modifier.size(21.dp)) }
        Column(Modifier.weight(1f).padding(start = 11.dp)) {
            Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Spacer(Modifier.height(3.dp))
            Text(subtitle, color = TextMuted, fontSize = 10.sp, maxLines = 1)
        }
        Text(formatDuration(durationMs), color = TextMuted, fontSize = 9.sp)
    }
}

private fun formatDuration(durationMs: Long): String {
    val totalSeconds = (durationMs / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun SearchSurface(hint: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(17.dp))
            .background(Color(0xAA17243A))
            .border(1.dp, Hairline, RoundedCornerShape(17.dp))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, null, tint = TextMuted, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(10.dp))
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 12.sp),
            modifier = Modifier.weight(1f),
            decorationBox = { inner ->
                Box {
                    if (value.isEmpty()) Text(hint, color = TextMuted, fontSize = 12.sp)
                    inner()
                }
            }
        )
        if (value.isNotEmpty()) {
            Icon(Icons.Rounded.Close, contentDescription = "Clear search", tint = TextMuted,
                modifier = Modifier.size(17.dp).clickable { onValueChange("") })
        }
    }
}

@Composable
private fun FilterPill(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(CircleShape)
            .background(if (active) Color(0xFF31578D) else Color(0x88172335))
            .border(1.dp, if (active) Color(0x668AB8FF) else Hairline, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp)
    ) {
        Text(label, color = if (active) Color.White else TextMuted, fontSize = 10.sp, fontWeight = if (active) FontWeight.SemiBold else FontWeight.Medium)
    }
}

@Composable
private fun EmptyLibraryCard(icon: ImageVector, title: String, body: String) {
    HazeCard(
        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
        brush = Brush.linearGradient(listOf(Color(0xCC1B2A42), Color(0xAA101A2A))),
        radius = 23.dp
    ) {
        Box(
            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.size(76.dp).clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0x553D70B7), Color(0x111B2A42))))
                    .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Accent, modifier = Modifier.size(31.dp)) }
        }
        Spacer(Modifier.height(14.dp))
        Text(title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(body, color = TextMuted, fontSize = 11.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(13.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(Accent))
            Spacer(Modifier.width(7.dp))
            Text("LIBRARY UI READY · MEDIA SCAN PENDING", color = Accent, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}
