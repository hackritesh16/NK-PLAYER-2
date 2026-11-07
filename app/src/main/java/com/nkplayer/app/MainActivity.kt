package com.nkplayer.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF090D18)
private val Panel = Color(0xFF151D2D)
private val Blue = Color(0xFF72A7FF)
private val Muted = Color(0xFF8F9BB0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { NKPlayerApp() }
    }
}

private enum class PlayerTab { HOME, AUDIO, VIDEO, LIBRARY }

@Composable
private fun NKPlayerApp() {
    var selectedTab by remember { mutableStateOf(PlayerTab.HOME) }
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
            Column(
                modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                when (selectedTab) {
                    PlayerTab.HOME -> HomeScreen(onTabSelected = { selectedTab = it })
                    PlayerTab.AUDIO -> SimpleTab("Your Audio", "Your music library will appear here after media scanning is added.", Icons.Rounded.MusicNote)
                    PlayerTab.VIDEO -> SimpleTab("Your Videos", "Your video library will appear here after media scanning is added.", Icons.Rounded.VideoLibrary)
                    PlayerTab.LIBRARY -> SimpleTab("Your Library", "Playlists, favorites and history will live here.", Icons.Rounded.LibraryMusic)
                }
                Spacer(Modifier.weight(1f))
                BottomNavigation(selectedTab, onSelect = { selectedTab = it })
            }
        }
    }
}

@Composable
private fun HomeScreen(onTabSelected: (PlayerTab) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("NK PLAYER", color = Blue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(Modifier.height(7.dp))
                Text("Welcome back.", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                Text("Your entertainment, one place.", color = Muted, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier.size(46.dp).clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF345B9B), Color(0xFF19263E)))),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Rounded.Album, contentDescription = null, tint = Color.White) }
        }
        Spacer(Modifier.height(28.dp))
        Text("PICK UP WHERE YOU LEFT OFF", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                .background(Brush.linearGradient(listOf(Color(0xFF243D66), Color(0xFF17243A), Color(0xFF17202F))))
                .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(24.dp))
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(58.dp).clip(RoundedCornerShape(17.dp))
                    .background(Color(0xFF3D5F91)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp)) }
            Column(modifier = Modifier.weight(1f).padding(start = 14.dp)) {
                Text("Nothing playing yet", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text("Choose a track or video to begin", color = Muted, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(26.dp))
        Text("YOUR ACTIVITY", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActivityTile("Favorites", Icons.Rounded.FavoriteBorder, Modifier.weight(1f))
            ActivityTile("Playlists", Icons.Rounded.LibraryMusic, Modifier.weight(1f))
            ActivityTile("Recently added", Icons.Rounded.Schedule, Modifier.weight(1f))
        }
        Spacer(Modifier.height(26.dp))
        Text("QUICK ACCESS", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickTile("Audio", Icons.Rounded.MusicNote, Modifier.weight(1f)) { onTabSelected(PlayerTab.AUDIO) }
            QuickTile("Video", Icons.Rounded.VideoLibrary, Modifier.weight(1f)) { onTabSelected(PlayerTab.VIDEO) }
            QuickTile("Library", Icons.Rounded.LibraryMusic, Modifier.weight(1f)) { onTabSelected(PlayerTab.LIBRARY) }
        }
        Spacer(Modifier.height(18.dp))
        Text("Local media scanning and playback are the next implementation stage.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun ActivityTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp))
            .background(Panel).border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(18.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(9.dp))
        Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun QuickTile(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(18.dp)).background(Panel)
            .clickable(onClick = onClick).padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(7.dp))
        Text(label, color = Color.White, fontSize = 11.sp)
    }
}

@Composable
private fun SimpleTab(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 40.dp)) {
        Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(36.dp))
        Spacer(Modifier.height(16.dp))
        Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(description, color = Muted, fontSize = 14.sp, lineHeight = 21.sp)
    }
}

@Composable
private fun BottomNavigation(selected: PlayerTab, onSelect: (PlayerTab) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 10.dp)
            .clip(RoundedCornerShape(22.dp)).background(Color(0xFF121A29))
            .border(1.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(22.dp))
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavItem("Home", Icons.Rounded.Home, selected == PlayerTab.HOME) { onSelect(PlayerTab.HOME) }
        NavItem("Audio", Icons.Rounded.MusicNote, selected == PlayerTab.AUDIO) { onSelect(PlayerTab.AUDIO) }
        NavItem("Video", Icons.Rounded.VideoLibrary, selected == PlayerTab.VIDEO) { onSelect(PlayerTab.VIDEO) }
        NavItem("Library", Icons.Rounded.LibraryMusic, selected == PlayerTab.LIBRARY) { onSelect(PlayerTab.LIBRARY) }
    }
}

@Composable
private fun NavItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, active: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(14.dp)).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = if (active) Blue else Muted, modifier = Modifier.size(21.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = if (active) Blue else Muted, fontSize = 10.sp, fontWeight = if (active) FontWeight.Bold else FontWeight.Normal)
    }
}
