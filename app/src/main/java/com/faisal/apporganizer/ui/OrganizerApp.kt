package com.faisal.apporganizer.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.NewReleases
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.faisal.apporganizer.data.AppActions
import com.faisal.apporganizer.data.AppItem
import com.faisal.apporganizer.data.Category
import com.faisal.apporganizer.ui.theme.FavoriteGold
import com.faisal.apporganizer.ui.theme.HeroGradient
import com.faisal.apporganizer.ui.theme.ScreenGradient

private const val SEARCH_COLUMNS = 4

@Composable
fun OrganizerApp(vm: OrganizerViewModel) {
    val context = LocalContext.current
    var sheetPackage by remember { mutableStateOf<String?>(null) }

    val onOpen: (AppItem) -> Unit = { app ->
        if (AppActions.launch(context, app.packageName)) vm.recordLaunch(app.packageName)
    }
    val onLongPress: (AppItem) -> Unit = { sheetPackage = it.packageName }

    BackHandler(enabled = vm.selected != null || vm.query.isNotEmpty()) {
        if (vm.selected != null) vm.selected = null else vm.query = ""
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(ScreenGradient)
            .systemBarsPadding()
            .imePadding(),
    ) {
        if (vm.loading && vm.apps.isEmpty()) {
            LoadingView()
        } else {
            AnimatedContent(
                targetState = vm.selected,
                transitionSpec = {
                    (fadeIn(tween(250)) + scaleIn(tween(250), initialScale = 0.96f)) togetherWith
                        fadeOut(tween(180))
                },
                label = "screen",
            ) { category ->
                if (category == null) {
                    HomeScreen(vm, onOpen, onLongPress)
                } else {
                    CategoryScreen(vm, category, onOpen, onLongPress)
                }
            }
        }
    }

    val sheetApp = sheetPackage?.let { pkg -> vm.apps.firstOrNull { it.packageName == pkg } }
    if (sheetApp != null) {
        AppSheet(
            app = sheetApp,
            isFavorite = vm.isFavorite(sheetApp.packageName),
            onDismiss = { sheetPackage = null },
            onOpen = {
                sheetPackage = null
                onOpen(sheetApp)
            },
            onToggleFavorite = { vm.toggleFavorite(sheetApp.packageName) },
            onMove = { vm.moveTo(sheetApp.packageName, it) },
        )
    }
}

// ───────────────────────────── الشاشة الرئيسية ─────────────────────────────

@Composable
private fun HomeScreen(
    vm: OrganizerViewModel,
    onOpen: (AppItem) -> Unit,
    onLongPress: (AppItem) -> Unit,
) {
    val groups = vm.groups
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        fullLine("hero") { Hero(vm.apps.size, groups.size) }
        fullLine("search") { SearchField(vm.query) { vm.query = it } }

        if (vm.query.isNotBlank()) {
            val results = vm.searchResults
            if (results.isEmpty()) {
                fullLine("empty") { EmptyState("لا توجد نتائج مطابقة") }
            } else {
                fullLine("results-title") { SectionTitle("نتائج البحث (${results.size})", Icons.Rounded.Search) }
                results.chunked(SEARCH_COLUMNS).forEachIndexed { index, row ->
                    fullLine("row-$index") {
                        Row(Modifier.fillMaxWidth()) {
                            row.forEach { app ->
                                AppTile(app, vm.isFavorite(app.packageName), onOpen, onLongPress, Modifier.weight(1f))
                            }
                            repeat(SEARCH_COLUMNS - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        } else {
            appRowSection("fav", "المفضلة", Icons.Rounded.Star, vm.favoriteApps, vm, onOpen, onLongPress)
            appRowSection("used", "الأكثر استخدامًا", Icons.Rounded.TrendingUp, vm.mostUsed, vm, onOpen, onLongPress)
            appRowSection("new", "أُضيفت حديثًا", Icons.Rounded.NewReleases, vm.recentlyInstalled, vm, onOpen, onLongPress)

            fullLine("cats-title") { SectionTitle("الفئات", Icons.Rounded.Dashboard) }
            items(groups, key = { it.first.name }) { (category, apps) ->
                CategoryCard(category, apps) { vm.selected = category }
            }
        }
    }
}

private fun LazyGridScope.fullLine(key: String, content: @Composable () -> Unit) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }) { content() }
}

private fun LazyGridScope.appRowSection(
    key: String,
    title: String,
    icon: ImageVector,
    apps: List<AppItem>,
    vm: OrganizerViewModel,
    onOpen: (AppItem) -> Unit,
    onLongPress: (AppItem) -> Unit,
) {
    if (apps.isEmpty()) return
    fullLine("$key-title") { SectionTitle(title, icon) }
    fullLine("$key-row") {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            items(apps, key = { it.packageName }) { app ->
                AppTile(app, vm.isFavorite(app.packageName), onOpen, onLongPress, Modifier.width(80.dp))
            }
        }
    }
}

@Composable
private fun Hero(total: Int, categories: Int) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(HeroGradient)
            .padding(20.dp),
    ) {
        Icon(
            Icons.Rounded.Dashboard,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.align(Alignment.CenterEnd).size(72.dp),
        )
        Column {
            Text(
                "منظّم التطبيقات",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "${appsCount(total)} مرتّبة في ${categoriesCount(categories)}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.88f),
            )
        }
    }
}

