$baseDir = "d:\VidyaSetu AI\vidyastu_mboile_app\app\src\main\java\com\example\vidyastu_mboile_app"

function Create-File($relPath, $content) {
    $fullPath = Join-Path $baseDir $relPath
    $parentDir = Split-Path $fullPath
    if (!(Test-Path $parentDir)) {
        New-Item -ItemType Directory -Path $parentDir -Force | Out-Null
    }
    [System.IO.File]::WriteAllText($fullPath, $content, [System.Text.Encoding]::UTF8)
    Write-Host "Created: $fullPath"
}

# 1. Create Core Files
Create-File "core\auth\AuthManager.kt" "package com.example.vidyastu_mboile_app.core.auth`n`nclass AuthManager"
Create-File "core\auth\SessionManager.kt" "package com.example.vidyastu_mboile_app.core.auth`n`nclass SessionManager"
Create-File "core\auth\RoleManager.kt" "package com.example.vidyastu_mboile_app.core.auth`n`nclass RoleManager"
Create-File "core\database\AppDatabase.kt" "package com.example.vidyastu_mboile_app.core.database`n`nclass AppDatabase"
Create-File "core\database\Converters.kt" "package com.example.vidyastu_mboile_app.core.database`n`nclass Converters"
Create-File "core\network\SupabaseConfig.kt" "package com.example.vidyastu_mboile_app.core.network`n`nclass SupabaseConfig"
Create-File "core\network\ApiClient.kt" "package com.example.vidyastu_mboile_app.core.network`n`nclass ApiClient"
Create-File "core\sync\SyncManager.kt" "package com.example.vidyastu_mboile_app.core.sync`n`nclass SyncManager"
Create-File "core\sync\SyncWorker.kt" "package com.example.vidyastu_mboile_app.core.sync`n`nclass SyncWorker"
Create-File "core\sync\ConflictResolver.kt" "package com.example.vidyastu_mboile_app.core.sync`n`nclass ConflictResolver"
Create-File "core\notification\FcmHandler.kt" "package com.example.vidyastu_mboile_app.core.notification`n`nclass FcmHandler"
Create-File "core\analytics\AnalyticsManager.kt" "package com.example.vidyastu_mboile_app.core.analytics`n`nclass AnalyticsManager"
Create-File "core\common\SharedModels.kt" "package com.example.vidyastu_mboile_app.core.common`n`nclass SharedModels"
Create-File "core\common\Constants.kt" "package com.example.vidyastu_mboile_app.core.common`n`nobject Constants"
Create-File "core\ui\components\AppButtons.kt" "package com.example.vidyastu_mboile_app.core.ui.components`n`n// Reusable UI components"

$spacingContent = @"
package com.example.vidyastu_mboile_app.core.ui.spacing

import androidx.compose.ui.unit.dp

object Spacing {
    val extraSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val extraLarge = 32.dp
}
"@
Create-File "core\ui\spacing\Spacing.kt" $spacingContent

$appColorsContent = @"
package com.example.vidyastu_mboile_app.core.ui.colors

import androidx.compose.ui.graphics.Color

object AppColors {
    val Primary = Color(0xFF6200EE)
    val Secondary = Color(0xFF03DAC6)
    val Background = Color(0xFFFFFFFF)
}
"@
Create-File "core\ui\colors\AppColors.kt" $appColorsContent

Create-File "core\ui\icons\AppIcons.kt" "package com.example.vidyastu_mboile_app.core.ui.icons`n`nobject AppIcons"
Create-File "core\ui\typography\AppTypography.kt" "package com.example.vidyastu_mboile_app.core.ui.typography`n`nobject AppTypography"

# Move and update theme files to core\ui\theme
$colorContent = @"
package com.example.vidyastu_mboile_app.core.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
"@
Create-File "core\ui\theme\Color.kt" $colorContent

$typeContent = @"
package com.example.vidyastu_mboile_app.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
"@
Create-File "core\ui\theme\Type.kt" $typeContent

$themeContent = @"
package com.example.vidyastu_mboile_app.core.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun VidyaStuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
"@
Create-File "core\ui\theme\Theme.kt" $themeContent

