package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LocalizationDictionary
import com.example.data.SupportedLanguages
import com.example.ui.components.CountryFlagVector
import com.example.ui.components.PremiumCheckIcon
import com.example.ui.theme.NaturalAccentGold
import com.example.ui.theme.NaturalBackgroundLight
import com.example.ui.theme.NaturalBorderLight
import com.example.ui.theme.NaturalPrimaryLight
import com.example.ui.theme.NaturalSecondaryLight
import com.example.ui.theme.NaturalSurfaceLight
import com.example.ui.theme.NaturalTextPrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectionScreen(
    onLanguageConfirmed: (AppLanguage) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    val filteredLanguages = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            SupportedLanguages.languages
        } else {
            SupportedLanguages.languages.filter {
                it.nativeName.contains(searchQuery, ignoreCase = true) ||
                        it.countryNameNative.contains(searchQuery, ignoreCase = true) ||
                        it.code.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val currentStrings = selectedLanguage?.let { LocalizationDictionary.getStrings(it.code) }
        ?: LocalizationDictionary.getStrings("bn")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_logo_1786643462553),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = currentStrings.selectLanguageTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = NaturalTextPrimaryLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NaturalBackgroundLight
                )
            )
        },
        containerColor = NaturalBackgroundLight
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = currentStrings.selectLanguageSubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(text = currentStrings.searchLanguagePlaceholder) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .testTag("search_language_input"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaturalAccentGold,
                    unfocusedBorderColor = NaturalBorderLight,
                    focusedContainerColor = NaturalSurfaceLight,
                    unfocusedContainerColor = NaturalSurfaceLight
                )
            )

            // Dynamic Scrollable Languages Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(filteredLanguages, key = { it.code }) { lang ->
                    val isSelected = selectedLanguage?.code == lang.code
                    LanguageCardItem(
                        language = lang,
                        isSelected = isSelected,
                        onSelect = { selectedLanguage = lang }
                    )
                }
            }

            // Continue Button
            Button(
                onClick = {
                    selectedLanguage?.let { onLanguageConfirmed(it) }
                },
                enabled = selectedLanguage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 12.dp)
                    .testTag("continue_language_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaturalPrimaryLight,
                    disabledContainerColor = Color(0xFFD0C6B8)
                )
            ) {
                Text(
                    text = currentStrings.continueButton,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun LanguageCardItem(
    language: AppLanguage,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1.0f,
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NaturalAccentGold else NaturalBorderLight,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("language_card_${language.code}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF2E6D0) else NaturalSurfaceLight
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CountryFlagVector(
                    countryCode = language.countryCode,
                    size = 28.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = language.nativeName,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 15.sp,
                    color = NaturalTextPrimaryLight
                )
            }

            if (isSelected) {
                PremiumCheckIcon(
                    size = 22.dp,
                    tint = NaturalSecondaryLight
                )
            }
        }
    }
}