@Composable
private fun SearchField(value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("ابحث عن تطبيق أو فئة…") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "مسح")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
    )
}

@Composable
private fun SectionTitle(text: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun CategoryCard(category: Category, apps: List<AppItem>, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(156.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(category.start, category.end)))
            .clickable(onClick = onClick)
            .padding(14.dp),
    ) {
        Icon(
            category.icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-12).dp, y = 14.dp)
                .size(96.dp),
        )
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(category.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                category.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(appsCount(apps.size), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            Spacer(Modifier.weight(1f))
            // معاينة متداخلة لأيقونات أول التطبيقات
            Box {
                apps.take(4).forEachIndexed { index, app ->
                    Image(
                        bitmap = app.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = (index * 20).dp)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp),
                    )
                }
            }
        }
    }
}

// ───────────────────────────── شاشة الفئة ─────────────────────────────

@Composable
private fun CategoryScreen(
    vm: OrganizerViewModel,
    category: Category,
    onOpen: (AppItem) -> Unit,
    onLongPress: (AppItem) -> Unit,
) {
    val apps = vm.appsIn(category)
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Brush.linearGradient(listOf(category.start, category.end)))
                .padding(8.dp),
        ) {
            Icon(
                category.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp).size(88.dp),
            )
            Column {
                IconButton(onClick = { vm.selected = null }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "رجوع", tint = Color.White)
                }
                Column(Modifier.padding(start = 12.dp, bottom = 12.dp)) {
                    Text(
                        category.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(appsCount(apps.size), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.88f))
                }
            }
        }

        Row(
            Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SortMode.entries.forEach { mode ->
                FilterChip(
                    selected = vm.sort == mode,
                    onClick = { vm.sort = mode },
                    label = { Text(mode.title) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.start.copy(alpha = 0.35f),
                        selectedLabelColor = Color.White,
                    ),
                )
            }
        }

        if (apps.isEmpty()) {
            EmptyState("لا توجد تطبيقات في هذه الفئة")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(84.dp),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(apps, key = { it.packageName }) { app ->
                    AppTile(app, vm.isFavorite(app.packageName), onOpen, onLongPress)
                }
            }
        }
    }
}

// ───────────────────────────── عناصر مشتركة ─────────────────────────────

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppTile(
    app: AppItem,
    favorite: Boolean,
    onOpen: (AppItem) -> Unit,
    onLongPress: (AppItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .combinedClickable(onClick = { onOpen(app) }, onLongClick = { onLongPress(app) })
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Image(bitmap = app.icon, contentDescription = app.label, modifier = Modifier.size(54.dp))
            if (favorite) {
                Icon(
                    Icons.Rounded.Star,
                    contentDescription = null,
                    tint = FavoriteGold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(18.dp)
                        .background(MaterialTheme.colorScheme.background, CircleShape)
                        .padding(2.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            app.label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun LoadingView() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
        Text("جارٍ ترتيب تطبيقاتك…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

// ───────────────────────────── قائمة إجراءات التطبيق ─────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AppSheet(
    app: AppItem,
    isFavorite: Boolean,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit,
    onMove: (Category?) -> Unit,
) {
    val context = LocalContext.current
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(bitmap = app.icon, contentDescription = null, modifier = Modifier.size(56.dp))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        app.label,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(app.category.title, style = MaterialTheme.typography.bodyMedium, color = app.category.start)
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SheetAction(Icons.Rounded.PlayArrow, "فتح", onOpen)
                SheetAction(
                    if (isFavorite) Icons.Rounded.Star else Icons.Rounded.StarBorder,
                    if (isFavorite) "إزالة من المفضلة" else "إضافة للمفضلة",
                    onToggleFavorite,
                    tint = if (isFavorite) FavoriteGold else MaterialTheme.colorScheme.onSurface,
                )
                SheetAction(Icons.Rounded.Info, "معلومات") { AppActions.openInfo(context, app.packageName) }
                SheetAction(Icons.Rounded.Delete, "إلغاء التثبيت") {
                    onDismiss()
                    AppActions.uninstall(context, app.packageName)
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("نقل إلى فئة", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Category.entries.forEach { category ->
                    FilterChip(
                        selected = app.category == category,
                        onClick = { onMove(category) },
                        label = { Text(category.title) },
                        leadingIcon = {
                            Icon(category.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = category.start.copy(alpha = 0.4f),
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White,
                        ),
                    )
                }
            }

            if (app.category != app.autoCategory) {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { onMove(null) }) {
                    Icon(Icons.Rounded.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("إرجاع للتصنيف التلقائي (${app.autoCategory.title})")
                }
            }
        }
    }
}

@Composable
private fun SheetAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Column(
        Modifier
            .width(78.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = tint)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2)
    }
}

// ───────────────────────────── صياغة الأعداد بالعربية ─────────────────────────────

private fun appsCount(n: Int): String = when {
    n == 0 -> "لا توجد تطبيقات"
    n == 1 -> "تطبيق واحد"
    n == 2 -> "تطبيقان"
    n % 100 in 3..10 -> "$n تطبيقات"
    else -> "$n تطبيقًا"
}

private fun categoriesCount(n: Int): String = when {
    n == 0 -> "بلا فئات"
    n == 1 -> "فئة واحدة"
    n == 2 -> "فئتين"
    n % 100 in 3..10 -> "$n فئات"
    else -> "$n فئة"
}