# 2. Features Files
$features = @(
    @{ name = "feature_auth"; entity = "AuthEntity"; dao = "AuthDao"; ds_local = "AuthLocalDataSource"; api = "AuthApi"; dto = "AuthDto"; ds_remote = "AuthRemoteDataSource"; mapper = "AuthMapper"; repo_impl = "AuthRepositoryImpl"; model = "AuthUser"; repo = "AuthRepository"; usecase = "LoginUseCase"; screen = "LoginScreen"; vm = "LoginViewModel"; state = "LoginUiState"; event = "LoginEvent"; comp = "AuthComponents" },
    @{ name = "feature_feed"; entity = "FeedEntity"; dao = "FeedDao"; ds_local = "FeedLocalDataSource"; api = "FeedApi"; dto = "FeedDto"; ds_remote = "FeedRemoteDataSource"; mapper = "FeedMapper"; repo_impl = "FeedRepositoryImpl"; model = "FeedItem"; repo = "FeedRepository"; usecase = "GetHomeFeedUseCase"; screen = "HomeScreen"; vm = "HomeViewModel"; state = "HomeUiState"; event = "HomeEvent"; comp = "FeedComponents" },
    @{ name = "feature_journey"; entity = "JourneyEntity"; dao = "JourneyDao"; ds_local = "JourneyLocalDataSource"; api = "JourneyApi"; dto = "JourneyDto"; ds_remote = "JourneyRemoteDataSource"; mapper = "JourneyMapper"; repo_impl = "JourneyRepositoryImpl"; model = "Journey"; repo = "JourneyRepository"; usecase = "GenerateJourneyUseCase"; screen = "JourneyScreen"; vm = "JourneyViewModel"; state = "JourneyUiState"; event = "JourneyEvent"; comp = "JourneyComponents" },
    @{ name = "feature_tournament"; entity = "TournamentEntity"; dao = "TournamentDao"; ds_local = "TournamentLocalDataSource"; api = "TournamentApi"; dto = "TournamentDto"; ds_remote = "TournamentRemoteDataSource"; mapper = "TournamentMapper"; repo_impl = "TournamentRepositoryImpl"; model = "Tournament"; repo = "TournamentRepository"; usecase = "CreateTournamentUseCase"; screen = "TournamentScreen"; vm = "TournamentViewModel"; state = "TournamentUiState"; event = "TournamentEvent"; comp = "TournamentComponents" },
    @{ name = "feature_campus"; entity = "CampusEntity"; dao = "CampusDao"; ds_local = "CampusLocalDataSource"; api = "CampusApi"; dto = "CampusDto"; ds_remote = "CampusRemoteDataSource"; mapper = "CampusMapper"; repo_impl = "CampusRepositoryImpl"; model = "CampusRoom"; repo = "CampusRepository"; usecase = "GetCampusRoomsUseCase"; screen = "CampusScreen"; vm = "CampusViewModel"; state = "CampusUiState"; event = "CampusEvent"; comp = "CampusComponents" },
    @{ name = "feature_institution"; entity = "InstitutionEntity"; dao = "InstitutionDao"; ds_local = "InstitutionLocalDataSource"; api = "InstitutionApi"; dto = "InstitutionDto"; ds_remote = "InstitutionRemoteDataSource"; mapper = "InstitutionMapper"; repo_impl = "InstitutionRepositoryImpl"; model = "Institution"; repo = "InstitutionRepository"; usecase = "GetInstitutionDetailsUseCase"; screen = "InstitutionScreen"; vm = "InstitutionViewModel"; state = "InstitutionUiState"; event = "InstitutionEvent"; comp = "InstitutionComponents" },
    @{ name = "feature_profile"; entity = "ProfileEntity"; dao = "ProfileDao"; ds_local = "ProfileLocalDataSource"; api = "ProfileApi"; dto = "ProfileDto"; ds_remote = "ProfileRemoteDataSource"; mapper = "ProfileMapper"; repo_impl = "ProfileRepositoryImpl"; model = "UserProfile"; repo = "ProfileRepository"; usecase = "GetUserProfileUseCase"; screen = "ProfileScreen"; vm = "ProfileViewModel"; state = "ProfileUiState"; event = "ProfileEvent"; comp = "ProfileComponents" },
    @{ name = "feature_notifications"; entity = "NotificationEntity"; dao = "NotificationDao"; ds_local = "NotificationLocalDataSource"; api = "NotificationApi"; dto = "NotificationDto"; ds_remote = "NotificationRemoteDataSource"; mapper = "NotificationMapper"; repo_impl = "NotificationRepositoryImpl"; model = "AppNotification"; repo = "NotificationRepository"; usecase = "GetNotificationsUseCase"; screen = "NotificationScreen"; vm = "NotificationViewModel"; state = "NotificationUiState"; event = "NotificationEvent"; comp = "NotificationComponents" }
)

foreach ($feat in $features) {
    $feat_name = $feat.name
    $feat_pkg = "com.example.vidyastu_mboile_app.$feat_name"
    
    Create-File "$feat_name\data\local\dao\$($feat.dao).kt" "package $feat_pkg.data.local.dao`n`ninterface $($feat.dao)"
    Create-File "$feat_name\data\local\entity\$($feat.entity).kt" "package $feat_pkg.data.local.entity`n`nclass $($feat.entity)"
    Create-File "$feat_name\data\local\datasource\$($feat.ds_local).kt" "package $feat_pkg.data.local.datasource`n`nclass $($feat.ds_local)"
    
    Create-File "$feat_name\data\remote\api\$($feat.api).kt" "package $feat_pkg.data.remote.api`n`ninterface $($feat.api)"
    Create-File "$feat_name\data\remote\dto\$($feat.dto).kt" "package $feat_pkg.data.remote.dto`n`nclass $($feat.dto)"
    Create-File "$feat_name\data\remote\datasource\$($feat.ds_remote).kt" "package $feat_pkg.data.remote.datasource`n`nclass $($feat.ds_remote)"
    
    Create-File "$feat_name\data\mapper\$($feat.mapper).kt" "package $feat_pkg.data.mapper`n`nclass $($feat.mapper)"
    Create-File "$feat_name\data\repository\$($feat.repo_impl).kt" "package $feat_pkg.data.repository`n`nclass $($feat.repo_impl)"
    
    Create-File "$feat_name\domain\model\$($feat.model).kt" "package $feat_pkg.domain.model`n`nclass $($feat.model)"
    Create-File "$feat_name\domain\repository\$($feat.repo).kt" "package $feat_pkg.domain.repository`n`ninterface $($feat.repo)"
    Create-File "$feat_name\domain\usecase\$($feat.usecase).kt" "package $feat_pkg.domain.usecase`n`nclass $($feat.usecase)"
    
    Create-File "$feat_name\presentation\component\$($feat.comp).kt" "package $feat_pkg.presentation.component`n`n// UI Components for $feat_name"
    Create-File "$feat_name\presentation\state\$($feat.state).kt" "package $feat_pkg.presentation.state`n`nclass $($feat.state)"
    Create-File "$feat_name\presentation\event\$($feat.event).kt" "package $feat_pkg.presentation.event`n`ninterface $($feat.event)"
    Create-File "$feat_name\presentation\viewmodel\$($feat.vm).kt" "package $feat_pkg.presentation.viewmodel`n`nimport androidx.lifecycle.ViewModel`n`nclass $($feat.vm) : ViewModel()"
    
    if ($feat_name -eq "feature_auth") {
        $loginContent = @"
package com.example.vidyastu_mboile_app.feature_auth.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "this is log in screen")
    }
}
"@
        Create-File "feature_auth\presentation\screen\LoginScreen.kt" $loginContent

        $splashContent = @"
package com.example.vidyastu_mboile_app.feature_auth.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(key1 = true) {
        delay(2000)
        onNavigateToLogin()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "this is splash screen",
            color = Color.Black
        )
    }
}
"@
        Create-File "feature_auth\presentation\screen\SplashScreen.kt" $splashContent
    } else {
        Create-File "$feat_name\presentation\screen\$($feat.screen).kt" "package $feat_pkg.presentation.screen`n`nimport androidx.compose.runtime.Composable`n`n@Composable`nfun $($feat.screen)()"
    }
}

# 3. Create Navigation Files
$rootNavGraphContent = @"
package com.example.vidyastu_mboile_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vidyastu_mboile_app.feature_auth.presentation.screen.LoginScreen
import com.example.vidyastu_mboile_app.feature_auth.presentation.screen.SplashScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
}

@Composable
fun RootNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Login.route) {
            LoginScreen()
        }
    }
}
"@
Create-File "navigation\RootNavGraph.kt" $rootNavGraphContent

Create-File "navigation\AuthNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass AuthNavGraph"
Create-File "navigation\MainNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass MainNavGraph"
Create-File "navigation\FeedNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass FeedNavGraph"
Create-File "navigation\JourneyNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass JourneyNavGraph"
Create-File "navigation\TournamentNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass TournamentNavGraph"
Create-File "navigation\CampusNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass CampusNavGraph"
Create-File "navigation\InstitutionNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass InstitutionNavGraph"
Create-File "navigation\ProfileNavGraph.kt" "package com.example.vidyastu_mboile_app.navigation`n`nclass ProfileNavGraph"

# Delete the old ui/theme folder
$oldUiThemeDir = Join-Path $baseDir "ui"
if (Test-Path $oldUiThemeDir) {
    Remove-Item -Path $oldUiThemeDir -Recurse -Force
    Write-Host "Removed old ui folder: $oldUiThemeDir"
}

Write-Host "All directories and files created successfully!"
